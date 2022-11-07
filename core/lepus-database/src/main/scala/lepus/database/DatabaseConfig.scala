/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import cats.data.NonEmptyList

/** Configuration of database settings to be retrieved from Conf file
  *
  * @param path
  *   Key values for conf file.
  * @param database
  *   Database Name
  * @param replication
  *   A value to distinguish between relational databases.
  */
case class DataSource(
  path:        String,
  database:    String,
  replication: String
):
  override final def equals(other: Any): Boolean = other match
    case that: DataSource =>
      (that _equal this) &&
      (this.path == that.path) &&
      (this.database == that.database) &&
      (this.replication == that.replication)
    case _ => false
  private def _equal(other: Any) = other.isInstanceOf[DataSource]

  override def toString: String = s"$path://$database/$replication"

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
  replication: NonEmptyList[String]
):

  def dataSource: NonEmptyList[DataSource] = replication.map(v => {
    DataSource(path, database, v)
  })

  override def toString: String = s"$path://$database/${ replication.toList.mkString("|") }"

object DatabaseConfig:

  val SYNTAX_DATABASE_CONFIG = """^([.\w]+)://(\w+?)$""".r

  def apply(str: String, replication: NonEmptyList[String]): DatabaseConfig = str match
    case SYNTAX_DATABASE_CONFIG(path, database) => DatabaseConfig(path, database, replication)
    case _ =>
      throw new IllegalArgumentException(
        s"""
           |$str does not match DatabaseConfig format
           |
           |example:
           |  DatabaseConfig(path://database)
           |
           |  // conf file contents
           |  path.database = {
           |    ...
           |  }
           |""".stripMargin
      )

  def apply(str: String, replication: String): DatabaseConfig =
    this.apply(str, NonEmptyList.one(replication))
