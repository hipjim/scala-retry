import java.util.Random

/**
 * Created by cristi on 5/20/14.
 */
trait RetryStrategy {
  def shouldRetry(): Boolean
  def update(): RetryStrategy
}

case class MaxNumberOfRetriesStrategy(maxRetries: Int) extends RetryStrategy {
  override def shouldRetry(): Boolean =
    maxRetries > 0

  override def update(): RetryStrategy =
    this.copy(maxRetries = (maxRetries -1))
}

case class FixedWaitRetryStrategy(override val maxRetries: Int, millis: Long)
  extends MaxNumberOfRetriesStrategy(maxRetries) {
      
  override def update(): RetryStrategy = {
    try {
      Thread.sleep(millis)
    } catch {
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new RuntimeException("retry failed")
    }

    FixedWaitRetryStrategy(maxRetries - 1, millis)
  }
}

case class RandomWaitRetryStrategy(override val maxRetries: Int, maximumTime: Long)
  extends MaxNumberOfRetriesStrategy(maxRetries) {

  private[this] final val random: Random = new Random()

  override def update(): RetryStrategy = {
    val t: Long = math.abs(random.nextLong) % maximumTime

    try {
      Thread.sleep(t)
    } catch {
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new RuntimeException("retry failed")
    }

    FixedWaitRetryStrategy(maxRetries - 1, maximumTime)
  }
}
