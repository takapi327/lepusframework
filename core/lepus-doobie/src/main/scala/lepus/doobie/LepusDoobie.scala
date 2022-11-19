/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

import scala.annotation.targetName

import cats.data.{ OptionT, EitherT, Kleisli }

import cats.effect.IO
import cats.effect.kernel.MonadCancelThrow

import fs2.{ Pipe, Stream }

/** Aliases for top-level imports defined in doobie
  */
trait LepusDoobie extends doobie.Aliases, doobie.hi.Modules, doobie.free.Modules, doobie.free.Types:

  object implicits
    extends doobie.free.Instances,
            doobie.syntax.AllSyntax,
            doobie.util.meta.SqlMeta,
            doobie.util.meta.TimeMeta,
            doobie.util.meta.LegacyMeta,
            syntax.ConnectionIOOps,
            syntax.TransactOps
