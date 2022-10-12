/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.database

import org.specs2.mutable.Specification

import lepus.core.generic.Schema
import lepus.core.generic.semiauto.*

object SchemaHelperTest extends Specification, SchemaHelper:

  "Testing the DoobieQueryHelper buildAnyValues method" should {

    "For size 2 the result of buildAnyValues is ?, ? in the case of size 2" in {
      buildAnyValues(2) === "?, ?"
    }

    "For size 1 the result of buildAnyValues is ? in the case of size 1" in {
      buildAnyValues(1) === "?"
    }

    "For size 0 the result of buildAnyValues is empty in the case of size 0" in {
      buildAnyValues(0) === ""
    }
  }

  "Testing the DoobieQueryHelper schemaFieldSize method" should {
    "A class with one parameter has a size of 1" in {
      case class Test(param: String)

      schemaFieldSize(deriveSchemer[Test]) === 1
    }

    "A class with zero parameter has a size of 0" in {
      case class Test()

      schemaFieldSize(deriveSchemer[Test]) === 0
    }
  }

  "Testing the DoobieQueryHelper schemaFieldNames method" should {
    "Test(param: String) becomes param" in {
      case class Test(param: String)

      schemaFieldNames(deriveSchemer[Test]) === "param"
    }

    "User(name: String, age: Long) becomes name, age" in {
      case class User(name: String, age: Long)

      schemaFieldNames(deriveSchemer[User]) === "name, age"
    }

    "From no parameter to character" in {
      case class Test()

      schemaFieldNames(deriveSchemer[Test]) === ""
    }
  }

  "Testing the DoobieQueryHelper schemaFieldNames with Naming method" should {
    "If camelCase is specified, all parameters are in camelCase format" in {
      case class Test(camelCase: String, PascalCase: String, snake_case: String, `kebab-case`: String)

      schemaFieldNames(deriveSchemer[Test], CAMEL) === "camelCase, pascalCase, snakeCase, kebabCase"
    }

    "If PascalCase is specified, all parameters are in PascalCase format" in {
      case class Test(camelCase: String, PascalCase: String, snake_case: String, `kebab-case`: String)

      schemaFieldNames(deriveSchemer[Test], PASCAL) === "CamelCase, PascalCase, SnakeCase, KebabCase"
    }

    "If snake_case is specified, all parameters are in snake_case format" in {
      case class Test(camelCase: String, PascalCase: String, snake_case: String, `kebab-case`: String)

      schemaFieldNames(deriveSchemer[Test], SNAKE) === "camel_case, pascal_case, snake_case, kebab_case"
    }

    "If kebab-case is specified, all parameters are in kebab-case format" in {
      case class Test(camelCase: String, PascalCase: String, snake_case: String, `kebab-case`: String)

      schemaFieldNames(deriveSchemer[Test], KEBAB) === "camel-case, pascal-case, snake-case, kebab-case"
    }
  }
