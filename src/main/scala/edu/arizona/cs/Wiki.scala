package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import java.io.File
import java.nio.file.{FileSystem, Files, Paths}

import org.apache.lucene.analysis.standard.StandardAnalyzer

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

  def QueryTop(qString:String, rule: CategoryRules): String ={
      val res =inverted.Run(qString)
      if (res.length != 0) {
        var i = 0
        if (!rule.IgnoreTermInQuery){
          while (i < res.length && IsTermInQuery(qString, res(i).Title)) {
            i += 1
          }
        }
        return res(i).Title
      } else
          return ""
  }

  private def IsTermInQuery(query:String, term: String): Boolean ={
    var result = false
    for (word <- term.split("\\s+")){

      if (!(StandardAnalyzer.ENGLISH_STOP_WORDS_SET.contains(word.toLowerCase())) && query.toLowerCase().contains(word.toLowerCase())){
        result = true
      }
    }
    return result
  }

  def Query(qString:String): List[String] ={
    val res =inverted.Run(qString)
    if (res.length != 0)
      return res.map(_.Title).toList
    else
      return List("")
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

  // debugging
  def GetDocument(title:String): String ={
    return inverted.GetDocument(title)
  }

  def TestQuery(qString:String): Unit ={
    var proxi = "\"" + qString + "\"~30"
    var result = inverted.Run(proxi)
    proxi = "\"" + qString + "\"~50"
    result = inverted.Run(proxi)
    proxi = "\"" + qString + "\"~1000"
    result = inverted.Run(proxi)
    println("done")
  }

}
