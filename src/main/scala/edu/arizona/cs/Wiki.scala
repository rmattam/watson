package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import java.io.File
import java.nio.file.{FileSystem, Files, Paths}

class Wiki(val index_file_path:String = "lucene/watson") {

  var doc = new WikiDoc()
  val inverted: Index = new Index(index_file_path)

  def Index(dirName: String): Unit = {
    inverted.Create()
    try {
      for (file:File <- getFiles(dirName)) {
        println("Indexing file: "+ file.getName)
        val lines = Files.lines(file.toPath)
        try {
          lines.forEach(line => {
            Parse(line)
          })
        } finally {
          lines.close()
        }
      }
    } finally {
      inverted.CloseAll()
    }
  }

  def QueryTop(qString:String): String ={
      val res =inverted.Run(qString)
      if (res.length != 0)
        return res(0).toString
      else
        return ""
  }

  def Close(): Unit ={
    inverted.Close()
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

  private def getFiles(dirName: String):List[File] = {
    val d = new File(dirName)
    var x:List[File] = null
    if (d.exists && d.isDirectory) {
      x = d.listFiles.filter(x => (x.isFile && x.getName().startsWith("enwiki"))).toList
    } else if (d.exists && d.isFile) {
      x = List(d)
    } else {
      x = List[File]()
    }
    return x
  }
}
