package Starter

abstract class Exceptions

//exceptions
abstract class ParseException(msg: String = null) extends Exception(msg)

case class CustomParseException(msg: String = null) extends ParseException(msg)

abstract class DesugarException(msg: String = null) extends Exception(msg)

case class CustomDesugarException(msg: String = null) extends DesugarException(msg)

abstract class InterpException(msg: String = null) extends Exception(msg)

case class CustomInterpException(msg: String = null) extends InterpException(msg)

case class NotImplementedException(s: String) extends RuntimeException(s)
case class CannotDesugarException(s: String) extends DesugarException(s)
case class CannotInterpException(s: String)  extends InterpException(s)
case class NotStringException (s: String) extends InterpException(s)
case class NotIntegerException (s: String) extends InterpException(s)