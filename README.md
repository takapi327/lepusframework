![lepusframework](https://socialify.git.ci/takapi327/lepusframework/image?description=1&font=Inter&language=1&logo=https%3A%2F%2Fuser-images.githubusercontent.com%2F57429437%2F170270360-93f29bbf-aef3-47d7-8910-f5baba490ba6.png&owner=1&pattern=Plus&theme=Light)

<div align="center">
  <img src="https://img.shields.io/badge/lepus-v0.3.0-blue">
  <a href="https://en.wikipedia.org/wiki/MIT_License">
    <img src="https://img.shields.io/badge/license-MIT-green">
  </a>
  <a href="https://github.com/lampepfl/dotty">
    <img src="https://img.shields.io/badge/scala-v3.x-red">
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

## Documentation
Coming soon...

## Quickstart with sbt
Lepus Framework is not an official, publicly available plugin, but is privately maintained.

Therefore, S3 is used as maven. To use Lepus Framework plugins from s3, the following plugin must be configured under project/project/build.sbt of the project to be used.

:warning: If it is project/build.sbt or plugin.sbt, I can't get the plugin properly and an error occurs. :warning:

※ Once officially released, the following settings will no longer be necessary.

in: project/project/build.sbt
```sbt
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.20.0")
```

Add the following dependencies to plugins.sbt

in: project/plugins.sbt
```sbt
ThisBuild / resolvers += "Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/lepus/"
addSbtPlugin("com.github.takapi327" % "sbt-plugin" % <version>)
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
package sample

import cats.effect.IO
import cats.data.NonEmptyList

import org.http4s.dsl.io.*
  
import lepus.router.{ *, given }

object HelloApp extends RouterProvider[IO]:

  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") ->> RouterConstructor.of {
      case GET => Ok(s"Hello ${summon[String]}")
    }
  )
```

You must set the path of the object that inherits RouterProvider in application.conf.

※ We plan to eliminate the need for configuration in the near future.
```text
lepus.server.routes = "sample.HelloApp"
```

### Generate OpenAPI documentation
Load the required project with build.sbt

```sbt
lazy val root = (project in file("."))
  .settings(...)
  .enablePlugins(LepusSwagger)
```

Add the settings for OpenAPI document generation to the routing.

```scala
import cats.effect.*

import io.circe.*
import io.circe.generic.semiauto.*

import org.http4s.Status.*
import org.http4s.dsl.io.*

import lepus.router.{ *, given }
import lepus.router.model.Schema
import lepus.router.generic.semiauto.*

import lepus.swagger.*
import lepus.swagger.model.OpenApiResponse

case class Sample(info: String)
object Sample:
  given Encoder[Sample] = deriveEncoder
  given Schema[Sample]  = deriveSchemer

object HelloRoute extends OpenApiConstructor[IO, String]:

  override val summary     = Some("Sample Paths")
  override val description = Some("Sample Paths")

  override def responses = {
    case GET => List(
      OpenApiResponse[Sample](NoContent, List.empty, "Sample information acquisition")
    )
  }

  override def routes = {
    case GET => Ok(s"Hello ${summon[String]}")
  }

object HelloApp extends RouterProvider[IO]:

  override def routes = NonEmptyList.of(
    "hello" / bindPath[String]("name") ->> HelloRoute
  )
```

After running Compile, the generateApi command generates OpenApi documentation.

docs/OpenApi.yaml is generated directly under the root project.

```shell
$ sbt compile
$ sbt generateApi
```

### Mock server startup

Mock servers are started using libraries such as prism.

Settings must be made for each project.

Below is an example of starting a mock server in Docker using prism.

```yaml
version: '3.9'

services:
  swagger-editor:
    image: swaggerapi/swagger-editor
    container_name: "swagger-editor"
    ports:
      - 8001:8080

  swagger-ui:
    image: swaggerapi/swagger-ui
    container_name: "swagger-ui"
    ports:
      - 8002:8080
    volumes:
      - ./OpenApi.yaml:/usr/share/nginx/html/OpenApi.yaml
    environment:
      API_URL: ./OpenApi.yaml

  swagger-api:
    image: stoplight/prism:3
    container_name: "swagger-api"
    ports:
      - 8003:4010
    command: mock -h 0.0.0.0 /OpenApi.yaml
    volumes:
      - ./OpenApi.yaml:/OpenApi.yaml
```
