package safe_interpreter

object Interp {

  //local type
  type Store = List[Cell]
  type PointerEnvironment = List[Pointer]

  // main Interpreter -------------------------
  def interp(e: ExprC): Value = interp(e, Nil, Nil)._1

  def interp(e: ExprC, nv: PointerEnvironment, st1: Store): (Value, Store) = e match {
    case NumC(n)  => (NumV(n), st1)
    case TrueC()  => (BoolV(true), st1)
    case FalseC() => (BoolV(false),st1)
    case NilC()   => (NilV(), st1)

    case PlusC(l, r)  =>
      val (lv, st2) = interp(l, nv, st1)
      val (rv, st3) = interp(r, nv, st2)
      (NumV(getInt(lv) + getInt(rv)), st3)

    case MultC(l, r)  =>
      val (lv, st2) = interp(l, nv, st1)
      val (rv, st3) = interp(r, nv, st2)
      (NumV(getInt(lv) * getInt(rv)), st3)

    case EqNumC(l, r) =>
      val (lv, st2) = interp(l, nv, st1)
      val (rv, st3) = interp(r, nv, st2)
      (BoolV(getInt(lv) == getInt(rv)), st3)

    case LtC(l, r) =>
      val (lv, st2) = interp(l, nv, st1)
      val (rv, st3) = interp(r, nv, st2)
      (BoolV(getInt(lv) < getInt(rv)), st3)

    case IfC(c, t, e) =>
      val (bool, st2) = interp(c, nv, st1)
      bool match {
        case BoolV(true)  => interp(t, nv, st2)
        case BoolV(false) => interp(e, nv, st2)
        case _ =>
          throw InterpExc("Evaluate if-else, the value isn't BoolV(): { "+ (bool, st2) +" }")
      }

    case ConsC(h, t) => {
      val (head, st2) = interp(h, nv, st1)
      val (tail, st3) = interp(t, nv, st2)

      (ConsV(head, tail), st3)
    }

    case HeadC(l) =>
      interp(l, nv, st1) match {
        case (ConsV(h, t), st2)  => (h, st2)
        case _            => throw InterpExc("The argument of head function isn't a list: { "+ l +" }")
      }

    case TailC(l) =>
      interp(l, nv, st1) match {
        case (ConsV(h, t), st2) => (t, st2)
        case _           => throw InterpExc("The argument of tail function isn't a list: { "+ l +" }")
      }

    case IsNilC(l) =>
      interp(l, nv, st1) match {
        case (NilV(), st2)        => (BoolV(true), st2)
        case (ConsV(h, t), st2)   => (BoolV(false), st2)
        case _                    => throw InterpExc("The argument of is-nil function isn't a list or NilC(): { "+ l +" }")
      }

    case FdC(params, body) =>
      (PointerClosV(FdC(params, body), nv), st1)

    case AppC(f, args) =>
      interp(f, nv, st1) match {
        case (PointerClosV(FdC(params, body), nv_f), st2) =>
          val (nv_p, st_p) = addPointers(params, args, nv, st2)
          interp(body, nv_p ::: nv_f, st_p)

        case other =>
          throw InterpExc("AppC() expected a FunV(), but got: " + other)
      }

    case IdC(s) => (fetch(lookup(s, nv), st1), st1)

    case SeqC(l, r) =>
      val (lv, st2) = interp(l, nv, st1)
      interp(r, nv, st2)

    case SetC(id, newExpr) =>
      val (v, st2) = interp(newExpr, nv, st1)
      (v, update(lookup(id, nv), v, st2))

    case BoxC(e) =>
      val (v, st2) = interp(e, nv, st1)
      val (loc, st3) = store(v, st2)
      (BoxV(loc), st3)

    case UnboxC(b) =>
      val (box, st2) = interp(b, nv, st1)
      box match {
        case BoxV(loc) =>
          (fetch(loc, st2), st2)
        case _ =>
          throw InterpExc("Cann'r unbox non-box value: " + box)
      }

    case SetboxC(b, newExpr) =>
      val (box, st2) = interp(b, nv, st1)
      val (v, st3) = interp(newExpr, nv, st2)
      box match {
        case BoxV(loc) =>
          (v, update(loc, v, st3))
        case _ =>
          throw InterpExc("Cann'r unbox non-box value: " + box)
      }

    case UninitializedC() => (UninitializedV(), st1)

    case PairC(l, r) =>
      val (lv, st2) = interp(l, nv, st1)
      val (rv, st3) = interp(r, nv, st2)
      (PairV(lv, rv), st3)

    case FstC(e) =>
      interp(e, nv, st1) match {
        case (PairV(lv, rv), st2) =>
          (lv, st2)
        case other =>
          throw InterpExc("using {fst} operator on not-pair value : " + other)
      }

    case SndC(e) =>
      interp(e, nv, st1) match {
        case (PairV(lv, rv), st2) =>
          (rv, st2)
        case other =>
          throw InterpExc("using {snd} operator on not-pair value : " + other)
      }

    case other =>
      throw InterpExc("Error! final case in interp: " + other)
  }

  /* return the store (memory) location of identifier */
  def lookup(s: String, nv: PointerEnvironment): Int = nv match {
    case Nil =>
      throw InterpExc("free IdC(" + s + ") - lookup()")

    case Pointer(name, location) :: restBinds =>
      if (name == s) location
      else lookup(s, restBinds)

    case other =>
      throw InterpExc("Can't recognize the list of pointers: " + other + " - lookup()")
  }

  def fetch(loc: Int, st: Store): Value = st match {
    case Nil =>
      throw InterpExc("Cound't find value at location (" + loc + ") - Store(" + st + ") - lookup()")

    case Cell(locCell, value) :: tail =>
      if(loc == locCell) value
      else fetch(loc, tail)

    case other =>
      throw InterpExc("Can't recognize ( " + other + ") - in Store(" + st + ") - lookup()")
  }

  /* return the store after replacing the value at location loc with newValue*/
  def update(loc: Int, newValue: Value, st: Store): Store = st match {
    case Nil =>
      throw InterpExc("Cound't find value at location (" + loc + ") - Store(" + st + ") - update()")

    case Cell(locCell, value) :: tail =>
      if(loc == locCell) Cell(loc, newValue) :: tail
      else Cell(locCell, value) :: update(loc, newValue, tail)

    case other =>
      throw InterpExc("Can't recognize the list of pointers: " + other + " - update()")
  }

  def store(v: Value, st: Store): (Int, Store) = st match {
    case Nil =>
      (0, Cell(0, v) :: st)

    case _ :+ Cell(loc, _) =>
      (loc+1, st ::: Cell(loc+1, v) :: Nil)

    case other =>
      throw InterpExc("Can't recognize ( " + other + ") - in Store(" + st + ") - lookup()")
  }

  /* make {Pointer}s from params/args and add them to the head of environment  */
  def addPointers(params: List[String], args: List[ExprC], nv: PointerEnvironment, st: Store): (PointerEnvironment, Store) =
    (params, args) match {
      case (Nil, Nil) => (Nil, st)

      case (param :: restParams, arg :: restArgs) =>
        val (argV, st2) = interp(arg, nv, st)
        val (loc, st3) = store(argV, st2)
        val (nv_p, st_p) = addPointers(restParams, restArgs, nv, st3)
        (Pointer(param, loc) :: nv_p, st_p)

      case _ =>
        throw InterpExc("#params not equals #args: " + params + " args: " + args + " env: " + nv)
    }

  def getInt(v: Value): Int =
    v match {
      case NumV(n)  => n
      case _        => throw InterpExc("Error! call getInt() for non NumV")
    }

  def getBool(v: Value): Boolean =
    v match {
      case BoolV(b)  => b
      case _        => throw InterpExc("Error! call getBool() for non NumV")
    }
}