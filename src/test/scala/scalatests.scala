import org.junit.Test
import org.scalatest._
import org.scalatest.Matchers._

import scala.collection.mutable

class scalatests extends FunSuite {


  def maybeItWillReturnSomething(flag: Boolean): Option[String] = {
    if (flag) Some("Found value") else None
  }


  test("ss") {

    val value1 = maybeItWillReturnSomething(true)
    val value2 = maybeItWillReturnSomething(false)

    value1 getOrElse "No value" should be(
      "Found value"
    )
    value2 getOrElse "No value" should be(
      "No value"
    )
    value2 getOrElse {
      "default function"
    } should be(
      "default function"
    )
  }


  test("opention map") {
    val number: Option[Int] = Some(3)
    val noNumber: Option[Int] = None
    val result1 = number.map(_ * 1.5)
    val result2 = noNumber.map(_ * 1.5)

    result1 should be(
      Option[Double](4.5)
    )
    result2 should be(
      None
    )
  }

  test("option fold") {

    val number: Option[Int] = Some(3)
    val noNumber: Option[Int] = None
    val result1 = number.fold(1)(_ * 3)
    val result2 = noNumber.fold(1)(_ * 3)

    result1 should be(9)

    result2 should be(1)
  }


  test("high order function"){

    def addWithoutSyntaxSugar(x: Int): Function1[Int, Int] = {
      new Function1[Int, Int]() {
        def apply(y: Int): Int = x + y
      }
    }
    addWithoutSyntaxSugar(1).isInstanceOf[Function1[Int, Int]] should be(
      true
    )

    addWithoutSyntaxSugar(2)(3) should be(
      5
    )

    def fiveAdder: Function1[Int, Int] = addWithoutSyntaxSugar(5)
    fiveAdder(5) should be(
      10
    )
  }

  test("list internal"){
    val a = List(1, 3, 5, 7, 9)
    a(0) should equal(
    1)
    a(2) should equal(
    5)
    a(4) should equal(
    9)


    a.length

    a.reverse

  }


  test("immutableMap internal"){

    val m=Map(1->1,2->2,3->3,4->4,5->5)

    val i = m.get(1)

    i should be (1)

  }

  test("mutableMap internal"){

    val m=new mutable.HashMap[Int,Int]()

    m.put(1,1)


    val i = m.get(1)

    i should be (1)

  }

  test("case"){
    def goldilocks(expr: Any) = expr match {
      case ("porridge", bear) ⇒
        bear + " said someone's been eating my porridge"
      case ("chair", bear) ⇒ bear + " said someone's been sitting in my chair"
      case ("bed", bear) ⇒ bear + " said someone's been sleeping in my bed"
      case _ ⇒ "what?"
    }

    goldilocks(("porridge", "Papa")) should be(
      "Papa said someone's been eating my porridge"
    )
    goldilocks(("chair", "Mama")) should be(
      "Mama said someone's been sitting in my chair"
    )
  }
}
