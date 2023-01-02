/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.session

import org.typelevel.vault.{ Key, Vault }

import cats.effect.SyncIO

object SessionKey:
  def apply[T]: Key[T] = Key.newKey[SyncIO, T].unsafeRunSync()

case object SessionReset:
  val key: Key[SessionReset.type] = SessionKey[SessionReset.type]

case class SessionRemove(list: List[Key[?]])
object SessionRemove:
  val key: Key[SessionRemove] = SessionKey[SessionRemove]
