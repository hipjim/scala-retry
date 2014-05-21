scala-retry
===========

Simple retry mechanism for arbitrary function calls in scala.

```scala
import utils._
implicit val retryStrategy =
   fixedWaitRetry(10, TimeUnit.SECONDS, limitOfRetries = 10)

val result:Try[Long] = retry(1 / 0)
```
