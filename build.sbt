import sbt.Keys.libraryDependencies

name := "citizens"
version := "0.1"
scalaVersion := "2.12.4"

val versions = new {
  val logback = "1.2.3"
  val scala_logging = "3.9.2"
  val jackson = "2.10.5"
  val cats = "2.4.2"
  val doobie = "0.12.1"
  val mysql = "8.0.23"
/*  val dotty          = "0.26.0-bin-20200718-c753ca3-NIGHTLY"
  val zio            = "1.0.0-RC21-2"
  val zioInteropCats = "2.1.4.0-RC17"*/
  val tests = new {
    val scalaTest = "3.2.0"
    val mockito = "1.16.0"
  }
}

scalacOptions += "-Ypartial-unification"

lazy val root = (project in file("."))
  .settings(
    name := "citizens",
    compileOrder:= CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % versions.scala_logging,
      "org.typelevel" %% "cats-core" % versions.cats,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % versions.jackson,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % versions.jackson,
      "com.fasterxml.jackson.core" % "jackson-databind" % versions.jackson,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "mysql" % "mysql-connector-java" % versions.mysql,
      "org.tpolecat" %% "doobie-core" % versions.doobie,
      "org.tpolecat" %% "doobie-h2" % versions.doobie,
      "org.tpolecat" %% "doobie-specs2" % versions.doobie % "test",
      "org.tpolecat" %% "doobie-scalatest" % versions.doobie % "test",
      "org.scalatest" %% "scalatest" % versions.tests.scalaTest % "test",
      "org.mockito" %% "mockito-scala-scalatest" % versions.tests.mockito % "test"
/*      "dev.zio" %% "zio"              % versions.zio,
      "dev.zio" %% "zio-streams"      % versions.zio,
      "dev.zio" %% "zio-interop-cats" % versions.zioInteropCats,
      "dev.zio" %% "zio-test"         % versions.zio % "test",
      "dev.zio" %% "zio-test-sbt"     % versions.zio % "test"*/
    )
  )

