package edu.arizona.cs
import org.rogach.scallop._


class Config(arguments: Seq[String]) extends ScallopConf(arguments) {
  val data = opt[String]()
  val index = opt[String](required = true)
  val query = opt[String](required = true)
  val tfidf = opt[Boolean]()
  val lemma = opt[Boolean]()
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

      //eval.ViewDocument("Hasbro")
//      import edu.stanford.nlp.simple._
//      import collection.JavaConverters._
//      var lemms = new Sentence("rock with you").lemmas().asScala.mkString(" ")
//      eval.TestQuery("1980 AND \"rock with you\"AND best AND selling AND song AND writer" , "1980 rock with you")
      println("search done")
    } catch {
      case ex: Exception => println(ex.getMessage)
    }
  }
}
