import org.scalatest._
import org.scalatest.Matchers._

class test2 extends FunSuite {


  test("case list") {
    val secondElement = List(1, 2, 3) match {
      case x :: y :: xs ⇒ y
      case _ ⇒ 0
    }

    secondElement should be(
      2
    )
  }
}
