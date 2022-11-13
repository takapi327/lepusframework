/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database.specs2

import cats.effect.{ Async, IO }

import doobie.{ Update, Update0, ConnectionIO, HC }
import doobie.syntax.connectionio.*
import doobie.util.query.{ Query, Query0 }
import doobie.util.testing.*
import doobie.util.analysis.Analysis

import org.specs2.mutable.Specification
import org.specs2.specification.core.{ Fragment, Fragments }
import org.specs2.specification.create.FormattingFragments as Format
import org.specs2.specification.dsl.Online.*

import org.tpolecat.typename.*

/** copied from doobie-specs2:
  * https://github.com/tpolecat/doobie/blob/v1.0.0-RC2/modules/specs2/src/main/scala/doobie/specs2/analysisspec.scala
  *
  * Module with a mix-in trait for specifications that enables checking of doobie `Query` and `Update` values.
  * {{{
  * // An example specification, taken from the examples project.
  * class AnalysisTestSpec extends Specification, IOChecker {
  *
  *   // The transactor to use for the tests.
  *   val transactor = Transactor.fromDriverManager[IO](
  *     "org.postgresql.Driver",
  *     "jdbc:postgresql:world",
  *     "postgres",
  *     ""
  *   )
  *
  *   // Now just mention the queries. Arguments are not used.
  *   check(MyDaoModule.findByNameAndAge(null, 0))
  *   check(MyDaoModule.allWoozles)
  *
  * }
  * }}}
  */
trait Checker[F[_]] extends CheckerBase[F]:
  this: Specification =>

  def check[A: Analyzable](a: A): Fragments =
    checkImpl(Analyzable.unpack(a))

  def checkOutput[A: TypeName](q: Query0[A]): Fragments =
    checkImpl(
      AnalysisArgs(
        s"Query0[${ typeName[A] }]",
        q.pos,
        q.sql,
        q.outputAnalysis
      )
    )

  def checkOutput[A: TypeName, B: TypeName](q: Query[A, B]): Fragments =
    checkImpl(
      AnalysisArgs(
        s"Query[${ typeName[A] }, ${ typeName[B] }]",
        q.pos,
        q.sql,
        q.outputAnalysis
      )
    )

  def checkOutput[A: TypeName](q: Update[A]): Fragments =
    checkImpl(
      AnalysisArgs(
        s"Update[${ typeName[A] }]",
        q.pos,
        q.sql,
        q.analysis
      )
    )

  // TODO: HC.prepareUpdateAnalysis0(q.sql) will be replaced by the outputAnalysis method after the following pull request release
  // https://github.com/tpolecat/doobie/pull/1764
  def checkOutput(q: Update0): Fragments =
    checkImpl(
      AnalysisArgs(
        "Update0",
        q.pos,
        q.sql,
        HC.prepareUpdateAnalysis0(q.sql)
      )
    )

  private def checkImpl(args: AnalysisArgs): Fragments =
    // continuesWith is necessary to make sure the query doesn't run too early
    s"${ args.header }\n\n${ args.cleanedSql.padLeft("  ").toString }\n" >> ok.continueWith {
      val report: AnalysisReport = U.unsafeRunSync(analyze(args).transact(transactor))
      indentBlock(
        report.items.map { item =>
          item.description ! item.error.fold(ok) { err =>
            ko(err.wrap(70).toString)
          }
        }
      )
    }

  private def indentBlock(fs: Seq[Fragment]): Fragments =
    // intersperse fragments with newlines, and indent them.
    // This differs from standard version (FragmentsDsl.fragmentsBlock()) in
    // that any failure gets properly indented, too.
    Fragments.empty
      .append(Format.t)
      .append(fs.flatMap(Seq(Format.br, _)))
      .append(Format.bt)

trait IOChecker extends Checker[IO]:
  self: Specification =>

  import cats.effect.unsafe.implicits.global
  override given M: Async[IO] = IO.asyncForIO
  override given U: UnsafeRun[IO] = new UnsafeRun[IO]:
    def unsafeRunSync[A](ioa: IO[A]): A = ioa.unsafeRunSync()
