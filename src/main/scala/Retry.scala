import java.util.concurrent.TimeUnit
import scala.annotation.implicitNotFound
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

package object utils extends Retry {

  @implicitNotFound("no implicit retry strategy found")
  override def retry[T](fn: => T)(implicit strategy: RetryStrategy): Try[T] =
    Try(fn) match {
      case x: Success[T] => x
      case _ if strategy.shouldRetry() => retry(fn)(strategy.update())
      case f => f
    }

  def noWaitRetry(limitOfRetries: Int) =
    new MaxNumberOfRetriesStrategy(limitOfRetries)

  def fixedWaitRetry(waitTime: Long, timeUnit: TimeUnit, limitOfRetries: Int) =
    new FixedWaitRetryStrategy(timeUnit.toMillis(waitTime), limitOfRetries)

  def randomWaitRetry(minimumWaitTime: Long, maximumWaitTime: Long, timeUnit: TimeUnit, limitOfRetries: Int) =
    new RandomWaitRetryStrategy(timeUnit.toMillis(minimumWaitTime), timeUnit.toMillis(maximumWaitTime), limitOfRetries)
}

object runner extends App {

  import utils._

  implicit val retryStrategy = fixedWaitRetry(10, TimeUnit.SECONDS, limitOfRetries = 10)

  println(retry(1 / 0))
  println(retry(2 / 1))
}


