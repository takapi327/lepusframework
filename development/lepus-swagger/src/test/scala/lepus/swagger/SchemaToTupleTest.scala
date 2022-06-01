/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.swagger

import org.specs2.mutable.Specification

import lepus.router.model.Schema
import lepus.router.generic.semiauto._

case class Address(zipCode: String, country: String, prefecture: String)
object Address {
  implicit val schema: Schema[Address]  = deriveSchemer
}

case class User(name: String, address: Address)
object User {
  implicit val schema: Schema[User]  = deriveSchemer
}

object SchemaToTupleTest extends Specification {
  val schemaToTuple    = new SchemaToTuple()
  val userSchemaTuples = schemaToTuple(User.schema)

  "Testing the SchemaToTuple" should {

    "Contains the specified Shame Name." in {
      val schemaNameUser = Schema.Name("lepus.swagger.User", List())
      userSchemaTuples.map(_._1).contains(schemaNameUser)
    }

    "Contains the specified Shame Name and Schema Tuple." in {
      val schemaNameUser  = Schema.Name("lepus.swagger.User", List())
      val userSchemaTuple = (schemaNameUser, User.schema)
      userSchemaTuples.contains(userSchemaTuple)
    }

    "Schema Name of the nested model is also included." in {
      val schemaNameAddress = Schema.Name("lepus.swagger.Address", List())
      userSchemaTuples.map(_._1).contains(schemaNameAddress)
    }

    "Schema Name and Schema Tuple of the nested model are also included." in {
      val schemaNameAddress  = Schema.Name("lepus.swagger.Address", List())
      val addressSchemaTuple = (schemaNameAddress, Address.schema)
      userSchemaTuples.contains(addressSchemaTuple)
    }

    "Nothing but the specified Schema Name is generated." in {
      val schemaNameUser    = Schema.Name("lepus.swagger.User", List())
      val schemaNameAddress = Schema.Name("lepus.swagger.Address", List())
      userSchemaTuples.map(_._1).forall(v => v == schemaNameUser || v == schemaNameAddress)
    }

    "Tuples can be generated from Schema with Shame Name as the key." in {
      val schemaNameUser     = Schema.Name("lepus.swagger.User", List())
      val schemaNameAddress  = Schema.Name("lepus.swagger.Address", List())
      val userSchemaTuple    = (schemaNameUser, User.schema)
      val addressSchemaTuple = (schemaNameAddress, Address.schema)
      println(userSchemaTuples.map(_._1))
      userSchemaTuples.map(_._1).contains(schemaNameUser) &&
      userSchemaTuples.map(_._1).contains(schemaNameAddress) &&
      userSchemaTuples.contains(userSchemaTuple) &&
      userSchemaTuples.contains(addressSchemaTuple)
    }
  }
}
