package safe_interpreter

object Main {

  def main(args: Array[String]): Unit = {
    print(Reader.read("(list : Num (2 3))"))
  }
}