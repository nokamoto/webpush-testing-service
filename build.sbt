scalaVersion := "2.12.6"

name := "webpush-testing-service"

version := IO.readLines(file("./VERSION")).head

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "org.seleniumhq.selenium" % "selenium-java" % "3.14.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)
