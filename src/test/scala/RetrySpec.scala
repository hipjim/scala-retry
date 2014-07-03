import org.scalatest._

import scala.util.retry
import scala.util.retry.{Retry, Success, Failure}
import scala.concurrent.duration._

/**
 * Created by dev on 7/2/14.
 */
class RetrySpec extends FlatSpec with Matchers {

  implicit val retryStrategy =
    Retry.fixedWaitRetry(retryInterval = 1.seconds, maxRetries = 3)

  "A Retry" should "return `Success` in case of a successful operation" in {
    Retry(1 / 1) should be(Success(1))
  }

  "A Retry" should "return `Failure` in case of a failed operation" in {
    Retry(1 / 0) should equal(Failure(new ArithmeticException("/ by zero")))
  }

}
