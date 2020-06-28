package mutation_interpreter

import mutation_interpreter.Interp.Pointer

//Values
sealed abstract class Value

case class NumV(v: Int) extends Value
case class BoolV(v: Boolean) extends Value
case class NilV() extends Value
case class ConsV(head: Value, tail: Value) extends Value

case class PointerClosV(f: FdC, env: List[Pointer]) extends Value
case class BoxV(l: Int) extends Value
case class UninitializedV() extends Value
