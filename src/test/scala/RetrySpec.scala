import org.scalatest._

import scala.concurrent.duration._
import scala.util.retry.{Failure, Retry, Success}

/**
 * Created by dev on 7/2/14.
 */
class RetrySpec extends FlatSpec with Matchers {

  implicit val retryStrategy =
    Retry.fixedWaitRetry(retryInterval = 1.seconds, maxRetries = 3)

  "A `Retry` " should "return `Success` in case of a successful operation" in {
    Retry(1 / 1) should be(Success(1))
  }

  "A `Retry` " should "return `Failure` in case of a failed operation" in {
    Retry(1 / 0) should equal(Failure(new ArithmeticException("/ by zero")))
  }

  "A `Retry` " should "recover in case of a failure `Failure` " in {
    val result = Retry(1 / 0) recover {
      case _ => -1
    }
    result should be(Success(1))
  }

  "A `Retry.get` " should " return the computed value" in {
    val result = Retry(1 / 1).get
    result should be(1)
  }

}
