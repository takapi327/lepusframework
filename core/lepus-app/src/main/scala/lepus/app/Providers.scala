/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app

import javax.inject.{ Singleton, Provider }

import lepus.core.util.Configuration

/**
 * List of Providers to include by default in your application
 */
object Providers:

  @Singleton
  class ConfigurationProvider extends Provider[Configuration]:
    override def get(): Configuration = Configuration.load()
