package lazy_interpreter

import lazy_interpreter.Interp.interp

object Interp {

  // local classes and type --------------------
  case class Bind(name: String, var value: Value)
  type Environment = List[Bind]

  // main Interpreter -------------------------
  def interp(e: ExprC): Value = interp(e, Nil)

  def interp(e: ExprC, nv: Environment): Value =  e match {
    case TrueC() => BoolV(true)
    case FalseC() => BoolV(false)
    case NumC(num) => NumV(num)
    case NilC() => NilV()
    case ConsC(h, t) => ConsV(ThunkV(Left((h, nv))), ThunkV(Left((t, nv))))
    case IdC(id) => lookup(id, nv)
    case ForceC(a) => force(interp(a, nv))
    case IfC(a, b, c) => strict(interp(a, nv)) match {
      case BoolV(true) => interp(b, nv)
      case BoolV(false) => interp(c, nv)
      case _ => throw CannotInterpException("IfC isn't right! ")
    }
    case MultC(a, b) => NumV(inVal(strict(interp(a, nv))) * inVal(strict(interp(b, nv))))
    case PlusC(a, b) => NumV(inVal(strict(interp(a, nv))) + inVal(strict(interp(b, nv))))
    case EqNumC(a, b) => BoolV(inVal(strict(interp(a, nv))) == inVal(strict(interp(b, nv))))
    case LtC(a, b) => BoolV(inVal(strict(interp(a, nv))) < inVal(strict(interp(b, nv))))
    case HeadC(a) => strict(interp(a, nv)) match {
      case NilV() => throw CannotInterpException("Head Nil")
      case ConsV(h, _) => h
      case _ => throw CannotInterpException("Head should never get here")
    }
    case TailC(a) => strict(interp(a, nv)) match {
      case ConsV(h, t) => t
      case _ => throw CannotInterpException("Head should never get here")
    }

    case IsNilC(a) => if(strict(interp(IsListC(a), nv)) == BoolV(true)) strict(interp(a, nv)) match {
      case NilV() => BoolV(true)
      case _ => BoolV(false)
    }else {
      throw CannotInterpException("IsNil should never get here")
    }
    case IsListC(a) => strict(interp(a, nv)) match {
      case NilV() => BoolV(true)
      case ConsV(h, t) => BoolV(true)
      case _ => BoolV(false)
    }

    case FdC(l: List[String], exp: ExprC) => ClosV(FdC(l, exp), nv)

    case AppC(exp: ExprC, a: List[ExprC]) => strict(interp(exp, nv)) match {
      case ClosV(FdC(l, exp), Nil) => interp(exp, bindTogether(l, a, nv))
      case ClosV(FdC(l, exp), nv1) => interp(exp, bindTogether(l, a, nv) ::: nv1)
      case _ => throw CannotInterpException("Something went wrong at AppC")
    }

    case LetRecC(binds: List[LetBindC], exp: ExprC) => ???
    /**
     * Need to be added
     */
    //    case LetRecC(binds, body) =>
    //      val bind_nv: List[Bind] = binds map { case LetBindC(name, _) => Bind(name, UninitializedV())}
    //      val new_nv =  bind_nv ::: nv
    //
    //      val thunks: List[Bind] = binds map { case LetBindC(name, exprC) => Bind(name, ThunkV(Left(exprC, new_nv)))}
    //      bind_nv.zip(thunks)
    //        .foreach { case (b@Bind(n1, _), Bind(n2, thunk)) if n1 == n2 =>  b.value = thunk }
    //      interp(body, new_nv)
    case UndefinedC() =>  throw CannotInterpException("Is undefined!!! ")
    case _ => throw CannotInterpException("Is Unknown!!! ")
  }

  def inVal(v: Value): Int = v match {
    case NumV(n) => n
    case _ => throw NotIntegerException("Not Integer")
  }

  // IMPORTANT: DO NOT USE SUBSTITUTION FOR THIS ASSIGNMENT
  def lookup(x: String, nv: Environment): Value = nv match {
    case Nil => throw CannotInterpException("Free not in look up !! ")
    // case Bind(a, ThunkV(Left((b, nv1)))) :: Nil => if (x == a) interp(b, nv1) else throw CannotInterpException("Free not in look up !! ")
    case Bind(a, b) :: Nil => if (x == a) b else throw CannotInterpException("Free not in look up !! ")
    case Bind(a, ThunkV(Left((b, nv1)))) :: nvTail => if (x == a) interp(b, nv1) else lookup(x, nvTail)
    case Bind(a, b) :: nvTail => if (x == a) b else lookup(x, nvTail)
    case _ => throw CannotInterpException("Unknown situation !! ")
  }

  def bindTogether(stringList: List[String], list: List[ExprC], nv: Environment): List[Bind] = {
    if(stringList.size != list.size){
      throw CannotInterpException("Lists are not the same size!")
    }else {
      (stringList, list) match {
        case (List(), List()) => List()
        case (a :: Nil, b :: Nil) => List(Bind(a, ThunkV(Left(b, nv))))
        case (a :: c, b :: d) => List(Bind(a, ThunkV(Left(b, nv)))) ::: bindTogether(c, d, nv)
        case _ => throw CannotInterpException("Strings are not equal to expressions cannot bind together")
      }
    }
  }

//  def interp(e: ExprC): Value = interp(e, Nil)

  def strict(v: Value): Value = v match {
    case t@ThunkV(Left((exp, nv))) => {
      val res = strict(interp(exp, nv))
      t.value = Right(res)
      res
    }
    case ThunkV(Right(r)) => r
    case rest => rest
  }

  def force(v: Value): Value = v match {
    case t@ThunkV(_) => force(strict(t))
    case ConsV(hea, tai) => ConsV(force(hea), force(tai))
    case rest => rest
  }
}