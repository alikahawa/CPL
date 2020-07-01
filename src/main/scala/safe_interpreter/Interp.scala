package safe_interpreter

object Interp {


  type Store = List[Cell]
  type PointerEnvironment = List[Pointer]

  // Do not remove this method. We use this for grading.
  def interp(e: ExprC): Value = interp(e, Nil, Nil)._1

  def interp(e: ExprC, nv: PointerEnvironment, st1: Store): (Value, Store) = e match {
    case TrueC() => (BoolV(true), st1)
    case FalseC() => (BoolV(false), st1)
    case NumC(num: Int) => (NumV(num), st1)
    case NilC() => (NilV(), st1)
    case IdC(id) => {
      val location = lookup(id, nv)
      val res = fetch(location, st1)
      (res, st1)
    }
    case ConsC(h, t) => {
      val (hV, st2) = interp(h, nv, st1)
      val (tV, st3) = interp(t, nv, st2)
      (ConsV(hV, tV), st3)
    }
    case IfC(a, b, c) => interp(a, nv, st1) match {
      case (BoolV(true), st2) => interp(b, nv, st2)
      case (BoolV(false), st3) => interp(c, nv, st3)
      case _ => throw CannotInterpException("IfC isn't right! ")
    }
    case MultC(a, b) => { //(NumV(inVal(interp(a, nv, st1)) * inVal(interp(b, nv, st2))), st1)
      val (aV, st2) = interp(a, nv, st1)
      val (bV, st3) = interp(b, nv, st2)
      (aV, bV) match {
        case (NumV(aV), NumV(bV)) => (NumV(aV * bV), st3)
        case _ => throw CannotInterpException("Is not a number at * ")
      }
    }
    case PlusC(a, b) => {// (NumV(inVal(interp(a, nv, st1)) + inVal(interp(b, nv, st2))), st1)
      val (aV, st2) = interp(a, nv, st1)
      val (bV, st3) = interp(b, nv, st2)
      (aV, bV) match {
        case (NumV(aV), NumV(bV)) => (NumV(aV + bV), st3)
        case _ => throw CannotInterpException("Is not a number at + ")
      }
    }
    case EqNumC(a, b) => {// (BoolV(inVal(interp(a, nv, st1)) == inVal(interp(b, nv, st2))), st1)
      val (aV, st2) = interp(a, nv, st1)
      val (bV, st3) = interp(b, nv, st2)
      (aV, bV) match {
        case (NumV(aV), NumV(bV)) => (BoolV(aV == bV), st3)
        case _ => throw CannotInterpException("Is not a number at == ")
      }
    }
    case LtC(a, b) => { //(BoolV(inVal(interp(a, nv, st1)) < inVal(interp(b, nv, st2))), st1) {
      val (aV, st2) = interp(a, nv, st1)
      val (bV, st3) = interp(b, nv, st2)
      (aV, bV) match {
        case (NumV(aV), NumV(bV)) => (BoolV(aV < bV), st3)
        case _ => throw CannotInterpException("Is not a number at < ")
      }
    }
    case HeadC(a) => {
      val (aV, st2) = interp(a, nv, st1)
      aV match {
        case NilV() => throw CannotInterpException("Head Nil")
        case ConsV(h, _) => (h, st2)
        case _ => throw CannotInterpException("Head should never get here")
      }
    }
    case TailC(a) => {
      val (aV, st2) = interp(a, nv, st1)
      aV match {
        case ConsV(h, t) => (t, st2)
        case _ => throw CannotInterpException("Head should never get here")
      }
    }

    case IsNilC(a) => {
      val (aV, st2) = interp(a, nv, st1)
      aV match {
        case NilV() => (BoolV(true), st2)
        case ConsV(a, b) => (BoolV(false), st2)
        case _ => throw CannotInterpException("IsNil should never get here")
      }
    }
    case PairC(l: ExprC, r: ExprC) => {
      val (lV, st2) = interp(l, nv, st1)
      val (rV, st3) = interp(r, nv, st2)
      (PairV(lV, rV), st3)
    }
    case FstC(e: ExprC) => {
      val (eV, st2) = interp(e, nv, st1)
      eV match{
        case PairV(left, right) => (left, st2)
        case _ => throw CannotInterpException("FsTC should never get here")
      }
    }
    case SndC(e: ExprC) => {
      val (eV, st2) = interp(e, nv, st1)
      eV match{
        case PairV(left, right) => (right, st2)
        case _ => throw CannotInterpException("SNDC should never get here")
      }
    }
    // {
    //   val (aV1, st2) = interp(IsListC(a), nv, st1)
    //   if(aV1 == BoolV(true)) {
    //   val (aV, st2) = interp(a, nv, st1)
    //     aV match {
    //       case NilV() => (BoolV(true), st2)
    //       case _ => (BoolV(false), st2)
    //     }
    //   }else {
    //     throw CannotInterpException("IsNil should never get here")
    //   }
    // }
    // case IsListC(a) => {
    //   val (aV, st2) = interp(a, nv, st1)
    //   aV match {
    //   case NilV() => (BoolV(true), st2)
    //   case ConsV(h, t) => (BoolV(true), st2)
    //   case _ => (BoolV(false), st2)
    //   }
    // }
    case FdC(l: List[String], exp: ExprC) => (PointerClosV(FdC(l, exp), nv), st1)

    case AppC(exp: ExprC, a: List[ExprC]) => interp(exp, nv, st1) match {
      case (PointerClosV(FdC(l, expF), nv1), st2) => {
        val (nv2, st3) = pointCellTogether(l, nv, a, st2)
        interp(expF, nv2 ::: nv1, st3)
      }
      case _ => throw CannotInterpException("Something went wrong at AppC")
    }

    case BoxC(box) => {
      val (bV, st2) = interp(box, nv, st1)
      val (newLocation, st3) = extendStore(st2, bV)
      (BoxV(newLocation), st3)
    }

    case UnboxC(box) => {
      val (b, st2) = interp(box, nv, st1)
      b match {
        case BoxV(n) => {
          (fetch(n, st2), st2)
        }
        case aaa => throw CannotInterpException("Value at BoxV is not Int! " + aaa.toString)
      }
    }

    case SetboxC(exp: ExprC, expValue: ExprC) => {
      val(expV, st2) = interp(exp, nv, st1)
      val (expValueN, st3) =  interp(expValue, nv, st2)
      expV match {
        case BoxV(location) => {
          (expValueN, updatValueInStore(st3, Cell(location, expValueN)))
        }
        case _ => throw CannotInterpException("SetBox isn't having a boxV after Interp! ")
      }
    }

    case SetC(v: String, exp: ExprC) => {
      val location = lookup(v, nv)
      val (expV, st2) = interp(exp, nv, st1)
      (expV, updatValueInStore(st2, Cell(location, expV)))
    }

    case SeqC(exp: ExprC, exp1: ExprC) => {
      val (expV, st2) = interp(exp, nv, st1)
      val (exp1V, st3) = interp(exp1, nv, st2)
      (exp1V, st3)
    }


    case UninitializedC() =>  (UninitializedV(), st1)
    case _ => throw CannotInterpException("Is Unknown!!! ")
  }

  def inVal(v: Value): Int = v match {
    case NumV(n) => n
    case _ => throw NotIntegerException("Not Integer")
  }

  def getBoolV(v: Value): BoolV = v match {
    case v1: BoolV => v1
    case _ => throw CannotInterpException("Is not Boolean! ")
  }

  def getBoxV(v: Value): BoxV = v match {
    case v1: BoxV => v1
    case _ => throw CannotInterpException("Is not Box! ")
  }

  def lookup(x: String, nv: List[Pointer]): Int = nv match {
    case Nil => throw CannotInterpException("Free not in look up exception !! ")
    case Pointer(a, b) :: Nil => if (x == a) b else throw CannotInterpException("Free not in look up check one !! " + x)
    case Pointer(a, b) :: nvTail => if (x == a) b else lookup(x, nvTail)
    case _ => throw CannotInterpException("Unknown situation at lookup!! ")
  }

  def fetch(location: Int, st: Store): Value = {
    if(location < st.size && location >= 0) {
      st(location).value
    } else{
      throw CannotInterpException("Cannot fetch at fetch !! ")
    }
  }

  /**
   * def extendStore(cell: Cell, st: Store): Store =  st :: List(cell) This adds new value to the store
   * and return the location of that stored value + the new store since that scala....
   */
  def extendStore(st1: Store, v: Value): (Int, Store) = st1 match {
    case Nil => (0, Cell(0, v) :: st1)
    case _ :+ Cell(lastLocation, _) => (lastLocation + 1, st1 ::: List(Cell(lastLocation + 1, v)))
    case _ => throw CannotInterpException("Something went wrong while adding new cell to the store !! ")
  }

  def updatValueInStore(st: Store, newCell: Cell): Store = st match {
    case Nil => throw CannotInterpException("Something went wrong while updating a cell at the Nil store!! ")
    case Cell(oldLocation, oldValue) :: Nil => {
      if(oldLocation ==  getIntFromCell(newCell)) {
        newCell :: Nil
      }else{
        throw CannotInterpException("Updating a cell at the Nil store didn't go well!! ")
      }
    }
    case Cell(oldLocation, oldValue) :: tai => {
      if(oldLocation == getIntFromCell(newCell)) {
        newCell :: tai
      }else{
        Cell(oldLocation, oldValue) :: updatValueInStore(tai, newCell)
      }
    }
    case _ => throw CannotInterpException("SSomething went wrong while updating a cell at the store!! ")
  }

  def getIntFromCell(cell: Cell): Int = cell match {
    case Cell(loc: Int, value: Value) => loc
  }

  def getValFromCell(cell: Cell): Value = cell match {
    case Cell(loc: Int, value: Value) => value
  }

  def pointCellTogether(stringList: List[String], nv: List[Pointer], exps: List[ExprC], st: Store): (List[Pointer], Store) = {
    if(stringList.size != exps.size){
      throw CannotInterpException("Lists are not the same size at CellPointTogether!")
    }else {
      (stringList, exps) match {
        case (Nil, Nil) => (Nil, st)
        case (s :: Nil, exp :: Nil) => {
          val (expV, st1) = interp(exp, nv, st)
          val (locationOfExpV, newStore) = extendStore(st1, expV)
          (Pointer(s, locationOfExpV) :: Nil, newStore)
        }
        case (s :: tailString, exp :: tailExps) => {
          val (expV, st1) = interp(exp, nv, st)
          val (locationOfExpV, newStore) = extendStore(st1, expV)
          val (nv1, stRes) = pointCellTogether(tailString, nv, tailExps, newStore)
          (Pointer(s, locationOfExpV) :: nv1, stRes)
        }
        case _ => throw CannotInterpException("Something went wrong at CellPointTogether!")
      }
    }
  }

  //  //local type
  //  type Store = List[Cell]
  //  type PointerEnvironment = List[Pointer]
  //
  //  // main Interpreter -------------------------
  //  def interp(e: ExprC): Value = interp(e, Nil, Nil)._1
  //
  //  def interp(e: ExprC, nv: PointerEnvironment, st1: Store): (Value, Store) = e match {
  //    case NumC(n)  => (NumV(n), st1)
  //    case TrueC()  => (BoolV(true), st1)
  //    case FalseC() => (BoolV(false),st1)
  //    case NilC()   => (NilV(), st1)
  //
  //    case PlusC(l, r)  =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      val (rv, st3) = interp(r, nv, st2)
  //      (NumV(getInt(lv) + getInt(rv)), st3)
  //
  //    case MultC(l, r)  =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      val (rv, st3) = interp(r, nv, st2)
  //      (NumV(getInt(lv) * getInt(rv)), st3)
  //
  //    case EqNumC(l, r) =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      val (rv, st3) = interp(r, nv, st2)
  //      (BoolV(getInt(lv) == getInt(rv)), st3)
  //
  //    case LtC(l, r) =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      val (rv, st3) = interp(r, nv, st2)
  //      (BoolV(getInt(lv) < getInt(rv)), st3)
  //
  //    case IfC(c, t, e) =>
  //      val (bool, st2) = interp(c, nv, st1)
  //      bool match {
  //        case BoolV(true)  => interp(t, nv, st2)
  //        case BoolV(false) => interp(e, nv, st2)
  //        case _ =>
  //          throw InterpExc("Evaluate if-else, the value isn't BoolV(): { "+ (bool, st2) +" }")
  //      }
  //
  //    case ConsC(h, t) => {
  //      val (head, st2) = interp(h, nv, st1)
  //      val (tail, st3) = interp(t, nv, st2)
  //
  //      (ConsV(head, tail), st3)
  //    }
  //
  //    case HeadC(l) =>
  //      interp(l, nv, st1) match {
  //        case (ConsV(h, t), st2)  => (h, st2)
  //        case _            => throw InterpExc("The argument of head function isn't a list: { "+ l +" }")
  //      }
  //
  //    case TailC(l) =>
  //      interp(l, nv, st1) match {
  //        case (ConsV(h, t), st2) => (t, st2)
  //        case _           => throw InterpExc("The argument of tail function isn't a list: { "+ l +" }")
  //      }
  //
  //    case IsNilC(l) =>
  //      interp(l, nv, st1) match {
  //        case (NilV(), st2)        => (BoolV(true), st2)
  //        case (ConsV(h, t), st2)   => (BoolV(false), st2)
  //        case _                    => throw InterpExc("The argument of is-nil function isn't a list or NilC(): { "+ l +" }")
  //      }
  //
  //    case FdC(params, body) =>
  //      (PointerClosV(FdC(params, body), nv), st1)
  //
  //    case AppC(f, args) =>
  //      interp(f, nv, st1) match {
  //        case (PointerClosV(FdC(params, body), nv_f), st2) =>
  //          val (nv_p, st_p) = addPointers(params, args, nv, st2)
  //          interp(body, nv_p ::: nv_f, st_p)
  //
  //        case other =>
  //          throw InterpExc("AppC() expected a FunV(), but got: " + other)
  //      }
  //
  //    case IdC(s) => (fetch(lookup(s, nv), st1), st1)
  //
  //    case SeqC(l, r) =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      interp(r, nv, st2)
  //
  //    case SetC(id, newExpr) =>
  //      val (v, st2) = interp(newExpr, nv, st1)
  //      (v, update(lookup(id, nv), v, st2))
  //
  //    case BoxC(e) =>
  //      val (v, st2) = interp(e, nv, st1)
  //      val (loc, st3) = store(v, st2)
  //      (BoxV(loc), st3)
  //
  //    case UnboxC(b) =>
  //      val (box, st2) = interp(b, nv, st1)
  //      box match {
  //        case BoxV(loc) =>
  //          (fetch(loc, st2), st2)
  //        case _ =>
  //          throw InterpExc("Cann'r unbox non-box value: " + box)
  //      }
  //
  //    case SetboxC(b, newExpr) =>
  //      val (box, st2) = interp(b, nv, st1)
  //      val (v, st3) = interp(newExpr, nv, st2)
  //      box match {
  //        case BoxV(loc) =>
  //          (v, update(loc, v, st3))
  //        case _ =>
  //          throw InterpExc("Cann'r unbox non-box value: " + box)
  //      }
  //
  //    case UninitializedC() => (UninitializedV(), st1)
  //
  //    case PairC(l, r) =>
  //      val (lv, st2) = interp(l, nv, st1)
  //      val (rv, st3) = interp(r, nv, st2)
  //      (PairV(lv, rv), st3)
  //
  //    case FstC(e) =>
  //      interp(e, nv, st1) match {
  //        case (PairV(lv, rv), st2) =>
  //          (lv, st2)
  //        case other =>
  //          throw InterpExc("using {fst} operator on not-pair value : " + other)
  //      }
  //
  //    case SndC(e) =>
  //      interp(e, nv, st1) match {
  //        case (PairV(lv, rv), st2) =>
  //          (rv, st2)
  //        case other =>
  //          throw InterpExc("using {snd} operator on not-pair value : " + other)
  //      }
  //
  //    case other =>
  //      throw InterpExc("Error! final case in interp: " + other)
  //  }
  //
  //  /* return the store (memory) location of identifier */
  //  def lookup(s: String, nv: PointerEnvironment): Int = nv match {
  //    case Nil =>
  //      throw InterpExc("free IdC(" + s + ") - lookup()")
  //
  //    case Pointer(name, location) :: restBinds =>
  //      if (name == s) location
  //      else lookup(s, restBinds)
  //
  //    case other =>
  //      throw InterpExc("Can't recognize the list of pointers: " + other + " - lookup()")
  //  }
  //
  //  def fetch(loc: Int, st: Store): Value = st match {
  //    case Nil =>
  //      throw InterpExc("Cound't find value at location (" + loc + ") - Store(" + st + ") - lookup()")
  //
  //    case Cell(locCell, value) :: tail =>
  //      if(loc == locCell) value
  //      else fetch(loc, tail)
  //
  //    case other =>
  //      throw InterpExc("Can't recognize ( " + other + ") - in Store(" + st + ") - lookup()")
  //  }
  //
  //  /* return the store after replacing the value at location loc with newValue*/
  //  def update(loc: Int, newValue: Value, st: Store): Store = st match {
  //    case Nil =>
  //      throw InterpExc("Cound't find value at location (" + loc + ") - Store(" + st + ") - update()")
  //
  //    case Cell(locCell, value) :: tail =>
  //      if(loc == locCell) Cell(loc, newValue) :: tail
  //      else Cell(locCell, value) :: update(loc, newValue, tail)
  //
  //    case other =>
  //      throw InterpExc("Can't recognize the list of pointers: " + other + " - update()")
  //  }
  //
  //  def store(v: Value, st: Store): (Int, Store) = st match {
  //    case Nil =>
  //      (0, Cell(0, v) :: st)
  //
  //    case _ :+ Cell(loc, _) =>
  //      (loc+1, st ::: Cell(loc+1, v) :: Nil)
  //
  //    case other =>
  //      throw InterpExc("Can't recognize ( " + other + ") - in Store(" + st + ") - lookup()")
  //  }
  //
  //  /* make {Pointer}s from params/args and add them to the head of environment  */
  //  def addPointers(params: List[String], args: List[ExprC], nv: PointerEnvironment, st: Store): (PointerEnvironment, Store) =
  //    (params, args) match {
  //      case (Nil, Nil) => (Nil, st)
  //
  //      case (param :: restParams, arg :: restArgs) =>
  //        val (argV, st2) = interp(arg, nv, st)
  //        val (loc, st3) = store(argV, st2)
  //        val (nv_p, st_p) = addPointers(restParams, restArgs, nv, st3)
  //        (Pointer(param, loc) :: nv_p, st_p)
  //
  //      case _ =>
  //        throw InterpExc("#params not equals #args: " + params + " args: " + args + " env: " + nv)
  //    }
  //
  //  def getInt(v: Value): Int =
  //    v match {
  //      case NumV(n)  => n
  //      case _        => throw InterpExc("Error! call getInt() for non NumV")
  //    }
  //
  //  def getBool(v: Value): Boolean =
  //    v match {
  //      case BoolV(b)  => b
  //      case _        => throw InterpExc("Error! call getBool() for non NumV")
  //    }
}