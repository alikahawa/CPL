//package mutation_interpreter
//
//import org.scalatest.FunSuite
//
// class testMutation extends FunSuite {
//
//
//
//
//  // test("Verify correct implementation set let and lambda all together") {
//  //   assertResult(NumV(0)) {
//  //     interp(desugar(parse("""
//  //       (let ((x 1) (y 2) (z 3))
//  //             ((lambda (a b c) (* (+ a b) c)))
//  //               x (seq (seq (set x 0) (set z 0) y ) z)
//  //                 )
//  //           """)), Nil)
//  //   }
//  // }
//
//  test("Verify correct implementation function set exception not in scope ") {
//    intercept[InterpException]  {
//      interp(desugar(parse("""
//          (let ((y 1)) (set x y))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set exception 10 to y ") {
//    intercept[ParseException]  {
//      interp(desugar(parse("""
//          (let ((y 1)) (set y (if)))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set exception nested arguments ") {
//    intercept[InterpException]  {
//      interp(desugar(parse("""
//          (let ((y 1)) (set x (set y (set z 10))))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("""
//          (let ((x 1)) (set x 10))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set0") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("""
//          (let ((x 1) (y 10)) (set x y))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set10") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("""
//          (let ((x 1) (y 10)) (set x (set y (set y (set y 10)))))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function set exception") {
//    intercept[InterpException]  {
//      interp(desugar(parse("""
//          (let ((y 1)) (set x 10))
//      """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Imperative Counter let") {
//    assertResult(NumV(2)) {
//      interp(desugar(parse("""
//      ( let ((counter
//                (lambda ()
//                    (let ((n (box 0)))
//                      (list
//                        (lambda () (setbox n (+ (unbox n) 1 )))
//                        (lambda () (setbox n 0)))))
//                        ))
//              (let ((c (counter)))
//                  (seq ((head c)) ((head c)) )
//                  ))
//          """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Imperative Fibonaccie letrec inside let") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse(""" (let ((a 0) (b 1) (sum 0))
//  (letrec
//    ((fib
//      (lambda (n)
//        (if (or (num= n 0) (num= n 1))
//          sum
//          (seq (set sum (+ a b))
//          (seq (set a b)
//          (seq (set b sum)
//              (fib (- n 1)))))))))
//      (fib 5)))
//          """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Imperative letrec sum") {
//    assertResult(NumV(6)) {
//      interp(desugar(parse(""" (letrec ((sum
//           (lambda (n)
//             (if (num= n 0)
//               0
//               (+ n (sum (- n 1)))))))
//            (sum 3))
//          """)), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Rec-Lam sum of 10") {
//    assertResult(NumV(55)) {
//      interp(desugar(parse("""((rec-lam sum (n)
//        (if (num= n 0)
//          0
//           (+ n (sum (- n 1)))))
//          10)""")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Lambda doubquad") {
//    assertResult(NumV(40)) {
//      interp(desugar(parse("(let ((doub (lambda (a) (+ a a) ))) (let ((quad (lambda (a) (doub (doub a))))) (quad 10)))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really") {
//    assertResult(NumV(6)) {
//      interp(desugar(parse("( (lambda (y) (+ y 5)) 1 )")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple reall0y") {
//    intercept[InterpException] {
//      interp(desugar(parse("((lambda (y) (6))1)")), Nil)
//    }
//  }
//
//
//  test("Verify correct implementation function NoException works Lambda simple really1") {
//    assertResult(NumV(0)) {
//      interp(desugar(parse("( (lambda (y) (if (num= y 1) (- y 1) (0))) 1 )")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really01") {
//    assertResult(NumV(9)) {
//      interp(desugar(parse("((lambda (y) (if (num= 1 1) (- 10 1) (0)))1)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really but with two params") {
//    assertResult(NumV(3)) {
//      interp(desugar(parse("((lambda (y x) (+ y x))2 1)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really02") {
//    assertResult(NumV(12)) {
//      interp(desugar(parse("((lambda (y) (+ y (* y 5)))2)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really but with good argument") {
//    assertResult(NumV(-12)) {
//      interp(desugar(parse("((lambda (x) (* 1 (- 10 (+ 20 x))))2)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda simple really001") {
//    intercept[InterpException] {
//      interp(desugar(parse("((lambda (y) (if ((lambda (x) (if (num= x 1) (true) (false))) 1) (- 10 1) (0)))1)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Lambda") {
//    assertResult(NumV(2)) {
//      interp(desugar(parse("( (lambda (y a) (y a))  (lambda (b) (+ b b)) 1 )")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function NoException works Let simple really") {
//    assertResult(NumV(15)) {
//      interp(desugar(parse("(let ((y 10)) (+ y 5))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function InterException Exception Let0001111") {
//    intercept[InterpException] {
//      interp(desugar(parse("(let ((y(s 10))) (+ s 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function ParseException Exception Let0 same s") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let ((s 5 )(s 10)) (+ s 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function ParseException Exception Let0 free binded") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let () (+ s 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function ParseException Exception Let0 reserved s") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let ((- 5 )) (+ 3 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function ParseException Exception Let0 only one s") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let (s 5 ) (+ s 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Lambda beast and un beatable") {
//    assertResult(NumV(720)) {
//      interp(desugar(parse(
//        """
//                  ((lambda (you)
//                      (let ((func (lambda (self me)
//                        (if (num= me 0)
//                          1
//                          (* me (self self (- me 1)))
//                        )
//                      )))
//                  (func func you))) 6)
//                  """
//      )), Nil)
//    }
//  }
//
//  test("Verify correct implementation function bound identifier and incorrectly") {
//    intercept[ParseException] {//(LetExt(List(LetBindExt("f",FdExt(List("self", "x"),FdExt(List("x"),BinOpExt("+",IdExt("x"),IdExt("self")))))),AppExt(IdExt("f"),List(NumExt(1), NumExt(2))))) {
//      parse("(let ((f f (lambda (self x) (lambda (x) (+ x self))))) (f 1 2))")
//    }
//  }
//
//  test("Verify correct implementation function bound identifier") {
//    assertResult(LetExt(List(LetBindExt("f",FdExt(List("self", "x"),FdExt(List("x"),BinOpExt("+",IdExt("x"),IdExt("self")))))),AppExt(IdExt("f"),List(NumExt(1), NumExt(2))))) {
//      parse("(let ((f (lambda (self x) (lambda (x) (+ x self))))) (f 1 2))")
//    }
//  }
//
//  test("Verify correct implementation function Exception lambda") {
//    assertResult(AppExt(AppExt(FdExt(List("f"),FdExt(List("x"),AppExt(IdExt("f"),List(IdExt("x"))))),List(FdExt(List("y"),BinOpExt("+",IdExt("y"),IdExt("x"))))),List(NumExt(21)))) {
//      parse("(((lambda (f) (lambda (x) (f x))) (lambda (y) (+ y x))) 21)")
//    }
//  }
//
//  test("Verify correct implementation function Parsing Exception Let001") {
//    intercept[ParseException] {
//      parse("(let ((s 10)) ())")
//    }
//  }
//
//  test("Verify correct implementation function Parsing Exception lambda001") {
//    intercept[ParseException] {
//      parse("( lambda (c c) (+ c 8))")
//    }
//  }
//
//  test("Verify correct implementation function Parsing Exception Let011") {
//    intercept[ParseException] {
//      parse("(let (s 10) ())")
//    }
//  }
//
//  test("Verify correct implementation function Parsing Exception Let011 ReserevedWord") {
//    intercept[ParseException] {
//      parse("(let ((if 10)) ())")
//    }
//  }
//
//
//  test("Verify correct implementation function Parsing Exception Let0112") {
//    intercept[ParseException] {
//      parse("(let ((f 10 i)) (+ 5 f))")
//    }
//  }
//
//  test("Verify correct implementation function Parsing Exception Let0121") {
//    intercept[ParseException] {
//      parse("(let () (+ a 10))")
//    }
//  }
//
//  test("Verify correct implementation function Exception lambda0") {
//    intercept[InterpException] {
//      interp(desugar(parse("(((lambda (f) (lambda (x) (f x))) (lambda (y) (+ y x))) 21)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Exception lambda01") {
//    intercept[ParseException] {
//      interp(desugar(parse("(((lambda (1) (lambda () (1 x))) (lambda (y) (+ y x))) 21)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function Exception lambda001789") {
//    assertResult(NumV(30)) {
//      interp(desugar(parse("(((lambda (x) (lambda (x) (+ 10 x)))10) 20)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function bound identifier1") {
//    assertResult(AppExt(IdExt("f"), List(NumExt(1), NumExt(2)))) {
//      parse("(f 1 2)")
//    }
//  }
//
//  test("Verify correct implementation function bound identifier12") {
//    assertResult(AppExt(IdExt("f"), List())) {
//      parse("(f )")
//    }
//  }
//
//  test("Verify correct implementation Lambda interp3453") {
//    intercept[InterpException] {
//      interp(desugar(parse(" (let ((f (lambda (self x) (if (num= x 0) 0 (+ 1 (self sjjelf (- x 1)))   ) ))) (f f 10))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Lambda interp Shadowing 53423") {
//    assertResult(NumV(7)) {
//      interp(desugar(parse("(((lambda (x y) (lambda (x) (+ x y))) 2 3) 4)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation parse lambda shadowing001837254") {
//    intercept[ParseException] {
//      parse("(lambda (a a) (+ 7 a))")
//    }
//  }
//
//  test("Verify correct implementation parse lambda no arguments53453") {
//    assertResult(FdExt((Nil), (BinOpExt("+", NumExt(7), NumExt(7))))) {
//      parse("(lambda () (+ 7 7))")
//    }
//  }
//
//  test("Verify correct implementation parse lambda reserved word") {
//    intercept[ParseException] {
//      parse("(lambda (true a) (+ 7 a))")
//    }
//  }
//
//
//  test("Verify correct implementation Let2 exception") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let ((f (lambda  (if (num= x 0) 0 (+ 1 (self self (- x 1)))))))())")), Nil)
//    }
//  }
//  test("Verify correct implementation Let2 exception1") {
//    intercept[ParseException] {
//      interp(desugar(parse("(let ((x 1 2)) ((f (lambda  (if (num= x 0) 0 (+ 1 (self self (- x 1))))))))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation listl12312567") {
//    assertResult(
//      ConsV(NilV(), ConsV(NumV(0), ConsV(NumV(1), ConsV(NilV(), ConsV(NumV(3), ConsV(NumV(4), NilV()))))))
//    ) {
//      interp(desugar(parse("(list nil 0 1 nil 3 4)")), Nil)
//    }
//  }
//
//  test("Verify correct implementationPlus1211212121") {
//    assertResult(NumV(6)) {
//      interp("(+ 1 5)")
//    }
//  }
//
//  test("Verify correct implementation Let simple ParseAnd keep going001") {
//    assertResult(LetExt(List(LetBindExt("x",NumExt(21))),BinOpExt("+",IdExt("x"),IdExt("x")))) {
//      parse("""
//      (let ((x 21)) (+ x x))
//      """)
//    }
//  }
//
//  test("Verify correct implementation Let simple ParseAnd keep going002") {
//    assertResult(AppC(FdC(List("x"),PlusC(IdC("x"),IdC("x"))),List(NumC(21)))) {
//      desugar(parse("""
//      (let ((x 21)) (+ x x))
//      """))
//    }
//  }
//
//  test("Verify correct implementation parse lambda shadowing") {
//    intercept[ParseException] {
//      parse("(lambda (a a) (+ 7 a))")
//    }
//  }
//  test("Verify correct implementation Lambda Shadowing001") {
//    assertResult(AppExt(AppExt(FdExt(List("x"),FdExt(List("x"),IdExt("x"))),List(NumExt(1))),List(NumExt(2)))) {
//      parse("(((lambda (x) (lambda (x) x)) 1) 2)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Shadowing002") {
//    assertResult(AppC(AppC(FdC(List("x"),FdC(List("x"),IdC("x"))),List(NumC(1))),List(NumC(2)))) {
//      desugar(parse("(((lambda (x) (lambda (x) x)) 1) 2)"))
//    }
//  }
//
//  test("Verify correct implementation parse lambda no arguments") {
//    assertResult(FdExt((Nil), (BinOpExt("+", NumExt(7), NumExt(7))))) {
//      parse("(lambda () (+ 7 7))")
//    }
//  }
//
//  test("Verify correct implementation desugaring let") {
//    assertResult(
//      AppC(	FdC( List("x","y") , MultC(IdC("x"), IdC("y"))) , List( NumC(10), NumC(21))  )
//    ) {
//      desugar(LetExt(List(LetBindExt("x", NumExt(10)), LetBindExt("y", NumExt(21))),BinOpExt("*",IdExt("x"), IdExt("y")) ))
//    }
//  }
//
//  test("Verify correct implementationPlus1111111") {
//    assertResult(NumV(6)) {
//      interp("(+ 2 (- 5 1))")
//    }
//  }
//
//  test("Verify correct implementation Let") {
//    intercept[ParseException] {
//      interp("(let 1 5)")
//    }
//  }
//
//  ////////////////////////// Let Parse Exception ////////////////////////////////////
//  test("Verify correct implementation Let simple ParseException") {
//    intercept[ParseException] {
//      interp("""
//      (let (x 21) (+ x x))
//      """)
//    }
//  }
//
//  test("Verify correct implementation Let simple ParseAnd keep going") {
//    assertResult(NumV(42)) {
//      interp("""
//      (let ((x 21)) (+ x x))
//      """)
//    }
//  }
//
//  ////////////////////////// Let Parse Shadow ////////////////////////////////////
//  test("Verify correct implementation Let Shadowing") {
//    assertResult(NumV(2)) {
//      interp("(let ((x 1)) (let ((x 2)) x))")
//    }
//  }
//
//  test("Verify correct implementation Let Shadowing1") {
//    assertResult(NumV(1)) {
//      interp("(let ((x 1)) (let ((x x)) x))")
//    }
//  }
//
//  ////////////////////////// Let Recursive self ////////////////////////////////////
//  test("Verify correct implementation Let1") {
//    assertResult(NumV(10)) {
//      interp("(let ((f (lambda (self x) (if (num= x 0) 0 (+ 1 (self self (- x 1)))))))(f f 10))")
//    }
//  }
//
//
//  test("Verify correct implementation Let2") {
//    intercept[ParseException] {
//      interp("(let ((f (lambda  (if (num= x 0) 0 (+ 1 (self self (- x 1)))))))(f f 10))")
//    }
//  }
//
//  test("Verify correct implementation Let3") {
//    intercept[ParseException] {
//      interp("(let (double x) (+ x x) (double 5))")
//    }
//  }
//
//
//  ////////////////////////// Let Twice ////////////////////////////////////
//  test("Verify correct implementation Let Twice") {
//    intercept[InterpException] {
//      interp("""
//      (let ((twice (lambda (f) (lambda (x) (f (f x))))))
//                (let ((quad (twice (lambda (y) (+ y x)))))
//                        (quad 1)))
//            """)
//    }
//  }
//
//  test("Verify correct implementation Let TwiceCorrect") {
//    assertResult(NumV(4)) {
//      interp("""
//      (let ((twice (lambda (f) (lambda (x) (f (f x))))))
//                (let ((quad (twice (lambda (y) (+ y y)))))
//                        (quad 1)))
//            """)
//    }
//  }
//
//  ////////////////////////// Lambda Silly ////////////////////////////////////
//  test("Verify correct implementation Lambda Silly") {
//    assertResult(NumV(2)) {
//      interp("(((lambda (x) (lambda (x) x)) 1) 2)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Silly01") {
//    intercept[InterpException] {
//      interp("((lambda (x) (lambda (x y) (+ y x))) 1 2)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Silly11") {
//    assertResult(NumV(10)) {
//      interp("((lambda (x ) (+ x x)) 5)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Not Silly anymore") {
//    intercept[InterpException] {
//      interp("((lambda (x) (lambda (y) (+ y x))) 2 5)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Not Silly anymore at all") {
//    assertResult(NumV(6)) {
//      interp("(((lambda (x y) (lambda (x) (+ y x))) 2 5) 1)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Not Silly anymore at all1") {
//    assertResult(NumV(3)) {
//      interp("(((lambda (x y) (lambda (y) (+ y x))) 2 5) 1)")
//    }
//  }
//
//  test("Verify correct implementation Lambda Not Silly anymore at all21") {
//    intercept[InterpException] {
//      interp("((lambda (x) (lambda (x) (+ x 2))) 2 5)")
//    }
//  }
//  // test("Parse set of Ids") {
//  //   assertResult(
//  //     FdExt(List("x"), ListExt(BinOpExt("+", IdExt("x"), IdExt("x")) :: Nil))
//  //   ) {
//  //     parse("(lambda (x ) (+ x x))")
//  //   }
//  // }
//
//  ////////////////////////// Lambda Silly with some movements ////////////////////////////////////
//  test("Verify correct implementation Lambda Silly1") {
//    assertResult(NumV(6)) {
//      interp("(((lambda (x) (lambda (x) (+ x (* x x)))) 1) 2)")
//    }
//  }
//
//  test("Verify correct implementation Lambda") {
//    assertResult(NumV(2)) {
//      interp(desugar(parse("((lambda (x) (+ x x))1)")), Nil)
//    }
//  }
//
//  ////////////////////////// Lambda Parse Exception ////////////////////////////////////
//  test("Verify correct implementation Lambda simple ParseException") {
//    intercept[ParseException] {
//      interp("""
//      (lambda (if 21) (+ x x))
//      """)
//    }
//  }
//
//  ////////////////////////// Lambda Interp Exception ////////////////////////////////////
//  test("Verify correct implementation Lambda simple InterpException") {
//    intercept[InterpException] {
//      interp("""
//      (doubble (lambda (x) (+ x x)))
//      """)
//    }
//  }
//  //  ((double x) (+ x x) (double 5 ))
//
//  test("Verify correct implementationasasaasaasasa") {
//    assertResult(NumV(5)) {
//      interp("5")
//    }
//  }
//
//  test("Catch erroneous parse behaviorasasasaas") {
//    intercept[ParseException] {
//      parse("()")
//    }
//  }
//
//  test("Catch erroneous interp behavior asasaas") {
//    intercept[InterpException] {
//      interp("(+ true 5)")
//    }
//  }
//
//  test("Verify correct implementation true True") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("true")), Nil)
//    }
//  }
//  test("Verify correct implementation false False") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("false")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Minus") {
//    assertResult(NumV(-1)) {
//      interp(desugar(parse("(- 2 3)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation nil Nil") {
//    assertResult(NilV()) {
//      interp(desugar(parse("nil")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Long List") {
//    assertResult(ConsV(NumV(1), ConsV( NumV(2), ConsV(NumV(3) , ConsV( NumV(4), ConsV(NumV(5), ConsV(NumV(6), ConsV(NumV(7), ConsV(NumV(8), ConsV(NumV(9), ConsV(NumV(10), NilV()))))))))))) {
//      interp(desugar(parse("(list 1 2 3 4 5 6 7 8 9 10 )")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Long List 1") {
//    assertResult(ConsV(NilV(), ConsV( NumV(6), ConsV(NumV(3) , ConsV( NumV(4), ConsV(NumV(5), ConsV(NumV(6), ConsV(NumV(7), ConsV(NumV(8), ConsV(NumV(9), ConsV(NumV(10), NilV()))))))))))) {
//      interp(desugar(parse("(list nil (+ 2 4) 3 4 5 6 7 8 9 10 )")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Long List Parse") {
//    assertResult(ListExt(List(NilExt(), BinOpExt("+",NumExt(2),NumExt(4)), NumExt(3), NumExt(4), NumExt(5), NumExt(6), NumExt(7), NumExt(8), NumExt(9), NumExt(10)))) {
//      (parse("(list nil (+ 2 4) 3 4 5 6 7 8 9 10 )"))
//    }
//  }
//
//  //   test("Verify correct implementation Long List ParseNil") {
//  //   assertResult(ListExt(ConsC(NilC(),ConsC(PlusC(NumC(2),NumC(4)),ConsC(NumC(3),ConsC(NumC(4),ConsC(NumC(5),ConsC(NumC(6),ConsC(NilC(),ConsC(NumC(8),ConsC(NumC(9),ConsC(NilC(),NilC())))))))))))) {
//  //     desugar(parse("(list nil (+ 2 4) 3 4 5 6 nil 8 9 nil )"))
//  //   }
//  // }
//
//  test("Verify correct implementation Long List 1 Desugar") {
//    assertResult(ConsC(NilC(),ConsC(PlusC(NumC(2),NumC(4)),ConsC(NumC(3),ConsC(NumC(4),ConsC(NumC(5),ConsC(NumC(6),ConsC(NumC(7),ConsC(NumC(8),ConsC(NumC(9),ConsC(NumC(10),NilC()))))))))))) {
//      desugar(parse("(list nil (+ 2 4) 3 4 5 6 7 8 9 10 )"))
//    }
//  }
//
//  test("Verify correct implementation Long List 2") {
//    assertResult(ConsV(NilV(), ConsV( NumV(6), ConsV(NumV(3) , ConsV( NumV(4), ConsV(NumV(5), ConsV(NumV(6), ConsV(NumV(7), ConsV(NumV(8), ConsV(NumV(9), ConsV(NumV(10), NilV()))))))))))) {
//      interp(desugar(parse("(list nil (+ 2 4) (if (num> 1 3) 1 3) 4 5 6 7 8 9 10 )")), List())
//    }
//  }
//
//  // test("Verify correct implementation Cond cond 10") {
//  //   assertResult(NumV(10)) {
//  //     interp(desugar(parse("(cond (false 1) (true 10))")), List())
//  //   }
//  // }
//
//  test("Verify correct implementation if If if 1") {
//    intercept[ParseException] {
//      interp(desugar(parse("(if (true 1) (false 2) (true 4) (nil))")), List())
//    }
//  }
//
//
//
//  test("Interp 5+true throws InterpException") {
//    intercept[InterpException] {
//      interp(PlusC(NumC(5), TrueC()),List())
//    }
//  }
//
//  test("Verify correct implementation") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("5")), List())
//    }
//  }
//
//  test("Verify correct implementation+") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("(+ 1 4)")), List())
//    }
//  }
//
//  test("Verify correct implementation++") {
//    assertResult(NumV(15)) {
//      interp(desugar(parse("(+ 1 (+ 4 (+ 6 4)))")), List())
//    }
//  }
//
//  test("Verify correct implementation*+-*") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("(* 5 (+ 1 (- 5 (* 1 4))))")), List())
//    }
//  }
//
//  test("Verify correct implementation+And") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("(+ 1 (and true 9))")), List())
//    }
//  }
//
//  test("Verify correct implementation+And*") {
//    assertResult(NumV(10)) {
//      interp(desugar(parse("(+ 1 (and true (* 1 9)))")), List())
//    }
//  }
//
//  test("Verify correct implementationAnd") {
//    assertResult(NumV(9)) {
//      interp(desugar(parse("(and true 9)")), List())
//    }
//  }
//
//  test("Verify correct implementationAnd0") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(and true false)")), List())
//    }
//  }
//
//  test("Verify correct implementationAnd1") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(and true true)")), List())
//    }
//  }
//
//
//  test("Verify correct implementation*") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("(* 5 1)")), List())
//    }
//  }
//
//  test("Verify correct implementation**") {
//    assertResult(NumV(60)) {
//      interp(desugar(parse("(* 5 (* 1 (* 6 (* 2 1))))")), List())
//    }
//  }
//
//  test("Verify correct implementation-") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("(- 6 1)")), List())
//    }
//  }
//
//  test("Verify correct implementation-U") {
//    assertResult(NumV(6)) {
//      interp(desugar(parse("(* -6 -1)")), List())
//    }
//  }
//
//  test("Verify correct implementation num< false") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(num< 1 0)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation num< true") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(num> 1 0)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation num< false2") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(num< 1 (+ 0 0))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation num< true2") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(num> 1 (* 10 0 ))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation num= false2") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(num= 1 (+ 0 0))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation num= true2") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(num= 0 (* 10 0 ))")), Nil)
//    }
//  }
//
//
//
//  test("Verify correct implementation NilV") {
//    assertResult(NilV()) {
//      interp(desugar(parse("nil")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Cons") {
//    assertResult(ConsV(NumV(10), NilV())) {
//      interp(desugar(parse("(cons 10 nil)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Cons0") {
//    assertResult(ConsV(NumV(10), ConsV(NumV(0), NilV()))) {
//      interp(desugar(parse("(cons 10 (cons 0 nil))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation Cons1") {
//    assertResult(ConsV(NumV(10), ConsV(NumV(0), ConsV(NumV(0), NilV())))) {
//      interp(desugar(parse("(cons 10 (cons 0 (cons 0 nil)))")), Nil)
//    }
//  }
//
//  test("is-list False") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(is-list 1)")), Nil)
//    }
//  }
//
//  test("is-list True") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(is-list nil)")), Nil)
//    }
//  }
//
//  test("is-list Exception") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(is-list (list 1 nil))")), Nil)
//    }
//  }
//
//  test("is-list True1") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(is-list (cons 10 nil))")), Nil)
//    }
//  }
//
//  test("is-nil True 1") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(is-nil nil)")), Nil)
//    }
//  }
//
//  test("is-nil False") {
//    assertResult(BoolV(false)) {
//      interp(desugar(parse("(is-nil (cons 10 nil))")), Nil)
//    }
//  }
//
//  test("is-nil Exception") {
//    intercept[InterpException] {
//      interp(desugar(parse("(is-nil 5)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation head") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("(head (cons 5 nil))")), Nil)
//    }
//  }
//
//
//  test("Verify correct implementation headEmpty") {
//    intercept[InterpException] {
//      interp(desugar(parse("(head 5)")), Nil)
//    }
//  }
//
//  test("Verify correct implementation tailSimple") {
//    assertResult(NumV(5)) {
//      interp(desugar(parse("(tail (cons 2 5))")), Nil)
//    }
//  }
//
//  // test("Verify correct implementation condExt") {
//  //   assertResult(NumV(1)) {
//  //     interp(desugar(parse("(cond ((num< 1 0) 0) ((num> 1 0) 1))")), Nil)
//  //   }
//  // }
//
//  //   test("Verify correct implementation condExtTrue") {
//  //   assertResult(BoolV(true)) {
//  //     interp(desugar(parse("(cond ((num< 1 0) 0) ((num> 1 0) true))")), Nil)
//  //   }
//  // }
//
//  // test("Verify correct implementation condEExt") {
//  //   assertResult(NumV(1)) {
//  //     interp(desugar(parse("(cond ((num< 1 0) 0) (else 1))")), Nil)
//  //   }
//  // }
//
//  // test("Verify correct implementation condEExt0") {
//  //   assertResult(NumV(1)) {
//  //     interp(desugar(parse("(cond ((num< 1 0) 0) ((num< 1 0) 0) ((num< 1 0) 0) (else 1))")), Nil)
//  //   }
//  // }
//
//  // test("Verify correct implementation condEExt1") {
//  //   assertResult(NumV(0)) {
//  //     interp(desugar(parse("(cond ((num< 1 0) 0) ((num> 1 0) 0) ((num< 1 0) 0) (else 1))")), Nil)
//  //   }
//  // }
//
//  test("Verify correct implementation condEExtComp") {
//    intercept[InterpException] {
//      interp(desugar(parse("(cond ((and true false) 3))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation condExtCompComp") {
//    intercept[InterpException] {
//      interp(desugar(parse("(cond ((and true false) 4) ((and true false) 3))")), Nil)
//    }
//  }
//
//  // test("Verify correct implementation condExtCompComp00") {
//  //   assertResult(NumV(3)) {
//  //     interp(desugar(parse("(cond ((and true false) 2) ((and true false) 3) ((and true false) 3) ((and true true) 3))")), Nil)
//  //   }
//  // }
//
//  // test("Verify correct implementation condExtCompComp1") {
//  //   assertResult(NumV(3)) {
//  //     interp(desugar(parse("(cond ((and true false) 2) ((and true false) 3) ((and true false) 3) ((or true true) 3))")), Nil)
//  //   }
//  // }
//
//  test("Verify correct implementation condEExtException") {
//    intercept[InterpException] {
//      interp(desugar(parse("(cond ((num< 1 0) 0) ((num< 1 0) 0) ((num< 1 0) 0) ((num< 1 0) 0))")), Nil)
//    }
//  }
//
//  // test("Verify correct implementation condEExtException0") {
//  //   intercept[ParseException] {
//  //     parse("(cond (nil))")
//  //   }
//  // }
//
//  test("Verify correct implementation condEExtException01") {
//    intercept[ParseException] {
//      parse("(cond (head))")
//    }
//  }
//
//  test("Verify correct implementation If") {
//    intercept[ParseException] {
//      parse("(if (head))")
//    }
//  }
//
//  test("Verify correct implementation ifExc") {
//    intercept[ParseException] {
//      interp(desugar(parse("(if (and true false) 2 )")), List())
//    }
//  }
//
//  test("Verify correct implementation if1") {
//    assertResult(BoolV(true)) {
//      interp(desugar(parse("(if (and true false) 2 (or true true))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation if12") {
//    intercept[ParseException] {
//      interp(desugar(parse("(if (and true false) 2 (if (or true false) 2))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation tailComplex") {
//    assertResult(ConsV(NumV(6), ConsV(NumV(0), ConsV(NumV(7), ConsV(NumV(8), ConsV(NumV(9), NumV(5))))))) {
//      interp(desugar(parse("(tail (cons 2 (cons 6 (cons 0 (cons 7 (cons 8 (cons 9 5)))))))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation tail") {
//    assertResult(ConsV(NumV(2), NilV())) {
//      interp(desugar(parse("(tail (cons 5 (cons 2 nil)))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation tailEmpty") {
//    intercept[InterpException] {
//      interp(desugar(parse("(tail 5)")), Nil)
//    }
//  }
//
//  test("list construction") {
//    assertResult(ConsV(NumV(1), (ConsV(NumV(2), (ConsV(NumV(3), NilV())))))) {
//      interp(desugar(parse("(list 1 2 3)")), Nil)
//    }
//  }
//
//  test("list construction12") {
//    assertResult(ConsV(NumV(1), (ConsV(NumV(2), (ConsV(NumV(3), NilV())))))) {
//      interp(desugar(parse("(list 1 2 3)")), Nil)
//    }
//  }
//
//  test("list construction00") {
//    assertResult(ConsV(BoolV(true), (ConsV(NumV(2), NilV())))) {
//      interp(desugar(parse("(list true 2)")), Nil)
//    }
//  }
//
//  test("list construction0") {
//    assertResult(ConsV(NumV(1), (ConsV(NumV(2), (ConsV(NumV(3), NilV())))))) {
//      interp(desugar(parse("(list 1 2 3)")), Nil)
//    }
//  }
//
//  test("Head the tail of the list") {
//    assertResult(NumV(2)) {
//      interp(desugar(parse("(head (tail (list 1 2 3)))")), Nil)
//    }
//  }
//
//  test("Verify correct implementation function bound identifier01") {
//    assertResult(LetExt(List(LetBindExt("f",FdExt(List("self", "x"),FdExt(List("x"),BinOpExt("+",IdExt("x"),IdExt("self")))))),AppExt(IdExt("f"),List(NumExt(1), NumExt(2))))) {
//      parse("(let ((f (lambda (self x) (lambda (x) (+ x self))))) (f 1 2))")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException") {
//    intercept[ParseException] {
//      interp("""(rec-lam sumOfNumber () (+ 4 5)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException01") {
//    intercept[ParseException] {
//      interp("""(rec-lam sumOfSam (x x) (+ 4 5)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parse") {
//    assertResult(RecLamExt("multFunction", "a", BinOpExt("*", IdExt("a"), NumExt(10)))) {
//      parse("""(rec-lam multFunction (a) (* a 10)
//                                )""")
//    }
//  }
//
//  // test("Verify correct implementation function reclam simple parse desugar") {
//  //     assertResult(RecLamExt("multFunction", "a", BinOpExt("*", IdExt("a"), NumExt(10)))) {
//  //       desugar(parse("""(rec-lam multFunction (a) (* a 10)
//  //                                 )"""))
//  //     }
//  //   }
//
//
//  test("Verify correct implementation function reclam simple parse forever") {
//    assertResult( RecLamExt("forever", "a", AppExt(IdExt("forever"), List(IdExt("a"))))) {
//      parse("""(rec-lam forever (a) (forever a)
//                                )""")
//    }
//  }
//  // test("Verify correct implementation function lambda simple") {
//  //   assertResult(NumV(2)) {
//  //     interp(desugar(parse("""(lambda (x) (x)
//  //                               )""")), List(Bind("x", NumV(2))))
//  //   }
//  // }
//
//
//  test("Verify correct implementation function reclam simple parse 001") {
//    intercept[ParseException] {
//      parse("""(rec-lam 8 (a) (+ 4 5)
//                                )""")
//    }
//  }
//
//  // test("Verify correct implementation function let") {
//  //   assertResult(NumV(4)) {
//  //     interp(desugar(parse("""(let ((y 0)
//  //                               (app (lambda (f y) (f y)))
//  //                               (app (x) (+ y x)) 2
//  //                               ))""")), Nil)
//  //   }
//  // }
//
////  test("Verify correct implementation binding") {
////    assertResult(NumV(5)) {
////      interp(desugar(parse("x")), List(Bind("x", NumV(5))))
////    }
////  }
//
//  test("Verify correct implementatioxn function Exception lambda") {
//    assertResult(AppExt(AppExt(FdExt(List("f"),FdExt(List("x"),AppExt(IdExt("f"),List(IdExt("x"))))),List(FdExt(List("y"),BinOpExt("+",IdExt("y"),IdExt("x"))))),List(NumExt(21)))) {
//      parse("(((lambda (f) (lambda (x) (f x))) (lambda (y) (+ y x))) 21)")
//    }
//  }
//
//  // test("Verify correct implementation rec-lam") {
//  //   assertResult(NumV(6)) { //ClosV(FdC(List("n"),IfC(EqNumC(IdC("n"),NumC(0)),NumC(0),PlusC(IdC("n"),AppC(IdC("sum"),List(PlusC(IdC("n"),MultC(NumC(-1),NumC(1)))))))), <env>)
//  //     interp(desugar(parse("(rec-lam sum (n) (if (num= n 0) 0 (+ n (sum (- n 1)))))")), Nil)
//  //   }
//  // }
//  // """
//  // (rec-lam sum (n)
//  // (if (num= n 0)
//  //   0
//  //   (+ n (sum (- n 1)))))
//  // """
//
//  test("Verify correct implementation function reclam simple parseException reserved word") {
//    intercept[ParseException] {
//      interp("""(rec-lam if () (+ 4 5)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException reserved word1") {
//    intercept[ParseException] {
//      interp("""(rec-lam if (else) (+ 4 5)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException fress1") {
//    intercept[ParseException] {
//      interp("""(rec-lam  () (+ 4 5)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException No arguments") {
//    intercept[ParseException] {
//      interp("""(rec-lam a (b) ()
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException No arguments1") {
//    intercept[ParseException] {
//      interp("""(rec-lam a (b) (if else)
//                                )""")
//    }
//  }
//
//  test("Verify correct implementation function reclam simple parseException No arguments11 nothing") {
//    intercept[ParseException] {
//      interp("""(rec-lam  () ()
//                                )""")
//    }
//  }
//
//  test("Catch erroneous parse behavior") {
//    intercept[ParseException] {
//      parse("()")
//    }
//  }
//
//  test("Catch erroneous interp behavior") {
//    intercept[InterpException] {
//      interp("(+ true 5)")
//    }
//  }
//
//
//  def parse(expr: String): ExprExt
//  def desugar(expr: ExprExt): ExprC
//  // Binds are automatically converted to Pointers with Cells
//  def interp(expr: ExprC, env: List[(String, Value)]): Value
//  def interp(s: String): Value = interp(desugar(parse(s)), Nil)
//
////  /** ---------------- basic tests ---------------- **/
////
////  test("Desugar 5") {
////    assertResult(
////      NumC(5)
////    ) {
////      desugar(NumExt(5))
////    }
////  }
////
////  test("Interp 5") {
////    assertResult(
////      NumV(5)
////    ) {
////      interp(NumC(5))
////    }
////  }
////
////  test("Interp 5+true throws InterpException") {
////    intercept[InterpException] {
////      interp(PlusC(NumC(5), TrueC()))
////    }
////  }
////
////  /** ---------------- lambda tests ---------------- **/
////
////  //parser
////  test("parser - lambda no arguments - simple") {
////    assertResult(FdExt(List(), NumExt(15))) {
////      parse("(lambda () 15)")
////    }
////  }
////
////  test("parser - lambda one argument") {
////    assertResult(FdExt(List("x"), BinOpExt("+", IdExt("x"), NumExt(15)))) {
////      parse("(lambda (x) (+ x 15))")
////    }
////  }
////
////  test("parser - lambda three argument") {
////    assertResult(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("x"), IdExt("y")))) {
////      parse("(lambda (x y z) (+ x y))")
////    }
////  }
////
////  test("parser - lambda three argument - free name!") {
////    assertResult(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("a"), IdExt("a")))) {
////      parse("(lambda (x y z) (+ a a))")
////    }
////  }
////
////  test("Catch parser exception - lambda with reserved name!") {
////    intercept[ParseException] {
////      interp(desugar(parse("(lambda (true) (+ true 15))")))
////    }
////  }
////
////  test("Catch parser exception - lambda doublicate arg name") {
////    intercept[ParseException] {
////      interp(desugar(parse("(lambda (x y x) (+ x y))")))
////    }
////  }
////
////  test("Catch parser exception - lambda with nested lambda as argument!") {
////    intercept[ParseException] {
////      interp(desugar(parse("(lambda ((lambda (x) x)) (+ x 15))")))
////    }
////  }
////
////  //desugar
////  test("desugar - lambda no arguments - simple") {
////    assertResult(FdC(List(), NumC(15))) {
////      desugar(parse("(lambda () 15)"))
////    }
////  }
////
////  test("desugar - lambda no arguments") {
////    assertResult(FdC(List(), PlusC(NumC(5), NumC(15)))) {
////      desugar(parse("(lambda () (+ 5 15))"))
////    }
////  }
////
////  test("desugar - lambda no arguments - free name!") {
////    assertResult(FdC(List(), PlusC(IdC("x"), NumC(15)))) {
////      desugar(parse("(lambda () (+ x 15))"))
////    }
////  }
////
////  test("desugar - lambda multiple arguments") {
////    assertResult(FdC(List("x", "y", "z"), PlusC(PlusC(IdC("x"), IdC("y")), IdC("z")))) {
////      desugar(parse("(lambda (x y z) (+ (+ x y) z))"))
////    }
////  }
////
////  //interp
//////  test("interp - lambda no argument") {
//////    assertResult(ClosV(FdC(List(), NumC(15)), List())) {
//////      interp(desugar(parse("(lambda () 15)")))
//////    }
//////  }
//////
//////  test("interp - lambda no arguments with free name!") {
//////    assertResult(ClosV(FdC(List(), PlusC(IdC("x"), NumC(15))), List())) {
//////      interp(desugar(parse("(lambda () (+ x 15))")))
//////    }
//////  }
//////
//////  test("interp - lambda wrong free name") {
//////    assertResult(ClosV(FdC(List(), AppC(IdC("x"), List())), List())) {
//////      interp(desugar(parse("(lambda () (x))")))
//////    }
//////  }
//////
//////  test("interp - lambda with nested lambda has outer variable and added to number") {
//////    assertResult(ClosV(FdC(List("x"), PlusC(FdC(List(), IdC("x")), NumC(15))), List())) {
//////      interp(desugar(parse("(lambda (x) (+ (lambda () x) 15))")))
//////    }
//////  }
////
////
////  /** ---------------- Apply test ------------------- **/
////
////  //parser
////  test("parser - apply no arguments") {
////    assertResult(AppExt(FdExt(List(), NumExt(15)), List())) {
////      parse("((lambda () 15))")
////    }
////  }
////
////  test("parser - lambda wrong arguments") {
////    intercept[ParseException] {
////      parse("((lambda ((lambda (x) x)) 15) 6)")
////    }
////  }
////
////  test("parser - lambda reserved word as argument") {
////    assertResult(AppExt(IdExt("notReservedWord"), List(IdExt("anotherWord"), NumExt(15)))) {
////      parse("(notReservedWord anotherWord 15)")
////    }
////  }
////
////  test("parse - apply three arguments") {
////    assertResult(AppExt(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("x"), IdExt("y"))), List(NumExt(10), NumExt(5), NumExt(0)))) {
////      parse("((lambda (x y z) (+ x y)) 10 5 0)")
////    }
////  }
////
////  //desuagr
////  test("desugar - apply lambda nested lambda using external variable simple") {
////    assertResult(AppC(AppC(FdC(List("x"), FdC(List(),PlusC(IdC("x"),NumC(4)))),List(NumC(6))),List())) {
////      desugar(parse("(((lambda (x) (lambda () (+ x 4))) 6))"))
////    }
////  }
////
////  test("desugar - apply lambda name capture complex") {
////    assertResult(AppC(AppC(FdC(List("x"),FdC(List("y"),AppC(IdC("x"),List(IdC("y"))))),List(FdC(List("z"),MultC(IdC("z"),IdC("y"))))),List(NumC(15)))) {
////      desugar(parse("(((lambda (x) (lambda (y) (x y))) (lambda (z) (* z y))) 15)"))
////    }
////  }
////
////  test("desugar  - apply lambda with clouser!") {
////    assertResult(AppC(FdC(List("x"),FdC(List("y"),MultC(IdC("x"),IdC("y")))),List(NumC(5)))) {
////      desugar(parse("((lambda (x) (lambda (y) (* x y))) 5)"))
////    }
////  }
////
////  //interp
////  test("interp - apply one agrument") {
////    assertResult(NumV(3)) {
////      interp(desugar(parse("((lambda (x) (+ x 1)) 2)")))
////    }
////  }
////
////  test("interp - apply three arguments") {
////    assertResult(NumV(15)) {
////      interp(desugar(parse("((lambda (x y z) (+ x y)) 10 5 0)")))
////    }
////  }
////
////  test("interp - apply name shadowing") {
////    assertResult(NumV(20)) {
////      interp(desugar(parse("((lambda (x) ( (lambda (x) (+ 10 x)) 10)) 20)")))
////    }
////  }
////
////  test("interp - app dynamic scoping 1") {
////    assertResult(NumV(1)) {
////      interp(desugar(parse("(((lambda (x) (lambda (y) x)) 1) 2)")))
////    }
////  }
////
////  test("interp - app dynamic scoping 2") {
////    assertResult(NumV(2)) {
////      interp(desugar(parse("(((lambda (x) (lambda (y) y)) 1) 2)")))
////    }
////  }
////
////  test("Catch interp exception - apply lambda no arguments with free name!") {
////    intercept[InterpException] {
////      interp(desugar(parse("((lambda () (+ x 15)))")))
////    }
////  }
////
////  test("Catch interp exception - apply lambda not equale number of arguments!") {
////    intercept[InterpException] {
////      interp(desugar(parse("((lambda (x) (+ x 15)) 4 3)")))
////    }
////  }
////
////  test("Catch interp exception - apply declare function") {
////    intercept[InterpException] {
////      interp(desugar(parse("(declareFun (lambda (x) (+ x 5)))")))
////    }
////  }
////
////  test("Catch interp exception - apply lambda return lambda with too many argument") {
////    intercept[InterpException] {
////      interp(desugar(parse("(((lambda (x) (lambda (y) (+ x 4))) 6 8))")))
////    }
////  }
////
////  test("interp - apply lambda nested lambda using external variable simple") {
////    assertResult(NumV(10)) {
////      interp(desugar(parse("(((lambda (x) (lambda () (+ x 4))) 6))")))
////    }
////  }
////
////  test("interp - apply lambda nested apply lambda using external variable") {
////    assertResult(NumV(10)) {
////      interp(desugar(parse("((lambda (x) ((lambda (y) (+ x y)) 4)) 6)")))
////    }
////  }
////
////  test("interp - apply lambda nested apply lambda using external variable complex") {
////    assertResult(NumV(12)) {
////      interp(desugar(parse("((lambda (y) ((lambda (x) (+ x y)) y)) 6)")))
////    }
////  }
////
////  test("Catch interp exception - apply lambda name capture complex") {
////    intercept[InterpException] {
////      interp(desugar(parse("(((lambda (x) (lambda (y) (x y))) (lambda (z) (* z y))) 15)")))
////    }
////  }
////
////
////  /** ----------------- Let tests ------------------- **/
////
////  //parser
////  test("parser - let expression simple") {
////    assertResult(LetExt(List(LetBindExt("x", NumExt(15))), BinOpExt("+", IdExt("x"), NumExt(5)))) {
////      parse("(let ((x 15)) (+ x 5))")
////    }
////  }
////
////  test("parser - let three binders with binder replacing y -> b") {
////    assertResult(LetExt(List(LetBindExt("x", NumExt(15)), LetBindExt("y", IdExt("b")), LetBindExt("z", NumExt(0))), BinOpExt("+", IdExt("x"), IdExt("y")))) {
////      parse("(let ((x 15) (y b) (z 0)) (+ x y))")
////    }
////  }
////
////  test("catch parser exception - let no binders") {
////    intercept[ParseException] {
////      parse("(let () (+ x 5))")
////    }
////  }
////
////  test("catch parser exception - let wrong binder") {
////    intercept[ParseException] {
////      parse("(let ((x 15 x)) (+ x 5))")
////    }
////  }
////
////  test("catch parser exception - let wrong binder 2") {
////    intercept[ParseException] {
////      parse("(let (x 15) (+ x 5))")
////    }
////  }
////
////  test("catch parser exception - let no expression") {
////    intercept[ParseException] {
////      parse("(let ((x 15)) ())")
////    }
////  }
////
////  test("catch parser exception - let reserved word") {
////    intercept[ParseException] {
////      parse("(let ((+ 15)) (+ 4 5))")
////    }
////  }
////
////  test("catch parser exception - let doublicate binder name") {
////    intercept[ParseException] {
////      parse("(let ((x 15) (x 4)) (+ x 5))")
////    }
////  }
////
////  //desugar
////  test("desugar - let free name") {
////    assertResult(AppC(FdC(List("x"), PlusC(IdC("y"), NumC(5))), List(NumC(15)))) {
////      desugar(parse("(let ((x 15)) (+ y 5))"))
////    }
////  }
////
////  test("desugar - let two binder") {
////    assertResult(AppC(FdC(List("x", "y"), PlusC(IdC("y"), NumC(5))), List(NumC(15), NumC(4)))) {
////      desugar(parse("(let ((x 15) (y 4)) (+ y 5))"))
////    }
////  }
////
////
////  test("desugar - let apply with identifer as argument") {
////    assertResult(AppC(FdC(List("x"), AppC(FdC(List("quad"), AppC(IdC("quad"), List(NumC(10)))), List(FdC(List("x"),PlusC(IdC("x"),PlusC(IdC("x"),IdC("x"))))))),List(IdC("quad")))) {
////      desugar(parse("""
////              (let ((x quad))
////                (let ((quad (lambda (x) (+ x (+ x x)))))
////                  (quad 10)))
////            """))
////    }
////  }
////
////  //interp
////  test("interp - let two binder") {
////    assertResult(NumV(9)) {
////      interp("(let ((x 15) (y 4)) (+ y 5))")
////    }
////  }
////
////  test("interp - let two binder complex") {
////    assertResult(NumV(25)) {
////      interp("(let ((x 15) (y 5)) (+ y ((lambda (x) (+ y x)) x)))")
////    }
////  }
////
////  test("interp - let two binder complex 2") {
////    assertResult(NumV(30)) {
////      interp("(let ((x 15) (y 5)) (+ (let ((x 10)) x) ((lambda (x) (+ y x)) x)))")
////    }
////  }
////
////  test("interp - let dynamic scope and name capture") {
////    assertResult(NumV(2)) {
////      interp("""
////          (let ((y 0) (app (lambda (f y) (f y))))
////              (app (lambda (x) (+ y x)) 2))
////          """)
////    }
////  }
////
////  test("interp - let apply to function simple") {
////    assertResult(NumV(20)) {
////      interp("""(let ((double (lambda (x) (+ x x))))
////                      (double 10))""")
////    }
////  }
////
////  test("interp - let apply to function complex") {
////    intercept[InterpException] {
////      interp("""
////                (let ((quadruple (lambda (x) (double (double x)))))
////                  (let ((double (lambda (x) (+ x x))))
////                    (quadruple 10)))
////            """)
////    }
////  }
////
////  test("catch interp exception - let with bind identifier to another identifier") {
////    intercept[InterpException] {
////      interp("""
////              (let ((y quad))
////                (let ((y (lambda (x) (+ x (+ x x)))))
////                  (quad 10)))
////            """)
////    }
////  }
////
////  test("interp - let extra binder assign to numerical value") {
////    assertResult(NumV(15)) {
////      interp("""
////              (let ((y 5) (z 15))
////                ((lambda (x) (+ x (+ x x))) y))
////            """)
////    }
////  }
////
////  test("catch interp exception - let extra binder assign to identifier") {
////    intercept[InterpException] {
////      interp("""
////              (let ((y 5) (z quad))
////                ((lambda (x) (+ x (+ x x))) y))
////            """)
////    }
////  }
////
////  test("interp - let scoping - skip identifier if same name in lambda argument incorrect") {
////    intercept[InterpException] {
////      interp("""
////              (let ( (x quad) )
////                (let ( (quad (lambda (x) (+ x (+ x x)))) )
////                  (quad 10)))
////            """)
////    }
////  }
////
////  test("interp - let scoping check") {
////    assertResult(NumV(10)) {
////      interp("""
////              (let ((x 10) (y 5))
////                ((lambda (x) (let ((quad 5)) (+ x x))) y))
////            """)
////    }
////  }
////
////  test("interp - let scoping - skip identifier if same name in lambda argument") {
////    assertResult(NumV(30)) {
////      interp("""
////              (let ( (x 5) )
////                (let ( (quad (lambda (x) (+ x (+ x x)))) )
////                  (quad 10)))
////            """)
////    }
////  }
////
////  test("interp - let scoping into nested let binder with lambda different identifier") {
////    assertResult(NumV(15)) {
////      interp("""
////              (let ( (x 5) )
////                (let ( (quad (lambda (y) (+ x (+ x x)))) )
////                  (quad 10)))
////            """)
////    }
////  }
////
////  test("catch interp exception - let recursive binder") {
////    intercept[InterpException] {
////      interp(desugar(parse("(let ((y (x 15))) (+ y 5))")))
////    }
////  }
////
////  test("interp - let twice example") {
////    assertResult(NumV(4)) {
////      interp("""
////              (let ((twice (lambda (f) (lambda (x) (f (f x))))))
////                        (let ((quad (twice (lambda (y) (+ y y)))))
////                                (quad 1)))
////            """
////      )
////    }
////  }
////
////  test("catch interp exception - let free name") {
////    intercept[InterpException] {
////      interp(desugar(parse("(let ((x 15)) (+ y 5))")))
////    }
////  }
////
////  test("catch interp exception - let recursive definition") {
////    intercept[InterpException] {
////      interp("(let ((ones (cons 1 ones))) ones)"
////      )
////    }
////  }
////
////  /** --------------- Complex tests ----------------- **/
////  test("desugar factorial") {
////    assertResult(AppC(FdC(List("y"), AppC(FdC(List("f"), AppC(IdC("f"), List(IdC("f"), IdC("y")))), List(FdC(List("self", "x"), IfC(EqNumC(IdC("x"), NumC(1)), NumC(1), MultC(IdC("x"), AppC(IdC("self"), List(IdC("self"), PlusC(IdC("x"), MultC(NumC(-1),NumC(1))))))))))),List(NumC(3)))) {
////      desugar(parse(
////        """
////                ((lambda (y)
////                  (
////                    let ((f (lambda (self x)
////                              (if (num= x 1)
////                                1
////                                (* x (self self (- x 1)))
////                              )
////                            )
////                        ))
////                    (f f y)
////                  )
////                ) 3)
////              """
////      ))
////    }
////  }
////
////  test("factorial") {
////    assertResult(NumV(6)) {
////      interp("""
////                ((lambda (y)
////                  (
////                    let ((f (lambda (self x)
////                              (if (num= x 1)
////                                1
////                                (* x (self self (- x 1)))
////                              )
////                            )
////                        ))
////                    (f f y)
////                  )
////                ) 3)
////              """
////      )
////    }
////  }
////
////  /** --------------- Z Combinator ----------------- **/
////
////  test("desugar Z Combinator") {
////    assertResult(FdC(List("f"), AppC(FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))), List(FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))))))) {
////      desugar("""
////          (lambda (f) (
////              (lambda (x) (f (lambda (v) ((x x) v))))
////              (lambda (x) (f (lambda (v) ((x x) v))))
////           ))
////        """
////      )
////    }
////  }
////
////  test("desugar Z Combinator - alternative implemenation") {
////    assertResult(FdC(List("f"),
////                    AppC(FdC(List("x"), AppC(IdC("x"), List(IdC("x")))),
////                      List(FdC(List("x"),AppC(IdC("f"),List(FdC(List("v"),AppC(AppC(IdC("x"),List(IdC("x"))),List(IdC("v"))))))))))) {
////      desugar("""
////          (lambda (f) (
////              (lambda (x) (x x))
////              (lambda (x) (f (lambda (v) ((x x) v))))
////           ))
////        """
////      )
////    }
////  }
////
//////  test("Z Combinator") {
//////    assertResult(ClosV(FdC(List("f"),
//////                            AppC(FdC(List("x"),
//////                                     AppC(IdC("f"),
//////                                          List(FdC(List("v"),
//////                                               AppC(AppC(IdC("x"),
//////                                                         List(IdC("x"))),
//////                                                    List(IdC("v"))))))),
//////                                 List(FdC(List("x"),
//////                                          AppC(IdC("f"),
//////                                               List(FdC(List("v"),
//////                                                        AppC(AppC(IdC("x"),
//////                                                                  List(IdC("x"))),
//////                                                             List(IdC("v")))))))))),
//////                      List())) {
//////      interp("""
//////          (lambda (f) (
//////              (lambda (x) (f (lambda (v) ((x x) v))))
//////              (lambda (x) (f (lambda (v) ((x x) v))))
//////           ))
//////        """
//////      )
//////    }
//////  }
////
////  test("desugar factorial using Z Combinator") {
////    assertResult( AppC( AppC(FdC(List("f"),
////                                 AppC(FdC(List("x"),
////                                          AppC(IdC("f"),
////                                               List(FdC(List("v"), AppC( AppC(IdC("x"),List(IdC("x"))), List(IdC("v"))))))),
////                                      List(FdC(List("x"),AppC(IdC("f"),List(FdC(List("v"),AppC(AppC(IdC("x"),List(IdC("x"))),List(IdC("v")))))))))),
////                              List(FdC(List("f"),FdC(List("n"),IfC(EqNumC(IdC("n"),NumC(1)),NumC(1),MultC(IdC("n"),AppC(IdC("f"),List(PlusC(IdC("n"),MultC(NumC(-1),NumC(1))))))))))),
////                        List(NumC(3)))) {
////      desugar("""
////      (
////        (
////          (lambda (f) (
////            (lambda (x) (f (lambda (v) ((x x) v))))
////            (lambda (x) (f (lambda (v) ((x x) v))))
////          ))
////          (lambda (f) (
////            lambda (n) (
////              if (num= n 1)
////                  1
////                 (* n (f (- n 1)))
////            )
////          ))
////        )
////        3
////      )
////        """
////      )
////    }
////  }
////
////  test("factorial using Z Combinator") {
////    assertResult(NumV(6)) {
////      interp("""
////      (
////        (
////          (lambda (f) (
////            (lambda (x) (f (lambda (v) ((x x) v))))
////            (lambda (x) (f (lambda (v) ((x x) v))))
////          ))
////          (lambda (f) (
////            lambda (n) (
////              if (num= n 1)
////                  1
////                 (* n (f (- n 1)))
////            )
////          ))
////        )
////        3
////      )
////        """
////      )
////    }
////  }
////
////
////  /** ------------- Test rec-lam ---------------- **/
////  test("factorial using rsc-lam") {
////    assertResult(NumV(120)) {
////      interp(
////        """
////        (
////          (rec-lam factorial (n)
////            (if (num= n 1)
////              1
////              (* n (factorial (- n 1))))
////          )
////          5
////        )
////        """
////      )
////    }
////  }
////
////
////  /** ------------- Test letrec ---------------- **/
////  test("parse - letrec simple") {
////    assertResult(LetRecExt(List(LetBindExt("x",NumExt(15)), LetBindExt("y",IdExt("x"))),BinOpExt("+",IdExt("x"),IdExt("y")))) {
////      parse("(letrec ((x 15) (y x)) (+ x y))")
////    }
////  }
////
////  test("parse - letrec quad function") {
////    assertResult(LetRecExt(List(LetBindExt("x",NumExt(5)), LetBindExt("y",IdExt("x"))),LetRecExt(List(LetBindExt("quad",FdExt(List("y"),BinOpExt("+",IdExt("x"),BinOpExt("+",IdExt("x"),IdExt("x")))))),AppExt(IdExt("quad"),List(NumExt(10)))))) {
////      parse("""
////        (letrec ( (x 5) (y x) )
////          (letrec ( (quad (lambda (y) (+ x (+ x x)))) )
////            (quad 10)))
////      """)
////    }
////  }
////
////  test("parse - letrec even-odd") {
////    assertResult(LetRecExt(List(LetBindExt("even",FdExt(List("x"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(0)),IdExt("ture"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(1)),FalseExt(),AppExt(IdExt("odd"),List(BinOpExt("-",IdExt("x"),NumExt(1)))))))), LetBindExt("odd",FdExt(List("x"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(0)),FalseExt(),IfExt(BinOpExt("num=",IdExt("x"),NumExt(1)),TrueExt(),AppExt(IdExt("even"),List(BinOpExt("-",IdExt("x"),NumExt(1))))))))),ListExt(List(IdExt("even"), IdExt("odd"))))) {
////      parse(
////        """
////                (letrec (
////                          (even (lambda (x) (if (num= x 0)
////                                                ture
////                                                (if (num= x 1)
////                                                    false
////                                                    (odd (- x 1))))))
////                         (odd (lambda (x) (if (num= x 0)
////                                                false
////                                                (if (num= x 1)
////                                                    true
////                                                    (even (- x 1))))))
////                        )
////                  (list even odd))
////          """)
////    }
////  }
////
////  //desugar
////  test("desugar - letrec even-odd") {
////    assertResult(AppC(FdC(List("even", "odd"),SeqC(SetC("even",FdC(List("x"),IfC(EqNumC(IdC("x"),NumC(0)),TrueC(),IfC(EqNumC(IdC("x"),NumC(1)),FalseC(),AppC(IdC("odd"),List(PlusC(IdC("x"),MultC(NumC(-1),NumC(1))))))))),SeqC(SetC("odd",FdC(List("x"),IfC(EqNumC(IdC("x"),NumC(0)),FalseC(),IfC(EqNumC(IdC("x"),NumC(1)),TrueC(),AppC(IdC("even"),List(PlusC(IdC("x"),MultC(NumC(-1),NumC(1))))))))),ConsC(IdC("even"),ConsC(IdC("odd"),NilC()))))),List(UninitializedC(), UninitializedC()))) {
////      desugar(
////        """
////                (letrec (
////                          (even (lambda (x) (if (num= x 0)
////                                                true
////                                                (if (num= x 1)
////                                                    false
////                                                    (odd (- x 1))))))
////                         (odd (lambda (x) (if (num= x 0)
////                                                false
////                                                (if (num= x 1)
////                                                    true
////                                                    (even (- x 1))))))
////                        )
////                  (list even odd))
////          """)
////    }
////  }
////
////  //interp
////  test("interp - letrec even-odd") {
////    assertResult(BoolV(true)) {
////      interp(
////        """
////                ((head
////                  (letrec (
////                            (even (lambda (x) (if (num= x 0)
////                                                  true
////                                                  (if (num= x 1)
////                                                      false
////                                                      (odd (- x 1))))))
////                           (odd (lambda (x) (if (num= x 0)
////                                                  false
////                                                  (if (num= x 1)
////                                                      true
////                                                      (even (- x 1))))))
////                          )
////                    (list even odd))
////                  )
////                2)
////          """)
////    }
////  }
////
////  /** ------------- Test set ---------------- **/
////
////  test("parse - set simple") {
////    assertResult(FdExt(List(),BinOpExt("seq",SetExt("x",IdExt("y")),FalseExt()))) {
////      parse("(lambda () (seq (set x y) false))")
////    }
////  }
////
////  //desugar
////  test("desugar - set simple") {
////    assertResult(FdC(List(),SeqC(SetC("x",IdC("y")),FalseC()))) {
////      desugar("(lambda () (seq (set x y) false))")
////    }
////  }
////
////  //interp
////  test("interp - set simple") {
////    assertResult(PointerClosV(FdC(List(),SeqC(SetC("x",IdC("y")),FalseC())),List())) {
////      interp("(lambda () (seq (set x y) false))")
////    }
////  }
////
////  test("interp - set with seq and let") {
////    assertResult(NumV(7)) {
////      interp("(let ((x 5) (y 7)) ( (lambda () (seq (set x y) y)) ))")
////    }
////  }
////
////
////  /** ------------- Test box ---------------- **/
////
////  test("parse - box simple") {
////    assertResult(FdExt(List(),BinOpExt("seq",BinOpExt("setbox",IdExt("x"),UnOpExt("unbox",IdExt("y"))),FalseExt()))) {
////      parse("(lambda () (seq (setbox x (unbox y)) false))")
////    }
////  }
////
////  //desugar
////  test("desugar - box simple") {
////    assertResult(FdC(List(),SeqC(SetboxC(IdC("x"),UnboxC(IdC("y"))),FalseC()))) {
////      desugar("(lambda () (seq (setbox x (unbox y)) false))")
////    }
////  }
////
////
////
////  /**
////   * Helpers
////   */
////  def parse(expr: String): ExprExt = Parser.parse(expr)
////  def desugar(expr: ExprExt): ExprC = Desugar.desugar(expr)
////  def desugar(expr: String): ExprC = Desugar.desugar(parse(expr))
////  def interp(expr: ExprC): Value = Interp.interp(expr)
////  def interp(expr: String): Value = Interp.interp(desugar(parse(expr)))
//}
