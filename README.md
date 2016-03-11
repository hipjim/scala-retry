scala-retry
===========

Simple retry mechanism for arbitrary function calls in scala.

```scala
import scala.concurrent.duration._
import util.retry.blocking.Retry._

// define the retry strategy
implicit val retryStrategy =
    Retry.fixedWaitRetry(retryInterval = 2.seconds, maxRetries = 5)

// pattern match the result
val r = Retry(1 / 1) match {
    case Success(x) => x
    case Failure(t) => log("I got 99 problems but you won't be one", t)
}

// recover in case of a failure
val recover = Retry(1 / 0) recover {
    case NonFatal(t) => Int.MaxValue
}

// get or else in case of failure
val result = Retry(1 / 0).getOrElse(1)

// can be used in for comprehensions
val result = for {
  x <- Retry(1 / 0) // fails with java.lang.ArithmeticException: / by zero
  y <- Retry(1 / 1) // success
} yield x + y // result is Failure with java.lang.ArithmeticException: / by zero

```
