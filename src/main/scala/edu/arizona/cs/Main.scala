package edu.arizona.cs
import org.rogach.scallop._


class Config(arguments: Seq[String]) extends ScallopConf(arguments) {
  val data = opt[String]()
  val index = opt[String](required = true)
  val query = opt[String](required = true)
  verify()
}

object Main {
  def main(args: Array[String]): Unit = {
    try {
      val conf = new Config(args)
      println("Configs: index: " + conf.index()+ ", query file: " + conf.query())

      val wiki: Wiki = new Wiki(conf.index())
      if (conf.data.isSupplied) {
        println("reading wiki data from: " + conf.data())
        wiki.Index(conf.data())
        println("indexing done")
      }

      val input = Jeopardy.Parse(conf.query())
      wiki.QueryUI()
      println("search done")
    } catch {
      case ex: Exception => println(ex.getMessage)
    }
  }
}
