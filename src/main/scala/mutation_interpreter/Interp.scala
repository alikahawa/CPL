package mutation_interpreter

object Interp {

  // local classes and type
  case class Pointer(name: String, location: Int)
  case class Cell(location: Int, value: Value)
  type Store = List[Cell]
  type PointerEnvironment = List[Pointer]

  // main Interpreter
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
      val (tV, st3) = interp(t, nv, st1)
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
      val (aV1, st2) = interp(IsListC(a), nv, st1)
      if(aV1 == BoolV(true)) {
        val (aV, st2) = interp(a, nv, st1)
        aV match {
          case NilV() => (BoolV(true), st2)
          case _ => (BoolV(false), st2)
        }
      }else {
        throw CannotInterpException("IsNil should never get here")
      }
    }
    case IsListC(a) => {
      val (aV, st2) = interp(a, nv, st1)
      aV match {
        case NilV() => (BoolV(true), st2)
        case ConsV(h, t) => (BoolV(true), st2)
        case _ => (BoolV(false), st2)
      }
    }
    case FdC(l: List[String], exp: ExprC) => (PointerClosV(FdC(l, exp), nv), st1)

    case AppC(exp: ExprC, a: List[ExprC]) => interp(exp, nv, st1) match {
      case (PointerClosV(FdC(l, expF), nv1), st2) => {
        val (nv2, st3) = addPointers(l, nv, a, st2)
        interp(expF, nv2 ::: nv1, st3)
      }
      case _ => throw CannotInterpException("Something went wrong at AppC")
    }

    case SeqC(exp: ExprC, exp1: ExprC) => {
      val (expV, st2) = interp(exp, nv, st1)
      interp(exp1, nv, st2)
    }

    case BoxC(box) => {
      val (bV, st2) = interp(box, nv, st1)
      val (newLocation, st3) = store(st2, bV)
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
          (expValueN, updateStore(st3, Cell(location, expValueN)))
        }
        case _ => throw CannotInterpException("SetBox isn't having a boxV after Interp! ")
      }
    }

    case SetC(v: String, exp: ExprC) => {
      val location = lookup(v, nv)
      val (expV, st2) = interp(exp, nv, st1)
      (expV, updateStore(st2, Cell(location, expV)))
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
  def store(st1: Store, v: Value): (Int, Store) = st1 match {
    case Nil => (0, Cell(0, v) :: st1)
    case _ :+ Cell(lastLocation, _) => (lastLocation + 1, st1 ::: Cell(lastLocation + 1, v) :: Nil)
    case _ => throw CannotInterpException("Something went wrong while adding new cell to the store !! ")
  }

  def updateStore(st: Store, newCell: Cell): Store = st match {
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
        Cell(oldLocation, oldValue) :: updateStore(tai, newCell)
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

  def addPointers(stringList: List[String], nv: List[Pointer], exps: List[ExprC], st: Store): (List[Pointer], Store) = {
    if(stringList.size != exps.size){
      throw CannotInterpException("Lists are not the same size at CellPointTogether!")
    }else {
      (stringList, exps) match {
        case (Nil, Nil) => (Nil, st)
        case (s :: Nil, exp :: Nil) => {
          val (expV, st1) = interp(exp, nv, st)
          val (locationOfExpV, newStore) = store(st1, expV)
          (Pointer(s, locationOfExpV) :: Nil, newStore)
        }
        case (s :: tailString, exp :: tailExps) => {
          val (expV, st1) = interp(exp, nv, st)
          val (locationOfExpV, newStore) = store(st1, expV)
          val (nv1, stRes) = addPointers(tailString, nv, tailExps, newStore)
          (Pointer(s, locationOfExpV) :: nv1, stRes)
        }
        case _ => throw CannotInterpException("Something went wrong at CellPointTogether!")
      }
    }
  }
}