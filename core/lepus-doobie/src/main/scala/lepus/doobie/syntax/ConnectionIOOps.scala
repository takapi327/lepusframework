/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.doobie.syntax

import scala.annotation.targetName

import lepus.doobie.ConnectionIO

/**
  * Cats Effect Defines the same extension methods as those defined in IO.
  */
trait ConnectionIOOps:

  extension [T](connection: ConnectionIO[T])

    /**
      * Runs the current ConnectionIO, then runs the parameter, keeping its result.
      * The result of the first action is ignored.
      * If the source fails, the other action won't run.
      * Evaluation of the parameter is done lazily, making this suitable for recursion.
      */
    @targetName("flatMapAlias1")
    def >>(that: => ConnectionIO[T]): ConnectionIO[T] =
      connection.flatMap(_ => that)

    @targetName("flatMapAlias2")
    def <<(that: => ConnectionIO[T]): ConnectionIO[T] =
      connection.flatMap(v => that.as(v))

    /**
      * Replaces the result of this IO with the given value.
      */
    def as[B](b: B): ConnectionIO[B] =
      connection.map(_ => b)

    /**
      * Combination of flatMap and as
      */
    def flatTap[B](f: T => ConnectionIO[B]): ConnectionIO[T] =
      connection.flatMap(a => f(a).as(a))
