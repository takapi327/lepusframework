/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.server

import javax.inject.Singleton

import com.google.inject.{ AbstractModule, TypeLiteral }

import cats.effect.{ IO, Resource }

import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

import lepus.guice.module.ResourceModule

/**
 * Module for building http4s Ember Client
 * If you want to customize your configuration instead of the default client, you must inherit this module to build it.
 *
 * example:
 * {{{
 *   @Singleton
 *   class CustomClientModule extends ClientModule[IO, Client[IO]]:
 *     protected val resource: Resource[IO, Client[IO]] =
 *       ...custom settings
 *
 *   // setting conf file
 *   lepus.modules.disable += "lepus.server.DefaultClientModule"
 *   lepus.modules.enable  += "path.package.CustomClientModule"
 * }}}
 */
trait ClientModule extends ResourceModule[IO, Client[IO]]:
  protected val resource: Resource[IO, Client[IO]] =
    EmberClientBuilder.default[IO].build

  override private[lepus] lazy val build: Resource[IO, AbstractModule] =
    resource.map(v =>
      new AbstractModule:
        override def configure(): Unit =
          bind(new TypeLiteral[Client[IO]]() {})
            .toInstance(v)
    )

/**
 * Default Http4s client
 */
@Singleton
class DefaultClientModule extends ClientModule
