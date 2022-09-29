/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie

trait LepusDoobie
  extends doobie.Aliases,
          doobie.hi.Modules,
          doobie.free.Modules,
          doobie.free.Types,
          doobie.util.meta.LegacyInstantMetaInstance,
          doobie.free.Instances,
          doobie.syntax.AllSyntax
