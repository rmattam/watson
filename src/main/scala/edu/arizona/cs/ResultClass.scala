
package edu.arizona.cs

import edu.stanford.nlp.simple._

import collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class JeopardyResult {
  var Title: String = ""
  var Content: String = ""
  var Score: Double = 0
  var NormalizedScore:Double = 0

  var questionwords_in_answer = 0
  var questionpairwords_inorder_in_sentence = 0
  var questionpairwords_inorder_in_document = 0

  def analyze(qString:String): Unit ={
    val query = new Sentence(qString.toLowerCase())
    val doc = new Document(Content.toLowerCase())
    var candidates = scala.collection.mutable.Map[String, (ListBuffer[Int], ListBuffer[Int])]()
    var sent_count = 0
    for (sent <- doc.sentences().asScala){
      sent_count +=1

      // clear sentence word order hash map for current words in the query
      for (q <- 0 until query.length()) {
        if (candidates.contains(query.lemma(q))) candidates(query.lemma(q))._1.clear()
      }

      for (q <- 0 until query.length()){
        for (w <- 0 until sent.length()){

          // word in query matches a word in the sentence.
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

    // check for word order in the whole document
    for (q <- 0 until query.length()) {
      if (candidates.contains(query.lemma(q))){
        for(w <- candidates(query.lemma(q))._2){
          for (prev <- 0 until q){
            if (candidates.contains(query.lemma(prev))){
              for (order <- candidates(query.lemma(prev))._2){
                if (order < w) questionpairwords_inorder_in_document += 1
              }
            }
          }
        }
      }
    }

  }

  def NormalizeScore(MaxScore: Double, MaxQsWord:Int, MaxQsWordSent: Int, MaxQsWordDoc: Int): Unit ={

//    NormalizedScore =  (if (MaxScore == 0) Score else (Score /  MaxScore)) +
//      (if (MaxQsWordDoc == 0) questionpairwords_inorder_in_document else (questionpairwords_inorder_in_document/MaxQsWordDoc)) +
//      (if (MaxQsWordSent == 0) questionpairwords_inorder_in_sentence else (questionpairwords_inorder_in_sentence/MaxQsWordSent)) +
//        (if (MaxQsWord == 0) questionwords_in_answer else (questionwords_in_answer/ MaxQsWord))

    NormalizedScore = (Score*150) +
      //(if (MaxQsWordDoc == 0) questionpairwords_inorder_in_document else (questionpairwords_inorder_in_document/MaxQsWordDoc) * 10) +
      //(if (MaxQsWordSent == 0) questionpairwords_inorder_in_sentence else (questionpairwords_inorder_in_sentence/MaxQsWordSent) * 60) +
      questionwords_in_answer
  }
}
