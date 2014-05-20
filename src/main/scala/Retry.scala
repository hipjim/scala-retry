import scala.util.{Success, Try}

/**
 * Created by cristi on 5/20/14.
 */
trait Retry {
  def retry[T](fn: => T)(implicit strategy: RetryStrategy): Try[T]
}

final class RetryImpl extends Retry {

  @annotation.tailrec
  override def retry[T](fn: => T)(implicit strategy: RetryStrategy): Try[T] =
    Try(fn) match {
      case x: Success[T] => x
      case _ if strategy.shouldRetry() => println("retrying"); retry(fn)(strategy.update())
      case f => f
    }
}

trait RetryStrategy {
  def shouldRetry(): Boolean
  def update(): RetryStrategy
}

case class NumberOfRetriesStrategy(numberOfTimes: Int) extends RetryStrategy {
  override def shouldRetry(): Boolean = numberOfTimes > 0
  override def update(): RetryStrategy = NumberOfRetriesStrategy(numberOfTimes - 1)
}

case class NumberOfRetriesStrategyWithDelay(numberOfTimes: Int, millis: Long) extends RetryStrategy {
  override def shouldRetry(): Boolean = numberOfTimes > 0

  override def update(): RetryStrategy = {
    Thread.sleep(millis)
    NumberOfRetriesStrategyWithDelay(numberOfTimes - 1, millis)
  }
}

object runner extends App {
  implicit val retryStrategy = NumberOfRetriesStrategyWithDelay(100, 1000)

  val retry = new RetryImpl()
  val result = retry.retry(1 / 0)
  println(result)
}


