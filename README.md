scala-retry
===========

Simple retry mechanism for arbitrary function calls in scala.

```scala
import retry._
implicit val retryStrategy =
    fixedWaitRetry(1.seconds, limitOfRetries = 3)

val r = Retry(1 / 1) match {
    case Success(x) => println(x)
    case Failure(t) => println(t)
}

val recover = Retry(1 / 0) recover {
    case NonFatal(t) => Int.MaxValue
}
```
