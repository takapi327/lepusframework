package lepus.router.mvc

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

import org.specs2.mutable.Specification

object BodyConverterTest extends Specification {

  case class Person(name: String, age: Option[Long])

  object Person {
    implicit lazy val encoder: Encoder[Person] = deriveEncoder
    implicit lazy val decoder: Decoder[Person] = deriveDecoder
  }

  val person       = Person("takapi", Some(26))
  val personString = "{\"name\":\"takapi\",\"age\":26}"

  "Testing the BodyConverter" should {

    "If toJson succeeds, whether or not the return value matches JsValue" in {
      val encoded = BodyConverter.Json.toJson[Person](person)
      encoded must beAnInstanceOf[ConvertResult.JsValue[Person]]
    }

    "Can the specified model be converted to a Json string" in {
      val encoded = BodyConverter.Json.toJson[Person](Person("takapi", Some(26)))
      encoded.toString must_== personString
    }

    "Can you convert a string to a specified model" in {
      val decoded = BodyConverter.Json.toJson[Person](personString)
      decoded must_== ConvertResult.JsValue(Person("takapi", Some(26)))
    }

    "Decode the encoded value to see if it matches the original value" in {
      val encoded = BodyConverter.Json.toJson[Person](person)
      val decoded = BodyConverter.Json.toJson[Person](encoded.toString)

      decoded must_== ConvertResult.JsValue(person)
    }

    "Encode with the decoded value and see if it matches the original value" in {
      val decoded = BodyConverter.Json.toJson[Person](personString)
      val encoded = BodyConverter.Json.toJson[Person](decoded.asInstanceOf[ConvertResult.JsValue[Person]].value)

      encoded.toString must_== personString
    }
  }
}
