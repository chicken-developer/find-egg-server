name := "ubisoft-game-server"

version := "1.0"

scalaVersion := "2.13.6"
val akkaVersion = "2.6.10"
val JacksonVersion = "2.10.5.1"

mainClass in Compile := Some("EasterEggExtremeServer.ServerEntryPoint")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-core" % "10.2.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2",
  "com.typesafe.akka" %% "akka-http2-support" % "10.2.2",

  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,

  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,

  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.4" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.2" % Test,

  "com.google.protobuf" % "protobuf-java" % "3.17.3",

)

