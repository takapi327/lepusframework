/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.session

import cats.Functor
import cats.syntax.all.*

import cats.effect.{ Sync, Async }
import cats.effect.std.Random

import io.chrisdavenport.mapref.MapRef

/**
 * Session storage managed by the application
 *
 * @tparam F
 *   the effect type.
 * @tparam A
 *   Value managed by Session
 */
trait SessionStorage[F[_], A]:

  /** session identifier */
  def sessionId: F[SessionIdentifier]

  /** Methods for retrieving session values from session identifiers */
  def get(id: SessionIdentifier): F[Option[A]]

  /** Methods for updating Session values */
  def modify[B](id: SessionIdentifier, func: Option[A] => (Option[A], B)): F[B]

object SessionStorage:

  /**
   *  Default method to create SessionStorage
   *
   * @param numShards
   *   Desired number of elements to generate Random
   * @param numBytes
   *   The session ID length must be at least 128 bits (16 bytes)
   * @tparam F
   *   the effect type.
   * @tparam T
   *   Value managed by Session
   * @return
   *    A Session Storage
   */
  def default[F[_]: Sync: Async, T](
    numShards: Int = 4,
    numBytes:  Int = 32,
  ): F[SessionStorage[F, T]] =
    for
      random <- Random.javaSecuritySecureRandom(numShards)
      ref    <- MapRef.inShardedImmutableMap[F, F, SessionIdentifier, T](numShards)
    yield new MemorySessionStorage[F, T](random, numBytes, ref)

  /**
   * Class for in-memory Session storage using MapRef.
   *
   * @param random
   *   Random is the ability to get random information, each time getting a different result.
   * @param numBytes
   *   Minimum recommended by OWASP is 16 bytes if you have 64 bits of entropy
   * @param access
   *   This is a total Map from K to Ref[F, V]
   * @tparam F
   *   the effect type.
   * @tparam T
   *   Value managed by Session
   */
  private class MemorySessionStorage[F[_]: Functor, T](
    random:   Random[F],
    numBytes: Int,
    access:   MapRef[F, SessionIdentifier, Option[T]]
  ) extends SessionStorage[F, T]:

    override def sessionId: F[SessionIdentifier] = SessionIdentifier.create(random, numBytes)

    override def get(id: SessionIdentifier): F[Option[T]] = access(id).get

    override def modify[A](id: SessionIdentifier, func: Option[T] => (Option[T], A)): F[A] =
      access(id).modify(func)
