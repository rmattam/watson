package edu.arizona.cs

object Main {
  def main(args: Array[String]): Unit = {
    try {
      val fileName: String = "wiki-example.txt"
      val wiki: Wiki = new Wiki(fileName)
      wiki.Index()
      println("indexing done")

      wiki.QueryUI()
      println("search done")
    } catch {
      case ex: Exception => println(ex.getMessage)
    }
  }
}
