//package EnvironmentBasedInterprete
//
//import Parser._
//
//
//case class NotImplementedException(s: String) extends RuntimeException(s)
//case class CannotDesugarException(s: String) extends DesugarException(s)
//case class CannotInterpException(s: String)  extends InterpException(s)
//case class NotStringException (s: String) extends InterpException(s)
//case class NotIntegerException (s: String) extends InterpException(s)
//
//object Desugar {
//  def desugar(e: ExprExt): ExprC = e match {
//    case TrueExt() => TrueC()
//    case FalseExt() => FalseC()
//    case NumExt(num: Int) => NumC(num)
//    case NilExt() => NilC()
//    case IdExt(id: String) => IdC(id)
//    case BinOpExt(s: String, left: ExprExt, right: ExprExt) => s match {
//      case "-" => PlusC(desugar(left), MultC(NumC(-1), desugar(right)))
//      case "+" => PlusC(desugar(left), desugar(right))
//      case "*" => MultC(desugar(left), desugar(right))
//      case "num=" => EqNumC(desugar(left), desugar(right))
//      case "num<" => LtC(desugar(left), desugar(right))
//      case "num>" => LtC(desugar(right), desugar(left))
//      case "and" => IfC(desugar(left), desugar(right), desugar(left))
//      case "or" => IfC(desugar(left), desugar(left), desugar(right))
//      case "cons" => ConsC(desugar(left), desugar(right))
//      case "setbox" => SetboxC(desugar(left), desugar(right))
//      case "seq" => SeqC(desugar(left), desugar(right))
//      case _ => UndefinedC()
//    }
//    case UnOpExt(s: String, exp: ExprExt) => s match {
//      case "-" => MultC(NumC(-1), desugar(exp))
//      case "not" => IfC(desugar(exp), FalseC(), TrueC())
//      case "head" => HeadC(desugar(exp))
//      case "tail" => TailC(desugar(exp))
//      case "is-nil" => IsNilC(desugar(exp))
//      case "is-list" => IsListC(desugar(exp))
//      case "box" => BoxC(desugar(exp))
//      case "unbox" => UnboxC(desugar(exp))
//      case _ => UninitializedC()
//    }
//    case IfExt(c: ExprExt, t: ExprExt, e: ExprExt) => IfC(desugar(c), desugar(t), desugar(e))
//    case ListExt(l: List[ExprExt]) => l match {
//      case Nil => NilC()
//      case NilExt() :: Nil => NilC()
//      case h :: Nil => ConsC(desugar(h), NilC())
//      case h :: t => ConsC(desugar(h), desugar(ListExt(t)))
//    }
//    case CondExt(cs) => cs match {
//      case Nil => NilC()
//      case (x, y) :: Nil => IfC(desugar(x), desugar(y), UndefinedC())
//      case (x, y) :: b => IfC(desugar(x), desugar(y), desugar(CondExt(b)))
//      case _ => UninitializedC()
//    }
//    case CondEExt(cs, e) => cs match {
//      case Nil => NilC()
//      case (x, y) :: Nil => IfC(desugar(x), desugar(y), desugar(e))
//      case (x, y) :: xs => IfC(desugar(x), desugar(y), desugar(CondEExt(xs, e)))
//      // case _ => UndefinedC()
//    }
//    case AppExt(exp: ExprExt, lExp: List[ExprExt]) => AppC(desugar(exp), lExp.map(e => desugar(e)))
//    case FdExt(lString: List[String], exp: ExprExt) => FdC(lString, desugar(exp))
//    case LetExt(lBind: List[LetBindExt], exp: ExprExt) => AppC(FdC(lBind.map(e => stringFromLetBE(e)), desugar(exp)),
//      lBind.map(e => extprissionFromLetBE(e)))
//    case RecLamExt(a: String, b: String,
//    exp: ExprExt) => AppC(cY, List(FdC(List(a), FdC(List(b), desugar(exp)))))
//    case LetRecExt(lBind: List[LetBindExt],
//    exp: ExprExt) => AppC(FdC(lBind.map(e => stringFromLetBE(e)), letRecInitBody(lBind, exp)),
//      lBind.map(e => UninitializedC()))
//
//    case SetExt(id: String, e: ExprExt) => SetC(id, desugar(e))
//    case _ => UninitializedC()
//  }
//  def cY = desugar(parse("""
//        (lambda (g) ((lambda (a) (a a))
//         (lambda (a) (g (lambda (b)
//                    ((a a) b))))))
//          """))
//
//  def stringFromLetBE(letB: LetBindExt): String = letB match {
//    case LetBindExt(s, e) => s
//    // case _ => throw CannotDesugarException("Cannot get the string from the letBindExt Desugaring")
//  }
//
//  def extprissionFromLetBE(letB: LetBindExt): ExprC = letB match {
//    case LetBindExt(s, e) => desugar(e)
//    // case _ => throw CannotDesugarException("Cannot get the expression from the letBindExt Desugaring")
//  }
//
//  def letRecInitBody(list: List[LetBindExt], e: ExprExt): ExprC = list match {
//    case Nil => desugar(e)
//    case LetBindExt(a, b) :: Nil => SeqC(SetC(a, desugar(b)), desugar(e))
//    case LetBindExt(a, b) :: tail => SeqC(SetC(a, desugar(b)), letRecInitBody(tail, e))
//  }
//}
//
//object Interp {
//  type Store = List[Cell]
//  type PointerEnvironment = List[Pointer]
//
//  // Do not remove this method. We use this for grading.
//  def interp(e: ExprC): Value = interp(e, Nil, Nil)._1
//
//  def interp(e: ExprC, nv: PointerEnvironment, st1: Store): (Value, Store) = e match {
//    case TrueC() => (BoolV(true), st1)
//    case FalseC() => (BoolV(false), st1)
//    case NumC(num: Int) => (NumV(num), st1)
//    case NilC() => (NilV(), st1)
//    case IdC(id) => {
//      val location = lookup(id, nv)
//      val res = fetch(location, st1)
//      (res, st1)
//    }
//    case ConsC(h, t) => {
//      val (hV, st2) = interp(h, nv, st1)
//      val (tV, st3) = interp(t, nv, st1)
//      (ConsV(hV, tV), st3)
//    }
//    case IfC(a, b, c) => interp(a, nv, st1) match {
//      case (BoolV(true), st2) => interp(b, nv, st2)
//      case (BoolV(false), st3) => interp(c, nv, st3)
//      case _ => throw CannotInterpException("IfC isn't right! ")
//    }
//    case MultC(a, b) => { //(NumV(inVal(interp(a, nv, st1)) * inVal(interp(b, nv, st2))), st1)
//      val (aV, st2) = interp(a, nv, st1)
//      val (bV, st3) = interp(b, nv, st2)
//      (aV, bV) match {
//        case (NumV(aV), NumV(bV)) => (NumV(aV * bV), st3)
//        case _ => throw CannotInterpException("Is not a number at * ")
//      }
//    }
//    case PlusC(a, b) => {// (NumV(inVal(interp(a, nv, st1)) + inVal(interp(b, nv, st2))), st1)
//      val (aV, st2) = interp(a, nv, st1)
//      val (bV, st3) = interp(b, nv, st2)
//      (aV, bV) match {
//        case (NumV(aV), NumV(bV)) => (NumV(aV + bV), st3)
//        case _ => throw CannotInterpException("Is not a number at + ")
//      }
//    }
//    case EqNumC(a, b) => {// (BoolV(inVal(interp(a, nv, st1)) == inVal(interp(b, nv, st2))), st1)
//      val (aV, st2) = interp(a, nv, st1)
//      val (bV, st3) = interp(b, nv, st2)
//      (aV, bV) match {
//        case (NumV(aV), NumV(bV)) => (BoolV(aV == bV), st3)
//        case _ => throw CannotInterpException("Is not a number at == ")
//      }
//    }
//    case LtC(a, b) => { //(BoolV(inVal(interp(a, nv, st1)) < inVal(interp(b, nv, st2))), st1) {
//      val (aV, st2) = interp(a, nv, st1)
//      val (bV, st3) = interp(b, nv, st2)
//      (aV, bV) match {
//        case (NumV(aV), NumV(bV)) => (BoolV(aV < bV), st3)
//        case _ => throw CannotInterpException("Is not a number at < ")
//      }
//    }
//    case HeadC(a) => {
//      val (aV, st2) = interp(a, nv, st1)
//      aV match {
//        case NilV() => throw CannotInterpException("Head Nil")
//        case ConsV(h, _) => (h, st2)
//        case _ => throw CannotInterpException("Head should never get here")
//      }
//    }
//    case TailC(a) => {
//      val (aV, st2) = interp(a, nv, st1)
//      aV match {
//        case ConsV(h, t) => (t, st2)
//        case _ => throw CannotInterpException("Head should never get here")
//      }
//    }
//
//    case IsNilC(a) => {
//      val (aV1, st2) = interp(IsListC(a), nv, st1)
//      if(aV1 == BoolV(true)) {
//        val (aV, st2) = interp(a, nv, st1)
//        aV match {
//          case NilV() => (BoolV(true), st2)
//          case _ => (BoolV(false), st2)
//        }
//      }else {
//        throw CannotInterpException("IsNil should never get here")
//      }
//    }
//    case IsListC(a) => {
//      val (aV, st2) = interp(a, nv, st1)
//      aV match {
//        case NilV() => (BoolV(true), st2)
//        case ConsV(h, t) => (BoolV(true), st2)
//        case _ => (BoolV(false), st2)
//      }
//    }
//    case FdC(l: List[String], exp: ExprC) => (PointerClosV(FdC(l, exp), nv), st1)
//
//    case AppC(exp: ExprC, a: List[ExprC]) => interp(exp, nv, st1) match {
//      case (PointerClosV(FdC(l, expF), nv1), st2) => {
//        val (nv2, st3) = pointCellTogether(l, nv, a, st2)
//        interp(expF, nv2 ::: nv1, st3)
//      }
//      case _ => throw CannotInterpException("Something went wrong at AppC")
//    }
//
//    case BoxC(box) => {
//      val (bV, st2) = interp(box, nv, st1)
//      val (newLocation, st3) = extendStore(st2, bV)
//      (BoxV(newLocation), st3)
//    }
//
//    case UnboxC(box) => {
//      val (b, st2) = interp(box, nv, st1)
//      b match {
//        case BoxV(n) => {
//          (fetch(n, st2), st2)
//        }
//        case aaa => throw CannotInterpException("Value at BoxV is not Int! " + aaa.toString)
//      }
//    }
//
//    case SetboxC(exp: ExprC, expValue: ExprC) => {
//      val(expV, st2) = interp(exp, nv, st1)
//      val (expValueN, st3) =  interp(expValue, nv, st2)
//      expV match {
//        case BoxV(location) => {
//          updatValueInStore(st3, Cell(location, expValueN))
//        }
//        case _ => throw CannotInterpException("SetBox isn't having a boxV after Interp! ")
//      }
//    }
//
//    case SetC(v: String, exp: ExprC) => {
//      val location = lookup(v, nv)
//      val (expV, st2) = interp(exp, nv, st1)
//      updatValueInStore(st2, Cell(location, expV))
//    }
//
//    case SeqC(exp: ExprC, exp1: ExprC) => {
//      val (expV, st2) = interp(exp, nv, st1)
//      val (exp1V, st3) = interp(exp1, nv, st2)
//      (exp1V, st3)
//    }
//
//
//    case UninitializedC() =>  (UninitializedV(), st1)
//    case _ => throw CannotInterpException("Is Unknown!!! ")
//  }
//
//  def inVal(v: Value): Int = v match {
//    case NumV(n) => n
//    case _ => throw NotIntegerException("Not Integer")
//  }
//
//  def getBoolV(v: Value): BoolV = v match {
//    case v1: BoolV => v1
//    case _ => throw CannotInterpException("Is not Boolean! ")
//  }
//
//  def getBoxV(v: Value): BoxV = v match {
//    case v1: BoxV => v1
//    case _ => throw CannotInterpException("Is not Box! ")
//  }
//
//  def lookup(x: String, nv: List[Pointer]): Int = nv match {
//    case Nil => throw CannotInterpException("Free not in look up exception !! ")
//    case Pointer(a, b) :: Nil => if (x == a) b else throw CannotInterpException("Free not in look up check one !! ")
//    case Pointer(a, b) :: nvTail => if (x == a) b else lookup(x, nvTail)
//    case _ => throw CannotInterpException("Unknown situation at lookup!! ")
//  }
//
//  def fetch(location: Int, st: Store): Value = {
//    if(location < st.size && location >= 0) {
//      st(location).value
//    } else{
//      throw CannotInterpException("Cannot fetch at fetch !! ")
//    }
//  }
//
//  // def extendStore(cell: Cell, st: Store): Store =  st :: List(cell) This adds new value to the store
//  // and return the location of that stored value + the new store since that scala....
//  def extendStore(st: Store, v: Value): (Int, Store) = st match {
//    case Nil => (0, Cell(0, v) :: st)
//    case _ :+ Cell(lastLocation, _) => (lastLocation + 1, st ::: List(Cell(lastLocation + 1, v)))
//    case _ => throw CannotInterpException("Something went wrong while adding new cell to the store !! ")
//  }
//
//  def updatValueInStore(st: Store, newCell: Cell): (Value, Store) = st match {
//    case Nil => throw CannotInterpException("Something went wrong while updating a cell at the Nil store!! ")
//    case Cell(oldLocation, oldValue) :: Nil => {
//      if(oldLocation ==  getIntFromCell(newCell)) {
//        (getValFromCell(newCell), newCell :: st)
//      }else{
//        throw CannotInterpException("Updating a cell at the Nil store didn't go well!! ")
//      }
//    }
//    case Cell(oldLocation, oldValue) :: tai => {
//      if(oldLocation == getIntFromCell(newCell)) {
//        (getValFromCell(newCell), newCell :: st)
//      }else{
//        updatValueInStore(tai, newCell)
//      }
//    }
//    case _ => throw CannotInterpException("SSomething went wrong while updating a cell at the store!! ")
//  }
//
//  def getIntFromCell(cell: Cell): Int = cell match {
//    case Cell(loc: Int, value: Value) => loc
//  }
//
//  def getValFromCell(cell: Cell): Value = cell match {
//    case Cell(loc: Int, value: Value) => value
//  }
//
//  def pointCellTogether(stringList: List[String], nv: List[Pointer], exps: List[ExprC], st: Store): (List[Pointer], Store) = {
//    if(stringList.size != exps.size){
//      throw CannotInterpException("Lists are not the same size at CellPointTogether!")
//    }else {
//      (stringList, exps) match {
//        case (Nil, Nil) => (Nil, st)
//        case (s :: Nil, exp :: Nil) => {
//          val (expV, st1) = interp(exp, nv, st)
//          val (locationOfExpV, newStore) = extendStore(st1, expV)
//          (Pointer(s, locationOfExpV) :: nv, newStore)
//        }
//        case (s :: tailString, exp :: tailExps) => {
//          val (expV, st1) = interp(exp, nv, st)
//          val (locationOfExpV, newStore) = extendStore(st1, expV)
//          val (nv1, stRes) = pointCellTogether(tailString, nv, tailExps, newStore)
//          (Pointer(s, locationOfExpV) :: nv, newStore)
//        }
//        case _ => throw CannotInterpException("Something went wrong at CellPointTogether!")
//      }
//    }
//  }
//
//
//  // case class Pointer(name: String, location: Int)
//  // def pointTogether(stringList: List[String], Locationlist: List[Int]): List[Pointer] = {
//  //   if(stringList.size != Locationlist.size){
//  //     throw CannotInterpException("Lists are not the same size!")
//  //   }else {
//  //     (stringList, Locationlist) match {
//  //       case (List(), List()) => List()
//  //       case (a :: Nil, b :: Nil) => List(Pointer(a, b))
//  //       case (a :: c, b :: d) => List(Pointer(a, b)) ::: pointTogether(c, d)
//  //       case _ => throw CannotInterpException("Strings are not equal to expressions cannot bind together")
//  //     }
//  //   }
//  // }
//
//  // def cellTogether(nv: List[Pointer], exps: List[ExprC]): Store = {
//  //   if(nv.size != exps.size){
//  //     throw CannotInterpException("Lists are not the same size at CellTogether!")
//  //   }else {
//  //     (nv, exps) match {
//  //       case (List(), List()) => List()
//  //       case (Pointer(aString, aInt) :: Nil, b :: Nil) => List(Cell(aInt, interp(b, Nil, Nil)._1))
//  //       case (Pointer(aString, aInt) :: c, b :: d) => List(Cell(aInt, interp(b, Nil, Nil)._1)) ::: cellTogether(c, d)
//  //       case _ => throw CannotInterpException("Ints are not equal to expressions cannot cell together")
//  //     }
//  //   }
//  // }
//}
//
