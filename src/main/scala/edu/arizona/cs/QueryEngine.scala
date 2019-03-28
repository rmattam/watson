
package edu.arizona.cs
import org.apache.lucene.analysis.standard.StandardAnalyzer
import java.io._
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.search.similarities.ClassicSimilarity
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import scala.collection.mutable.ListBuffer
import java.io.File
import java.io.IOException
import java.util.Scanner
import QueryEngine._
import scala.collection.JavaConversions._
import scala.io.Source
import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scala.collection.immutable

object QueryEngine {

  def main(args: Array[String]): Unit = {
    try {
      val fileName: String = "input.txt"
      println("********Welcome to  Homework 3!")
      val query13a: List[String] = List("information", "retrieval")
      val objQueryEngine: QueryEngine = new QueryEngine(fileName)
      objQueryEngine.runQ1(query13a)
      objQueryEngine.runQ13a(query13a)
      objQueryEngine.runQ13b(query13a)
      objQueryEngine.runQ13c(query13a)
      objQueryEngine.runQ1d(query13a)

    } catch {
      case ex: Exception => println(ex.getMessage)

    }
  }

}

class QueryEngine(var input_file: String) {

  var luceneIndex = new RAMDirectory()
  val analyzer = new StandardAnalyzer()

  def runQ1(query: List[String]): ListBuffer[ResultClass] = {
    InitializeIndex()
    println("**************************")
    println("Query Results: Question 1")
    return RunQuery(new QueryParser("Text", analyzer).parse(query.mkString(" ")))
  }

  def runQ13a(query: List[String]): ListBuffer[ResultClass] = {
    InitializeIndex()
    println("**************************")
    println("Query Results: Question 1(c) a:")
    val qString = query.mkString(" AND ")
    return RunQuery(new QueryParser("Text", analyzer).parse(qString))
  }

  def runQ13b(query: List[String]): ListBuffer[ResultClass] = {
    InitializeIndex()
    println("**************************")
    println("Query Results: Question 1(c) b:")
    val qString = List(query(0), "AND", "NOT", query(1)).mkString(" ")
    return RunQuery(new QueryParser("Text", analyzer).parse(qString))
  }

  def runQ13c(query: List[String]): ListBuffer[ResultClass] = {
    InitializeIndex()
    println("**************************")
    println("Query Results: Question 1(c) c:")
    val qString = "\"" + query(0) + " " + query(1) + "\"~1"
    return RunQuery(new QueryParser("Text", analyzer).parse(qString))
  }

  def runQ1d(query: List[String]): ListBuffer[ResultClass] = {
    InitializeIndex(false)
    println("**************************")
    println("Query Results: Question 1(d):")
    return RunQuery(new QueryParser("Text", analyzer).parse(query.mkString(" ")), false)
  }

  private def returnDummyResults(): ListBuffer[ResultClass] = {
    var doc_score_list = new ListBuffer[ResultClass]()

    for (i <- 0.until(2)) {
      val doc: Document = new Document()
      doc.add(new TextField("title", "", Field.Store.YES))
      doc.add(
        new StringField("docid",
          "Doc" + java.lang.Integer.toString(i + 1),
          Field.Store.YES))
      val objResultClass: ResultClass = new ResultClass()
      objResultClass.DocName = doc
      doc_score_list += (objResultClass)
    }
    return doc_score_list
  }

  private def RunQuery(query: Query, default: Boolean = true): ListBuffer[ResultClass] = {
    var doc_score_list = new ListBuffer[ResultClass]()
    val hitsPerPage = 10
    val reader = DirectoryReader.open(luceneIndex)
    val searcher:IndexSearcher = new IndexSearcher(reader)
    if(!default) searcher.setSimilarity(new ClassicSimilarity())
    val docs = searcher.search(query, hitsPerPage)
    val hits = docs.scoreDocs

    var i = 0
    while (i < hits.length) {
      val docId = hits(i).doc
      val d = searcher.doc(docId)
      val objResultClass: ResultClass = new ResultClass()
      objResultClass.DocName = d
      objResultClass.doc_score = hits(i).score
      println("Hit:"+ (i+1) +" DocName: " + objResultClass.DocName.get("docid") + " DocScore: " + objResultClass.doc_score)
      doc_score_list += (objResultClass)
      i += 1
    }
    return doc_score_list
  }

  private def InitializeIndex(default: Boolean = true):Unit = {
    luceneIndex = new RAMDirectory()
    val config = if (default) new IndexWriterConfig(analyzer) else new IndexWriterConfig(analyzer).setSimilarity(new ClassicSimilarity())
    val w = new IndexWriter(luceneIndex, config)
    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(input_file))
    for (line:String <- source.getLines()) {
      val tokens = line.split("[ ]+")
      w.addDocument(CreateDocument(tokens(0), tokens.slice(1, tokens.length).mkString(" ")))
    }
    source.close()
    w.close()
  }

  private def CreateDocument(DocId: String, Text: String): Document = {
    val doc: Document = new Document()
    doc.add(new TextField("Text", Text, Field.Store.YES))
    doc.add(new StringField("docid", DocId, Field.Store.YES))
    return doc
  }
}