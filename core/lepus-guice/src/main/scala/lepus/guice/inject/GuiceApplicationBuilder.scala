/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import com.google.inject.{ Guice, Injector }

import cats.effect.{ Resource, IO }

/**
  * Object to generate an [[com.google.inject.Injector]] for Google guice using the module used in the application
  */
object GuiceApplicationBuilder extends GuiceInjectBuilder:

  /**
    * Generate an [[com.google.inject.Injector]] from the module to be used in the application.
    * Since the Resource module is included, the return value is [[cats.effect.Resource]].
    */
  def build: Resource[IO, Injector] =
    loadResouceModules().map(modules =>
      Guice.createInjector((modules ++ loadModules()): _*)
    )
