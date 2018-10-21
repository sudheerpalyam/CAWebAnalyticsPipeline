package au.com.ca.assessment.spark.play

object HelloWorld {
  def main(args: Array[String]) {
    val input = scala.io.StdIn.readLine()
    try {
      val num = input.toInt
      for (i <- 1 to num) println("HELLO")
    } catch {
      case _: NumberFormatException =>
        println("that's not a number, try again")
    }
  }
}