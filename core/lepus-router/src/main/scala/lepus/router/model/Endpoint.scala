/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.model

import lepus.router.http.RequestMethod

trait Endpoint {
  def method:      RequestMethod
  def description: Option[String]
}
