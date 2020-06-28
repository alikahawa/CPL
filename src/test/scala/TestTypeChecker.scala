package safe_interpreter

import org.scalatest.FunSuite

class TestTypeChecker extends FunSuite {

  /** ---------------- basic parse tests ---------------- **/

  //test list
  test("Test parse list normal") {
    assertResult(ListExt(NumT(), List(NumExt(2), NumExt(3)))) {
      parse("(list : Num (2 3))")
    }
  }

  test("Test parse list empty") {
    assertResult(ListExt(NumT(),List())) {
      parse("(list : Num () )")
    }
  }

  test("Test parse list too many elements + nil") {
    assertResult(ListExt(NumT(),List(NumExt(5), NumExt(3), NumExt(2), NumExt(6), NilExt(NumT()), NumExt(8)))) {
      parse("(list : Num (5 3 2 6 (nil : Num) 8))")
    }
  }

  test("Test parse boolean List") {
    assertResult(ListExt(BoolT(),List(NilExt(BoolT()), BinOpExt("num=",NumExt(2),NumExt(4)), FalseExt(), TrueExt()))) {
      parse("(list : Bool ((nil : Bool) (num= 2 4) false true))")
    }
  }


  //arithmatic
  test("parse arithmatic - plus with boolean") {
    assertResult(BinOpExt("+",TrueExt(),NumExt(10))) {
      parse("(+ true 10)")
    }
  }

  test("parse arithmatic - correct nested arithmatic") {
    assertResult(BinOpExt("+",NumExt(5), BinOpExt("-",NumExt(3),NumExt(7)))) {
      parse("(+ 5 (- 3 7))")
    }
  }

  //if
  test("parse if - correct simple if statment") {
    assertResult(IfExt(BinOpExt("num=",NumExt(5),NumExt(4)),FalseExt(),TrueExt())) {
      parse("(if (num= 5 4) false true)")
    }
  }

  test("parse if - wrong condition type") {
    assertResult(IfExt(BinOpExt("+",NumExt(5),NumExt(4)),FalseExt(),TrueExt())) {
      parse("(if (+ 5 4) false true)")
    }
  }

  test("parse if - return different type in else branch") {
    assertResult(IfExt(BinOpExt("num<",NumExt(5),NumExt(4)),NumExt(5),TrueExt())) {
      parse("(if (num< 5 4) 5 true)")
    }
  }

  //lambda
  test("pasrse lambda correct simple lambda") {
    assertResult(FdExt(List(Param("x",NumT()), Param("y",NumT())),BinOpExt("+",IdExt("x"),IdExt("y")))) {
      parse("(lambda ((x : Num) (y : Num)) (+ x y))")
    }
  }

  test("parse lambda correct with no params") {
    assertResult(FdExt(List(),TrueExt())) {
      parse("(lambda () true)")
    }
  }

  test("parse lambda correct that take function and return function") {
    assertResult(FdExt(List(Param("x", FunT(List(BoolT()), BoolT())), Param("y",NumT())),IdExt("x"))) {
      parse("(lambda ((x : ((Bool) -> Bool)) (y : Num)) x)")
    }
  }

  test("parse lambda - lambda type mismatch in body") {
    assertResult(FdExt(List(Param("x",BoolT()), Param("y",NumT())),BinOpExt("+",IdExt("x"),IdExt("y")))) {
      parse("(lambda ((x : Bool) (y : Num)) (+ x y))")
    }
  }

  //app
  test("parse app correct with two argument") {
    assertResult(AppExt(FdExt(List(Param("x",NumT()), Param("y",NumT())),BinOpExt("+",IdExt("x"),IdExt("y"))),List(NumExt(5), NumExt(2)))) {
      parse("((lambda ((x : Num) (y : Num)) (+ x y)) 5 2)")
    }
  }

  test("parse app correct with no params") {
    assertResult(AppExt(FdExt(List(),TrueExt()),List())) {
      parse("((lambda () true))")
    }
  }

  test("parse app - pass incorrect argument") {
    assertResult(AppExt(FdExt(List(Param("x",NumT()), Param("y",NumT())),BinOpExt("+",IdExt("x"),IdExt("y"))),List(TrueExt(), NumExt(2)))) {
      parse("((lambda ((x : Num) (y : Num)) (+ x y)) true 2)")
    }
  }

  test("parse app - pass too many argument") {
    assertResult(AppExt(FdExt(List(Param("x",NumT())),BinOpExt("+",IdExt("x"),NumExt(5))),List(NumExt(2), NumExt(5)))) {
      parse("((lambda ((x : Num)) (+ x 5)) 2 5)")
    }
  }

  //set
  test("parse - set simple") {
    assertResult(FdExt(List(),BinOpExt("seq",SetExt("x",IdExt("y")),FalseExt()))) {
      parse("(lambda () (seq (set x y) false))")
    }
  }

  //rec-lam
  test("factorial using rsc-lam") {
    assertResult(RecLamExt("factorial",NumT(),NumT(),"x",IfExt(BinOpExt("num=",IdExt("x"),NumExt(1)),NumExt(1),BinOpExt("*",IdExt("x"),AppExt(IdExt("factorial"),List(BinOpExt("-",IdExt("x"),NumExt(1)))))))) {
      parse(
        """
        (
          rec-lam (factorial : Num -> Num)
                  (x)
                  (if (num= x 1)
                      1
                      (* x (factorial (- x 1))))
        )
        """
      )
    }
  }

  //letrec
  test("parse - letrec  apply to function in binder") {
    assertResult(LetRecExt(List(LetRecBindExt("fun",FunT(List(NumT()),NumT()),FdExt(List(Param("x",NumT())),IdExt("x")))),AppExt(IdExt("fun"),List(NumExt(1))))) {
      parse("""
        (
          letrec (
                  ( (fun : ((Num) -> Num)) (lambda ((x : Num)) x) )
                 )
          (fun 1)
        )
      """)
    }
  }

  //let
  test("parse let - lambda param shadow with correct type") {
    assertResult(LetExt(List(LetBindExt("x",NumExt(5))),AppExt(FdExt(List(Param("x",BoolT()), Param("y",NumT())),BinOpExt("and",IdExt("x"),FalseExt())),List(TrueExt(), NumExt(5))))) {
      parse("""
              (let(
                    (x 5)
                  )
                  (
                    (lambda ((x : Bool) (y : Num)) (and x false))
                    true
                    5
                  )
              )
            """)
    }
  }

  /** ------------------- Type Checker Tests ------------------------ **/
  test("Verify correct type checking behavior") {
    assertResult(NumT()) {
      typeOf("5")
    }
  }

  test("Catch erroneous type checking behavior") {
    intercept[TypeException] {
      typeOf("x")
    }
  }

  test("Verify correct interp behavior") {
    assertResult(NumV(3)) {
      safeInterp("(+ 1 2)")
    }
  }

  // ----------------------- arithmetic -----------------------
  test("check arithmetic - plus with boolean") {
    intercept[TypeException] {
      typeOf("(+ true 10)")
    }
  }

  test("check arithmetic - correct nested arithmatic") {
    assertResult(NumT()) {
      typeOf("(+ 5 (- 3 7))")
    }
  }

  test("check arithmetic - plus with list") {
    intercept[TypeException] {
      typeOf("(- 5 (list : Num (1 2 3)))")
    }
  }

  test("check arithmetic - plus with lambda (Num -> Num)") {
    intercept[TypeException] {
      typeOf("(+ 10 (* (lambda ((x : Num)) x) 5))")
    }
  }

  test("check arithmetic - plus with app return list") {
    intercept[TypeException] {
      typeOf("""(+ 10 (
                        (lambda ((x : (List Num))) x)
                        (list : Num (1 2 3))
                      )
                )
            """)
    }
  }

  //----------------------- arithmatic boolean -----------------------
  test("check arithmetic - num= with boolean") {
    intercept[TypeException] {
      typeOf("(num= 5 false)")
    }
  }

  test("check arithmetic - num> with boolean") {
    intercept[TypeException] {
      typeOf("(num> 5 false)")
    }
  }

  test("check arithmetic - num< with boolean") {
    intercept[TypeException] {
      typeOf("(num< 5 false)")
    }
  }

  test("check arithmetic - num> with nested num=") {
    intercept[TypeException] {
      typeOf("(num< 5 (num= 5 7))")
    }
  }

  test("check arithmetic - num= with is-nil") {
    intercept[TypeException] {
      typeOf("""(num= 0
                      (is-nil (cons 5 (nil : Num))))""")
    }
  }

  test("check arithmetic correct and with is-nil") {
    assertResult(BoolT()) {
      typeOf("""(and true
                      (is-nil (cons 5 (nil : Num))))""")
    }
  }

  test("check arithmetic correct or with let") {
    assertResult(BoolT()) {
      typeOf("""(or false
                    (let ((x 15) (y 68)) (num> x y)))
      """)
    }
  }

  test("check arithmetic - and with is-nil and number") {
    intercept[TypeException] {
      typeOf("""(and 0
                      (is-nil (cons 5 (nil : Num))))""")
    }
  }

  // ----------------------- list -----------------------
  test("check list - correct list using cons") {
    assertResult(ListT(NumT())) {
      typeOf("(cons 5 (cons 10 (nil : Num)))")
    }
  }

  test("check list - heterogeneous using list") {
    intercept[TypeException] {
      typeOf("(list : Num (50 true 3 false))")
    }
  }

  test("check list - heterogeneous using cons") {
    intercept[TypeException] {
      typeOf("(cons 5 (cons true (nil : Bool)))")
    }
  }

  test("check list - nil wrong type using cons") {
    intercept[TypeException] {
      typeOf("(cons 5 (cons 5 (nil : Bool)))")
    }
  }

  test("check list - cons without nil") {
    intercept[TypeException] {
      typeOf("(cons 5 (cons 5 6))")
    }
  }

  test("check list - heterogeneous list 3") {
    intercept[TypeException] {
      typeOf("(list : Bool (1 2 3))")
    }
  }

  test("check list correct head") {
    assertResult(BoolT()) {
      typeOf("(head (list : Bool (true false)))")
    }
  }

  test("interp - head with if as list member - complex") {
    assertResult(NumV(7)) {
      safeInterp("(head (list : Num ((if (num< -7 0) (- -7) -7))))")
    }
  }

  // ----------------------- if -----------------------
  test("check if - correct simple if statement") {
    assertResult(BoolT()) {
      typeOf("(if (num= 5 4) false true)")
    }
  }

  test("check if - wrong condition type") {
    intercept[TypeException] {
      typeOf("(if (+ 5 4) false true)")
    }
  }

  test("check if - return different type in else branch") {
    intercept[TypeException] {
      typeOf("(if (num< 5 4) 5 true)")
    }
  }

  // ----------------------- lambda -----------------------
  test("check lambda correct simple lambda") {
    assertResult(FunT(List(NumT(), NumT()), NumT())) {
      typeOf("(lambda ((x : Num) (y : Num)) (+ x y))")
    }
  }

  test("check lambda correct with no params") {
    assertResult(FunT(List(), BoolT())) {
      typeOf("(lambda () true)")
    }
  }

  test("check lambda correct that take function and return function") {
    assertResult(FunT(List(
      FunT(List(BoolT()), BoolT()),
      NumT()
    ),
      FunT(List(BoolT()), BoolT()))) {
      typeOf("(lambda ((x : ((Bool) -> Bool)) (y : Num)) x)")
    }
  }

  test("check lambda - lambda type mismatch in body") {
    intercept[TypeException] {
      typeOf("(lambda ((x : Bool) (y : Num)) (+ x y))")
    }
  }

  // ----------------------- app -----------------------
  test("check app correct with two argument") {
    assertResult(NumT()) {
      typeOf("((lambda ((x : Num) (y : Num)) (+ x y)) 5 2)")
    }
  }

  test("check app correct with no params") {
    assertResult(BoolT()) {
      typeOf("((lambda () true))")
    }
  }

  test("check app - pass incorrect argument") {
    intercept[TypeException] {
      typeOf("((lambda ((x : Num) (y : Num)) (+ x y)) true 2)")
    }
  }

  test("check app - pass too many argument") {
    intercept[TypeException] {
      typeOf("((lambda ((x : Num)) (+ x 5)) 2 5)")
    }
  }

  test("check app pass function as parameter that apply the passed function to its second arg") {
    assertResult(NumT()) {
      typeOf("((lambda ((f : ((Num) -> Num))  (x : Num)) (f x))  (lambda ((x : Num)) (* x x)) 2)")
    }
  }

  // ----------------------- let -----------------------
  test("check let exception in body") {
    intercept[TypeException] {
      typeOf("(let ((x true)) (+ x 5))")
    }
  }

  test("check let correct with arithmatic") {
    assertResult(NumT()) {
      typeOf("(let ((x 5) (y 7)) (+ x y))")
    }
  }

  test("check let exception and for number") {
    intercept[TypeException] {
      typeOf("(let ((x 5) (y 7)) (and x y))")
    }
  }

  test("check  checking let"){
    assertResult(NumT()){
      typeOf("(let ((x 1)) (let ((x x)) x))")
    }
  }

  test("check let exception using nested let") {
    intercept[TypeException] {
      typeOf("""(+ 6
                   (let ((x 1))
                         (let ((x false))
                                x)
                         )
                )
              """)
    }
  }

  test("check let - lambda param shadow with correct type ") {
    assertResult(BoolT()) {
      typeOf("""
              (
                (let(
                      (x 5)
                    )
                    (
                      lambda ((x : Bool) (y : Num)) (and x false)
                    )
                )
                true
                5
              )
            """)
    }
  }

  test("check let correct with lambda and name capture"){
    assertResult(NumT()) {
      typeOf("""(let (
                        (y 0)
                        (app (lambda (
                                      (f : ((Num) -> Num))
                                      (y : Num)
                                     )
                                     (f y)))
                      )
                      (app (lambda ((x : Num)) (+ y x)) 2)
                )
              """)
    }
  }

  test("check nested let return Num in {or} expr - exception"){
    intercept[TypeException]{
      typeOf("(or false (let ((x 1) (y 2)) (+ x y)))")
    }
  }

  test("check nested let return Bool in {or} expr - correct"){
    assertResult(BoolT()){
      typeOf("(or false (let ((x 1) (y 2)) (num> x y)))")
    }
  }

  test("check let with nested lambda that takes different args type"){
    intercept[TypeException]{
      typeOf("(let ((x 15) (y 50)) ((lambda ((x : Bool) (y : Bool)) (and x y) ) x y))")
    }
  }

  test("check let with nested lambda that takes same args type"){
    assertResult(BoolT()){
      typeOf("(let ((x false) (y true)) ((lambda ((x : Bool) (y : Bool)) (and x y) ) x y))")
    }
  }

  test("interp - let shadowing simple"){
    assertResult(NumV(5)){
      safeInterp("(let ((x 1)) (let ((x 5)) x))")
    }
  }

  test("interp - box, setbox, unbox with let and boolean arithmetic") {
    assertResult(BoolV(true)) {
      interp("(let ((x (box false)) (y (box true))) (or (setbox x (unbox y)) (setbox y false) ))")
    }
  }

  test("check let binder in independent scope - free name {x}"){
    intercept[TypeException] {
      typeOf("(let ((x 15) (fun (lambda ((y : Num)) (* x y)))) (fun 5))")
    }
  }

  // --------------------- rec-lam -----------------------
  test("check type rec-lam simple"){
    assertResult(NumT()){
      typeOf("((rec-lam  (fun : Num -> Num) (x) (fun x) )  5)")
    }
  }

  test("check rec-lam application using wrong argument"){
    intercept[TypeException] {
      typeOf("((rec-lam  (f : Num -> Num) (x) (f x)) true)")
    }
  }

  // ----------------------- seq -----------------------
  test("check seq correct with let") {
    assertResult(BoolT()) {
      typeOf("""
              (seq 5 false)
            """)
    }
  }

  test("interp - seq with let and box"){
    assertResult(NumV(1)){
      safeInterp("(let ((x (box 0))) (seq (setbox x 1) (unbox x)))")
    }
  }

  /**
   * Think about this for a while,
   * a lot of bad behavior will now throw a
   * TypeException instead of InterpException!
   * What things can still actually cause an
   * exception on runtime, without causing a
   * TypeException?
   *
   * There will not be many cases!
   * This relates to the concept of type soundness.
   */
  test("Catch interp list-related exception - get the head of nil") {
    intercept[InterpException] {
      safeInterp("((lambda ((ls : (List Num))) (head ls)) (nil : Num))")
    }
  }

  test("Catch interp list-related exception - get the tail of nil") {
    intercept[InterpException] {
      safeInterp("((lambda ((ls : (List Num))) (tail ls)) (nil : Num))")
    }
  }

  test("interp - letrec  apply to function in binder") {
    assertResult(NumV(1)) {
      safeInterp("""
        (
          letrec (
                  ( (fun : ((Num) -> Num)) (lambda ((x : Num)) x) )
                 )
          (fun 1)
        )
      """)
    }
  }

  // ----------------------- box -----------------------
  test("check type box correct"){
    assertResult(RefT(NumT())){
      typeOf("(box 1)")
    }
  }

  test("check type unbox inside let"){
    assertResult(NumT()){
      typeOf("(let ((x (box 5))) (unbox x) )")
    }
  }

  test("interp box nested"){
    assertResult(NumV(0)){
      safeInterp("(let ((x (box 15)) (y (box 0))) (setbox x (unbox y)))")
    }
  }

  test("check box nested wrong type"){
    intercept[TypeException]{
      safeInterp("(let ((x (box 15)) (y (box false))) (setbox x (unbox y)))")
    }
  }

  /**
   * Interp Pair
   */
  test("interp - fst and pair simple"){
    assertResult(NumV(26)){
      safeInterp("(fst (pair 26 2))")
    }
  }

  test("interp - fst and pair complex") {
    assertResult(BoolV(true)) {
      safeInterp(
        """(fst (pair true
                      (pair 15
                            (lambda ( (x : Bool) ) x)
                        )
                  )
              )""")
    }
  }

  test("check type - snd with nested fst and pair") {
    intercept[TypeException] {
      safeInterp(
        """(snd (fst (pair true
                          (pair 15
                                (lambda ( (x : Bool) ) x)
                            )
                      )
                  )
             )""")
    }
  }


  /**
   * Additional tests ---------------
   */

  // ----------------------- list -----------------------
  test("interp head with cons") {
    assertResult(NumV(5)) {
      interp("(head (cons 5 (cons 10 (nil : Num))))")
    }
  }

  test("interp making list with same type"){
    assertResult(NumV(2)){
      interp("(head (tail (list : Num (1 2 3))))")
    }
  }

  test("interp is-nil with list"){
    assertResult(BoolV(false)){
      interp("(is-nil (list : Num (1 2 3)))")
    }
  }

  test("check is-nil with none-list value"){
    intercept[TypeException] {
      interp("(is-nil true)")
    }
  }


  test("interp is-nil with none list correct"){
    assertResult(BoolV(false)){
      interp("(is-nil (cons 1 (nil : Num)))")
    }
  }

  test("check nil wrong type"){
    intercept[TypeException]{
      interp("(cons 5 (cons 5 (nil : Bool)))")
    }
  }

  test("check head with list with wrong type"){
    intercept[TypeException]{
      interp("(head (list : Num (1 2 3 false 2)))")
    }
  }

  test("catch interp exception head with nil"){
    intercept[InterpException] {
      safeInterp("(head (nil : Num ))")
    }
  }


  test("interp - function that takes a list and returns its head"){
    assertResult(NumV(1)) {
      safeInterp("(let ( (x (lambda ((l : (List Num) ))  (head l))) (y (list : Num (1 2 3))) ) (x y)  )")
    }
  }

  test("interp - box with number comparison num=") {
    assertResult(BoolV(false)) {
      safeInterp("(let ((x (box 5)) (y (box 2))) (num= (setbox x (unbox y)) (setbox y 3) ))")
    }
  }

  /**
   * Helpers
   */
  def parse(expr: String): ExprExt = Parser.parse(expr)
  def desugar(expr: String): ExprC = Desugar.desugar(Parser.parse(expr))
  def typeOf(e: String): Type = typeOf(e, Nil)
  def typeOf(e: String, nv: List[TBind]): Type = TypeChecker.typeOf(parse(e), nv)
  def safeInterp(s: String): Value = SafeInterp.interp(parse(s))
  def interp(s: String): Value = safeInterp(s)
}
