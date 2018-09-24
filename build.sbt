scalaVersion := "2.12.6"

enablePlugins(PlayScala)

PB.protoSources in Compile := (file("webpush-protobuf/webpush/protobuf").getCanonicalFile * AllPassFilter).get

PB.includePaths in Compile := Seq(file("webpush-protobuf").getCanonicalFile,
                                  target.value / "protobuf_external")

PB.targets in Compile := Seq(
  scalapb
    .gen(grpc = false, flatPackage = true) -> (sourceManaged in Compile).value)

libraryDependencies ++= Seq(
  guice,
  "org.seleniumhq.selenium" % "selenium-java" % "2.35.0",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)
