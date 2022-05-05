/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

sealed abstract class RequestMethod(key: String) {
  override def toString(): String  = key
  def is(method: String):  Boolean = key.equalsIgnoreCase(method)
}

object RequestMethod {

  type GET     = Get.type
  type HEAD    = Head.type
  type POST    = Post.type
  type PUT     = Put.type
  type DELETE  = Delete.type
  type OPTIONS = Options.type
  type PATCH   = Patch.type
  type CONNECT = Connect.type
  type TRACE   = Trace.type

  object Get     extends RequestMethod("GET")
  object Head    extends RequestMethod("HEAD")
  object Post    extends RequestMethod("POST")
  object Put     extends RequestMethod("PUT")
  object Delete  extends RequestMethod("DELETE")
  object Options extends RequestMethod("OPTIONS")
  object Patch   extends RequestMethod("PATCH")
  object Connect extends RequestMethod("CONNECT")
  object Trace   extends RequestMethod("TRACE")

  val all = List(Get, Head, Post, Put, Delete, Options, Patch, Connect, Trace)
}
