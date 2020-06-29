package SubstitutionBasedInterprete

object Interp {
  def interp(e: ExprC): Value =e match {
    case TrueC() => BoolV(true)
    case FalseC() => BoolV(false)
    case NilC() => NilV()
    case NumC(n) => NumV(n)
    case ConsC(h, t) => ConsV(interp(h), interp(t))
    case MultC(a, b) => NumV(inVal(interp(a)) * inVal(interp(b)))
    case PlusC(a, b) => NumV(inVal(interp(a)) + inVal(interp(b)))
    case EqNumC(a, b) => BoolV(inVal(interp(a)) == inVal(interp(b)))
    case LtC(a, b) => BoolV(inVal(interp(a)) < inVal(interp(b)))

    case IfC(a, b, c) => interp(a) match {
      case BoolV(true) => interp(b)
      case BoolV(false) => interp(c)
      case _ => throw CannotInterpException("If should never get here")
    }

    case HeadC(a) => interp(a) match {
      case NilV() => throw CannotInterpException("Head Nil")
      case ConsV(h, _) => h
      case _ => throw CannotInterpException("Head should never get here")
    }
    case TailC(a) => interp(a) match {
      case ConsV(h, t) => t
      case _ => throw CannotInterpException("Head should never get here")
    }
    case IsNilC(a) => if(interp(IsListC(a)) == BoolV(true)) interp(a) match {
      case NilV() => BoolV(true)
      case ConsV(h, t) => BoolV(false)
    }else {
      throw CannotInterpException("IsNil should never get here")
    }

    case IsListC(a) => interp(a) match {
      case NilV() => BoolV(true)
      case ConsV(h, t) => BoolV(true)
      case _ => BoolV(false)
    }

    case FdC(xs, a) => FunV(FdC(xs, a))
    case AppC(a, b) => interp(a) match {
      case FunV(FdC(x, y)) => interp(subst(y, bindTogether(x, b)))
      case _ => throw CannotInterpException("Something went wrong at AppC")
    }

    case IdC(_) => throw CannotInterpException("Id should never get here")

    case ValC(a) => a match {
      case NumV(n) => NumV(n)
      case _ => a
    }
    case UndefinedC() => throw CannotInterpException("Cannot be interp since it is undefined")
    case ali => throw CannotInterpException("Cannot be interp since it is not implemented" + ali.toString)
  }

  def bindTogether(stringList: List[String], list: List[ExprC]): List[Bind] = {
    if(stringList.size != list.size){
      throw CannotInterpException("Lists are not the same size!")
    }else {
      (stringList, list) match {
        case (List(), List()) => List()
        case (a :: Nil, b :: Nil) => List(Bind(a, interp(b)))
        case (a :: c, b :: d) => List(Bind(a, interp(b))) ::: bindTogether(c, d)
        case _ => throw CannotInterpException("Strings are not equal to expressions cannot bind together")
      }
    }
  }
  def inVal(v: Value): Int = v match {
    case NumV(n) => n
    case _ => throw NotIntegerException("Not Integer")
  }

  def subst(e: ExprC, list: List[Bind]): ExprC = list match {
    case List() => e
    case hea :: Nil => e match {
      case TrueC() => TrueC()
      case FalseC() => FalseC()
      case NilC() => NilC()
      case NumC(n) => ValC(NumV(n))
      case ValC(v) => ValC(v)
      case ConsC(h, t) => ConsC(subst(h, list), subst(t, list))
      case MultC(a, b) => MultC(subst(a, list), subst(b, list))
      case PlusC(a, b) => PlusC(subst(a, list), subst(b, list))
      case EqNumC(a, b) => EqNumC(subst(a, list), subst(b, list))
      case LtC(a, b) => LtC(subst(a, list), subst(b, list))
      case IfC(a, b, c) => IfC(subst(a, list), subst(b, list), subst(c, list))
      case HeadC(a) => HeadC(subst(a, list))
      case TailC(a) => TailC(subst(a, list))
      case IsNilC(a) => IsNilC(subst(a, list))
      case IsListC(a) => IsListC(subst(a, list))
      case UndefinedC() => UndefinedC()
      case IdC(a) => {
        if (a == getIdBind(list.head)) {
          ValC(getEbind(list.head))
        } else {
          IdC(a)
        } // check the first element of the list only the first
      }
      case AppC(a, b) => AppC(subst(a, list), b.map(e => subst(e, list)))
      case FdC(a: List[String], b: ExprC) => if (a.contains(hea.name)) { // check the first element of the list
        FdC(a, b)
      } else {
        FdC(a, subst(b, List(hea)))
      }
      // case (head :: Nil) => (e, head) match {
      //   case (MultC(a, b), Bind(x, y)) => if(a == x){MultC(ValC(y), ValC(y))}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (PlusC(a, b), Bind(x, y)) => if(a == x){PlusC(ValC(y), ValC(y))}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (EqNumC(a, b), Bind(x, y)) => if(a == x){EqNumC(ValC(y), ValC(y))}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (LtC(a, b), Bind(x, y)) => if(a == x){LtC(ValC(y), ValC(y))}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (_, _) => throw CannotInterpException("Unknown kind of binding => Cannot bind")
      // }
      // case (head :: tail) => (e, head) match {
      //   case (MultC(a, b), Bind(x, y)) => if(a == x){subst(MultC(ValC(y), ValC(y)), tail)}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (PlusC(a, b), Bind(x, y)) => if(a == x){subst(PlusC(ValC(y), ValC(y)), tail)}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (EqNumC(a, b), Bind(x, y)) => if(a == x){subst(EqNumC(ValC(y), ValC(y)), tail)}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (LtC(a, b), Bind(x, y)) => if(a == x){subst(LtC(ValC(y), ValC(y)), tail)}else{throw CannotInterpException("id is not same => Cannot bind")}
      //   case (_, _) => throw CannotInterpException("Unknown kind of binding => Cannot bind")
      // }
      case abccc =>  throw CannotInterpException("Coudn't substitute" + abccc.toString)
    }
    case a :: b => subst(subst(e, List(a)), b)
    case _ => throw CannotInterpException("Something went wrong on subst")
  }

  def getIdBind(b: Bind): String = b match {
    case Bind(s, e) => s
    case _ =>  throw CannotInterpException("Coudn't substitute Not really a bind")
  }

  def getEbind(b: Bind): Value = b match {
    case Bind(s, e) => e
    case _ => throw CannotInterpException("Coudn't substitute Not really a bind")
  }

  def checkIdInBing(s: String, list: List[Bind]): Value = list match {
    case Nil => throw CannotInterpException("Coudn't substitute list of bindings Nil")
    case Bind(a, e) :: Nil => {
      if(s == a){
        e
      }else{
        throw CannotInterpException("Coudn't substitute Not in list of bindings")
      }
    }
    case Bind(a, e) :: b => if(s == a){
      e
    }else{ checkIdInBing(s, b)}
    case _ =>  throw CannotInterpException("Coudn't substitute Unknown list of bindings")
  }
}

//{
//  def interp(e: ExprC): Value = {
//    e match {
//      case ValC(v) => v
//      case NumC(n) => NumV(n)
//      case TrueC() => BoolV(true)
//      case FalseC() => BoolV(false)
//      case PlusC(l, r) => NumV(getIntValue(interp(l)) + getIntValue(interp(r)))
//      case MultC(l, r) => NumV(getIntValue(interp(l)) * getIntValue(interp(r)))
//      case IfC(c, t, e) => {
//        interp(c) match {
//          case BoolV(true) => interp(t)
//          case BoolV(false) => interp(e)
//          case _ => throw CustomInterpException("condition does not evaluate to boolean")
//        }
//      }
//      case EqNumC(l, r) => {
//        BoolV(getIntValue(interp(l)) == getIntValue(interp(r)))
//      }
//      case LtC(l, r) => {
//        BoolV(getIntValue(interp(l)) < getIntValue(interp(r)))
//      }
//      case NilC() => NilV()
//
//      case ConsC(l, r) => ConsV(interp(l), interp(r))
//
//      case HeadC(e) => {
//        interp(e) match {
//          case ConsV(l, r) => l
//          case _ => throw CustomInterpException("head with not list")
//        }
//      }
//
//      case TailC(e) => {
//        interp(e) match {
//          case ConsV(l, r) => r
//          case _ => throw CustomInterpException("tail with not list")
//        }
//      }
//
//      case IsNilC(e) => {
//        interp(e) match {
//          case NilV() => BoolV(true)
//          case ConsV(h, t) => BoolV(false)
//          case _ => throw CustomInterpException("is-nil with not list")
//        }
//      }
//      case IsListC(e) => {
//        interp(e) match {
//          case NilV() => BoolV(true)
//          case ConsV(l, r) => BoolV(true)
//          case _ => BoolV(false)
//        }
//      }
//
//      case FdC(l, body) => FunV(FdC(l, body))
//
//      case AppC(f, args) => {
//        interp(f) match {
//          case FunV(FdC(param, body)) => {
//            if (param.size != args.size) {
//              throw CustomInterpException("parameters dont match arguments")
//            }
//            interp(substitute(body, createBindList(param, args.map(e => interp(e)))))
//          }
//          case _ => throw CustomInterpException("Not a function: " + f)
//        }
//      }
//      case IdC(y) => throw CustomInterpException("Free identifier " + y)
//      case UndefinedC() => throw CustomInterpException("Undefined behavior")
//    }
//  }
//
//  def substitute(function: ExprC, binds: List[Bind]): ExprC = {
//    function match {
//      case ValC(a) => ValC(a)
//      case NumC(n) => NumC(n)
//      case TrueC() => TrueC()
//      case FalseC() => FalseC();
//
//      case PlusC(e1, e2) => PlusC(substitute(e1, binds), substitute(e2, binds))
//      case MultC(e1, e2) => MultC(substitute(e1, binds), substitute(e2, binds))
//      case EqNumC(e1, e2) => EqNumC(substitute(e1, binds), substitute(e2, binds))
//      case LtC(e1, e2) => LtC(substitute(e1, binds), substitute(e2, binds))
//      case IfC(x, y, z) => IfC(substitute(x, binds), substitute(y, binds), substitute(z, binds))
//      case HeadC(x) => HeadC(substitute(x, binds))
//      case TailC(x) => TailC(substitute(x, binds))
//      case IsListC(x) => IsListC(substitute(x, binds))
//      case IsNilC(x) => IsNilC(substitute(x, binds))
//      case ConsC(x, y) => ConsC(substitute(x, binds), substitute(y, binds))
//
//      // substitute inside f and all its arguments
//      case AppC(f, args) => AppC(substitute(f, binds), args.map(e => substitute(e, binds)))
//
//      case FdC(parms, body) => FdC(parms, substitute(body, binds.filter(e => !parms.contains(e.name))))
//
//      case IdC(y) => {
//        if (binds.indexOf(findLastBinding(binds.reverse, y)) == -1) {
//          IdC(y)
//        } else {
//          ValC(binds.apply(binds.indexOf(findLastBinding(binds, y))).value)
//        }
//      }
//    }
//  }
//
//  def findLastBinding(bindings: List[Bind], str: String): Bind = bindings match {
//    case Nil => Bind("", NilV())
//    case Bind(a, b) :: c => {
//      if (a == str) {
//        Bind(a, b)
//      } else {
//        findLastBinding(c, str)
//      }
//    }
//  }
//
//  def createBindList(params: List[String], args: List[Value]): List[Bind] = {
//    params.zip(args).map {
//      case (s, v) => Bind(s, v)
//    }
//  }
//
//  def getIntValue(v: Value): Int = {
//    v match {
//      case NumV(n) => n
//      case _ => throw CustomInterpException("Not a number")
//    }
//  }
//}
//
