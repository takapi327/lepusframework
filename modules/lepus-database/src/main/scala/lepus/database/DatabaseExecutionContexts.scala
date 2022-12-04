/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

import java.util.concurrent.{ Executors, ExecutorService }

import scala.concurrent.ExecutionContext

import cats.effect.kernel.{ Resource, Sync }

/** copied from doobie-core:
  * https://github.com/tpolecat/doobie/blob/v1.0.0-RC2/modules/core/src/main/scala/doobie/util/ExecutionContexts.scala#L11
  */
object DatabaseExecutionContexts:

  /** Enum to handle thread types */
  enum ThreadType:
    case FIXED, CACHED
  object ThreadType:
    def findByName(name: String): ThreadType =
      ThreadType.values.find(_.toString == name.toUpperCase())
        .getOrElse(throw new IllegalArgumentException(s"Thread Type that matches $name does not exist. Thread Type must be FIXED or CACHED"))

  /** Resource yielding an `ExecutionContext` backed by a fixed-size pool. */
  def fixedThreadPool[F[_]](size: Int)(using sf: Sync[F]): Resource[F, ExecutionContext] =
    val alloc = sf.delay(Executors.newFixedThreadPool(size))
    val free  = (es: ExecutorService) => sf.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)

  /** Resource yielding an `ExecutionContext` backed by an unbounded thread pool. */
  def cachedThreadPool[F[_]](using sf: Sync[F]): Resource[F, ExecutionContext] =
    val alloc = sf.delay(Executors.newCachedThreadPool)
    val free  = (es: ExecutorService) => sf.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)

  /** Execution context that runs everything synchronously. This can be useful for testing. */
  object synchronous extends ExecutionContext:
    def execute(runnable: Runnable):     Unit = runnable.run()
    def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
