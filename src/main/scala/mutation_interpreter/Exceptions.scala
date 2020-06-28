package mutation_interpreter

abstract class Exceptions
case class NotImplementedException(s: String = null) extends RuntimeException(s)
abstract class ParseException(msg: String = null)   extends Exception(msg)
abstract class DesugarException(msg: String = null) extends Exception(msg)
abstract class InterpException(msg: String = null)  extends Exception(msg)

case class ParseExc(s: String)      extends ParseException(s)
case class DesugarExc(s: String)    extends DesugarException(s)
case class InterpExc(s: String)     extends InterpException(s)

case class CannotDesugarException(s: String) extends DesugarException(s)
case class CannotInterpException(s: String)  extends InterpException(s)
case class NotStringException (s: String) extends InterpException(s)
case class NotIntegerException (s: String) extends InterpException(s)