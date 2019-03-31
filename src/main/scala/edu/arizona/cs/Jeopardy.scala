package edu.arizona.cs

import scala.collection.mutable.ListBuffer
import scala.io.Source

class Jeopardy {
  var category:String = null
  var answer:String = null
  var question:String = null
}

object Jeopardy {
  def Parse(fileName:String): ListBuffer[Jeopardy] ={
    val input = ListBuffer[Jeopardy]()
    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(fileName))
    var j = new Jeopardy()
    var i = 0
    for (line:String <- source.getLines()) {
      if (i == 0) j.category = line
      if (i == 1) j.question = line
      if (i == 2) j.answer = line
      if (i == 3) {
        input.append(j)
        j = new Jeopardy()
        i = (-1)
      }
      i+=1
    }
    source.close()
    return input
  }
}
