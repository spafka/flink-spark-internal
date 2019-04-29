import io.github.spafka.spark.util.Utils

object FuncTest {

  def main(args: Array[String]): Unit = {

    val max = (x: Int, y: Int) => {
      println(s"$x,$y")
      if (x < y) y else x
    }

    val a = func2(f2 = max)

    println(a(1, 2))

  }

  def func2(f2: (Int, Int) ⇒ Int) = {
    import java.io.{
      ByteArrayInputStream,
      ByteArrayOutputStream,
      ObjectInputStream,
      ObjectOutputStream
    }

    import scala.runtime.AbstractFunction2

   val array = Utils.serialize[Function2[Int,Int,Int]](f2)

    val str = new String(array)

    str.split("\n").foreach(println)
    f2
  }

}
