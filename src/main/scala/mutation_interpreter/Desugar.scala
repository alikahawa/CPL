package mutation_interpreter

object Desugar {
  def desugar(e: ExprExt): ExprC = e match {
    case TrueExt() => TrueC()
    case FalseExt() => FalseC()
    case NumExt(num: Int) => NumC(num)
    case NilExt() => NilC()
    case IdExt(id: String) => IdC(id)
    case BinOpExt(s: String, left: ExprExt, right: ExprExt) => s match {
      case "-" => PlusC(desugar(left), MultC(NumC(-1), desugar(right)))
      case "+" => PlusC(desugar(left), desugar(right))
      case "*" => MultC(desugar(left), desugar(right))
      case "num=" => EqNumC(desugar(left), desugar(right))
      case "num<" => LtC(desugar(left), desugar(right))
      case "num>" => LtC(desugar(right), desugar(left))
      case "and" => IfC(desugar(left), desugar(right), desugar(left))
      case "or" => IfC(desugar(left), desugar(right), desugar(right))
      case "cons" => ConsC(desugar(left), desugar(right))
      case "setbox" => SetboxC(desugar(left), desugar(right))
      case "seq" => SeqC(desugar(left), desugar(right))
      case _ => UndefinedC()
    }
    case UnOpExt(s: String, exp: ExprExt) => s match {
      case "-" => MultC(NumC(-1), desugar(exp))
      case "not" => IfC(desugar(exp), FalseC(), TrueC())
      case "head" => HeadC(desugar(exp))
      case "tail" => TailC(desugar(exp))
      case "is-nil" => IsNilC(desugar(exp))
      case "is-list" => IsListC(desugar(exp))
      case "box" => BoxC(desugar(exp))
      case "unbox" => UnboxC(desugar(exp))
      case _ => UninitializedC()
    }
    case IfExt(c: ExprExt, t: ExprExt, e: ExprExt) => IfC(desugar(c), desugar(t), desugar(e))
    case ListExt(l: List[ExprExt]) => l match {
      case Nil => NilC()
      case NilExt() :: Nil => NilC()
      case h :: Nil => ConsC(desugar(h), NilC())
      case h :: t => ConsC(desugar(h), desugar(ListExt(t)))
    }
    case CondExt(cs) => cs match {
      case Nil => NilC()
      case (x, y) :: Nil => IfC(desugar(x), desugar(y), UndefinedC())
      case (x, y) :: b => IfC(desugar(x), desugar(y), desugar(CondExt(b)))
      case _ => UninitializedC()
    }
    case CondEExt(cs, e) => cs match {
      case Nil => NilC()
      case (x, y) :: Nil => IfC(desugar(x), desugar(y), desugar(e))
      case (x, y) :: xs => IfC(desugar(x), desugar(y), desugar(CondEExt(xs, e)))
      // case _ => UndefinedC()
    }
    case AppExt(exp: ExprExt, lExp: List[ExprExt]) => AppC(desugar(exp), lExp.map(e => desugar(e)))
    case FdExt(lString: List[String], exp: ExprExt) => FdC(lString, desugar(exp))
    case LetExt(lBind: List[LetBindExt], exp: ExprExt) => AppC(FdC(lBind.map(e => stringFromLetBE(e)), desugar(exp)),
      lBind.map(e => extprissionFromLetBE(e)))
    case RecLamExt(a: String, b: String,
    exp: ExprExt) => AppC(cY, List(FdC(List(a), FdC(List(b), desugar(exp)))))
    case LetRecExt(lBind: List[LetBindExt],
    exp: ExprExt) => AppC(FdC(lBind.map(e => stringFromLetBE(e)), letRecInitBody(lBind, exp)),
      lBind.map(e => UninitializedC()))

    case SetExt(id: String, e: ExprExt) => SetC(id, desugar(e))
    case _ => UninitializedC()
  }

    /**
     * desugar helper functions
     * @return
     */
    def cY = desugar(Parser.parse("""
          (lambda (g) ((lambda (a) (a a))
           (lambda (a) (g (lambda (b)
                      ((a a) b))))))
            """))

    def stringFromLetBE(letB: LetBindExt): String = letB match {
      case LetBindExt(s, e) => s
      // case _ => throw CannotDesugarException("Cannot get the string from the letBindExt Desugaring")
    }

    def extprissionFromLetBE(letB: LetBindExt): ExprC = letB match {
      case LetBindExt(s, e) => desugar(e)
      // case _ => throw CannotDesugarException("Cannot get the expression from the letBindExt Desugaring")
    }

    def letRecInitBody(list: List[LetBindExt], e: ExprExt): ExprC = list match {
      case Nil => desugar(e)
      case LetBindExt(a, b) :: Nil => SeqC(SetC(a, desugar(b)), desugar(e))
      case LetBindExt(a, b) :: tail => SeqC(SetC(a, desugar(b)), letRecInitBody(tail, e))
    }
}
