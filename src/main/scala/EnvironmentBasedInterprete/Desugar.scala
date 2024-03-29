package EnvironmentBasedInterprete


object Desugar {

  def desugar(e: ExprExt): ExprC = e match {
    case TrueExt() => TrueC()
    case FalseExt() => FalseC()
    case NumExt(num: Int) =>NumC(num)
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
      case "or" => IfC(desugar(left), desugar(left), desugar(right))
      case "cons" => ConsC(desugar(left), desugar(right))
      case _ => UndefinedC()
    }
    case UnOpExt(s: String, exp: ExprExt) => s match {
      case "-" => MultC(NumC(-1), desugar(exp))
      case "not" => IfC(desugar(exp), FalseC(), TrueC())
      case "head" => HeadC(desugar(exp))
      case "tail" => TailC(desugar(exp))
      case "is-nil" => IsNilC(desugar(exp))
      case "is-list" => IsListC(desugar(exp))
      case _ => UndefinedC()
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
      case _ => UndefinedC()
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
    case RecLamExt(a: String, b: String, exp: ExprExt) => AppC(cY, List(FdC(List(a), FdC(List(b), desugar(exp)))))

    case _ => UndefinedC()
  }

  def cY = desugar(FdExt(List("f"), AppExt(FdExt(List("x"), AppExt(IdExt("x"), List(IdExt("x")))), List(FdExt(List("x"), AppExt(IdExt("f"), List(FdExt(List("y"), AppExt(AppExt(IdExt("x"), List(IdExt("x"))), List(IdExt("y")))))))))))

//  def cY = desugar(parse("""
//        (lambda (g) ((lambda (a) (a a))
//         (lambda (a) (g (lambda (b)
//                    ((a a) b))))))
//      """))

  def stringFromLetBE(letB: LetBindExt): String = letB match {
    case LetBindExt(s, e) => s
    // case _ => throw CannotDesugarException("Cannot get the string from the letBindExt Desugaring")
  }

  def extprissionFromLetBE(letB: LetBindExt): ExprC = letB match {
    case LetBindExt(s, e) => desugar(e)
    // case _ => throw CannotDesugarException("Cannot get the expression from the letBindExt Desugaring")
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
//      case RecLamExt(name, param, body) => AppC(Y, List(FdC(List(name), FdC(List(param), desugar(body)))))
//
//      case _ => UndefinedC()
//    }
//
//  }
//
//  // call by value Y its actually Z because its eager
//  //Y combinator: (lambda (f) ((lambda (x) (x x)) (lambda (x) (f (lambda (y) ((x x) y))))))
//  def Y = desugar(FdExt(List("f"), AppExt(FdExt(List("x"), AppExt(IdExt("x"), List(IdExt("x")))), List(FdExt(List("x"), AppExt(IdExt("f"), List(FdExt(List("y"), AppExt(AppExt(IdExt("x"), List(IdExt("x"))), List(IdExt("y")))))))))))
//
//  def condEExtDesugar(list: List[(ExprExt, ExprExt)], e: ExprExt): ExprC = {
//    list match {
//      case Nil => throw CustomDesugarException("nothing before else")
//      case (c, t) :: Nil => IfC(desugar(c), desugar(t), desugar(e))
//      case (c, t) :: f => IfC(desugar(c), desugar(t), condEExtDesugar(f, e))
//    }
//  }
//
//  def condExtDesugar(list: List[(ExprExt, ExprExt)]): ExprC = {
//    list match {
//      case Nil => NilC()
//      case (c, t) :: Nil => IfC(desugar(c), desugar(t), UndefinedC())
//      case (c, t) :: e => IfC(desugar(c), desugar(t), condExtDesugar(e))
//    }
//  }
}