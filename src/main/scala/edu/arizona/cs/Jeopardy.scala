package edu.arizona.cs
import edu.stanford.nlp.simple._

import scala.collection.mutable.ListBuffer
import scala.io.Source
import collection.JavaConverters._

class Jeopardy {
  var category:String = null
  var answer:Array[String] = null
  var question:String = null
  var raw_question:String = null
  var rules:CategoryRules = null

  var nouns:ListBuffer[String] = ListBuffer[String]()
  var nlp:Sentence = null
}

class CategoryRules(val IgnoreTermInQuery:Boolean = false){
}

object Jeopardy {
  def Parse(fileName:String, doNLP:Boolean, lemma_index:Boolean): ListBuffer[Jeopardy] ={
    val input = ListBuffer[Jeopardy]()
    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName))
    var j = new Jeopardy()
    var i = 0
    for (line:String <- source.getLines()) {
      if (i == 0) j.category = line
      if (i == 1) {
        j.question = line
        j.raw_question = line

        if(lemma_index) {
          if (j.category == "'80s NO.1 HITMAKERS") {
            // 1988: "Man In The Mirror" parse this question.
            j.question = line.slice(0, 4) + " AND "
            j.question += "\"" + line.slice(7, line.length - 1).toLowerCase() + "\""
            j.question += " AND best AND selling AND song AND writer"

            j.raw_question = line.replace(":", " ")

          } else if (j.category == "GOLDEN GLOBE WINNERS") {
            j.raw_question = line.toLowerCase().stripPrefix("in ").replace(":", " ")
            j.question = j.raw_question.split("[ ]+").mkString(" AND ")
            j.question += " AND \"golden globe award for best\" AND actor^3.3"
          } else if (j.category == "HE PLAYED A GUY NAMED JACK RYAN IN..."){
            var capture_span = false
            var span = ""
            val spans = ListBuffer[String]()
            for(c <- 0 until line.length){
              if (line(c) == '\"'){
                capture_span = !capture_span
                if (!capture_span) {
                  spans += "(\"" + new Sentence(span).lemmas().asScala.mkString(" ") + "\" OR \"" + span + "\")"
                  span = ""
                }
              } else{
                if (capture_span) span += line(c).toLower
              }
            }
            j.question = spans.mkString(" AND ") + " AND \"jack ryan\""
          } else {
            j.question = new Sentence(j.question).lemmas().asScala.mkString(" ").toLowerCase()
            if (doNLP) {
              j.nlp = new Sentence(j.question)
              for(s <- 0 to j.nlp.length - 1){
                val consti = j.nlp.parse()
                val depi = j.nlp.dependencyGraph()
                if (j.nlp.posTag(s).contains("NN") || j.nlp.posTag(s).contains("JJ") || j.nlp.posTag(s).contains("VB")|| j.nlp.posTag(s).contains("CD"))
                  j.nouns+= j.nlp.word(s)
              }
              j.question += " " + j.nouns.mkString("^0.1 ")
            }
          }
        }
      }
      if (i == 2) j.answer = line.split("\\|")
      if (i == 3) {
        j.rules = GetCatgoryRule(j.category)
        input.append(j)
        j = new Jeopardy()
        i = (-1)
      }
      i+=1
    }
    source.close()
    return input
  }

  def GetCatgoryRule(category:String): CategoryRules ={
    if (category == "RANKS & TITLES") return new CategoryRules(true)
    return new CategoryRules()
  }
}
