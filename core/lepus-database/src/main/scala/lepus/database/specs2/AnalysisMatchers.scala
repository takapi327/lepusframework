/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database.specs2

import cats.effect.{ Async, IO }
import cats.instances.list.*
import cats.syntax.foldable.*

import doobie.syntax.connectionio.*
import doobie.util.pretty.*
import doobie.util.testing.{ AnalysisReport, Analyzable, analyze, CheckerBase, UnsafeRun }

import org.specs2.matcher.{ Expectable, Matcher, MatchResult }

/** copied from doobie-specs2:
  * https://github.com/tpolecat/doobie/blob/v1.0.0-RC2/modules/specs2/src/main/scala/doobie/specs2/analysismatchers.scala
  *
  * Provides matcher syntax for query checking:
  *
  * {{{
  * sql"select 1".query[Int] must typecheck
  * }}}
  */
trait AnalysisMatchers[F[_]] extends CheckerBase[F]:

  def typecheck[T](using analyzable: Analyzable[T]): Matcher[T] =
    new Matcher[T]:
      def apply[S <: T](t: Expectable[S]): MatchResult[S] =
        val report = U.unsafeRunSync(
          analyze(
            analyzable.unpack(t.value)
          ).transact(transactor)
        )
        reportToMatchResult(report, t)

  private def reportToMatchResult[S](report: AnalysisReport, ex: Expectable[S]): MatchResult[S] =
    // We aim to produce the same format the fragment version does.

    val items = report.items.foldMap(itemToBlock)

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    val message =
      Block
        .fromString(report.header)
        .above(Block.fromString(""))
        .above(report.sql.wrap(70).padLeft("  "))
        .above(Block.fromString(""))
        .above(items)
        .toString

    Matcher.result(report.succeeded, message, message, ex)

  private def itemToBlock(item: AnalysisReport.Item): Block =
    item.error match
      case None =>
        Block.fromString(s"+ ${ item.description }")
      case Some(e) =>
        Block
          .fromString(s"x ${ item.description }")
          .above(
            Block.fromString(" x ").leftOf(e.wrap(70))
          )

trait IOAnalysisMatchers extends AnalysisMatchers[IO]:
  import cats.effect.unsafe.implicits.global
  override given M: Async[IO] = IO.asyncForIO
  override given U: UnsafeRun[IO] = new UnsafeRun[IO]:
    def unsafeRunSync[A](ioa: IO[A]): A = ioa.unsafeRunSync()
