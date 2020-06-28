package lazy_interpreter

object Desugar {
  /** desugar helper functions ------------------------------ **/
  def desugarList(list: List[ExprExt]): List[ExprC] = list match {
    case Nil            => Nil
    case head :: tail   => desugar(head) :: desugarList(tail)
    case _              => throw DesugarExc("The case: { " + list + " } isn't define - parserList()")
  }

  def bindNames(list: List[LetBindExt]): List[String] =
    list map { case LetBindExt(name, value) => name }

  def bindValues(list: List[LetBindExt]): List[ExprC] =
    list map { case LetBindExt(name, value) => desugar(value) }

  def desugar(e: ExprExt): ExprC = e match {
    case NumExt(n)  => NumC(n)
    case TrueExt()  => TrueC()
    case FalseExt() => FalseC()
    case NilExt()   => NilC()
    case IdExt(s) => IdC(s)

    case BinOpExt(s, l, r) =>
      s match {
        case "+" => PlusC(desugar(l), desugar(r))
        case "*" => MultC(desugar(l), desugar(r))
        case "-" => PlusC(desugar(l), MultC(NumC(-1), desugar(r)))

        case "and" => IfC(desugar(l), desugar(r), FalseC())
        case "or"  => IfC(desugar(l), TrueC(), desugar(r))

        case "num=" => EqNumC(desugar(l), desugar(r))
        case "num<" => LtC(desugar(l), desugar(r))
        case "num>" => LtC(desugar(r), desugar(l))

        case "cons" => ConsC(desugar(l), desugar(r))

        case _ =>
          throw DesugarExc("The operation { "+ s +" } can't be found - desugar BinOpExt()")
      }

    case UnOpExt(s, l) =>
      s match {
        case "-"    => MultC(NumC(-1), desugar(l))
        case "not"  => IfC(desugar(l), FalseC(), TrueC())
        case "head" => HeadC(desugar(l))
        case "tail" => TailC(desugar(l))
        case "is-nil"   => IsNilC(desugar(l))
        case "is-list"  => IsListC(desugar(l))
        case "force" => ForceC(desugar(l))

        case _ =>
          throw DesugarExc("The operation { "+ s +" } can't be found - desugar UnOpExt()")
      }

    case IfExt(cond, doTrue, doElse) =>
      IfC(desugar(cond), desugar(doTrue), desugar(doElse))

    case ListExt( Nil )          => NilC()
    case ListExt( head :: tail ) =>
      ConsC( desugar(head), desugar(ListExt(tail)))

    case CondExt(Nil)                    => UndefinedC()
    case CondExt((cond, doTrue) :: tail) =>
      IfC( desugar(cond), desugar(doTrue), desugar(CondExt(tail)))  //using CondExt() bad!

    case CondEExt( Nil, doElse)                   => desugar(doElse)
    case CondEExt((cond, doTrue) :: tail, doElse) =>
      IfC( desugar(cond), desugar(doTrue), desugar(CondEExt(tail, doElse)))  //using CondExt() bad!

    case FdExt(args, body) =>
      FdC(args, desugar(body))

    case AppExt(f, args) =>
      AppC(desugar(f), desugarList(args))

    case LetExt(binds, body) =>
      AppC(FdC(bindNames(binds), desugar(body)), bindValues(binds))

    case LetRecExt(binds, body) =>
        LetRecC(binds map { case LetBindExt(name, value) => LetBindC(name, desugar(value)) }, desugar(body))

    case RecLamExt(name, param, body) =>
      // val Z = FdC(List("f"), AppC(  FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))),
      //                         List(FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))))))

      //OR - to not duplicate the function
      val Z = FdC(List("f"), AppC(  FdC(List("x"), AppC(IdC("x"), List(IdC("x")))),
        List(FdC(List("x"),AppC(IdC("f"),List(FdC(List("v"),AppC(AppC(IdC("x"),List(IdC("x"))),List(IdC("v"))))))))))

      AppC(Z , List(FdC(List(name), FdC(List(param), desugar(body)))))

    case other =>
      throw DesugarExc("Error! final case in desugar: " + other)
  }
}
