package util.retry.blocking

import java.util.Random

/**
 * Interface defining a retry strategy
 */
sealed trait RetryStrategy {
  /**
   * Returns `true` if the retry should be performed
   */
  def shouldRetry(): Boolean

  /**
   * Returns the new retry strategy state
   */
  def update(): RetryStrategy
}

/**
 * Simplest retry strategy that performs retry
 * @param maxRetries the maximum number of retries
 */
class MaxNumberOfRetriesStrategy(val maxRetries: Int)
  extends RetryStrategy {
  override def shouldRetry(): Boolean = maxRetries > 0

  override def update(): RetryStrategy =
    new MaxNumberOfRetriesStrategy(maxRetries = maxRetries - 1)
}

class FixedWaitRetryStrategy(val millis: Long, override val maxRetries: Int)
  extends MaxNumberOfRetriesStrategy(maxRetries) with Sleep {

  override def update(): RetryStrategy = {
    sleep(millis)
    new FixedWaitRetryStrategy(millis, maxRetries - 1)
  }
}

class RandomWaitRetryStrategy(val minimumWaitTime: Long, val maximumWaitTime: Long, override val maxRetries: Int)
  extends MaxNumberOfRetriesStrategy(maxRetries) with Sleep {

  private[this] final val random: Random = new Random()

  override def update(): RetryStrategy = {
    val millis: Long = math.abs(random.nextLong) % (maximumWaitTime - minimumWaitTime)
    sleep(millis)
    new RandomWaitRetryStrategy(
      minimumWaitTime,
      maximumWaitTime,
      maxRetries - 1
    )
  }
}

class FibonacciBackOffStrategy(waitTime: Long, step: Long, override val maxRetries: Int)
  extends MaxNumberOfRetriesStrategy(maxRetries) with Sleep {
  def fibb(n: Long) = {
    n match {
      case x@(0L | 1L) => x
      case _ =>
        var prevPrev: Long = 0L
        var prev: Long = 1L
        var result: Long = 0L

        for (i <- 2L to n) {
          result = prev + prevPrev
          prevPrev = prev
          prev = result
        }
        result
    }
  }

  override def update(): RetryStrategy = {
    val millis: Long = fibb(step) * waitTime
    sleep(millis)
    new FibonacciBackOffStrategy(waitTime, step + 1, maxRetries - 1)
  }
}

trait Sleep {
  def sleep(millis: Long) = try {
    Thread.sleep(millis)
  } catch {
    case e: InterruptedException =>
      Thread.currentThread().interrupt()
      throw e
  }
}