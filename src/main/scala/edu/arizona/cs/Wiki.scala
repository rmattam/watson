package edu.arizona.cs

import scala.io.Source

class Wiki(var fileName: String) {

  var doc = new WikiDoc()
  def Index(): Unit = {
    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName))
    for (line:String <- source.getLines()) {
      Parse(line)
    }
    source.close()
  }

  private def Parse(line: String): Unit ={
    val title_regex = raw"\[\[(.+?)\]\]".r
    line match {
      case title_regex(title:String) => {
        if (doc.Title != "") {
          println(doc.Title)
          println(doc.Content)
        }
        doc.Title = title
      }
      case _ => {
        doc.Content += line
      }
    }
  }
}
