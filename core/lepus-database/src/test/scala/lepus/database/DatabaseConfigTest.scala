/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import org.specs2.mutable.Specification

object DatabaseConfigTest extends Specification:

  "Testing the DatabaseConfig" should {
    "DatabaseConfig matches the specified string" in {
      DatabaseConfig("lepus://database").toString === "lepus://database"
    }

    "DatabaseConfig matches the specified string" in {
      DatabaseConfig("lepus://database/writer").toString === "lepus://database/writer"
    }

    "DatabaseConfig named matches the specified string" in {
      DatabaseConfig("lepus://database").named === "database"
    }

    "DatabaseConfig named matches the specified string" in {
      DatabaseConfig("lepus://database/writer").named === "database_writer"
    }

    "IllegalArgumentException is raised if the specified argument format is not matched" in {
      DatabaseConfig("Exception") must throwAn[IllegalArgumentException]
    }
  }
