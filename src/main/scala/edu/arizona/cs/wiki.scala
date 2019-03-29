package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import scala.io.Source

class Wiki(var fileName: String) {

  var doc = new WikiDoc()
  var inverted = new Index()
  def Index(): Unit = {
    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName))
    inverted.Create()
    inverted.Open()
    var i = 1
    for (line:String <- source.getLines()) {
      Parse(line)
      println(i)
      i += 1
    }
    inverted.Close()
    source.close()
  }

  private def Parse(line: String): Unit ={
    val title_regex = raw"\[\[(.+?)\]\]".r
    val subheading_regex = raw"==(.+?)==".r
    line match {
      case title_regex(title:String) => {
        if (doc.Title != "") {
          inverted.Add(doc)
        }
        doc.Title = title
        doc.Content = ListBuffer[String]()
        doc.Content += ""
      }
      case subheading_regex(heading:String) => {
        doc.Content += heading
      }
      case _ => {
        doc.Content(doc.Content.length-1) += (" " + line)
      }
    }
  }
}
