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
      val eval = new Evaluate()
      eval.init_index(conf)
      eval.Baseline(conf.query())
      println("search done")
    } catch {
      case ex: Exception => println(ex.getMessage)
    }
  }
}
