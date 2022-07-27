/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import org.specs2.mutable.Specification

import lepus.router.model.Schema

import lepus.swagger.model.Reference

object SchemaToReferenceTest extends Specification:

  val schemaToTuple     = SchemaToTuple()
  val userSchemaTuples  = schemaToTuple(summon[Schema[User]])
  val schemaToReference = SchemaToReference(Some(userSchemaTuples.toListMap))

  "Testing the SchemaToReference" should {

    "The Reference conversion for a model with a Schema Name will be Some." in {
      val schemaNameUser = Schema.Name("lepus.swagger.User", List())
      schemaToReference.map(schemaNameUser).nonEmpty
    }

    "If the Schema does not have a Shame Name, it is set to None." in {
      val schemaNameUser    = Schema.Name("lepus.swagger.User", List())
      val stringSchemaTuple = schemaToTuple(summon[Schema[String]])
      val schemaToReference = new SchemaToReference(Some(stringSchemaTuple.toListMap))
      schemaToReference.map(schemaNameUser).isEmpty
    }

    "Models with Schema Name are converted to Reference." in {
      val schemaNameUser = Schema.Name("lepus.swagger.User", List())
      schemaToReference.map(schemaNameUser).contains(Reference(s"#/components/schemas/${ schemaNameUser.shortName }"))
    }
  }
