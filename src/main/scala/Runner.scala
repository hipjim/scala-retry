
import scala.concurrent.duration._

/**
 * Created by dev on 7/5/14.
 */
object Runner extends App {
  import util.retry.blocking.Retry
  implicit val retryStrategy = Retry.fibonacciBackOff(10.second, 11)

  val retry = Retry(1 / 0).getOrElse(10)
  println(retry)
}
