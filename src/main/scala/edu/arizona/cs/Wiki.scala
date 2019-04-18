package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import java.io.File
import java.nio.file.{FileSystem, Files, Paths}

import org.apache.lucene.analysis.standard.StandardAnalyzer

class Wiki(val index_file_path:String = "lucene/watson", val tfidf:Boolean = false, val lemma:Boolean = false) {

  var doc = new WikiDoc()
  val inverted: Index = new Index(index_file_path, !tfidf, lemma)

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

  def QueryTop(qString:String, rule: CategoryRules, rerank: Boolean, rawQ: String): String ={
      val res =inverted.Run(qString)
      if (res.length != 0) {
        var filtered_results = ListBuffer[JeopardyResult]()
        if (!rule.IgnoreTermInQuery){
          for (i <- 0 until res.length) {
            if(!IsTermInQuery(rawQ, res(i).Title)) filtered_results += res(i)
          }
        } else {
          filtered_results = res
        }
        if (rerank){
          filtered_results = Reranker(rawQ, filtered_results)
        }
        if (filtered_results.length != 0)
         return filtered_results(0).Title
      }
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

  private def Reranker(query:String, prior:ListBuffer[JeopardyResult]): ListBuffer[JeopardyResult] ={
    if (prior.length == 0) return prior
    prior.par.map(_.analyze(query))

    val maxscore = prior.maxBy(_.Score)
    val MaxQsWord = prior.maxBy(_.questionwords_in_answer).questionwords_in_answer
    val MaxQsWordSent = prior.maxBy(_.questionpairwords_inorder_in_sentence).questionpairwords_inorder_in_sentence
    val MaxQsWordDoc = prior.maxBy(_.questionpairwords_inorder_in_document).questionpairwords_inorder_in_document

    prior.par.map(_.NormalizeScore(maxscore.Score, MaxQsWord, MaxQsWordSent, MaxQsWordDoc))
    val result = prior.sortBy(- _.NormalizedScore)
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
