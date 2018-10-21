name := "CAWebAnalyticsPipeline"
version := "1.0"
scalaVersion := "2.11.9"

val sparkVersion = "2.3.0"
val kafkaVersion = "1.1.0"

resolvers += Resolver.mavenLocal

libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % Test
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % Test classifier "tests"
libraryDependencies += "org.pegdown"    %  "pegdown"     % "1.6.0"  % "test"


testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")

