package Starter

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