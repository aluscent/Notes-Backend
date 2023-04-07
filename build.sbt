ThisBuild / version := "0.1.0-SNAPSHOT"

val scala3Version = "3.2.1"
ThisBuild / scalaVersion := scala3Version

lazy val root = (project in file("."))
  .settings(
    name := "Notes-Scala3"
  )

val http4sVersion = "0.23.14"
val circeVersion = "0.14.5"
val doobieVersion = "1.0.0-RC2"
lazy val tyrianVersion = "0.6.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.typelevel" %% "cats-effect" % "3.4.8",

  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-core" % doobieVersion,

  "org.http4s" %% "http4s-core" % http4sVersion,
  "org.http4s" %% "http4s-client" % http4sVersion,
  "org.http4s" %% "http4s-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion
)
