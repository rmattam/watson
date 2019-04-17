
package edu.arizona.cs

import edu.stanford.nlp.simple._

import collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class JeopardyResult {
  var Title: String = ""
  var Content: String = ""
  var Score: Double = 0

  var questionwords_in_answer = 0
  var questionpairwords_inorder_in_sentence = 0
  var questionpairwords_inorder_in_document = 0

  def analyze(qString:String): Unit ={
    val query = new Sentence(qString)
    val doc = new Document(Content)
    var candidates = scala.collection.mutable.Map[String, (ListBuffer[Int], ListBuffer[Int])]()
    var sent_count = 0
    for (sent <- doc.sentences().asScala){
      sent_count +=1

      for (q <- 0 until query.length()){
        for (w <- 0 until sent.length()){
          if (query.lemma(q) == sent.lemma(w)){
            questionwords_in_answer += 1
            if (!candidates.contains(query.lemma(q))) candidates += (query.lemma(q) -> (ListBuffer[Int](), ListBuffer[Int]()))
            candidates(query.lemma(q))._1 += w
            candidates(query.lemma(q))._2 += (sent_count * 1000 + w)
            for (prev <- 0 until q){
              if (candidates.contains(query.lemma(prev))){
                for (order <- candidates(query.lemma(prev))._1){
                  if (order < w) questionpairwords_inorder_in_sentence += 1
                }
              }
            }
          }
        }
      }
    }

  }

}
