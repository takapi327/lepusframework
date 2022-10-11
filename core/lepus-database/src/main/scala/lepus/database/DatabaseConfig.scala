/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

/** Configuration of database settings to be retrieved from Conf file
  *
  * @param path
  *   Key values for conf file.
  * @param hostspec
  *   A value to distinguish between relational databases.
  * @param database
  *   Database Name
  */
case class DatabaseConfig(
  path:     String,
  hostspec: String,
  database: String
):

  override final def equals(other: Any): Boolean = other match
    case that: DatabaseConfig =>
      (that _equal this) &&
      (this.path == that.path) &&
      (this.hostspec == that.hostspec) &&
      (this.database == that.database)
    case _ => false
  private def _equal(other: Any) = other.isInstanceOf[DatabaseConfig]

  override def toString: String = s"$path://$hostspec/$database"

object DatabaseConfig:

  val SYNTAX_DATABASE_CONFIG = """^([.\w]+)://(\w+?)/(\w+)$""".r

  def apply(str: String): DatabaseConfig = str match
    case SYNTAX_DATABASE_CONFIG(path, hostspec, database) => DatabaseConfig(path, hostspec, database)
    case _ =>
      throw new IllegalArgumentException(
        s"""
         |$str does not match DatabaseConfig format
         |
         |example:
         |  path://hostspec/database
         |""".stripMargin
      )