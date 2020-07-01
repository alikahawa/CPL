package EnvironmentBasedInterprete

object Interp {
// Every List[Bind] is just the data structure Environment

  def interp(e: ExprC): Value = interp(e, Nil)

  def interp(e: ExprC, nv: List[Bind]): Value =  e match {
    case TrueC() => BoolV(true)
    case FalseC() => BoolV(false)
    case NumC(num) => NumV(num)
    case NilC() => NilV()
    case ConsC(h, t) => ConsV(interp(h, nv), interp(t, nv))
    case IdC(id) => lookup(id, nv)
    case IfC(a, b, c) => interp(a, nv) match {
      case BoolV(true) => interp(b, nv)
      case BoolV(false) => interp(c, nv)
      case _ => throw CannotInterpException("IfC isn't right! ")
    }
    case MultC(a, b) => NumV(inVal(interp(a, nv)) * inVal(interp(b, nv)))
    case PlusC(a, b) => NumV(inVal(interp(a, nv)) + inVal(interp(b, nv)))
    case EqNumC(a, b) => BoolV(inVal(interp(a, nv)) == inVal(interp(b, nv)))
    case LtC(a, b) => BoolV(inVal(interp(a, nv)) < inVal(interp(b, nv)))
    case HeadC(a) => interp(a, nv) match {
      case NilV() => throw CannotInterpException("Head Nil")
      case ConsV(h, _) => h
      case _ => throw CannotInterpException("Head should never get here")
    }
    case TailC(a) => interp(a, nv) match {
      case ConsV(h, t) => t
      case _ => throw CannotInterpException("Head should never get here")
    }

    case IsNilC(a) => if(interp(IsListC(a), nv) == BoolV(true)) interp(a, nv) match {
      case NilV() => BoolV(true)
      case _ => BoolV(false)
    }else {
      throw CannotInterpException("IsNil should never get here")
    }
    case IsListC(a) => interp(a, nv) match {
      case NilV() => BoolV(true)
      case ConsV(h, t) => BoolV(true)
      case _ => BoolV(false)
    }

    case FdC(l: List[String], exp: ExprC) => ClosV(FdC(l, exp), nv)

    case AppC(exp: ExprC, a: List[ExprC]) => interp(exp, nv) match {
      case ClosV(FdC(l, exp), Nil) => interp(exp, bindTogether(l, a, nv))
      case ClosV(FdC(l, exp), nv1) => interp(exp, bindTogether(l, a, nv) ::: nv1)
      case _ => throw CannotInterpException("Something went wrong at AppC")
    }

    case UndefinedC() =>  throw CannotInterpException("Is undefined!!! ")
    case _ => throw CannotInterpException("Is Unknown!!! ")
  }

  def inVal(v: Value): Int = v match {
    case NumV(n) => n
    case _ => throw NotIntegerException("Not Integer")
  }

  // IMPORTANT: DO NOT USE SUBSTITUTION FOR THIS ASSIGNMENT
  def lookup(x: String, nv: List[Bind] /*Environment*/): Value = nv match {
    case Nil => throw CannotInterpException("Free not in look up !! ")
    case Bind(a, b) :: Nil => if (x == a) b else throw CannotInterpException("Free not in look up !! ")
    case Bind(a, b) :: nvTail => if (x == a) b else lookup(x, nvTail)
    case _ => throw CannotInterpException("Unknown situation !! ")
  }

  def bindTogether(stringList: List[String], list: List[ExprC], nv: List[Bind]): List[Bind] = {
    if(stringList.size != list.size){
      throw CannotInterpException("Lists are not the same size!")
    }else {
      (stringList, list) match {
        case (List(), List()) => List()
        case (a :: Nil, b :: Nil) => List(Bind(a, interp(b, nv)))
        case (a :: c, b :: d) => List(Bind(a, interp(b, nv))) ::: bindTogether(c, d, nv)
        case _ => throw CannotInterpException("Strings are not equal to expressions cannot bind together")
      }
    }
  }

  //  def interp(e: ExprC): Value = interp(e, Nil)
//
//  def interp(e: ExprC, env: List[Bind]): Value = {
//
//    println("interpreting ==  " + e + "     with env==   " + env)
//
//    e match {
//      case ValC(v) => v
//
//      case NumC(n) => NumV(n)
//      case TrueC() => BoolV(true)
//      case FalseC() => BoolV(false)
//      case PlusC(l, r) => NumV(getIntValue(interp(l, env)) + getIntValue(interp(r, env)))
//      case MultC(l, r) => NumV(getIntValue(interp(l, env)) * getIntValue(interp(r, env)))
//      case IfC(c, t, e) => {
//        interp(c, env) match {
//          case BoolV(true) => interp(t, env)
//          case BoolV(false) => interp(e, env)
//          case _ => throw CustomInterpException("condition does not evaluate to boolean")
//        }
//      }
//      case EqNumC(l, r) => {
//        BoolV(getIntValue(interp(l, env)) == getIntValue(interp(r, env)))
//      }
//      case LtC(l, r) => {
//        BoolV(getIntValue(interp(l, env)) < getIntValue(interp(r, env)))
//      }
//      case NilC() => NilV()
//
//      case ConsC(l, r) => ConsV(interp(l, env), interp(r, env))
//
//      case HeadC(e) => {
//        interp(e, env) match {
//          case ConsV(l, r) => l
//          case _ => throw CustomInterpException("head with not list")
//        }
//      }
//
//      case TailC(e) => {
//        interp(e, env) match {
//          case ConsV(l, r) => r
//          case _ => throw CustomInterpException("tail with not list")
//        }
//      }
//
//      case IsNilC(e) => {
//        interp(e, env) match {
//          case NilV() => BoolV(true)
//          case ConsV(h, t) => BoolV(false)
//          case _ => throw CustomInterpException("is-nil with not list")
//        }
//      }
//      case IsListC(e) => {
//        interp(e, env) match {
//          case NilV() => BoolV(true)
//          case ConsV(l, r) => BoolV(true)
//          case _ => BoolV(false)
//        }
//      }
//
//      case FdC(l, body) => ClosV(FdC(l, body), env)
//
//      case AppC(f, args) => {
//        interp(f, env) match {
//          case ClosV(FdC(params, body), closEnv) => {
//
//            val envNew = creatEnvironment(params, args, env) ::: closEnv
//
//            interp(body, envNew)
//          }
//
//          case _ => throw CustomInterpException("Not a function: " + f)
//        }
//      }
//      case IdC(y) => lookUp(y, env)
//      case UndefinedC() => throw CustomInterpException("Undefined behavior")
//    }
//  }
//
//  def creatEnvironment(p: List[String], a: List[ExprC], env: List[Bind]): List[Bind] = {
//
//    if (p.size == a.size) {
//      p.zip(a.map(e => interp(e, env))).map({ case (n: String, v: Value) => Bind(n, v) })
//    } else throw CustomInterpException("the number of parameters does not match the number of arguments")
//
//  }
//
//  def lookUp(iden: String, env: List[Bind]): Value = {
//
//    for (bind <- env) {
//      if (bind.name == iden) {
//        return bind.value
//      }
//    }
//    throw CustomInterpException("Free variable")
//  }
//
//
//  def getIntValue(v: Value): Int = {
//    v match {
//      case NumV(n) => n
//      case _ => throw CustomInterpException("Not a number")
//    }
//  }
}