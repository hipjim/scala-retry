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
      case _ if strategy.shouldRetry() => println("trying"); retry(fn)(strategy.update())
      case f => f
    }
}


object runner extends App {
  implicit val retryStrategy2 = RandomWaitRetryStrategy(100, 100)

  val retry = new RetryImpl()
  val result = retry.retry(1 / 0)
  println(result)
}


