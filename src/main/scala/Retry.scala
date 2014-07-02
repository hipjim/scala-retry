import scala.concurrent.duration.FiniteDuration
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Retry interface
 */
sealed trait Retry[+T] {
  def isFailure: Boolean
  def isSuccess: Boolean

  def get: T

  def getOrElse[U >: T](default: => U): U =
    if (isSuccess) get else default

  def foreach[X](f : T => X)

  def recover[X >: T](f: PartialFunction[Throwable, X]): Retry[X]
}

final case class Success[+T](value: T) extends Retry[T] {
  override def isFailure: Boolean = false
  override def isSuccess: Boolean = true

  override def recover[X >: T](f: PartialFunction[Throwable, X]): Retry[X] = this
  override def get: T = value

  override def foreach[X](f: (T) => X): Unit = f(value)
}

final case class Failure[+T](val exception: Throwable) extends Retry[T] {
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

  override def foreach[X](f: (T) => X): Unit = ()
}


object Retry {
  def apply[T](fn: => T)(implicit strategy: RetryStrategy): Retry[T] =
    Try(fn) match {
      case x: scala.util.Success[T] => Success(x.value)
      case _ if strategy.shouldRetry() => apply(fn)(strategy.update())
      case f: scala.util.Failure[T] => Failure(f.exception)
    }

  def noWaitRetry(limitOfRetries: Int) =
    new MaxNumberOfRetriesStrategy(limitOfRetries)

  def fixedWaitRetry(duration: FiniteDuration, limitOfRetries: Int) =
    new FixedWaitRetryStrategy(duration.toMillis, limitOfRetries)

  def randomWaitRetry(minimumWaitTime: FiniteDuration, maximumWaitTime: FiniteDuration, limitOfRetries: Int) =
    new RandomWaitRetryStrategy(minimumWaitTime.toMillis, maximumWaitTime.toMillis, limitOfRetries)
}

object runner extends App {

  import Retry._

import scala.concurrent.duration._

  implicit val retryStrategy = fixedWaitRetry(1.seconds, limitOfRetries = 3)

  val r = Retry(1 / 0) match {
    case Success(x) => println(x)
    case Failure(t) => println(t)
  }

}


