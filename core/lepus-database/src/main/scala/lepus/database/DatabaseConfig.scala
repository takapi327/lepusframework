/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

/** Configuration of database settings to be retrieved from Conf file If replication is set up, you will have multiple
  * configurations.
  *
  * @param path
  *   Key values for conf file.
  * @param database
  *   Database Name
  * @param replication
  *   A value to distinguish between relational databases.
  */
case class DatabaseConfig(
  path:        String,
  database:    String,
  replication: Option[String]
):

  val named: String = database + replication.map(v => "_" + v).getOrElse("")

  override def toString: String = s"$path://$database${ replication.map(v => s"/$v").getOrElse("") }"

object DatabaseConfig:

  val SYNTAX_DATABASE_CONFIG1 = """^([.\w]+)://(\w+?)$""".r
  val SYNTAX_DATABASE_CONFIG2 = """^([.\w]+)://(\w+?)/(\w+)$""".r

  def apply(str: String): DatabaseConfig = str match
    case SYNTAX_DATABASE_CONFIG1(path, database)              => DatabaseConfig(path, database, None)
    case SYNTAX_DATABASE_CONFIG2(path, database, replication) => DatabaseConfig(path, database, Some(replication))
    case _ =>
      throw new IllegalArgumentException(
        s"""
          |$str does not match DatabaseConfig format
          |
          |example:
          |  path://database or path://database/replication
          |""".stripMargin
      )
