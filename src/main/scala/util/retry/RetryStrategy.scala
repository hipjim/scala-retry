package util.retry

import java.util.Random

/**
 * Interface defining a retry strategy
 */
trait RetryStrategy {
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
  extends MaxNumberOfRetriesStrategy(maxRetries) {

  override def update(): RetryStrategy = {
    try {
      Thread.sleep(millis)
    } catch {
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new RuntimeException("retry failed")
    }

    new FixedWaitRetryStrategy(
      millis,
      maxRetries - 1
    )
  }
}

class RandomWaitRetryStrategy(val minimumWaitTime: Long, val maximumWaitTime: Long, override val maxRetries: Int)
  extends MaxNumberOfRetriesStrategy(maxRetries) {

  private[this] final val random: Random = new Random()

  override def update(): RetryStrategy = {
    val t: Long = math.abs(random.nextLong) % (maximumWaitTime - minimumWaitTime)

    try {
      Thread.sleep(t)
    } catch {
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new RuntimeException("retry failed")
    }

    new RandomWaitRetryStrategy(
      minimumWaitTime,
      maximumWaitTime,
      maxRetries - 1
    )
  }
}
