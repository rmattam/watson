package edu.arizona.cs
import edu.stanford.nlp.simple._

import scala.collection.mutable.ListBuffer
import scala.io.Source
import collection.JavaConverters._

class Jeopardy {
  var category:String = null
  var answer:Array[String] = null
  var question:String = null
  var rules:CategoryRules = null

  var nouns:ListBuffer[String] = ListBuffer[String]()
  var nlp:Sentence = null
}

class CategoryRules(val IgnoreTermInQuery:Boolean = false, val proximity:Boolean = false, val proximityString:String = ""){
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

        if(lemma_index) {
          j.question = new Sentence(j.question).lemmas().asScala.mkString(" ")
        }

        if (doNLP) {
          j.nlp = new Sentence(j.question)
          for(s <- 0 to j.nlp.length - 1){
            if (j.nlp.posTag(s).contains("NN"))
              j.nouns+= j.nlp.word(s)
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
    if (category == "AFRICAN CITIES") return new CategoryRules(false, true, "africa african city")
    if (category == "GOLDEN GLOBE WINNERS") return new CategoryRules(false, true, "Golden Globe")
    if (category == "HE PLAYED A GUY NAMED JACK RYAN IN...") return new CategoryRules(false, true, "movie actor")
    else return new CategoryRules()
  }
}
