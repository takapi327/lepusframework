/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.router.internal

// copied from tapir:
// https://github.com/softwaremill/tapir/blob/master/core/src/main/scala/sttp/tapir/typelevel/ParamConcat.scala

trait ParamConcat[T, U]:
  type Out

object ParamConcat extends LowPriorityTupleConcat4:
  given [U]: ParamConcat[Unit, Unit] with
    override type Out = Unit

  given [U]: ParamConcat[Nothing, Nothing] with
    override type Out = Nothing

  given [U]: ParamConcat[Nothing, Unit] with
    override type Out = Unit

  given [U]: ParamConcat[Unit, Nothing] with
    override type Out = Unit

trait LowPriorityTupleConcat4 extends LowPriorityTupleConcat3:
  given [U]: ParamConcat[Unit, U] with
    override type Out = U

  given [U]: ParamConcat[Nothing, U] with
    override type Out = U

trait LowPriorityTupleConcat3 extends LowPriorityTupleConcat2:
  given [T]: ParamConcat[T, Unit] with
    override type Out = T

  // for void outputs
  given [T]: ParamConcat[T, Nothing] with
    override type Out = T

trait LowPriorityTupleConcat2 extends LowPriorityTupleConcat1:
  given concatTuples[T, U, TU] (using TupleOps.JoinAux[T, U, TU]): ParamConcat[T, U] with
    override type Out = TU

trait LowPriorityTupleConcat1 extends LowPriorityTupleConcat0:
  given concatSingleAndTuple[T, U, TU] (using TupleOps.JoinAux[Tuple1[T], U, TU]): ParamConcat[T, U] with
    override type Out = TU

  given concatTupleAndSingle[T, U, TU] (using TupleOps.JoinAux[T, Tuple1[U], TU]): ParamConcat[T, U] with
    override type Out = TU

trait LowPriorityTupleConcat0:
  type Aux[T, U, TU] = ParamConcat[T, U] { type Out = TU }

  given [T, U, TU] (using TupleOps.JoinAux[Tuple1[T], Tuple1[U], TU]): ParamConcat[T, U] with
    override type Out = TU
