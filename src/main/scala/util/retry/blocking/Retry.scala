package util.retry.blocking

import util.retry.blocking.RetryStrategy.RetryStrategyProducer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

/**
  * The `Retry` type represents a computation that is retrying itself in case of an exception. It uses a `RetryStrategy`
  * as a policy for the retry operation.
  *
  * The result may be successful consisting of the computation result value or a failure that
  * is wrapping the underlying exception. The type is similar to the scala [[scala.util.Try]] type.
  *
  * Example:
  * {{{
  * import scala.concurrent.duration._
  * import util.retry.blocking.{RetryStrategy, Failure, Retry, Success}
  *
  * implicit val retryStrategy =
  *  RetryStrategy.fixedBackOff(retryDuration = 1.seconds, maxAttempts = 2)
  *
  * val r = Retry(1 / 1) match {
  *   case Success(x) => x
  *   case Failure(t) => log("Exception occurred", t)
  * }
  * }}}
  */
sealed trait Retry[+T] {
  /**
   * Returns the result by mapping f to the current value of Success or a Failure if the result is Failure
   */
  def flatMap[S](f: T => Retry[S]): Retry[S]

  /**
    * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
    */
  def map[S](f: T => S)(implicit strategy: RetryStrategyProducer): Retry[S]

  /**
    * Returns `true` if the `Retry` is a `Failure` otherwise it returns `false`.
    */
  def isFailure: Boolean

  /**
    * Returns `true` if the `Retry` is a `Success` otherwise it returns `false` otherwise.
    */
  def isSuccess: Boolean

  /**
    * Returns the computation value in case of a `Success`.
    * In case of a `Failure` it throws the underlying exception.
    */
  def get: T

  /**
    * Returns the computation value in case of a `Success`. Otherwise it is returning the provided default.
    */
  def getOrElse[U >: T](default: => U): U = if (isSuccess) get else default

  /**
    * Applies the given function `f` if this is a `Success`, otherwise returns `Unit` if this is a `Failure`.
    */
  def foreach[X](f: T => X)

  /**
    * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
    */
  def recover[X >: T](f: PartialFunction[Throwable, X]): Retry[X]

  /**
    * Transforms the `Retry` value by applying a transformation function to its underlying value
    */
  def transform[X](f: T => X): X
}

final case class Success[+T](value: T) extends Retry[T] {
  override def isFailure: Boolean = false
  override def isSuccess: Boolean = true
  override def recover[X >: T](f: PartialFunction[Throwable, X]): Retry[X] =
    this
  override def get: T = value
  override def foreach[X](f: T => X): Unit = f(value)
  override def transform[X](f: T => X): X = f(value)
  override def flatMap[S](f: T => Retry[S]): Retry[S] = f(value)
  override def map[S](f: T => S)(implicit strategy: RetryStrategyProducer): Retry[S] = Retry(f(value))
}

final case class Failure[+T](exception: Throwable) extends Retry[T] {
  override def isFailure: Boolean = true
  override def isSuccess: Boolean = false
  override def recover[X >: T](f: PartialFunction[Throwable, X]): Retry[X] = {
    try {
      if (f.isDefinedAt(exception)) {
        Success(f(exception))
      } else this
    } catch {
      case NonFatal(e) => Failure(e)
    }
  }

  override def get: T = throw exception
  override def foreach[X](f: T => X): Unit = ()
  override def transform[X](f: T => X): X = throw exception
  override def flatMap[S](f: T => Retry[S]): Retry[S] = Failure(exception)
  override def map[S](f: T => S)(implicit strategy: () => RetryStrategy): Retry[S] = Failure(exception)
}

object Retry {
  def apply[T](fn: => T)(implicit strategy: () => RetryStrategy): Retry[T] = {
    def go(fn: => T)(strategy: RetryStrategy): Retry[T] = {
      Try(fn) match {
        case x: scala.util.Success[T] =>
          Success(x.value)
        case _ if strategy.shouldRetry() =>
          go(fn)(strategy.update())
        case f: scala.util.Failure[T] =>
          Failure(f.exception)
      }
    }

    go(fn)(strategy())
  }
}
