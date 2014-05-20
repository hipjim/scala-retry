import scala.util.Try
import scala.util.Success

object retry {

	@annotation.tailrec
	def retry[T](nrOfTimes:Int)(fn: => T):Try[T] =
		Try(fn) match {
			case x:Success[T] => x
			case _ if nrOfTimes > 1 => retry(nrOfTimes - 1)(fn)
			case f => f
		}

}