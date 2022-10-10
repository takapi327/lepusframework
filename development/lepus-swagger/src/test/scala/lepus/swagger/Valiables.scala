/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import lepus.core.generic.Schema
import lepus.router.generic.semiauto.*

case class Address(zipCode: String, country: String, prefecture: String)
object Address:
  given Schema[Address] = deriveSchemer

case class User(name: String, address: Address)
object User:
  given Schema[User] = deriveSchemer
