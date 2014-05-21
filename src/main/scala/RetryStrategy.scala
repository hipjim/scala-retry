import java.util.Random

/**
 * Interface defining a retry strategy
 */
trait RetryStrategy {
  /**
   * Informs the caller if the retry should be performed
   * @return true if the operation should be retries
   */
  def shouldRetry(): Boolean

  /**
   * Returns the new retry strategy state
   * @return the new retry strategy
   */
  def update(): RetryStrategy
}

/**
 * Simplest retry strategy that performs retry
 * @param maxRetries the maximum number of retries
 */
class MaxNumberOfRetriesStrategy(val maxRetries: Int)
  extends RetryStrategy {
  override def shouldRetry(): Boolean =
    maxRetries > 0

  override def update(): RetryStrategy =
    new MaxNumberOfRetriesStrategy(maxRetries = (maxRetries - 1))
}

class FixedWaitRetryStrategy(override val maxRetries: Int, val millis: Long)
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
      maxRetries - 1,
      millis
    )
  }
}

class RandomWaitRetryStrategy(override val maxRetries: Int, val minimumWaitTime: Long, val maximumWaitTime: Long)
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
      maxRetries - 1,
      minimumWaitTime,
      maximumWaitTime
    )
  }
}
