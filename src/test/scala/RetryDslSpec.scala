import org.scalatest.{FlatSpec, Matchers}
import util.retry.blocking.{Failure, Success, Retry, RetryStrategy}

import scala.concurrent.duration._

/**
  * Created by dev on 11/03/16.
  */
class RetryDslSpec extends FlatSpec with Matchers {

  implicit val retryStrategy =
    RetryStrategy.fixedBackOff(retryDuration = 1.seconds, maxAttempts = 2)

  "A `Retry` " should "be used in for comprehensions" in {
    val result = for {
      x <- Retry(1 / 1)
      y <- Retry(1 / 1)
    } yield x + y

    result should be(Success(2))
  }
}
