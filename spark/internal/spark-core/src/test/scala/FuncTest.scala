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

    val arrayOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val objectInputStream: ObjectOutputStream = new ObjectOutputStream(
      arrayOutputStream
    )

    objectInputStream.writeObject(f2)
    val function = new ObjectInputStream(
      new ByteArrayInputStream(arrayOutputStream.toByteArray)
    ).readObject().asInstanceOf[Function2[Any, Any, Any]]

    f2
  }

}
