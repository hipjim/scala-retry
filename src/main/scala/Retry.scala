import scala.annotation.implicitNotFound
import scala.concurrent.duration.FiniteDuration
import scala.util.{Success, Try}

/**
 * Retry interface
 */
trait Retry {
  /**
   * The retry method takes a function and a {@see RetryStrategy}.
   *
   * @param fn the function call
   * @param strategy the retry strategy
   * @tparam T the response type
   * @return a Try wrapping the function call result
   */
  def retry[T](fn: => T)(implicit strategy: RetryStrategy): Try[T]
}

package object retry extends Retry {

  @implicitNotFound("no implicit retry strategy found")
  override def retry[T](fn: => T)(implicit strategy: RetryStrategy): Try[T] =
    Try(fn) match {
      case x: Success[T] => x
      case _ if strategy.shouldRetry() => retry(fn)(strategy.update())
      case f => f
    }

  def noWaitRetry(limitOfRetries: Int) =
    new MaxNumberOfRetriesStrategy(limitOfRetries)

  def fixedWaitRetry(duration:FiniteDuration, limitOfRetries: Int) =
    new FixedWaitRetryStrategy(duration.toMillis, limitOfRetries)

  def randomWaitRetry(minimumWaitTime: FiniteDuration, maximumWaitTime: FiniteDuration, limitOfRetries: Int) =
    new RandomWaitRetryStrategy(minimumWaitTime.toMillis, maximumWaitTime.toMillis, limitOfRetries)
}

object runner extends App {

  import retry._

import scala.concurrent.duration._

  implicit val retryStrategy = fixedWaitRetry(10.seconds, limitOfRetries = 10)

  println(retry(1 / 0))
  println(retry(2 / 1))
}


