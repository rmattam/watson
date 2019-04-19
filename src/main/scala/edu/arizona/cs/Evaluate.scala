package edu.arizona.cs

import java.io.{BufferedWriter, FileWriter}
import java.io.File

import scala.collection.mutable.ListBuffer

class Evaluate {

  var wiki: Wiki = null
  var conf:Config = null

  def init_index(config:Config): Unit ={
    conf = config
    wiki = new Wiki(conf.index(), conf.tfidf(), conf.lemma())
    if (conf.data.isSupplied) {
      println("reading wiki data from: " + conf.data())
      wiki.Index(conf.data())
      println("indexing done")
    }
    println("wiki index initialized")
  }

  def Baseline(fileName:String): Unit ={
    val tests = Jeopardy.Parse(fileName, false, conf.lemma())
    var correct = 0
    try {
      var question_number = 0
      for (item: Jeopardy <- tests) {
        question_number += 1
        // characters which have to be escaped: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        val question = item.question.replace('-', ' ').replace('!', ' ').replace(';',' ').replace(':', ' ')

        var prediction = wiki.QueryTop(question, item.rules, true, item.raw_question)

        if (item.category == "\"TIN\" MEN"){
          var filter = new ListBuffer[JeopardyResult]()
          for (pred <- prediction){
            if (pred.Title.toLowerCase().contains("tin")) filter += pred
          }
          prediction = filter.toList
        }

        if (item.answer.contains(prediction(0).Title)){
          println(Console.GREEN + "Question: "+ question_number +" is CORRECT!! expected: "+ item.answer.mkString(" | ") + " retrieved: " + prediction(0).Title)
          correct += 1
        } else println(Console.RED + "Question: " + question_number + " is WRONG!! expected: "+ item.answer.mkString(" | ")  + " retrieved: " + prediction(0).Title)
      }
      val Accuracy = (correct.toFloat / tests.length)
      println(Console.GREEN + "Performance Measurement Precision@1: " + Accuracy)
    } finally {
      wiki.Close()
    }
  }

  def ViewDocument(title:String): String ={
    val document = wiki.GetDocument(title)
    val doc = new File(title + ".txt")
    doc.createNewFile()
    val bw = new BufferedWriter(new FileWriter(doc, true))
    bw.write(document)
    bw.close()
    return document
  }

  def TestQuery(qString: String, rawQ: String): Unit ={
    val preds = wiki.QueryTop(qString, new CategoryRules(), true, rawQ)
    val jepa = new Jeopardy()
    jepa.question = qString
    jepa.category = "testing"
    jepa.answer = Array[String]("testing")
    report(false, jepa, preds)
  }

  def report(success: Boolean, item:Jeopardy, prediction: Seq[JeopardyResult]): Unit ={
    val errors = new File("analysis/v3/" + (if (success) "success" else "errors") +"/"+ item.category + ".txt")
    errors.createNewFile()
    val bw = new BufferedWriter(new FileWriter(errors, true))
    bw.write(item.question)
    bw.write("\n")
    bw.write(item.answer.mkString(" | "))
    bw.write("\n")
    bw.write(prediction.map(_.Title).toList.mkString(" || "))
    bw.write("\n")
    bw.write(prediction.map(_.NormalizedScore).toList.mkString(" || "))
    bw.write("\n")
    bw.write(prediction.map(_.Score).toList.mkString(" || "))
    bw.write("\n")
    bw.write(prediction.map(_.questionpairwords_inorder_in_sentence).toList.mkString(" || "))
    bw.write("\n")
    bw.write(prediction.map(_.questionpairwords_inorder_in_document).toList.mkString(" || "))
    bw.write("\n")
    bw.write(prediction.map(_.questionwords_in_answer).toList.mkString(" || "))
    bw.write("\n")
    bw.write("\n")
    bw.close()
  }
}
