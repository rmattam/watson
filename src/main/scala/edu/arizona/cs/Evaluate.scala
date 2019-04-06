package edu.arizona.cs

import java.io.{BufferedWriter, FileWriter}
import java.io.File

import com.sun.net.httpserver.Authenticator.Success
import jdk.nashorn.internal.runtime.QuotedStringTokenizer

class Evaluate {

  var wiki: Wiki = null

  def init_index(conf:Config): Unit ={
    wiki = new Wiki(conf.index())
    if (conf.data.isSupplied) {
      println("reading wiki data from: " + conf.data())
      wiki.Index(conf.data())
      println("indexing done")
    }
    println("wiki index initialized")
  }

  def Baseline(fileName:String): Unit ={
    val tests = Jeopardy.Parse(fileName)
    var correct = 0
    try {
      for (item: Jeopardy <- tests) {
        // characters which have to be escaped: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        var question = item.question.replace('-', ' ').replace('!', ' ').replace('\"', ' ').replace(';',' ').replace(':', ' ')

        if (item.rules.proximity){
          var temp_question = ""
          val tokens = question.split("\\s+")
          var i = 0
          while (i < tokens.length-2){
            if (temp_question != "") temp_question += " OR "
            temp_question += "\"" + tokens(i) + " " + tokens(i+1) + " " + tokens(i+2) + " " + item.rules.proximityString + "\"~100"
            i+=1
          }
          question = "("+ question + ") OR (" + temp_question + ")"
        }

        var prediction = List(wiki.QueryTop(question, item.rules))
        if (item.answer.contains(prediction(0))){
          correct += 1
          report(true, item, prediction)
        } else {
          report(false, item, prediction)
          prediction = wiki.Query(question)
          report(false, item, prediction)
        }
      }
      val Accuracy = (correct.toFloat / tests.length)
      println("Baseline Accuracy: " + Accuracy)
    } finally {
      wiki.Close()
    }
  }

  def ViewDocument(title:String): String ={
    val document = wiki.GetDocument(title)
    return document
  }

  def TestQuery(qString: String): Unit ={
    wiki.TestQuery(qString)
  }

  def report(success: Boolean, item:Jeopardy, prediction: Seq[String]): Unit ={
    val errors = new File("analysis/v2/" + (if (success) "success" else "errors") +"/"+ item.category + ".txt")
    errors.createNewFile()
    val bw = new BufferedWriter(new FileWriter(errors, true))
    bw.write(item.question)
    bw.write("\n")
    bw.write(item.answer.mkString(" | "))
    bw.write("\n")
    bw.write(prediction.mkString(" || "))
    bw.write("\n")
    bw.write("\n")
    bw.close()
  }
}
