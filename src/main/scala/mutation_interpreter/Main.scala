package mutation_interpreter

object Main {

  def main(args: Array[String]): Unit = {
    print(Reader.read("((lambda (x) (x)) 1)"))
  }

}