import java.util.Random

/**
 * Created by cristi on 5/20/14.
 */
trait RetryStrategy {
  def shouldRetry(): Boolean
  def update(): RetryStrategy
}

case class MaxNumberOfRetriesStrategy(numberOfTimes: Int) extends RetryStrategy {
  override def shouldRetry(): Boolean =
    numberOfTimes > 0

  override def update(): RetryStrategy =
    MaxNumberOfRetriesStrategy(numberOfTimes - 1)
}

case class FixedWaitRetryStrategy(override val numberOfTimes: Int, millis: Long)
  extends MaxNumberOfRetriesStrategy(numberOfTimes) {
      
  override def update(): RetryStrategy = {
    try {
      Thread.sleep(millis)
    } catch {
      case e: InterruptedException =>
        Thread.currentThread().interrupt()
        throw new RuntimeException("retry failed")
    }

    FixedWaitRetryStrategy(numberOfTimes - 1, millis)
  }
}

case class RandomWaitRetryStrategy(override val numberOfTimes: Int, maximumTime: Long)
  extends MaxNumberOfRetriesStrategy(numberOfTimes) {

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

    FixedWaitRetryStrategy(numberOfTimes - 1, maximumTime)
  }
}
