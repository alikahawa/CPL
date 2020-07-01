package safe_interpreter

import safe_interpreter.TypeChecker.typeOf

object TypeChecker {
  type TEnvironment = List[TBind]

  def typeOf(e: ExprExt): Type = typeOf(e, Nil)

  def typeOf(e: ExprExt, nv: TEnvironment): Type = e match {

  case NumExt(_) => NumT()
  case TrueExt() => BoolT()
  case FalseExt() => BoolT()

  //    case BinOpExt(s, l, r) => s match {
  //      // arithmetic numbers
  //      case "+" | "*" | "-" | "num=" | "num<" | "num>" =>
  //        typeOf(l, nv) match {
  //          case NumT() =>
  //            typeOf(r, nv) match {
  //              case NumT() =>
  //                if (List("num=", "num<", "num>") contains s) BoolT()
  //                else NumT()
  //
  //              case other =>
  //                throw TypeExc("right side of plus isn't a NumT: " + other)
  //            }
  //          case other =>
  //            throw TypeExc("left side of plus isn't a NumT: " + other)
  //        }
  //
  //      // arithmetic boolean
  //      case "and" | "or" =>
  //        typeOf(l, nv) match {
  //          case BoolT() =>
  //            typeOf(r, nv) match {
  //              case BoolT() => BoolT()
  //              case other =>
  //                throw TypeExc("right side of plus isn't a BoolT: " + other)
  //            }
  //          case other =>
  //            throw TypeExc("left side of plus isn't a BoolT: " + other)
  //        }
  //
  //      case "seq" =>
  //        typeOf(l, nv)   //check in case there is a type error in left branch
  //        typeOf(r, nv)   //return the type of the right branch
  //
  //      case "cons" =>
  //        typeOf(r, nv) match {
  //          // This will check if it ends with nil! ------------
  //          case ListT(expTy) =>
  //            val leftTy = typeOf(l, nv)
  //            if(leftTy == expTy) ListT(expTy)
  //            else throw TypeExc("cons not matching type, leftType: " + leftTy + " - rightType: " + ListT(expTy))
  //
  //          case other =>
  //            throw TypeExc("cons doesn't end with Nil, but found: " + other)
  //        }
  //
  //      case "setbox" =>
  //        val (leftTy, rightTy) = (typeOf(l, nv), typeOf(r, nv))
  //        if (leftTy == RefT(rightTy)) rightTy
  //        else throw TypeExc("set element in box with different type, boxType: " + leftTy + " - element type: " + rightTy)
  //
  //      case "pair" =>
  //        PairT(typeOf(l, nv), typeOf(r, nv))
  //
  //      case other =>
  //        throw TypeExc("Binary Operation cases exhausted: " + other)
  //    }

  //    case UnOpExt(s, e) =>
  //      val ty = typeOf(e, nv)
  //      s match {
  //      case "-" =>
  //        if (ty == NumT()) NumT()
  //        else throw TypeExc("NAN, cannot use {-} on non-number: " + ty + " - " + e)
  //
  //      case "not" =>
  //        if (ty == BoolT()) BoolT()
  //        else throw TypeExc("Cannot use {not} on non-boolean value: " + ty + " - " + e)
  //
  //      //list operations
  //      case "is-nil"  =>
  //        ty match {
  //          case ListT(_) => BoolT()
  //          case _ =>
  //            throw TypeExc("Cannot use {is-nil} on non-list values: " + ty + " - " + e)
  //        }
  //
  //      case "head" =>
  //        ty match {
  //          case ListT(listTy) => listTy
  //          case _ =>
  //            throw TypeExc("Cannot use {head} on non-list values: " + ty + " - " + e)
  //        }
  //
  //      case "tail" =>
  //        ty match {
  //          case ListT(_) => ty
  //          case _ =>
  //            throw TypeExc("Cannot use {tail} on non-list values: " + ty + " - " + e)
  //        }
  //
  //      case "box" =>
  //        RefT(ty)
  //
  //      case "unbox" =>
  //        ty match {
  //          case RefT(boxTy) => boxTy
  //          case _ =>
  //            throw TypeExc("Cannot use {unbox} on non-box values: " + ty + " - " + e)
  //        }
  //
  //      // pair operation
  //      case "fst" | "snd" =>
  //        ty match {
  //          case PairT(fTy, sTy) =>
  //            if(s == "fst") fTy
  //            else sTy
  //
  //          case _ =>
  //            throw TypeExc("Cannot use {fst or snd} on non-pair values: " + ty + " - " + e)
  //        }
  //
  //      case other =>
  //        throw TypeExc("Unary Operation cases exhausted: " + other)
  //    }
    case BinOpExt("+" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => NumT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp +")
    }
    case BinOpExt("-" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => NumT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp -")
    }
    case BinOpExt("*" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => NumT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp *")
    }
    case BinOpExt("num=" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => BoolT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp num=")
    }
    case BinOpExt("num<" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => BoolT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp num<")
    }
    case BinOpExt("num>" ,l ,r) => typeOf(l, nv) match {
      case NumT() => typeOf(r, nv) match {
        case NumT() => BoolT()
        case _ => throw TypeExceptionE("Something went wrong on right")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp num>")
    }

    case BinOpExt("or" ,l ,r) => typeOf(l, nv) match {
      case BoolT() => typeOf(r, nv) match {
        case BoolT() => BoolT()
        case _ => throw TypeExceptionE("Something went wrong at the right side BinaryOp or")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp or")
    }
    case BinOpExt("and" ,l ,r) => typeOf(l, nv) match {
      case BoolT() => typeOf(r, nv) match {
        case BoolT() => BoolT()
        case _ => throw TypeExceptionE("Something went wrong at the right side BinaryOp and")
      }
      case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp and")
    }
    case BinOpExt("cons", l, r) => typeOf(r, nv) match {
      case ListT(listE) => {
        val lVal = typeOf(l, nv)
        if(lVal == listE) ListT(listE) else throw TypeExceptionE("Something went wrong at left side not same type cons")
        // lVal match {
        //   case listE => ListT(listE)
        //   case _ => throw TypeExceptionE("Something went wrong at left side not same type cons")
        // }
      }
      case _ => throw TypeExceptionE("Something went wrong at right side BinaryOp cons no Nil!!!")
    }
    case BinOpExt("seq" ,l ,r) => {
      val typeLeft = typeOf(l, nv)
      typeOf(r, nv)
    }
    case BinOpExt("setbox" ,l ,r) => {
      val (lVal, rVal) = (typeOf(l, nv), typeOf(r, nv))
//      if(RefT(rVal) == lVal) rVal else throw TypeExceptionE("Something went wrong at left side BinaryOp set")
      lVal match {
        case RefT(rVal) => rVal
        case _ => throw TypeExceptionE("Something went wrong at left side BinaryOp set")
      }
    }
    case BinOpExt("pair", l, r) => PairT(typeOf(l, nv), typeOf(r, nv))
    case UnOpExt("-", e) => typeOf(e, nv) match {
      case NumT() => NumT()
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions -")
    }
    case UnOpExt("not", e) => typeOf(e, nv) match {
      case BoolT() => BoolT()
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions not")
    }
    case UnOpExt("head", e) => typeOf(e, nv) match {
      case ListT(list) => list
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions head")
    }
    case UnOpExt("tail", e) => val eT = typeOf(e, nv)
      eT match {
        case ListT(_) => eT
        case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions tail")
      }
    case UnOpExt("is-nil", e) => typeOf(e, nv) match {
      case ListT(_) => BoolT()
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions is-nil")
    }
    case UnOpExt("box", e) => RefT(typeOf(e, nv))
    case UnOpExt("unbox", e) => typeOf(e, nv) match {
      case RefT(boxType) => boxType
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions unbox")
    }
    case UnOpExt("fst", e) => typeOf(e, nv) match {
      case PairT(l, r) => l
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions fst")
    }
    case UnOpExt("snd", e) => typeOf(e, nv) match {
      case PairT(l, r) => r
      case _ => throw TypeExceptionE("Something went wrong at e UnaryOpertaions snd")
    }
    // if
    case IfExt(c, t, e) =>
      typeOf(c, nv) match {
        case BoolT() =>
          val (trueTy, elseTy) = (typeOf(t, nv), typeOf(e, nv))
          if (trueTy == elseTy) trueTy
          else throw TypeExc("if statements true and else branches aren't the same type: " + trueTy + " else : " + elseTy)

        case other =>
          throw TypeExc("condition of the if statement isn't boolean " + other)
      }

    //list
    case NilExt(ty) => ListT(ty)
    case ListExt(listTy, es) =>
      if (es.forall( typeOf(_) == listTy )) ListT(listTy)
      else throw TypeExc("list elements doesn't match list type: " + listTy + " elements: " + es)

    // lambda
    case IdExt(c) => lookup(c, nv)
  //    case FdExt(params, body) =>
  //      val paramsTy = params map { case Param(_, ty) => ty }
  //      val nv_f = params map { case Param(name, ty) => TBind(name, ty) }
  //      FunT(paramsTy, typeOf(body, nv_f ::: nv))
  //
  //    //app
  //    case AppExt(f, args) =>
  //      typeOf(f, nv) match {
  //        case FunT(paramTy, retTy) =>
  //          if (checkTypes(paramTy, args, nv)) return retTy
  //          else throw TypeExc("args' type doesn't match params' type (or not equal number of param and args)")
  //
  //        case other =>
  //          throw TypeExc("apply to non function: " + other)
  //      }
  //
  //    //rec-lam
  //    case RecLamExt(name, paramTy, retTy, param, body) =>
  //      val funTy = FunT(List(paramTy), retTy)
  //      val bodyTy = typeOf(body, (TBind(param, paramTy) :: TBind(name, funTy) :: Nil) ::: nv)
  //      if(bodyTy == retTy) funTy
  //      else throw TypeExc("body return type doesn't match the retTy: " + retTy + " - bodyTy: " + bodyTy)
  //
  //    //let
  //    case LetExt(binds, body) =>
  //      val nv_let = binds map { case LetBindExt(name, value) => TBind(name, typeOf(value, nv))  }
  //      typeOf(body, nv_let ::: nv)
  //
  //    //letrec
  //    case LetRecExt(binds, body) =>
  //      //update the environment first
  //      val nv_let_new = binds.map { case LetRecBindExt(name, ty, e) => TBind(name, ty) } ::: nv
  //
  //      //then check if all binds values have same type as specified
  //      if (binds forall { case LetRecBindExt(_, ty, e) => ty == typeOf(e, nv_let_new) })
  //        typeOf(body, nv_let_new)
  //      else
  //        throw TypeExc("Cannot set variable with different type: "  + " elements type: " )
  //
  //    //set
  //    case SetExt(id, e) =>
  //      val (idTy, eTy) = (lookup(id, nv), typeOf(e))
  //      if(idTy == eTy) eTy
  //      else throw TypeExc("Cannot set variable with different type: " + idTy + " elements type: " + eTy)
    case SetExt(id, e) => {
      val (idT, eT) = (lookup(id, nv) ,typeOf(e))
      if(idT == eT) idT else throw TypeExceptionE("Something went wrong at setExt e and id not same type")
      // idT match {
      //   case eT => idT
      //   case _ => throw TypeExceptionE("Something went wrong at setExt e and id not same type")
      // }
    }

    case FdExt(param, e) => {
      val nv1 = param.map(element => paraToTBind(element))
      FunT(param.map(element => typeFromPara(element)), typeOf(e, nv1 ::: nv))
    }

    case AppExt(e, listArgs) => typeOf(e, nv) match {
      case FunT(lpT, eF) => {
        if(checkTypes(lpT, listArgs, nv)) eF else throw TypeExceptionE("Something went wrong at AppExt not all same type")
      }
      case _ => throw TypeExceptionE("Something went wrong at AppExt e type")
    }

    case LetExt(lBinds, e) => {
      val nv1 = lBinds.map(element => letBindTbind(element, nv))
      typeOf(e, nv1 ::: nv)
    }

    case RecLamExt(name: String, paramTy: Type, retTy: Type,
    param: String, body: ExprExt) => {
      // remmember factorial from first excersice ;)
      val fType = FunT(List(paramTy), retTy)
      val bodyType = typeOf(body, List(TBind(param, paramTy), TBind(name, fType)) ::: nv)
      if(retTy == bodyType) fType else throw TypeExceptionE("Something went wrong at ReclamExt not same type")
    }

    case LetRecExt(binds: List[LetRecBindExt], body: ExprExt) => {
      val nv1 = binds.map(element => letBinRecTBind(element)) ::: nv
      if(binds.forall{case LetRecBindExt(_, t, e) => {typeOf(e, nv1) == t}}) typeOf(body, nv1)
      else throw TypeExceptionE("Something went wrong at LetRecExt not same type")
    }
    case other =>
      throw TypeExc("finial case Type Checker! " + other)
  }

  def letBindTbind(letBind: LetBindExt, nv: TEnvironment): TBind = letBind match {
    case LetBindExt(name: String, value: ExprExt) => TBind(name, typeOf(value, nv))
  }

  def letBinRecTBind(l: LetRecBindExt): TBind = l match {
    case LetRecBindExt(name: String, ty: Type, _) => TBind(name, ty)
  }

  def typeFromPara(p : Param): Type = p match {
    case Param(name: String, ty: Type) => ty
  }

  def paraToTBind(p: Param): TBind = p match {
    case Param(name: String, ty: Type) => TBind(name, ty)
  }

  def checkTypes(paramTy: List[Type], args: List[ExprExt], nv: TEnvironment): Boolean = {
    if(paramTy.size != args.size) false
    else {
      (paramTy, args) match {
        case (Nil, Nil) => true
        case (pt :: Nil, e :: Nil) => if(pt == typeOf(e, nv)) true else false
        case (pt :: pTail, e :: eTail) => if(pt == typeOf(e, nv)) checkTypes(pTail, eTail, nv) else false
        case _ => false
      }
    }
  }

  def lookup(id: String, nv: TEnvironment): Type = nv match {
    case Nil => throw TypeExceptionE("Enviroment is empty at lookup!")
    case TBind(name, v) :: Nil => if(name == id) v else throw TypeExceptionE("Lookup couldn't find id zero level!")
    case TBind(name, v) :: ta => if(name == id) v else lookup(id, ta)
    case _ => throw TypeExceptionE("Lookup couldn't find id in enviroment!")
  }
//  /* lookup function */
//  def lookup(s: String, nv : TEnvironment): Type =
//    nv match {
//      case Nil =>
//        throw TypeExc("can't find this bind in the environment " + s)
//
//      case TBind(name, ty) :: tail =>
//        if (name == s) return ty
//        else lookup(s, tail)
//    }
//
//  /* return true if the type of params match the type of args */
//  @scala.annotation.tailrec
//  def typeMatchArgs(paramsTy: List[Type], args: List[ExprExt], nv: TEnvironment): Boolean =
//    (paramsTy, args) match {
//      case (Nil, Nil) => true
//      case (paramTy :: restParamsTy, arg :: restArgs) if paramTy == typeOf(arg, nv) =>
//        typeMatchArgs(restParamsTy, restArgs, nv)
//
//      case _ => false
//    }
}
