
<div align="center">
  <img src="./documentation/images/lepus_logo.png" style="width:180px">
  <h1>Lepus Framework</h1>
  <img src="https://img.shields.io/badge/lepus-v0.0.0-blue">
  <a href="https://en.wikipedia.org/wiki/MIT_License">
    <img src="https://img.shields.io/badge/license-MIT-green">
  </a>
  <a href="https://github.com/scala/scala">
    <img src="https://img.shields.io/badge/scala-v2.13.x-red">
  </a>
</div>

---

Lepus Framework is an asynchronous REST API framework for Scala.

The Lepus Framework enables schema-driven development.

The back-end engineer simply writes the routing definition, and the front-end engineer can validate the API without waiting for the back-end process to complete.
This is because OpenAPI documents can be generated from routing definitions built with the Lepus Framework and mock servers can be started.

:warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning:

Lepus Framework was created to understand the structure of the Play Framework, functional programming using the cats effect, and schema-driven development using tapir.

We have not checked or verified the operation in a production environment, so please do not use it in a production environment.

:warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning::warning:

## Introduction

Lepus Framework was created to understand the structure of the Play Framework, functional programming using the cats effect, and schema-driven development using tapir.

This framework relies on several libraries (automatically installed by Lepus).

- [cats](https://github.com/typelevel/cats)
- [cats-effect](https://github.com/typelevel/cats-effect)
- [http4s](https://github.com/http4s/http4s)
- [doobie](https://github.com/tpolecat/doobie)

## Documentation
Coming soon...

## Quickstart with sbt
Add the following dependencies to plugins.sbt

```sbt
addSbtPlugin("com.github.takapi327" % "sbt-plugin" % "0.1.0-SNAPSHOT")
```

Load the required project with build.sbt
```sbt
lazy val root = (project in file("."))
  .settings(...)
  .enablePlugins(Lepus)
```

After setting up dependencies, develop with reference to the contents of Example.

## Example
The following is the minimum configuration for routing in the Lepus Framework.

```scala
import cats.effect._

import lepus.router._
import lepus.router.model.ServerResponse

object HelloRoute extends RouterConstructor[IO, (String, Long)] {

  override def endpoint = "hello" / bindPath[String]("name")

  override def routes = {
    case GET => req => IO(ServerResponse.NoContent)
  }
}

object HelloApp extends RouterProvider[IO] {

  override def routes: NonEmptyList[RouterConstructor[IO, _]] =
    NonEmptyList.of(HelloRoute)
}
```

### Generate OpenAPI documentation
Load the required project with build.sbt

```sbt
lazy val root = (project in file("."))
  .settings(
    ...
    swaggerTitle   := (Docker / packageName).value,
    swaggerVersion := (Docker / version).value
  )
  .enablePlugins(Lepus)
  .enablePlugins(LepusSwagger)
```

Add the settings for OpenAPI document generation to the routing.

```scala
import cats.effect._

import io.circe._
import io.circe.generic.semiauto._

import lepus.router._
import lepus.router.http._
import lepus.router.model.ServerResponse
import lepus.router.generic.SchemaDerivation

case class Sample(info: String)
object Sample extends SchemaDerivation {
  implicit lazy val encoder: Encoder[Sample] = deriveEncoder
  implicit val schema  = gen[Sample]
}

object HelloRoute extends RouterConstructor[IO, (String, Long)] {

  override def endpoint = "hello" / bindPath[String]("name")

  override def summary     = Some("Sample Paths")
  override def description = Some("Sample Paths")

  override def responses: PartialFunction[RequestMethod, List[Response[_]]] = {
    case GET => List(
      Response.build[Sample](
        status      = responseStatus.Ok,
        headers     = List.empty,
        description = "Sample information acquisition"
      )
    )
  }

  override def routes = {
    case GET => req => IO(ServerResponse.NoContent)
  }
}
```

After running Compile, the generateApi command generates OpenApi documentation.

docs/OpenApi.yaml is generated directly under the root project.

```shell
$ sbt compile
$ sbt generateApi
```
