/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

lazy val root = (project in file("."))
  .settings(
    name         := "lepus-swagger-scripted-test",
    scalaVersion := sys.props.get("scala.version").getOrElse("2.13.7"),
    version      := "0.1",
    run / fork   := true,
    javaOptions ++= Seq(
      "-Dconfig.file=conf/application.conf"
    ),
    swaggerTitle   := name.value,
    swaggerVersion := version.value
  )
  .enablePlugins(Lepus)
  .enablePlugins(LepusSwagger)
