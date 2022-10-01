/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

case class DataSource(
  path:     String,
  hostspec: String,
  database: String
):

  override def toString: String = s"$path://$hostspec/$database"

object DataSource:

  val SYNTAX_DATA_SOURCE = """^([.\w]+)://(\w+?)/(\w+)$""".r

  def apply(str: String): DataSource = str match
    case SYNTAX_DATA_SOURCE(path, hostspec, database) => DataSource(path, hostspec, database)
    case _ =>
      throw new IllegalArgumentException(
        s"""
         |$str does not match DataSource format
         |
         |example:
         |  path://hostspec/database
         |""".stripMargin
      )

type DataSourceCF[T] = DataSource ?=> T
