lazy val LepusFramework = (project in file("."))
  .settings(
    name         := "Lepus-Framework",
    scalaVersion := (LepusProject / scalaVersion).value
  )
  .settings(commonSettings: _*)
