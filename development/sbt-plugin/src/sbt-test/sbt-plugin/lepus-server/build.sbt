/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import scala.collection.mutable.ListBuffer

lazy val root = (project in file("."))
  .settings(
    name         := "lepus-server-scripted-test",
    scalaVersion := sys.props.get("scala.version").getOrElse("2.13.7"),
    version      := "0.1",
    run / fork   := true,
    javaOptions ++= Seq(
      "-Dconfig.file=conf/application.conf"
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4"
    ),
    InputKey[Unit]("callRequestAPI") := {
      val messages = ListBuffer.empty[String]

      val args           = Def.spaceDelimited("<path> <status> ...").parsed
      val path :: status = args

      try {
        val (responseStatus, _) = ScriptedTools.callUrl(path)

        if (status == responseStatus) messages += s"Resource at $path returned $status as expected"
        else throw new RuntimeException(s"Resource at $path returned $responseStatus instead of $status")

        messages.foreach(println)
      } catch {
        case e: Exception =>
          println(s"Got exception: $e. Cause was ${e.getCause}")
          throw e
      }
    }
  )
  .enablePlugins(Lepus)