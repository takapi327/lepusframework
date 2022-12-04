/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import com.google.inject.AbstractModule

import cats.effect.{ Resource, Sync }

import lepus.guice.module.ResourceModule

/** Trait to generate [[com.google.inject.AbstractModule]] for Google guice and [[lepus.guice.module.ResourceModule]]
  * for Lepus
  */
trait GuiceInjectBuilder:

  /** Method to generate an [[com.google.inject.AbstractModule]] from a [[lepus.guice.module.ResourceModule]] and return
    * it as a Resource
    */
  def loadResourceModules[F[_]: Sync](): Resource[F, Seq[AbstractModule]] =
    val default: Resource[F, Seq[AbstractModule]] = Resource.eval(Sync[F].delay(Seq.empty))
    val resourceModules: Seq[Resource[F, AbstractModule]] =
      ModuleLoader.load().map {
        case module: ResourceModule[?, ?] => module.build.asInstanceOf[Resource[F, AbstractModule]]
      }
    resourceModules.foldLeft(default)((_default, _resource) =>
      for
        modules <- _default
        module  <- _resource
      yield modules :+ module
    )

  /** Methods for generating [[com.google.inject.AbstractModule]]. An exception is raised if the type is not applicable.
    */
  def loadModules(): Seq[AbstractModule] =
    ModuleLoader.load().flatMap {
      case module: AbstractModule       => Some(module)
      case module: ResourceModule[?, ?] => None
      case unknown =>
        throw new IllegalArgumentException(s"Unknown module type, Module [$unknown] is not a a Guice module")
    }
