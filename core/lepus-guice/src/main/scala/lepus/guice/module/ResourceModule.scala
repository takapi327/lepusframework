/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.module

import scala.reflect.ClassTag

import com.google.inject.AbstractModule

import cats.effect.Resource

/** Module for incorporating what is built in [[cats.effect.Resource]] at implementation time into the Inject of guice
  */
trait ResourceModule[F[_], T: ClassTag]:

  /** Define implementation in Resource
    */
  protected val resource: Resource[F, T]

  /** Methods to build [[com.google.inject.AbstractModule]] so that what is built in [[cats.effect.Resource]] can be DI
    * in guice
    */
  private[lepus] lazy val build: Resource[F, AbstractModule] =
    resource.map(v =>
      new AbstractModule:
        override def configure(): Unit =
          bind(summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]).toInstance(v)
    )
