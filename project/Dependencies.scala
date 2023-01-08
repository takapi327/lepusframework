/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

import sbt._

object Dependencies {

  val guice = "com.google.inject" % "guice" % "5.1.0"

  val logback = "ch.qos.logback" % "logback-classic" % "1.4.5"

  val typesafeConfig = "com.typesafe" % "config" % "1.4.2"

  val magnolia3 = "com.softwaremill.magnolia1_3" %% "magnolia" % "1.2.6"

  val catsVersion = "2.9.0"
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % "3.4.4"

  val mapRef = "io.chrisdavenport" %% "mapref" % "0.2.1"

  val jjwtVersion = "0.11.5"
  val jwt: Seq[ModuleID] = Seq(
    "jjwt-api",
    "jjwt-impl",
    "jjwt-jackson"
  ).map("io.jsonwebtoken" % _ % jjwtVersion)

  val http4sVersion = "0.23.17"
  val http4sDsl = "org.http4s" %% "http4s-dsl" % http4sVersion
  val http4sEmber: Seq[ModuleID] = Seq(
    "http4s-ember-server",
    "http4s-ember-client"
  ).map("org.http4s" %% _ % http4sVersion)

  val circeVersion = "0.14.1"
  val circe: Seq[ModuleID] = Seq(
    "circe-core",
    "circe-generic",
    "circe-parser"
  ).map("io.circe" %% _ % circeVersion)

  val hikariCP = "com.zaxxer" % "HikariCP" % "5.0.1"

  val doobieVersion = "1.0.0-RC2"
  val doobie = "org.tpolecat" %% "doobie-core" % doobieVersion

  val specs2VersionForScala2 = "4.12.12"
  val specs2Version = "5.0.0"
  val specs2Deps: Seq[ModuleID] = Seq(
    "specs2-core",
    "specs2-junit",
  ).map("org.specs2" %% _ % specs2Version % Test)

  val scalaTest  = "org.scalatest"     %% "scalatest"       % "3.2.14"   % Test
  val scalaCheck = "org.scalacheck"    %% "scalacheck"      % "1.17.0"   % Test
  val scalaPlus  = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test

  val testDependencies: Seq[ModuleID] = Seq(scalaTest, scalaCheck, scalaPlus)

  val swaggerDependencies: Seq[ModuleID] = Seq("io.circe" %% "circe-yaml" % circeVersion) ++ testDependencies

  val routerDependencies: Seq[ModuleID] = http4sEmber ++ circe ++ testDependencies
}
