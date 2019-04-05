package edu.arizona.cs

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
        if (item.answer == wiki.QueryTop(item.question.replace('-', ' ').replace('!', ' '))) correct += 1
      }
      val Accuracy = (correct.toFloat / tests.length)
      println("Baseline Accuracy: " + Accuracy)
    } finally {
      wiki.Close()
    }
  }
}
