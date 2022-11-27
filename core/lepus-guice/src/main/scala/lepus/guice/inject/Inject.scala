/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import scala.reflect.ClassTag

import com.google.inject.Injector

/**
  * Objects to create instances using Google guice [[com.google.inject.Injector]]
  */
object Inject:

  def apply[T](clazz: Class[T])(using injector: Injector): T =
    injector.getInstance[T](clazz)
  def apply[T: ClassTag](using Injector): T =
    this.apply[T](summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
