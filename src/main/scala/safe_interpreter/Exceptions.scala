package safe_interpreter

abstract class Exceptions
case class NotImplementedException(s: String = null) extends RuntimeException(s)
abstract class ParseException(msg: String = null)    extends Exception(msg)
abstract class DesugarException(msg: String = null)  extends RuntimeException(msg)
abstract class InterpException(msg: String = null)   extends RuntimeException(msg)
abstract class TypeException(msg: String = null)     extends RuntimeException(msg)

case class ParseExc(s: String)      extends ParseException(s)
case class DesugarExc(s: String)    extends DesugarException(s)
case class InterpExc(s: String)     extends InterpException(s)
case class CannotInterpException(s: String) extends  InterpException(s)
case class TypeExc(s: String)       extends TypeException(s)
case class TypeExceptionE(s: String)extends TypeException(s)

case class CannotDesugarException(s: String) extends DesugarException(s)
case class NotStringException (s: String) extends InterpException(s)
case class NotIntegerException (s: String) extends InterpException(s)