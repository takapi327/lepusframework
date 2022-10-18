/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

/** Aliases for top-level imports defined in doobie
  */
trait LepusDoobie extends doobie.Aliases with doobie.hi.Modules with doobie.free.Modules with doobie.free.Types:

  object implicits
    extends doobie.free.Instances
       with doobie.syntax.AllSyntax
       with doobie.util.meta.SqlMeta
       with doobie.util.meta.TimeMeta
       with doobie.util.meta.LegacyMeta
