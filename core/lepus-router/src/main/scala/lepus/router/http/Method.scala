/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

enum Method:
  def is(method: String): Boolean =
    this.toString.toUpperCase.equalsIgnoreCase(method.toUpperCase)
  case Get, Head, Post, Put, Delete, Options, Patch, Connect, Trace
