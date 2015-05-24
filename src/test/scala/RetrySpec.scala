import org.scalatest._
import util.retry.blocking.{Retry, Success}

import scala.concurrent.duration._
import util.retry.blocking.Failure

/**
 * Created by dev on 7/2/14.
 */
class RetrySpec extends FlatSpec with Matchers {

  implicit val retryStrategy =
    Retry.fixedWait(retryDuration = 1.seconds, maxRetries = 3)

  "A `Retry` " should "return `Success` in case of a successful operation" in {
    Retry(1 / 1) should be(Success(1))
  }

  "A `Retry` " should "return `Failure` in case of a failed operation" in {
    Retry(1 / 0) match {
      case Failure(t) => () //OK
      case Success(_) => fail("Should return failure but it returned success")
    }
  }

  "A `Retry` " should "recover in case of a failure `Failure` " in {
    val result = Retry(1 / 0) recover {
      case _ => -1
    }
    result should be(Success(-1))
  }

  "A `Retry.get` " should " return the computed value" in {
    val result = Retry(1 / 1).get
    result should be(1)
  }

  "A `Retry.get` " should " throw an exception in case of a failure" in {
    try {
      Retry(1 / 0).get
      fail("should not get here")
    } catch {
      case e: ArithmeticException => ()
    }
  }

}
