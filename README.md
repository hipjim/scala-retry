scala-retry
===========

Simple retry mechanism for arbitrary function calls in scala.

```scala
import scala.concurrent.duration._
import Retry._

// define the retry strategy
implicit val retryStrategy =
    fixedWaitRetry(1.seconds, limitOfRetries = 3)

// pattern match the result
val r = Retry(1 / 1) match {
    case Success(x) => println(x)
    case Failure(t) => println(t)
}

// recover in case of a failure
val recover = Retry.apply(1 / 0) recover {
    case NonFatal(t) => Int.MaxValue
}
```
