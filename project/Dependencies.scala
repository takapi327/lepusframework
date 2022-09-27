/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import sbt._

object Dependencies {

  val logback = "ch.qos.logback" % "logback-classic" % "1.4.1"

  val typesafeConfig = "com.typesafe" % "config" % "1.4.2"

  val reflect = "org.scala-lang" % "scala-reflect" % "2.13.8"

  val magnolia3 = "com.softwaremill.magnolia1_3" %% "magnolia" % "1.2.0"

  val catsVersion = "2.8.0"
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.14"

  val http4sVersion = "0.23.16"
  val http4s = Seq(
    "http4s-dsl",
    "http4s-ember-server"
  ).map("org.http4s" %% _ % http4sVersion)

  val circeVersion = "0.14.1"
  val circe = Seq(
    "circe-core",
    "circe-generic",
    "circe-parser"
  ).map("io.circe" %% _ % circeVersion)

  val hikariCP = "com.zaxxer" % "HikariCP" % "5.0.0"

  val doobieVersion = "1.0.0-RC1"
  val doobie = Seq(
    "doobie-core",
    "doobie-hikari"
  ).map("org.tpolecat" %% _ % doobieVersion)

  val specs2VersionForScala2 = "4.12.12"
  val specs2Version = "5.0.0"
  val specs2Deps: Seq[ModuleID] = Seq(
    "specs2-core",
    "specs2-junit",
  ).map("org.specs2" %% _ % specs2Version % Test)

  val scalaTest  = "org.scalatest"     %% "scalatest"       % "3.2.12"   % Test
  val scalaCheck = "org.scalacheck"    %% "scalacheck"      % "1.17.0"   % Test
  val scalaPlus  = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test

  val testDependencies = Seq(scalaTest, scalaCheck, scalaPlus)

  val swaggerDependencies = Seq("io.circe" %% "circe-yaml" % circeVersion) ++ testDependencies

  val routerDependencies = http4s ++ circe ++ testDependencies
}
