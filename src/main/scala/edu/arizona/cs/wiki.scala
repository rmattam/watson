package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import java.io.File
import java.nio.file.{FileSystem, Files, Paths}

class Wiki(var dirName: String) {

  var doc = new WikiDoc()
  var inverted: Index = null
  private val index_file_path = "lucene/watson"

  def Index(): Unit = {
    inverted = new Index(index_file_path)
    inverted.Create()
    try {
      for (file:File <- getFiles(dirName)) {
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

  def QueryUI(): Unit ={
    inverted = new Index(index_file_path)
    try{
      inverted.Run("are a religious group that originated")
    } finally {
      inverted.Close()
    }
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

  private def getFiles(dir: String):List[File] = {
    val d = new File(dir)
    var x:List[File] = null
    if (d.exists && d.isDirectory) {
      x = d.listFiles.filter(_.isFile).toList
    } else if (d.exists && d.isFile) {
      x = List(d)
    } else {
      x = List[File]()
    }
    return x
  }
}
