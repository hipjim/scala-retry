import org.scalatest.{FlatSpec, Matchers}
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import util.retry.blocking._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by dev on 11/03/16.
  */
class RetryDslSpec extends FlatSpec with Matchers {

  implicit val retryStrategy: RetryStrategyProducer =
    RetryStrategy.fixedBackOff(retryDuration = 1.seconds, maxAttempts = 2)

  "A `Retry` " should "be used in for comprehensions" in {
    val result = for {
      x <- Retry(1 / 1)
      y <- Retry(1 / 1)
    } yield x + y

    implicit val ec = ExecutionContext.global
    Retry(Future(1))

    result should be(Success(2))
  }

}
