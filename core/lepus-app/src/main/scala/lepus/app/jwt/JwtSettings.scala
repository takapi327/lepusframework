/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.app.jwt

import javax.inject.Inject

/**
 * Setup to build Jwt.
 */
trait JwtSettings:

  /** Configuration to get the settings for building Jwt from the conf file. */
  val configReader: JwtConfigReader

  /** Setup for formatting Jwt. */
  val formatter: JwtFormatter

case class DefaultJwtSettings @Inject()(
  configReader: JwtConfigReader,
  formatter:    JwtFormatter
) extends JwtSettings
