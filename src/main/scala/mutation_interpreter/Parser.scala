package mutation_interpreter

object Parser {
  def parse(str: String): ExprExt = parse(Reader.read(str))

  /** parser helper functions ------------------------------ **/
  def parseList(list: List[SExpr]): List[ExprExt] = list match {
    case Nil            => Nil
    case head :: tail   => parse(head) :: parseList(tail)
    case _              => throw ParseExc("The case: { " + list + " } isn't define - parserList()")
  }

  def parseCond(list: List[SExpr]): List[(ExprExt, ExprExt)] = list match {
    case Nil            => Nil
    case head :: tail   => parseCondBranch(head) :: parseCond(tail)
    case _              => throw ParseExc("The case: { " + list + " } isn't define - parserCond()")
  }

  def parseCondBranch(sexpr: SExpr): (ExprExt, ExprExt) = sexpr match {
    case SList(e1 :: e2 :: Nil) => (parse(e1), parse(e2))
    case _                      => throw ParseExc("The case: { " + sexpr + " } isn't define - parserCondBranch()")
  }

  /* return the string if not reserved, otherwise raise an exception */
  def isReserved(s: String): String = s match {
    case "+" | "*" | "-" | "and" | "or" | "num=" | "num<" | "num>" | "cons"
         | "not" | "head" | "tail" | "is-nil" | "is-list"
         | "list" | "nil" | "if" | "lambda" | "let" | "true" | "false"
         | "rec-lam" | "letrec" | "box" | "unbox" | "setbox" | "seq"
    => throw ParseExc("Reserved word can't be used as identifier!")

    case otherId => otherId
  }

  /* return the string if unique, otherwise raise an exception */
  def isUnique(s: String, list: List[SExpr]): String = (s, list) match {
    case (s, Nil) => s
    case (s, SSym(head) :: tail) if s != head =>
      isUnique(s, tail)

    case (s, SList(SSym(name) :: value :: Nil) :: tail) if s != name =>
      isUnique(s, tail)

    case _ => throw ParseExc("The identifier: { " + s + " } isn't unique - isUnique()")
  }

  def parseArgs(list: List[SExpr]): List[String] =  list match {  //check for reserved words!!!!!!
    case Nil => Nil
    case SSym(s) :: tail =>
      isUnique(isReserved(s), tail) :: parseArgs(tail)
    //OR
    // if(tail map { case (SSym(str)) => str
    //               case _ => throw ParseExc("The identifier: { " + s + " } isn't unique - isUnique()") }
    //         contains s)
    //   throw ParseExc("The identifier: { " + s + " } isn't unique - isUnique()")
    // else
    //   isReserved(s) :: parseArgs(tail)

    case _ => throw ParseExc("The case: { " + list + " } isn't SSym() - parseArgs()")
  }

  def parseBinders(list: List[SExpr]): List[LetBindExt] = list match {
    case Nil => Nil
    case SList(SSym(name) :: value :: Nil) :: tail =>
      LetBindExt(isUnique(isReserved(name), tail), parse(value)) :: parseBinders(tail)
    case _ =>
      throw ParseExc("The case: { " + list + " } isn't SSym() - parseBinders()")
  }

  /** main parse ----------------------------- **/
  def parse(sexpr: SExpr): ExprExt = sexpr match {
    case SNum(n) => NumExt(n)
    case SSym(s) =>
      s match {
        case "true"   => TrueExt()
        case "false"  => FalseExt()
        case "nil"    => NilExt()
        case x        => IdExt(isReserved(x))
        // case _        => throw ParseExc("The symbol: { " + s + " } isn't define - main parser SSym(s)")
      }

    //list
    case SList(SSym(s) :: tail) if s == "list" =>
      ListExt(parseList(tail))

    //if
    case SList(SSym(s) :: condition :: doTrue :: doElse :: Nil) if s == "if" =>
      IfExt(parse(condition), parse(doTrue), parse(doElse))

    //cond
    case SList(SSym(s) :: tail) if s == "cond" =>
      tail match {
        case Nil  => throw ParseExc("Empty cond!")

        case SList(SSym(s) :: elseBranch :: Nil) :: Nil if s == "else" =>
          throw ParseExc("Cond with just else branch!")

        case branches :+ SList(SSym(s) :: elseBranch :: Nil) if s == "else" =>
          CondEExt(parseCond(branches), parse(elseBranch))

        case _ =>
          CondExt(parseCond(tail))
      }

    //lambda
    case SList(SSym("lambda") :: SList(args) :: tail :: Nil) =>
      FdExt(parseArgs(args), parse(tail))
    // throw ParseExc("The operation { "+ SList(SSym(s) :: SList(arg) :: tail :: Nil) +" } can't be found")

    //let
    case SList(SSym("let") :: SList(binders) :: tail :: Nil) if binders != Nil =>
      LetExt(parseBinders(binders), parse(tail))

    //letrec
    case SList(SSym("letrec") :: SList(binders) :: tail :: Nil) =>
      if(binders != Nil) LetRecExt(parseBinders(binders), parse(tail))
      else throw ParseExc("letrec with no binders!")

    //rec-lambda
    case SList(SSym("rec-lam") :: SSym(name) :: SList(SSym(param) :: Nil) :: body :: Nil) =>
      RecLamExt(isReserved(name), isReserved(param), parse(body))

    //set
    case SList(SSym("set") :: SSym(id) :: tail :: Nil) =>
      SetExt(id, parse(tail))

    //unary operation
    case SList(SSym(s) :: t1 :: Nil)
      if List("-", "not", "head", "tail", "is-nil", "is-list", "box", "unbox") contains s =>
      UnOpExt(s, parse(t1))

    //binary operation
    case SList(SSym(s) :: t1 :: t2 :: Nil)
      if List("+", "*", "-", "and", "or", "num=", "num<", "num>", "cons", "setbox", "seq") contains s =>
      BinOpExt(s, parse(t1), parse(t2))

    //app
    case SList(head :: tail) =>
      AppExt(parse(head), parseList(tail))

    case _ =>
      throw ParseExc("Error! final case in parser")
  }

  /**
   * Write a 0-argument function that sets the identifier `x` to the value
   * of the identifier `y` and returns false.
   * You can assume `x` and `y` are in scope.
   */
  val variable =
    """
      (lambda () (seq (set x y) false))
    """

  /**
   * Write a 0-argument function that sets the value of the box identified by `x`
   * to the value in the box identified by `y` and returns false.
   * You can assume `x` and `y` are in scope.
   */
  val box =
    """
      (lambda ()
              (seq (setbox x (unbox y))
                  false
              )
          )
    """

  /**
   * Implement two mutually-recursive single-argument functions, `even` and `odd`.
   *
   * Your `even` function should return `true` if the input is `0`,
   * `false` if the input is `1`, and otherwise use a recursive call of `odd`
   * to check whether the number is odd.
   *
   * Your `odd` function should return `false` if the input is `0`,
   * `true` if the input is `1`, and otherwise use a recursive call of `even`
   * to check whether the number is even.
   */
  val evenAndOdd =
    """
      (letrec ((even (lambda (n)
                            (if (num= n 0)
                              true
                              (if (num= n 1)
                                false
                                (odd (- n 1))))))
               (odd (lambda (n)
                            (if (num= n 1)
                              true
                              (if (num= n 0)
                                false
                                (even (- n 1)))
                                    )))
              )
        (list even odd))
    """

}
