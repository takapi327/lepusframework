/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import org.specs2.mutable.Specification

object DatabaseConfigReaderTest extends Specification, DatabaseConfigReader:

  "Testing the DatabaseConfigReader" should {

    "The value retrieved by the key matches the specified value" in {
      val result = readConfig(_.get[Option[String]]("name"))(using DatabaseConfig("lepus://database"))
      result === Some("lepus2")
    }

    "The value retrieved by the key matches the specified value" in {
      val result = readConfig(_.get[Option[String]]("name"))(using DatabaseConfig("lepus://database/writer"))
      result === Some("lepus3")
    }

    "Can be retrieved from any level" in {
      given DatabaseConfig = DatabaseConfig("lepus://database/writer")
      val result1          = readConfig(_.get[Option[Int]]("level1"))
      val result2          = readConfig(_.get[Option[Int]]("level2"))
      val result3          = readConfig(_.get[Option[Int]]("level3"))
      result1 === Some(1) and result2 === Some(2) and result3 === Some(3)
    }

    "If there are duplicate keys in the hierarchy, the data at the deepest point is retrieved" in {
      given DatabaseConfig = DatabaseConfig("lepus://database/writer")

      val result = readConfig(_.get[Option[Int]]("duplication"))
      result === Some(3)
    }

    "None if the specified key does not exist." in {
      given DatabaseConfig = DatabaseConfig("lepus://database/writer")

      val result = readConfig(_.get[Option[Int]]("nothing"))
      result === None
    }

    "None if the specified key does not exist." in {
      given DatabaseConfig = DatabaseConfig("nothing://conf")

      val result = readConfig(_.get[Option[Int]]("nothing"))
      result === None
    }
  }
