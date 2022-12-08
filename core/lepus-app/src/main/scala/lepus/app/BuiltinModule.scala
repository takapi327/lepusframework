/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app

import javax.inject.{ Singleton, Provider }

import com.google.inject.AbstractModule

import lepus.core.util.Configuration

/**
 * Modules that are included by default in the application
 */
@Singleton
private[lepus] class BuiltinModule extends AbstractModule:
  override def configure(): Unit =
    bind(classOf[Configuration]).toProvider(classOf[Providers.ConfigurationProvider])
