# CPL (Concepts of Programming Languages)

This project is to build an interpreter using scala.

Credit to Mahmoud & Omar helping out with building last weeks parser and 
together we managed to have the best test combination.
As a result a lot of tests can also be found on their own repo with no differences.

Also most of the parsers have a lot in common since we worked together to get
things work on IntelliJ to proceed faster.

### To run this project:
- You need to have scala on your machine
- Import the project using IntelliJ 
- IntelliJ have set everything up for you, Have fun!

### Learn more!
- Object-Oriented programing languages: Application and Interpretation
    - https://users.dcc.uchile.cl/~etanter/ooplai/
- Programming Languages: Application and Interpretation
    - http://cs.brown.edu/courses/cs173/2012/book/index.html
- If you like reading books: Learn Scala from those books
    - https://docs.scala-lang.org/books.html

### Credit to CPL teaching team at TU Delft 
The design of a programming language is the result of a selection from a general collection of programming language concepts. With some frequency, new languages are introduced that combine concepts in an novel manner and sometimes introduce new concepts. During his/her career, a computer scientist will have to work with more than one generation of programming languages. In order to learn new programming languages, a computer scientist should understand the basic principles in the design of programming languages.


## Substitution based interpreter
In this week we extend the Paret language with multi-argument higher-order functions and let expressions.

We implement an interpreter that uses substitution, and it is eager( also known as call-by-value or strict) and it uses left-to-right evaluation order.

- Multi-Argument substitution
- Multi-Argument Functions
- Multi-Binder let Expressions

## Environment based interpreter
Though we have a working definition of functions, we feel unease about it!
First our interpreter does not look up identifier, and its behavior is defined to be an error!!! While absolutely correct.

Another problem with substitution is that it traverse the source program, substitution travers everything, and force the program to be 
traversed once when substitution and once when interpreting. And the last problem is that our interpreter has and needs to access the source to interpret it.

Simply said substitution has two main problems:
- It is computationally expensive (Multiple traversals of the Abstract Syntax Tree(AST))
- Requires two functions that both traverse the AST (i.e. substitute, and interpret).

Solution is Environment:
- Defer substitution resulting in new data structure called environment.
- Avoids the need for source to source rewriting
- Maps nicely to low-level machine representations
- Solve name shadowing problem and name capturing
- Remember where is the set of substitutions that a term is closed under.

Remember that we are not changing the language itself, we are only changing the implementation strategy for the programming language.

## Mutation interpreter

This interpreter will add constructs for state and recursion language.
To allow mutation boxes have been added. 

The main differences between store and environment:
- Store allow mutation, i.e. you can change value of variable not in scope
- With the help of store, recursive functions with multiple parameters are now allowed, and it is all because we have the pointer step in between,
this avoids Interceptions and help to achieve the last value. It was before that possible not because of the environment but because of the Z-combinator.

Let-rec accepts a sequence of possibly-mutually-recursive binders. That is each identifier bound in a let-rec expression should be in scope in each binder.

UninitializedC is here to help desugaring let-rec as a dummy value in the AppC list of expressions. 

## Type checking

In the safe interpreter, we now have added the static type checking, our language is not dynamically-typed anymore.

The safe interpretation has an extra function that checks the type annotations and the elements in the given expression, which means
if our elements in the list are given to be integers (Num) then they must all be so, otherwise a typeException is thrown.

The type checker takes a ExperExt as input, which means that type is checked before desugaring, and also receives a TEnvironment as parameter which maps identifiers to types.
And this also follows from that desugraing erases types, cause types are included in ExprExt whereas ExprX does not include types. And also type checking happens statically in other words before the program is run.


##### For type checking the base language, note the following:

- Functions: The syntax of functions has been extended to support type annotations on function parameters. Instead of (lambda (x y) (+ x y)), we will write (lambda ((x : Num) (y : Num)) (+ x y)). The type of this anonymous function is ((Num Num) -> Num). See the grammar for the full syntax definition.

- Recursive functions: The syntax of recursive functions (rec-lam) have been extended to support type annotations on rec-lam. Instead of (rec-lam f (x) (f x)) we will write (rec-lam (f : Num -> Bool) (x) (f x)). The type of this function is ((Num) -> Bool).

- Recursive lets: The syntax of recursive let bindings has also been extended with type annotations on identifiers. Instead of (letrec ((x 1)) x) we will write (letrec (((x : Num) 1)) x). The type of this expression is Num.

- Lists:
Lists are homogeneous (i.e. all list elements should have the same type). List expressions are explicitly annotated. Instead of (list 1 2) we write (list : Num (1 2)).
You should enforce that cons is only used to prepend items to a list, similar to Scala’s ::.
To disambiguate what the type of a nil list is, the empty list is explicitly annotated, using the syntax (nil : T) where T is a type.
Note that, since we are now working with a type checked language, the is-list construct from previous weeks no longer serves a useful purpose, and so has been dropped from the language of this week.

- Boxes: Boxes have the type (Ref T), where T is the type of the expression that is inside the box.

- If expressions: Your type checker should require that both branches of an if statement have the same type. So, for instance, (if true 3 true) should not type check.

- Other types: For each type of expression, you have to check the types of the arguments. It is up to you to find out what type each expression is supposed to return.

- Subtyping: You do not need to implement function subtyping, and should not write test cases that assume it will be implemented.

# Grammars used throughout the project
```
module types

imports Common

context-free syntax

- Here we have the basis of every grammar:
  - Expr.NumExt       = INT      // integer literals
  - Expr.TrueExt      = [true]
  - Expr.FalseExt     = [false]
  - Expr.IdExt        = ID

- Then we have the binary and unary operations:
  - Expr.UnOpExt      = [([UnOp] [Expr])]
    - UnOp.MIN          = [-]
    - UnOp.NOT          = [not]
    - UnOp.HEAD         = [head]
    - UnOp.TAIL         = [tail]
    - UnOp.ISNIL        = [is-nil]
    - UnOp.BOX          = [box]
    - UnOp.UNBOX        = [unbox]
    - UnOp.FST          = [fst]
    - UnOp.SND          = [snd]

  - Expr.BinOpExt     = [([BinOp] [Expr] [Expr])]
    - BinOp.PLUS        = [+]
    - BinOp.MULT        = [*]
    - BinOp.MINUS       = [-]
    - BinOp.AND         = [and]
    - BinOp.OR          = [or]
    - BinOp.NUMEQ       = [num=]
    - BinOp.NUMLT       = [num<]
    - BinOp.NUMGT       = [num>]
    - BinOp.CONS        = [cons]
    - BinOp.SETBOX      = [setbox]
    - BinOp.SEQ         = [seq]
    - BinOp.PAIR        = [pair]

  

  
  
  Expr.IfExt        = [(if [Expr] [Expr] [Expr])]
  Expr.NilExt       = [(nil : [Type])]
  Expr.ListExt      = [(list : [Type] ([Expr*]))]
  Param.Param       = [([ID] : [Type])]
  Expr.FdExt        = [(lambda ([Param*]) [Expr])]
  Expr.AppExt       = [([Expr] [Expr*])]
  Expr.LetExt       = [(let ([LetBind+]) [Expr])]
  Expr.LetRecExt    = [(letrec ([LetRecBind+]) [Expr])]
  Expr.Set          = [(set [ID] [Expr])]
  
  LetBind.LetBindExt = [([ID] [Expr])]
  LetRecBind.LetRecBind = [([Param] [Expr])]
  Expr.RecLamExt    = [(rec-lam ([ID] : [Type] -> [Type]) ([ID]) [Expr])]
  
  
  Type.NumT         = [Num]
  Type.BoolT        = [Bool]
  Type.ListT        = [(List [Type])]
  Type.FunT         = [(([Type*]) -> [Type])]
  Type.PairT        = [(Pair [Type] [Type])]
  Type.RefT         = [(Ref [Type])]
```

# Classes

### Abstract Syntax
```
case class TrueExt() extends ExprExt
case class FalseExt() extends ExprExt
case class NumExt(num: Int) extends ExprExt
case class BinOpExt(s: String, l: ExprExt, r: ExprExt) extends ExprExt
case class UnOpExt(s: String, e: ExprExt) extends ExprExt
case class IfExt(c: ExprExt, t: ExprExt, e: ExprExt) extends ExprExt
case class NilExt(listTy: Type) extends ExprExt
case class ListExt(listTy: Type, es: List[ExprExt]) extends ExprExt
case class AppExt(f: ExprExt, args: List[ExprExt]) extends ExprExt
case class IdExt(c: String) extends ExprExt
case class FdExt(params: List[Param], body: ExprExt) extends ExprExt
case class LetExt(binds: List[LetBindExt], body: ExprExt) extends ExprExt
case class SetExt(id: String, e: ExprExt) extends ExprExt
case class RecLamExt(name: String,
                     paramTy: Type,
                     retTy: Type,
                     param: String,
                     body: ExprExt) extends ExprExt
case class LetRecExt(binds: List[LetRecBindExt],
                     body: ExprExt) extends ExprExt

case class LetBindExt(name: String, value: ExprExt)
case class LetRecBindExt(name: String, ty: Type, value: ExprExt)

object ExprExt {
  val binOps = Set("+", "*", "-", "and", "or", "num=", "num<", "num>",
    "cons", "setbox", "seq", "pair")
  val unOps = Set("-", "not", "head", "tail", "is-nil", "box", "unbox", "fst", "snd")
  val reservedWords = binOps ++ unOps ++ Set("list", "if", "lambda", 
    "let", "true", "false", "rec-lam", "set", "letrec",
    ":", "->", "Num", "Bool", "List", "Pair", "Ref")
}
```

### Desugared Syntax (Core)

```$xslt
abstract class ExprC
case class TrueC() extends ExprC
case class FalseC() extends ExprC
case class NumC(num: Int) extends ExprC
case class PlusC(l: ExprC, r: ExprC) extends ExprC
case class MultC(l: ExprC, r: ExprC) extends ExprC
case class IfC (c: ExprC, t: ExprC, e: ExprC) extends ExprC
case class EqNumC(l: ExprC, r: ExprC) extends ExprC
case class LtC(l: ExprC, r: ExprC) extends ExprC
case class NilC() extends ExprC
case class ConsC(l: ExprC, r: ExprC) extends ExprC
case class HeadC(e: ExprC) extends ExprC
case class TailC(e: ExprC) extends ExprC
case class IsNilC(e: ExprC) extends ExprC
case class AppC(f: ExprC, args: List[ExprC]) extends ExprC
case class IdC(c: String) extends ExprC
case class FdC(params: List[String], body: ExprC) extends ExprC
case class BoxC(v: ExprC) extends ExprC
case class UnboxC(b: ExprC) extends ExprC
case class SetboxC(b: ExprC, v: ExprC) extends ExprC
case class SetC(v: String, b: ExprC) extends ExprC
case class SeqC(b1: ExprC, b2: ExprC) extends ExprC
case class UninitializedC() extends ExprC
case class PairC(l: ExprC, r: ExprC) extends ExprC
case class FstC(e: ExprC) extends ExprC
case class SndC(e: ExprC) extends ExprC
```

### Values
```$xslt
abstract class Value
case class NumV(v: Int) extends Value
case class BoolV(v: Boolean) extends Value
case class NilV() extends Value
case class ConsV(hd: Value, tl: Value) extends Value
case class PointerClosV(f: FdC, env: List[Pointer]) extends Value
case class BoxV(l: Int) extends Value
case class UninitializedV() extends Value
case class PairV(l: Value, r: Value) extends Value
```

### Types
```$xslt
sealed abstract class Type
case class NumT() extends Type
case class BoolT() extends Type
case class FunT(paramTy: List[Type],
                retTy: Type) extends Type
case class ListT(expTy: Type) extends Type
case class PairT(fst: Type, snd: Type) extends Type
case class RefT(t: Type) extends Type
```

### Exceptions and data structures
```$xslt

case class Bind(name: String, value: Value)
type Environment = List[Bind]

case class Pointer(name: String, location: Int)
case class Cell(location: Int, value: Value)
case class TBind(name: String, ty: Type)
case class Param(name: String, ty: Type)

abstract class ParseException   extends RuntineException
abstract class DesugarException extends RuntimeException
abstract class InterpException  extends RuntimeException
abstract class TypeException    extends RuntimeException
```