/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.logger

import io.circe.Json
import io.circe.syntax.*

object JsonFormatter extends Formatter:
  override def format(msg: LogMessage): String =
    val timestamp = Formatter.formatTimestamp(msg.timestamp)
    val context   = Formatter.formatCtx(msg.context)
    val fileName  = s"${ msg.execLocation.fileName }:${ msg.execLocation.lineNumber }"
    val json = Json.obj(
      "timestamp"     -> timestamp.asJson,
      "level"         -> msg.level.toString.asJson,
      "threadName"    -> msg.threadName.asJson,
      "enclosureName" -> msg.execLocation.enclosureName.asJson,
      "message"       -> msg.message.value.asJson,
      "fileName"      -> fileName.asJson,
      "context"       -> context.asJson
    )
    json.toString
