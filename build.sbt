name := "watson"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "7.7.1"
libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "7.7.1"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "7.7.1"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" classifier "models"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.10"

libraryDependencies += "org.rogach" %% "scallop" % "3.2.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
// https://mvnrepository.com/artifact/org.scalatest/scalatest
//ibraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test
