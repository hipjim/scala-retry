import org.scalatest._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}

import scala.concurrent.duration._

/**
  * Created by dev on 7/2/14.
  */
trait AbstractRetrySpec extends FlatSpec with Matchers {

  implicit val retryStrategy: RetryStrategy

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

  "A `Retry.getOrElse` " should " should return a value even if an exception is thrown in the execution" in {
    val result = Retry(1 / 0).getOrElse(1)
    result should be(1)
  }
}

class NoRetrySpec extends AbstractRetrySpec {
  val retryStrategy = RetryStrategy.noRetry
}

class NoBackOffRetrySpec extends AbstractRetrySpec {
  val retryStrategy = RetryStrategy.noBackOff(maxRetries = 3)
}

class FixedBackOffRetrySpec extends AbstractRetrySpec {
  val retryStrategy = RetryStrategy.fixedBackOff(retryDuration = 1.seconds, maxRetries = 2)
}

class FibonacciBackOffRetrySpec extends AbstractRetrySpec {
  val retryStrategy = RetryStrategy.fibonacciBackOff(
    initialWaitDuration = 1.seconds,
    maxRetries = 3)
}
