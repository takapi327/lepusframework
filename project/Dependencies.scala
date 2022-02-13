/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import sbt._

object Dependencies {

  val logback = "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4"

  val typesafeConfig = "com.typesafe" % "config" % "1.4.1"

  val catsVersion = "2.6.1"
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.3"

  val http4sVersion = "0.23.6"
  val http4s = Seq(
    "http4s-dsl",
    "http4s-blaze-server"
  ).map("org.http4s" %% _ % http4sVersion)

  val circeVersion = "0.14.1"
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val hikariCP = "com.zaxxer" % "HikariCP" % "5.0.0"

  val doobieVersion = "1.0.0-RC1"
  val doobie = Seq(
    "doobie-core",
    "doobie-hikari"
  ).map("org.tpolecat" %% _ % doobieVersion)

  val specs2Version = "4.12.12"
  val specs2Deps = Seq(
    "specs2-core",
    "specs2-junit",
    "specs2-mock"
  ).map("org.specs2" %% _ % specs2Version)

  val scalaTest  = "org.scalatest"     %% "scalatest"       % "3.2.11"   % Test
  val scalaCheck = "org.scalacheck"    %% "scalacheck"      % "1.15.4"   % Test
  val scalaPlus  = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test

  val testDependencies = specs2Deps.map(_ % Test) ++ Seq(scalaTest, scalaCheck, scalaPlus)

  val serverDependencies = specs2Deps.map(_ % Test) ++ Seq(
    logback % Test
  )

  val routerDependencies = http4s ++ circe ++ testDependencies ++ tapir
}
