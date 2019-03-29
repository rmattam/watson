//
//package edu.arizona.cs
//import org.apache.lucene.analysis.standard.StandardAnalyzer
//import java.io._
//import org.apache.lucene.search.similarities.Similarity
//import org.apache.lucene.search.similarities.ClassicSimilarity
//import org.apache.lucene.document.Document
//import org.apache.lucene.document.Field
//import org.apache.lucene.document.StringField
//import org.apache.lucene.document.TextField
//import org.apache.lucene.index.DirectoryReader
//import org.apache.lucene.index.IndexReader
//import org.apache.lucene.index.IndexWriter
//import org.apache.lucene.index.IndexWriterConfig
//import org.apache.lucene.queryparser.classic.ParseException
//import org.apache.lucene.queryparser.classic.QueryParser
//import org.apache.lucene.search.IndexSearcher
//import org.apache.lucene.search.Query
//import org.apache.lucene.search.ScoreDoc
//import org.apache.lucene.search.TopDocs
//import org.apache.lucene.store.Directory
//import org.apache.lucene.store.RAMDirectory
//import scala.collection.mutable.ListBuffer
//import java.io.File
//import java.io.IOException
//import java.util.Scanner
//import scala.collection.JavaConversions._
//import scala.io.Source
//import scala.collection.mutable.{ListBuffer, ArrayBuffer}
//import scala.collection.immutable
//
//
//class QueryEngine(var input_file: String) {
//
//  var luceneIndex = new RAMDirectory()
//  val analyzer = new StandardAnalyzer()
//
//  private def RunQuery(query: Query, default: Boolean = true): ListBuffer[ResultClass] = {
//    var doc_score_list = new ListBuffer[ResultClass]()
//    val hitsPerPage = 10
//    val reader = DirectoryReader.open(luceneIndex)
//    val searcher:IndexSearcher = new IndexSearcher(reader)
//    if(!default) searcher.setSimilarity(new ClassicSimilarity())
//    val docs = searcher.search(query, hitsPerPage)
//    val hits = docs.scoreDocs
//
//    var i = 0
//    while (i < hits.length) {
//      val docId = hits(i).doc
//      val d = searcher.doc(docId)
//      val objResultClass: ResultClass = new ResultClass()
//      objResultClass.DocName = d
//      objResultClass.doc_score = hits(i).score
//      println("Hit:"+ (i+1) +" DocName: " + objResultClass.DocName.get("docid") + " DocScore: " + objResultClass.doc_score)
//      doc_score_list += (objResultClass)
//      i += 1
//    }
//    return doc_score_list
//  }
//
//  private def InitializeIndex(default: Boolean = true):Unit = {
//    luceneIndex = new RAMDirectory()
//    val config = if (default) new IndexWriterConfig(analyzer) else new IndexWriterConfig(analyzer).setSimilarity(new ClassicSimilarity())
//    val w = new IndexWriter(luceneIndex, config)
//    val source = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(input_file))
//    for (line:String <- source.getLines()) {
//      val tokens = line.split("[ ]+")
//      w.addDocument(CreateDocument(tokens(0), tokens.slice(1, tokens.length).mkString(" ")))
//    }
//    source.close()
//    w.close()
//  }
//
//  private def CreateDocument(DocId: String, Text: String): Document = {
//    val doc: Document = new Document()
//    doc.add(new TextField("Text", Text, Field.Store.YES))
//    doc.add(new StringField("docid", DocId, Field.Store.YES))
//    return doc
//  }
//}