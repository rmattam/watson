package edu.arizona.cs

import java.io.{BufferedWriter, FileWriter}
import java.io.File

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
      for (item: Jeopardy <- tests) {
        // characters which have to be escaped: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        val question = item.question.replace('-', ' ').replace('!', ' ').replace(';',' ').replace(':', ' ')

//        if (item.rules.proximity){
//          var temp_question = ""
//          val tokens = question.split("\\s+")
//          var i = 0
//          while (i < tokens.length-2){
//            if (temp_question != "") temp_question += " OR "
//            temp_question += "\"" + tokens(i) + " " + tokens(i+1) + " " + tokens(i+2) + " " + item.rules.proximityString + "\"~100"
//            i+=1
//          }
//          question = "("+ question + ") OR (" + temp_question + ")"
//        }

        var prediction = wiki.QueryTop(question, item.rules, true, item.raw_question)
        if (item.answer.contains(prediction(0).Title)){
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
