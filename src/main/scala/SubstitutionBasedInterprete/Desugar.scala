package SubstitutionBasedInterprete


object Desugar {

  def desugar(e: ExprExt): ExprC =  e match {
    case TrueExt() => TrueC()
    case FalseExt() => FalseC()
    case NilExt() => NilC()
    case NumExt(n) => NumC(n)
    case IdExt(s) => IdC(s)

    case ListExt(a) => a match {
      case Nil => NilC()
      case NilExt() :: Nil => NilC()
      case h :: Nil => ConsC(desugar(h), NilC())
      case h :: t => ConsC(desugar(h), desugar(ListExt(t)))
    }
    case BinOpExt("-", a, b) => PlusC(desugar(a), MultC(NumC(-1), desugar(b)))
    case BinOpExt("+", a, b) => PlusC(desugar(a), desugar(b))
    case BinOpExt("*", a, b) => MultC(desugar(a), desugar(b))
    case BinOpExt("num=", a, b) => EqNumC(desugar(a), desugar(b))
    case BinOpExt("num<", a, b) => LtC(desugar(a), desugar(b))
    case BinOpExt("num>", a, b) => LtC(desugar(b), desugar(a))
    case BinOpExt("or", a, b) => IfC(desugar(a), desugar(a), desugar(b))
    case BinOpExt("and", a, b) => IfC(desugar(a), desugar(b), desugar(a))
    case BinOpExt("cons", a, b) => ConsC(desugar(a), desugar(b))
    case UnOpExt("-", a) => MultC(NumC(-1), desugar(a))
    case UnOpExt("not", a) => IfC(desugar(a), FalseC(), TrueC())
    case UnOpExt("head", a) => HeadC(desugar(a))
    case UnOpExt("tail", a) => TailC(desugar(a))
    case UnOpExt("is-nil", a) => IsNilC(desugar(a))
    case UnOpExt("is-list", a) => IsListC(desugar(a))

    case IfExt(a, b, c) => IfC(desugar(a), desugar(b), desugar(c))
    case CondExt(cs) => cs match {
      case Nil => NilC()
      case (x, y) :: Nil => IfC(desugar(x), desugar(y), UndefinedC())
      case (x, y) :: b => IfC(desugar(x), desugar(y), desugar(CondExt(b)))
      case _ => UndefinedC()
    }
    case CondEExt(cs, e) => cs match {
      case Nil => NilC()
      case (x, y) :: Nil => IfC(desugar(x), desugar(y), desugar(e))
      case (x, y) :: xs => IfC(desugar(x), desugar(y), desugar(CondEExt(xs, e)))
      // case _ => UndefinedC()
    }
    case FdExt(a: List[String], b: ExprExt) => FdC(a, desugar(b))
    case AppExt(a: ExprExt, b: List[ExprExt]) => AppC(desugar(a), b.map(e => desugar(e)))
    case LetExt(a: List[LetBindExt], b: ExprExt) => AppC(FdC(a.map(e => stringFromLetBE(e)), desugar(b)), a.map(e => extprissionFromLetBE(e)))
    case _ => UndefinedC()
  }

  def stringFromLetBE(letB: LetBindExt): String = letB match {
    case LetBindExt(s, e) => s
    case _ => throw CannotDesugarException("Cannot get the string from the letBindExt Desugaring")
  }

  def extprissionFromLetBE(letB: LetBindExt): ExprC = letB match {
    case LetBindExt(s, e) => desugar(e)
    case _ => throw CannotDesugarException("Cannot get the expression from the letBindExt Desugaring")
  }

  //  def desugar(e: ExprExt): ExprC = {
//
//    e match {
//      case TrueExt() => TrueC()
//      case FalseExt() => FalseC()
//      case NumExt(n) => NumC(n)
//      case BinOpExt(s, l, r) => {
//        s match {
//          case "+" => PlusC(desugar(l), desugar(r))
//          case "-" => PlusC(desugar(l), MultC(NumC(-1), desugar(r)))
//          // do not generate terms inside the recursive call
//          // PlusC(desugar(l) , desugar(UnOpExt("-" , r)))
//          case "*" => MultC(desugar(l), desugar(r))
//          case "and" => {
//            IfC(desugar(l), desugar(r), FalseC())
//          }
//          case "or" => {
//            IfC(desugar(l), TrueC(), desugar(r))
//          }
//          case "num=" => EqNumC(desugar(l), desugar(r))
//          case "num<" => LtC(desugar(l), desugar(r))
//          case "num>" => LtC(desugar(r), desugar(l))
//
//          case "cons" => ConsC(desugar(l), desugar(r))
//
//        }
//      }
//      case UnOpExt(s, e) => {
//        s match {
//          case "-" => MultC(NumC(-1), desugar(e))
//          case "not" => {
//            IfC(desugar(e), FalseC(), TrueC())
//          }
//          case "head" => HeadC(desugar(e))
//          case "tail" => TailC(desugar(e))
//          case "is-nil" => IsNilC(desugar(e))
//          case "is-list" => IsListC(desugar(e))
//        }
//      }
//      case IfExt(c, t, e) => IfC(desugar(c), desugar(t), desugar(e))
//
//      case ListExt(l) => {
//        l match {
//          case Nil => NilC()
//          case e :: Nil => ConsC(desugar(e), NilC())
//          case e :: b => ConsC(desugar(e), desugar(ListExt(b))) // generative recursion
//        }
//      }
//      case NilExt() => NilC()
//      case CondExt(l) => {
//        condExtDesugar(l)
//      }
//      case CondEExt(l, e) => condEExtDesugar(l, e)
//      case FdExt(l, b) => FdC(l, desugar(b))
//      case IdExt(c) => IdC(c)
//
//      case AppExt(f, args) => AppC(desugar(f), args.map(e => desugar(e)))
//
//      case LetExt(binds, body) => {
//        AppC(FdC(binds.map {
//          case LetBindExt(s, e) => s
//        }, desugar(body)), binds.map {
//          case LetBindExt(s, e) => desugar(e)
//        })
//      }
//
//      case _ => UndefinedC()
//    }
//
//  }
//
//  def condEExtDesugar(list: List[(ExprExt, ExprExt)], e: ExprExt): ExprC = {
//    list match {
//      case Nil => throw CustomDesugarException("nothing before else")
//      case (c, t) :: Nil => IfC(desugar(c), desugar(t), desugar(e))
//      case (c, t) :: f => IfC(desugar(c), desugar(t), condEExtDesugar(f, e))
//    }
//  }
//
//
//  def condExtDesugar(list: List[(ExprExt, ExprExt)]): ExprC = {
//    list match {
//      case Nil => NilC()
//      case (c, t) :: Nil => IfC(desugar(c), desugar(t), UndefinedC())
//      case (c, t) :: e => IfC(desugar(c), desugar(t), condExtDesugar(e))
//    }
//  }
}