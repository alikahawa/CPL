package mutation_interpreter

import org.scalatest.FunSuite

class TestMutation extends FunSuite {

  /** ---------------- basic tests ---------------- **/

  test("Desugar 5") {
    assertResult(
      NumC(5)
    ) {
      desugar(NumExt(5))
    }
  }

  test("Interp 5") {
    assertResult(
      NumV(5)
    ) {
      interp(NumC(5))
    }
  }

  test("Interp 5+true throws InterpException") {
    intercept[InterpException] {
      interp(PlusC(NumC(5), TrueC()))
    }
  }

  /** ---------------- lambda tests ---------------- **/

  //parser
  test("parser - lambda no arguments - simple") {
    assertResult(FdExt(List(), NumExt(15))) {
      parse("(lambda () 15)")
    }
  }

  test("parser - lambda one argument") {
    assertResult(FdExt(List("x"), BinOpExt("+", IdExt("x"), NumExt(15)))) {
      parse("(lambda (x) (+ x 15))")
    }
  }

  test("parser - lambda three argument") {
    assertResult(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("x"), IdExt("y")))) {
      parse("(lambda (x y z) (+ x y))")
    }
  }

  test("parser - lambda three argument - free name!") {
    assertResult(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("a"), IdExt("a")))) {
      parse("(lambda (x y z) (+ a a))")
    }
  }

  test("Catch parser exception - lambda with reserved name!") {
    intercept[ParseException] {
      interp(desugar(parse("(lambda (true) (+ true 15))")))
    }
  }

  test("Catch parser exception - lambda doublicate arg name") {
    intercept[ParseException] {
      interp(desugar(parse("(lambda (x y x) (+ x y))")))
    }
  }

  test("Catch parser exception - lambda with nested lambda as argument!") {
    intercept[ParseException] {
      interp(desugar(parse("(lambda ((lambda (x) x)) (+ x 15))")))
    }
  }

  //desugar
  test("desugar - lambda no arguments - simple") {
    assertResult(FdC(List(), NumC(15))) {
      desugar(parse("(lambda () 15)"))
    }
  }

  test("desugar - lambda no arguments") {
    assertResult(FdC(List(), PlusC(NumC(5), NumC(15)))) {
      desugar(parse("(lambda () (+ 5 15))"))
    }
  }

  test("desugar - lambda no arguments - free name!") {
    assertResult(FdC(List(), PlusC(IdC("x"), NumC(15)))) {
      desugar(parse("(lambda () (+ x 15))"))
    }
  }

  test("desugar - lambda multiple arguments") {
    assertResult(FdC(List("x", "y", "z"), PlusC(PlusC(IdC("x"), IdC("y")), IdC("z")))) {
      desugar(parse("(lambda (x y z) (+ (+ x y) z))"))
    }
  }

  //interp
//  test("interp - lambda no argument") {
//    assertResult(ClosV(FdC(List(), NumC(15)), List())) {
//      interp(desugar(parse("(lambda () 15)")))
//    }
//  }
//
//  test("interp - lambda no arguments with free name!") {
//    assertResult(ClosV(FdC(List(), PlusC(IdC("x"), NumC(15))), List())) {
//      interp(desugar(parse("(lambda () (+ x 15))")))
//    }
//  }
//
//  test("interp - lambda wrong free name") {
//    assertResult(ClosV(FdC(List(), AppC(IdC("x"), List())), List())) {
//      interp(desugar(parse("(lambda () (x))")))
//    }
//  }
//
//  test("interp - lambda with nested lambda has outer variable and added to number") {
//    assertResult(ClosV(FdC(List("x"), PlusC(FdC(List(), IdC("x")), NumC(15))), List())) {
//      interp(desugar(parse("(lambda (x) (+ (lambda () x) 15))")))
//    }
//  }


  /** ---------------- Apply test ------------------- **/

  //parser
  test("parser - apply no arguments") {
    assertResult(AppExt(FdExt(List(), NumExt(15)), List())) {
      parse("((lambda () 15))")
    }
  }

  test("parser - lambda wrong arguments") {
    intercept[ParseException] {
      parse("((lambda ((lambda (x) x)) 15) 6)")
    }
  }

  test("parser - lambda reserved word as argument") {
    assertResult(AppExt(IdExt("notReservedWord"), List(IdExt("anotherWord"), NumExt(15)))) {
      parse("(notReservedWord anotherWord 15)")
    }
  }

  test("parse - apply three arguments") {
    assertResult(AppExt(FdExt(List("x", "y", "z"), BinOpExt("+", IdExt("x"), IdExt("y"))), List(NumExt(10), NumExt(5), NumExt(0)))) {
      parse("((lambda (x y z) (+ x y)) 10 5 0)")
    }
  }

  //desuagr
  test("desugar - apply lambda nested lambda using external variable simple") {
    assertResult(AppC(AppC(FdC(List("x"), FdC(List(),PlusC(IdC("x"),NumC(4)))),List(NumC(6))),List())) {
      desugar(parse("(((lambda (x) (lambda () (+ x 4))) 6))"))
    }
  }

  test("desugar - apply lambda name capture complex") {
    assertResult(AppC(AppC(FdC(List("x"),FdC(List("y"),AppC(IdC("x"),List(IdC("y"))))),List(FdC(List("z"),MultC(IdC("z"),IdC("y"))))),List(NumC(15)))) {
      desugar(parse("(((lambda (x) (lambda (y) (x y))) (lambda (z) (* z y))) 15)"))
    }
  }

  test("desugar  - apply lambda with clouser!") {
    assertResult(AppC(FdC(List("x"),FdC(List("y"),MultC(IdC("x"),IdC("y")))),List(NumC(5)))) {
      desugar(parse("((lambda (x) (lambda (y) (* x y))) 5)"))
    }
  }

  //interp
  test("interp - apply one agrument") {
    assertResult(NumV(3)) {
      interp(desugar(parse("((lambda (x) (+ x 1)) 2)")))
    }
  }

  test("interp - apply three arguments") {
    assertResult(NumV(15)) {
      interp(desugar(parse("((lambda (x y z) (+ x y)) 10 5 0)")))
    }
  }

  test("interp - apply name shadowing") {
    assertResult(NumV(20)) {
      interp(desugar(parse("((lambda (x) ( (lambda (x) (+ 10 x)) 10)) 20)")))
    }
  }

  test("interp - app dynamic scoping 1") {
    assertResult(NumV(1)) {
      interp(desugar(parse("(((lambda (x) (lambda (y) x)) 1) 2)")))
    }
  }

  test("interp - app dynamic scoping 2") {
    assertResult(NumV(2)) {
      interp(desugar(parse("(((lambda (x) (lambda (y) y)) 1) 2)")))
    }
  }

  test("Catch interp exception - apply lambda no arguments with free name!") {
    intercept[InterpException] {
      interp(desugar(parse("((lambda () (+ x 15)))")))
    }
  }

  test("Catch interp exception - apply lambda not equale number of arguments!") {
    intercept[InterpException] {
      interp(desugar(parse("((lambda (x) (+ x 15)) 4 3)")))
    }
  }

  test("Catch interp exception - apply declare function") {
    intercept[InterpException] {
      interp(desugar(parse("(declareFun (lambda (x) (+ x 5)))")))
    }
  }

  test("Catch interp exception - apply lambda return lambda with too many argument") {
    intercept[InterpException] {
      interp(desugar(parse("(((lambda (x) (lambda (y) (+ x 4))) 6 8))")))
    }
  }

  test("interp - apply lambda nested lambda using external variable simple") {
    assertResult(NumV(10)) {
      interp(desugar(parse("(((lambda (x) (lambda () (+ x 4))) 6))")))
    }
  }

  test("interp - apply lambda nested apply lambda using external variable") {
    assertResult(NumV(10)) {
      interp(desugar(parse("((lambda (x) ((lambda (y) (+ x y)) 4)) 6)")))
    }
  }

  test("interp - apply lambda nested apply lambda using external variable complex") {
    assertResult(NumV(12)) {
      interp(desugar(parse("((lambda (y) ((lambda (x) (+ x y)) y)) 6)")))
    }
  }

  test("Catch interp exception - apply lambda name capture complex") {
    intercept[InterpException] {
      interp(desugar(parse("(((lambda (x) (lambda (y) (x y))) (lambda (z) (* z y))) 15)")))
    }
  }


  /** ----------------- Let tests ------------------- **/

  //parser
  test("parser - let expression simple") {
    assertResult(LetExt(List(LetBindExt("x", NumExt(15))), BinOpExt("+", IdExt("x"), NumExt(5)))) {
      parse("(let ((x 15)) (+ x 5))")
    }
  }

  test("parser - let three binders with binder replacing y -> b") {
    assertResult(LetExt(List(LetBindExt("x", NumExt(15)), LetBindExt("y", IdExt("b")), LetBindExt("z", NumExt(0))), BinOpExt("+", IdExt("x"), IdExt("y")))) {
      parse("(let ((x 15) (y b) (z 0)) (+ x y))")
    }
  }

  test("catch parser exception - let no binders") {
    intercept[ParseException] {
      parse("(let () (+ x 5))")
    }
  }

  test("catch parser exception - let wrong binder") {
    intercept[ParseException] {
      parse("(let ((x 15 x)) (+ x 5))")
    }
  }

  test("catch parser exception - let wrong binder 2") {
    intercept[ParseException] {
      parse("(let (x 15) (+ x 5))")
    }
  }

  test("catch parser exception - let no expression") {
    intercept[ParseException] {
      parse("(let ((x 15)) ())")
    }
  }

  test("catch parser exception - let reserved word") {
    intercept[ParseException] {
      parse("(let ((+ 15)) (+ 4 5))")
    }
  }

  test("catch parser exception - let doublicate binder name") {
    intercept[ParseException] {
      parse("(let ((x 15) (x 4)) (+ x 5))")
    }
  }

  //desugar
  test("desugar - let free name") {
    assertResult(AppC(FdC(List("x"), PlusC(IdC("y"), NumC(5))), List(NumC(15)))) {
      desugar(parse("(let ((x 15)) (+ y 5))"))
    }
  }

  test("desugar - let two binder") {
    assertResult(AppC(FdC(List("x", "y"), PlusC(IdC("y"), NumC(5))), List(NumC(15), NumC(4)))) {
      desugar(parse("(let ((x 15) (y 4)) (+ y 5))"))
    }
  }


  test("desugar - let apply with identifer as argument") {
    assertResult(AppC(FdC(List("x"), AppC(FdC(List("quad"), AppC(IdC("quad"), List(NumC(10)))), List(FdC(List("x"),PlusC(IdC("x"),PlusC(IdC("x"),IdC("x"))))))),List(IdC("quad")))) {
      desugar(parse("""
              (let ((x quad))
                (let ((quad (lambda (x) (+ x (+ x x)))))
                  (quad 10)))
            """))
    }
  }

  //interp
  test("interp - let two binder") {
    assertResult(NumV(9)) {
      interp("(let ((x 15) (y 4)) (+ y 5))")
    }
  }

  test("interp - let two binder complex") {
    assertResult(NumV(25)) {
      interp("(let ((x 15) (y 5)) (+ y ((lambda (x) (+ y x)) x)))")
    }
  }

  test("interp - let two binder complex 2") {
    assertResult(NumV(30)) {
      interp("(let ((x 15) (y 5)) (+ (let ((x 10)) x) ((lambda (x) (+ y x)) x)))")
    }
  }

  test("interp - let dynamic scope and name capture") {
    assertResult(NumV(2)) {
      interp("""
          (let ((y 0) (app (lambda (f y) (f y))))
              (app (lambda (x) (+ y x)) 2))
          """)
    }
  }

  test("interp - let apply to function simple") {
    assertResult(NumV(20)) {
      interp("""(let ((double (lambda (x) (+ x x))))
                      (double 10))""")
    }
  }

  test("interp - let apply to function complex") {
    intercept[InterpException] {
      interp("""
                (let ((quadruple (lambda (x) (double (double x)))))
                  (let ((double (lambda (x) (+ x x))))
                    (quadruple 10)))
            """)
    }
  }

  test("catch interp exception - let with bind identifier to another identifier") {
    intercept[InterpException] {
      interp("""
              (let ((y quad))
                (let ((y (lambda (x) (+ x (+ x x)))))
                  (quad 10)))
            """)
    }
  }

  test("interp - let extra binder assign to numerical value") {
    assertResult(NumV(15)) {
      interp("""
              (let ((y 5) (z 15))
                ((lambda (x) (+ x (+ x x))) y))
            """)
    }
  }

  test("catch interp exception - let extra binder assign to identifier") {
    intercept[InterpException] {
      interp("""
              (let ((y 5) (z quad))
                ((lambda (x) (+ x (+ x x))) y))
            """)
    }
  }

  test("interp - let scoping - skip identifier if same name in lambda argument incorrect") {
    intercept[InterpException] {
      interp("""
              (let ( (x quad) )
                (let ( (quad (lambda (x) (+ x (+ x x)))) )
                  (quad 10)))
            """)
    }
  }

  test("interp - let scoping check") {
    assertResult(NumV(10)) {
      interp("""
              (let ((x 10) (y 5))
                ((lambda (x) (let ((quad 5)) (+ x x))) y))
            """)
    }
  }

  test("interp - let scoping - skip identifier if same name in lambda argument") {
    assertResult(NumV(30)) {
      interp("""
              (let ( (x 5) )
                (let ( (quad (lambda (x) (+ x (+ x x)))) )
                  (quad 10)))
            """)
    }
  }

  test("interp - let scoping into nested let binder with lambda different identifier") {
    assertResult(NumV(15)) {
      interp("""
              (let ( (x 5) )
                (let ( (quad (lambda (y) (+ x (+ x x)))) )
                  (quad 10)))
            """)
    }
  }

  test("catch interp exception - let recursive binder") {
    intercept[InterpException] {
      interp(desugar(parse("(let ((y (x 15))) (+ y 5))")))
    }
  }

  test("interp - let twice example") {
    assertResult(NumV(4)) {
      interp("""
              (let ((twice (lambda (f) (lambda (x) (f (f x))))))
                        (let ((quad (twice (lambda (y) (+ y y)))))
                                (quad 1)))
            """
      )
    }
  }

  test("catch interp exception - let free name") {
    intercept[InterpException] {
      interp(desugar(parse("(let ((x 15)) (+ y 5))")))
    }
  }

  test("catch interp exception - let recursive definition") {
    intercept[InterpException] {
      interp("(let ((ones (cons 1 ones))) ones)"
      )
    }
  }

  /** --------------- Complex tests ----------------- **/
  test("desugar factorial") {
    assertResult(AppC(FdC(List("y"), AppC(FdC(List("f"), AppC(IdC("f"), List(IdC("f"), IdC("y")))), List(FdC(List("self", "x"), IfC(EqNumC(IdC("x"), NumC(1)), NumC(1), MultC(IdC("x"), AppC(IdC("self"), List(IdC("self"), PlusC(IdC("x"), MultC(NumC(-1),NumC(1))))))))))),List(NumC(3)))) {
      desugar(parse(
        """
                ((lambda (y)
                  (
                    let ((f (lambda (self x)
                              (if (num= x 1)
                                1
                                (* x (self self (- x 1)))
                              )
                            )
                        ))
                    (f f y)
                  )
                ) 3)
              """
      ))
    }
  }

  test("factorial") {
    assertResult(NumV(6)) {
      interp("""
                ((lambda (y)
                  (
                    let ((f (lambda (self x)
                              (if (num= x 1)
                                1
                                (* x (self self (- x 1)))
                              )
                            )
                        ))
                    (f f y)
                  )
                ) 3)
              """
      )
    }
  }

  /** --------------- Z Combinator ----------------- **/

  test("desugar Z Combinator") {
    assertResult(FdC(List("f"), AppC(FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))), List(FdC(List("x"), AppC(IdC("f"), List(FdC(List("v"), AppC(AppC(IdC("x"), List(IdC("x"))), List(IdC("v"))))))))))) {
      desugar("""
          (lambda (f) (
              (lambda (x) (f (lambda (v) ((x x) v))))
              (lambda (x) (f (lambda (v) ((x x) v))))
           ))
        """
      )
    }
  }

  test("desugar Z Combinator - alternative implemenation") {
    assertResult(FdC(List("f"),
                    AppC(FdC(List("x"), AppC(IdC("x"), List(IdC("x")))),
                      List(FdC(List("x"),AppC(IdC("f"),List(FdC(List("v"),AppC(AppC(IdC("x"),List(IdC("x"))),List(IdC("v"))))))))))) {
      desugar("""
          (lambda (f) (
              (lambda (x) (x x))
              (lambda (x) (f (lambda (v) ((x x) v))))
           ))
        """
      )
    }
  }

//  test("Z Combinator") {
//    assertResult(ClosV(FdC(List("f"),
//                            AppC(FdC(List("x"),
//                                     AppC(IdC("f"),
//                                          List(FdC(List("v"),
//                                               AppC(AppC(IdC("x"),
//                                                         List(IdC("x"))),
//                                                    List(IdC("v"))))))),
//                                 List(FdC(List("x"),
//                                          AppC(IdC("f"),
//                                               List(FdC(List("v"),
//                                                        AppC(AppC(IdC("x"),
//                                                                  List(IdC("x"))),
//                                                             List(IdC("v")))))))))),
//                      List())) {
//      interp("""
//          (lambda (f) (
//              (lambda (x) (f (lambda (v) ((x x) v))))
//              (lambda (x) (f (lambda (v) ((x x) v))))
//           ))
//        """
//      )
//    }
//  }

  test("desugar factorial using Z Combinator") {
    assertResult( AppC( AppC(FdC(List("f"),
                                 AppC(FdC(List("x"),
                                          AppC(IdC("f"),
                                               List(FdC(List("v"), AppC( AppC(IdC("x"),List(IdC("x"))), List(IdC("v"))))))),
                                      List(FdC(List("x"),AppC(IdC("f"),List(FdC(List("v"),AppC(AppC(IdC("x"),List(IdC("x"))),List(IdC("v")))))))))),
                              List(FdC(List("f"),FdC(List("n"),IfC(EqNumC(IdC("n"),NumC(1)),NumC(1),MultC(IdC("n"),AppC(IdC("f"),List(PlusC(IdC("n"),MultC(NumC(-1),NumC(1))))))))))),
                        List(NumC(3)))) {
      desugar("""
      (
        (
          (lambda (f) (
            (lambda (x) (f (lambda (v) ((x x) v))))
            (lambda (x) (f (lambda (v) ((x x) v))))
          ))
          (lambda (f) (
            lambda (n) (
              if (num= n 1)
                  1
                 (* n (f (- n 1)))
            )
          ))
        )
        3
      )
        """
      )
    }
  }

  test("factorial using Z Combinator") {
    assertResult(NumV(6)) {
      interp("""
      (
        (
          (lambda (f) (
            (lambda (x) (f (lambda (v) ((x x) v))))
            (lambda (x) (f (lambda (v) ((x x) v))))
          ))
          (lambda (f) (
            lambda (n) (
              if (num= n 1)
                  1
                 (* n (f (- n 1)))
            )
          ))
        )
        3
      )
        """
      )
    }
  }


  /** ------------- Test rec-lam ---------------- **/
  test("factorial using rsc-lam") {
    assertResult(NumV(120)) {
      interp(
        """
        (
          (rec-lam factorial (n)
            (if (num= n 1)
              1
              (* n (factorial (- n 1))))
          )
          5
        )
        """
      )
    }
  }


  /** ------------- Test letrec ---------------- **/
  test("parse - letrec simple") {
    assertResult(LetRecExt(List(LetBindExt("x",NumExt(15)), LetBindExt("y",IdExt("x"))),BinOpExt("+",IdExt("x"),IdExt("y")))) {
      parse("(letrec ((x 15) (y x)) (+ x y))")
    }
  }

  test("parse - letrec quad function") {
    assertResult(LetRecExt(List(LetBindExt("x",NumExt(5)), LetBindExt("y",IdExt("x"))),LetRecExt(List(LetBindExt("quad",FdExt(List("y"),BinOpExt("+",IdExt("x"),BinOpExt("+",IdExt("x"),IdExt("x")))))),AppExt(IdExt("quad"),List(NumExt(10)))))) {
      parse("""
        (letrec ( (x 5) (y x) )
          (letrec ( (quad (lambda (y) (+ x (+ x x)))) )
            (quad 10)))
      """)
    }
  }

  test("parse - letrec even-odd") {
    assertResult(LetRecExt(List(LetBindExt("even",FdExt(List("x"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(0)),IdExt("ture"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(1)),FalseExt(),AppExt(IdExt("odd"),List(BinOpExt("-",IdExt("x"),NumExt(1)))))))), LetBindExt("odd",FdExt(List("x"),IfExt(BinOpExt("num=",IdExt("x"),NumExt(0)),FalseExt(),IfExt(BinOpExt("num=",IdExt("x"),NumExt(1)),TrueExt(),AppExt(IdExt("even"),List(BinOpExt("-",IdExt("x"),NumExt(1))))))))),ListExt(List(IdExt("even"), IdExt("odd"))))) {
      parse(
        """
                (letrec (
                          (even (lambda (x) (if (num= x 0)
                                                ture
                                                (if (num= x 1)
                                                    false
                                                    (odd (- x 1))))))
                         (odd (lambda (x) (if (num= x 0)
                                                false
                                                (if (num= x 1)
                                                    true
                                                    (even (- x 1))))))
                        )
                  (list even odd))
          """)
    }
  }

  //desugar
  test("desugar - letrec even-odd") {
    assertResult(AppC(FdC(List("even", "odd"),SeqC(SetC("even",FdC(List("x"),IfC(EqNumC(IdC("x"),NumC(0)),TrueC(),IfC(EqNumC(IdC("x"),NumC(1)),FalseC(),AppC(IdC("odd"),List(PlusC(IdC("x"),MultC(NumC(-1),NumC(1))))))))),SeqC(SetC("odd",FdC(List("x"),IfC(EqNumC(IdC("x"),NumC(0)),FalseC(),IfC(EqNumC(IdC("x"),NumC(1)),TrueC(),AppC(IdC("even"),List(PlusC(IdC("x"),MultC(NumC(-1),NumC(1))))))))),ConsC(IdC("even"),ConsC(IdC("odd"),NilC()))))),List(UninitializedC(), UninitializedC()))) {
      desugar(
        """
                (letrec (
                          (even (lambda (x) (if (num= x 0)
                                                true
                                                (if (num= x 1)
                                                    false
                                                    (odd (- x 1))))))
                         (odd (lambda (x) (if (num= x 0)
                                                false
                                                (if (num= x 1)
                                                    true
                                                    (even (- x 1))))))
                        )
                  (list even odd))
          """)
    }
  }

  //interp
  test("interp - letrec even-odd") {
    assertResult(BoolV(true)) {
      interp(
        """
                ((head
                  (letrec (
                            (even (lambda (x) (if (num= x 0)
                                                  true
                                                  (if (num= x 1)
                                                      false
                                                      (odd (- x 1))))))
                           (odd (lambda (x) (if (num= x 0)
                                                  false
                                                  (if (num= x 1)
                                                      true
                                                      (even (- x 1))))))
                          )
                    (list even odd))
                  )
                2)
          """)
    }
  }

  /** ------------- Test set ---------------- **/

  test("parse - set simple") {
    assertResult(FdExt(List(),BinOpExt("seq",SetExt("x",IdExt("y")),FalseExt()))) {
      parse("(lambda () (seq (set x y) false))")
    }
  }

  //desugar
  test("desugar - set simple") {
    assertResult(FdC(List(),SeqC(SetC("x",IdC("y")),FalseC()))) {
      desugar("(lambda () (seq (set x y) false))")
    }
  }

  //interp
  test("interp - set simple") {
    assertResult(PointerClosV(FdC(List(),SeqC(SetC("x",IdC("y")),FalseC())),List())) {
      interp("(lambda () (seq (set x y) false))")
    }
  }

  test("interp - set with seq and let") {
    assertResult(NumV(7)) {
      interp("(let ((x 5) (y 7)) ( (lambda () (seq (set x y) y)) ))")
    }
  }


  /** ------------- Test box ---------------- **/

  test("parse - box simple") {
    assertResult(FdExt(List(),BinOpExt("seq",BinOpExt("setbox",IdExt("x"),UnOpExt("unbox",IdExt("y"))),FalseExt()))) {
      parse("(lambda () (seq (setbox x (unbox y)) false))")
    }
  }

  //desugar
  test("desugar - box simple") {
    assertResult(FdC(List(),SeqC(SetboxC(IdC("x"),UnboxC(IdC("y"))),FalseC()))) {
      desugar("(lambda () (seq (setbox x (unbox y)) false))")
    }
  }



  /**
   * Helpers
   */
  def parse(expr: String): ExprExt = Parser.parse(expr)
  def desugar(expr: ExprExt): ExprC = Desugar.desugar(expr)
  def desugar(expr: String): ExprC = Desugar.desugar(parse(expr))
  def interp(expr: ExprC): Value = Interp.interp(expr)
  def interp(expr: String): Value = Interp.interp(desugar(parse(expr)))
}
