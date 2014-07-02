scala-retry
===========

Simple retry mechanism for arbitrary function calls in scala.

```scala
import retry._
implicit val retryStrategy =
   fixedWaitRetry(10, TimeUnit.SECONDS, limitOfRetries = 10)

val r = Retry(1 / 0) match {
    case Success(x) => println(x)
    case Failure(t) => println(t)
}
```
