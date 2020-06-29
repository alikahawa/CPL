package SubstitutionBasedInterprete


object Parser {

  def parse(str: String): ExprExt = parse(Reader.read(str))

  val binOps = Set("+", "*", "-", "and", "or", "num=", "num<", "num>", "cons")
  val unOps = Set("-", "not", "head", "tail", "is-nil", "is-list")
  val reservedWords: Set[String] = binOps ++ unOps ++ Set("list", "nil", "if", "lambda", "let", "true", "false")

  def checkId(s: String): String = {
    if(reservedWords.contains(s)){throw CannotParseException("ID Reserved word") }
    else s
  }

  def zip(xs:List[SExpr]): List[(ExprExt, ExprExt)] = xs match {
    case SList(SSym("else") :: els :: Nil) :: Nil => Nil
    case SList(h :: t :: Nil) :: Nil => (parse(h), parse(t)) :: Nil
    case SList((h :: t :: Nil)) :: branch => (parse(h), parse(t)) :: zip(branch)
    case _ => throw CannotParseException("No no no!")
  }

  def getIds(l: List[SExpr]): List[String] = l match {
    // case Nil =>  throw CannotParseException("IDs are not there it is Nil case => No")
    case List() => throw CannotParseException("IDs are not there Empty list => No")
    case a :: Nil => parse(a) match {
      case IdExt(id) => List(id.toString)
      case aaaaaaaa => throw CannotParseException("IDs are not there " + aaaaaaaa.toString)
    }
    case a :: b => getIds(List(a)) ::: getIds(b)
    //    case SSym(he) :: ta => if(idDublicatesCheck(((checkId(he)) :: getIds(ta)))){
    //      ((checkId(he)) :: getIds(ta))
    //    } else {
    //      throw CannotParseException("IDs shadowing problem")
    //    }
    case _ => throw CannotParseException("IDs Reserved word")
  }

  def LetBindXtList(l: List[SExpr]): List[LetBindExt] = l match {
    case List() => throw CannotParseException("LetBind an Empty List")
    case a :: Nil => a match {
      case SList(List(SSym(id), exp)) => if(!reservedWords.contains(id)) {List(LetBindExt(id, parse(exp)))
      }else{throw CannotParseException("Cannot let bindding, id is reserved word")}
      case _ => throw CannotParseException("Cannot let bindding, one element and not valid")
    }
    //    case SList(a :: b :: Nil) :: Nil => List(LetBindExt(getId(parse(a)), parse(b)))
    //    case SList(a :: b :: Nil) :: c => if(LetBindXDublicatesCheck(LetBindExt(getId(parse(a)), parse(b)) :: LetBindXtList(c))){
    //      LetBindExt(getId(parse(a)), parse(b)) :: LetBindXtList(c)
    //    } else {
    //      throw CannotParseException("LetBind unhandled case")
    //    }
    case a :: b => LetBindXtList(List(a)) ::: LetBindXtList(b)
    case _ => throw CannotParseException("LetBind unhandled case")
  }

  def LetBindXDublicatesCheck(list: List[LetBindExt]): Boolean = {
    val resList = list.map(e => stringFromLetBE(e))
    if(resList.distinct.size == list.size){
      true
    }else{
      throw CannotParseException("Let shadow or lambda shadow")
    }
  }

  def idDublicatesCheck(list: List[String]): List[String] = {
    if(list.distinct.size == list.size){
      list
    }else{
      throw CannotParseException("Id dubblicate")
    }
  }

  def stringFromLetBE(letB: LetBindExt): String = letB match {
    case LetBindExt(s, e) => s
    case _ => throw CannotParseException("Cannot get the string from the letBindExt Desugaring")
  }

  def parse(sexpr: SExpr): ExprExt = sexpr match {
    case SSym("true") => TrueExt()
    case SSym("false") => FalseExt()
    case SSym("nil") => NilExt()
    case SNum(n) => NumExt(n)
    case SSym(a) => IdExt(checkId(a))
    case SList(SSym("if") :: h) => h match {
      case (a :: b :: c :: Nil) => IfExt(parse(a), parse(b), parse(c))
      case _ => throw CannotParseException("Cannot be parsed If wrong")
    }

    case SList(SSym("lambda") :: SList(a) :: b :: Nil ) => a match {
      // case Nil => throw CannotParseException("Cannot be parsed nothing in Lambda?!")
      case List() => FdExt(List(), parse(b))
      case h :: t => FdExt(idDublicatesCheck(getIds(a)), parse(b))
      // case b :: Nil => throw CannotParseException("Cannot be parsed only one thing in Lambda?!")
      // case SList(h) :: t :: Nil => h match {
      //  case Nil => FdExt(List(), parse(t))
      //  case _ => FdExt(getIds(h), parse(t))
      //  }
      case aaaa => throw CannotParseException("Cannot be parsed in Lambda?! "  + aaaa.toString)
    }
    case SList(SSym("let") :: SList(a) :: b :: Nil) => {
      if(b == SSym("nil")){
        throw CannotParseException("Cannot be parsed Let is not correct No Expression!")
      } else{ a match {
        // case (_, SSym("nil")) => throw CannotParseException("Cannot be parsed Let is not correct No Expression!")
        case h :: t => {
          val l = LetBindXtList(a)
          if(LetBindXDublicatesCheck(l)){LetExt(l, parse(b))}else{
            throw CannotParseException("Cannot be parsed Let is not correct Shadowing problem!")
          }
        }
        //      case SList(h) => parse(b) match {
        //        case NilExt() => throw CannotParseException("Cannot be parsed Let is not correct No Expression")
        //        case _ => LetExt(LetBindXtList(h), parse(b))
        //      }
        case _ => throw CannotParseException("Cannot be parsed Let is not correct")
      }}}

    case SList(SSym("list") :: a ) => a match {
      case Nil => ListExt(Nil)
      case h :: Nil => ListExt(parse(h) :: Nil)
      case h :: t => ListExt(a.map(e => parse(e)))
      case _ => throw CannotParseException("List is not good")
    }
    case SList(SSym("cond") :: branchs) => branchs match {
      case Nil => throw CannotParseException("Nil expression!")
      case SList(SSym("else") :: el :: Nil) :: Nil => throw CannotParseException("Not valid expression!")
      case h :: t => branchs.last match {
        case SList(SSym("else") :: el :: Nil) => CondEExt(zip(branchs), parse(el))
        case _ => CondExt(zip(branchs))
      }
      case _ => throw CannotParseException("Not valid expression!")
    }
    case SList(SSym("+") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("+", NumExt(a), NumExt(b))
    case SList(SSym("*") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("*", NumExt(a), NumExt(b))
    case SList(SSym("-") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("-", NumExt(a), NumExt(b))
    case SList(SSym("num=") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("num=", NumExt(a), NumExt(b))
    case SList(SSym("num<") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("num<", NumExt(a), NumExt(b))
    case SList(SSym("num>") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("num>", NumExt(a), NumExt(b))
    case SList(SSym("and") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("and", NumExt(a), NumExt(b))
    case SList(SSym("or") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("or", NumExt(a), NumExt(b))
    case SList(SSym("cons") :: SNum(a) :: SNum(b) :: Nil) => BinOpExt("cons", NumExt(a), NumExt(b))

    case SList(SSym("-") :: a :: Nil) => UnOpExt("-", parse(a))
    case SList(SSym("not") :: a :: Nil) => UnOpExt("not", parse(a))
    case SList(SSym("head") :: a :: Nil) => UnOpExt("head", parse(a))
    case SList(SSym("tail") :: a :: Nil) => UnOpExt("tail", parse(a))
    case SList(SSym("is-nil") :: a :: Nil) => UnOpExt("is-nil", parse(a))
    case SList(SSym("is-list") :: a :: Nil) => UnOpExt("is-list", parse(a))


    case SList(SSym("+") :: ys :: xs :: Nil ) => BinOpExt("+", parse(ys), parse(xs))
    case SList((SSym("*") :: ys :: xs :: Nil)) => BinOpExt("*", parse(ys), parse(xs))
    case SList((SSym("-") :: ys :: xs :: Nil)) => BinOpExt("-", parse(ys), parse(xs))
    case SList((SSym("num=") :: ys :: xs :: Nil)) => BinOpExt("num=", parse(ys), parse(xs))
    case SList((SSym("num<") :: ys :: xs :: Nil)) => BinOpExt("num<", parse(ys), parse(xs))
    case SList((SSym("num>") :: ys :: xs :: Nil)) => BinOpExt("num>", parse(ys), parse(xs))
    case SList((SSym("and") :: ys :: xs :: Nil)) => BinOpExt("and", parse(ys), parse(xs))
    case SList((SSym("or") :: ys :: xs :: Nil)) => BinOpExt("or", parse(ys), parse(xs))
    case SList((SSym("cons") :: ys :: xs :: Nil)) => BinOpExt("cons", parse(ys), parse(xs))


    case SList(a :: b) => b match {
      case Nil => AppExt(parse(a), List())
      case h :: Nil => AppExt(parse(a), List(parse(h)))
      case h :: t => AppExt(parse(a), b.map(e => parse(e)))
      case _ => throw CannotParseException("Cannot be parsed since it is not good app")
    }

    case abcelse => throw CannotParseException("Cannot be parsed since it is not implemented" + abcelse.toString)
  }

//  def parse(str: String): ExprExt = parse(Reader.read(str))
//
//  def parse(sexpr: SExpr): ExprExt = {
//    sexpr match {
//      case SNum(num) => NumExt(num)
//      case SSym("true") => TrueExt()
//      case SSym("false") => FalseExt()
//      case SSym("nil") => NilExt()
//      case SList(list) => {
//        list match {
//          case Nil => throw CustomParseException("Empty Expression List")
//          case SSym("if") :: c :: t :: e :: Nil => IfExt(parse(c), parse(t), parse(e)) //?
//          case SSym("cond") :: branches => {
//
//            branches match {
//              case Nil => throw CustomParseException("Nothing after Cond")
//
//              case SList(c :: t :: Nil) :: e => {
//
//                branches.last match {
//                  case SList(SSym("else") :: e :: Nil) => { // check is only the else branch
//                    makeCondEExt(makeCondEExtList(branches), parse(e))
//                  }
//                  case SList(c :: t :: Nil) => CondExt(makeCondExtList(branches))
//                  case _ => throw CustomParseException("Wrong Branch format")
//                }
//              }
//              case _ => throw CustomParseException("Wrong use of cond")
//            }
//          }
//
//          case SSym("list") :: list => {
//            list match {
//              case Nil => ListExt(Nil)
//              case a :: Nil => ListExt(parse(a) :: Nil)
//              case _ => ListExt(list.map(e => parse(e)))
//            }
//          }
//
//          case SSym("lambda") :: SList(i) :: body :: Nil => {
//            //            FdExt(getIdentifierList(i), parse(body))
//
//            val idenlist = getIdentifierList(i)
//
//            if (idenlist.distinct.size == idenlist.size) {
//              FdExt(idenlist, parse(body))
//            } else throw CustomParseException("Repeated identifier name in a function definition")
//          }
//
//          case SSym("let") :: SList(bindings) :: e :: Nil => {
//            bindings match {
//              //              case l :: _ => LetExt(bindings.map((d: SExpr) => createLetBindExt(d)), parse(e))
//              case l :: _ => {
//                val letext = LetExt(bindings.map({ (d: SExpr) => createLetBindExt(d) }), parse(e))
//
//
//                if (letDeubCheck(letext.binds)) {
//                  letext
//                }
//                else throw CustomParseException("Duplicated Let name")
//              }
//              case _ => throw CustomParseException("no bindings")
//            }
//          }
//
//          case SSym("-") :: y => y match {
//            case a :: Nil => UnOpExt("-", parse(a))
//            case a :: b :: Nil => BinOpExt("-", parse(a), parse((b)))
//            case _ => throw CustomParseException("Wrong op use")
//          }
//
//          case SSym("not") :: a :: Nil => UnOpExt("not", parse(a))
//
//          case SSym("+") :: a :: b :: Nil => BinOpExt("+", parse(a), parse((b)))
//
//          case SSym("*") :: a :: b :: Nil => BinOpExt("*", parse(a), parse((b)))
//
//          case SSym("and") :: a :: b :: Nil => BinOpExt("and", parse(a), parse((b)))
//
//          case SSym("or") :: a :: b :: Nil => BinOpExt("or", parse(a), parse((b)))
//
//          case SSym("num=") :: a :: b :: Nil => BinOpExt("num=", parse(a), parse((b)))
//
//          case SSym("num<") :: a :: b :: Nil => BinOpExt("num<", parse(a), parse((b)))
//
//          case SSym("num>") :: a :: b :: Nil => BinOpExt("num>", parse(a), parse((b)))
//
//          case SSym("cons") :: a :: b :: Nil => BinOpExt("cons", parse(a), parse(b))
//
//
//          case SSym("head") :: a :: Nil => UnOpExt("head", parse(a))
//
//          case SSym("tail") :: a :: Nil => UnOpExt("tail", parse((a)))
//
//
//          case SSym("is-nil") :: a :: Nil => UnOpExt("is-nil", parse((a)))
//
//
//          case SSym("is-list") :: a :: Nil => UnOpExt("is-list", parse((a)))
//
//
//          case fun :: iden => AppExt(parse(fun), iden.map(e => parse(e)))
//
//
//          case _ => throw CustomParseException("Wrong Operation format")
//        }
//      }
//      case SSym(s) => {
//        if (ExprExt.reservedWords.contains(s)) throw CustomParseException("not allowed to use this name: " + s)
//        else IdExt(s)
//      }
//      case _ => throw CustomParseException("Wrong Syntax")
//
//    }
//
//  }
//
//
//  def createLetBindExt(s: SExpr): LetBindExt = {
//    s match {
//      case SList(y) => y match {
//        case a :: List(b) => LetBindExt(getStringFromIdExt(a), parse(b))
//        case _ => throw CustomParseException("not a name")
//      }
//      case _ => throw CustomParseException("wrong let format")
//    }
//  }
//
//  def getStringFromIdExt(s: SExpr): String = parse(s) match {
//    case IdExt(a) => a
//    case _ => throw CustomParseException("Not IdExt")
//  }
//
//  def creatLetBindExtList(bindings: List[SExpr]): List[LetBindExt] = {
//    bindings match {
//      case Nil => throw CustomParseException("empty identifier list")
//      case SSym(s) :: e :: Nil => {
//        if (!ExprExt.reservedWords.contains(s)) LetBindExt(s, parse(e)) :: Nil
//        else throw CustomParseException("name not allowed")
//      }
//      case SList(SSym(s) :: e :: Nil) :: r => {
//        if (!ExprExt.reservedWords.contains(s)) LetBindExt(s, parse(e)) :: creatLetBindExtList(r)
//        else throw CustomParseException("name not allowed")
//      }
//      case _ => throw CustomParseException("wrong bindings")
//    }
//  }
//
//  def getIdentifierList(list: List[SExpr]): List[String] = {
//    list match {
//      case Nil => Nil
//      case SSym(s) :: e => {
//        if (ExprExt.reservedWords.contains(s)) throw CustomParseException("not allowed to use this name: " + s)
//        else s :: getIdentifierList(e)
//      }
//      case _ => throw CustomParseException("wrong identifier list")
//    }
//  }
//
//  def makeCondEExtList(list: List[SExpr]): List[(ExprExt, ExprExt)] = {
//    list match {
//      case SList(SSym("else") :: e :: Nil) :: Nil => Nil
//      case SList(c :: t :: Nil) :: b => (parse(c), parse(t)) :: makeCondEExtList(b)
//      case _ => throw CustomParseException("Wrong Branch format")
//    }
//  }
//
//  def makeCondEExt(list: List[(ExprExt, ExprExt)], exprExt: ExprExt): CondEExt = {
//    list match {
//      case Nil => throw CustomParseException("no branches before the else branch")
//      case _ => CondEExt(list, exprExt)
//
//    }
//  }
//
//  def makeCondExtList(list: List[SExpr]): List[(ExprExt, ExprExt)] = {
//    list match {
//      case Nil => throw CustomParseException("Wrong Branch format")
//      case SList(c :: t :: Nil) :: Nil => (parse(c), parse(t)) :: Nil
//      case SList(c :: t :: Nil) :: b => (parse(c), parse(t)) :: makeCondExtList(b)
//      case _ => throw CustomParseException("Wrong Branch format")
//
//    }
//  }
//
//  def letDeubCheck(binds: List[LetBindExt]): Boolean = {
//    val names = binds.map({ case LetBindExt(n, v) => n })
//    names.distinct.size == names.size
//  }

}
