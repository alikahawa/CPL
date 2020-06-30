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

## Grammars used throughout the project
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