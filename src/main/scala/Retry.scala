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
}

object runner extends App {
  import utils.retry

  implicit val retryStrategy = new MaxNumberOfRetriesStrategy(10)

  println(retry(1 / 0))
  println(retry(2 / 1))
}


