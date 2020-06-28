package safe_interpreter

object SafeInterp {

  def interp(e: ExprExt): Value = {
    TypeChecker.typeOf(e)
    Interp.interp(Desugar.desugar(e))
  }

}