package edu.arizona.cs

import java.nio.file.Paths

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, StringField, TextField}
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig, Term}
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.similarities.ClassicSimilarity
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.FSDirectory

import scala.collection.mutable.ListBuffer

class Index(val file:String) {
    private var luceneIndex: FSDirectory = FSDirectory.open(Paths.get(file))
    private val analyzer = new StandardAnalyzer()
    private var writer:IndexWriter = null

    private def Open(mode: OpenMode, default:Boolean):Unit = {
      val config = if (default) new IndexWriterConfig(analyzer) else new IndexWriterConfig(analyzer).setSimilarity(new ClassicSimilarity())
      config.setOpenMode(mode)
      writer = new IndexWriter(luceneIndex, config)
    }

    def Create(default:Boolean): Unit ={
      Open(OpenMode.CREATE, default)
    }

    def Open(default:Boolean): Unit ={
      Open(OpenMode.CREATE_OR_APPEND, default)
    }

    def CloseAll(): Unit ={
      writer.close()
      Close()
    }

    def Close(): Unit ={
      luceneIndex.close()
    }

    def Add(document: WikiDoc): Unit ={
      writer.addDocument(CreateDocument(document))
    }

    private def CreateDocument(document: WikiDoc): Document = {
      val doc: Document = new Document()
      doc.add(new StringField("title", document.Title, Field.Store.YES))
      doc.add(new TextField("para-1", document.Content(0), Field.Store.YES))
      doc.add(new TextField("text", document.Content.slice(0, document.Content.length).mkString(" "), Field.Store.YES))
      return doc
    }

    def Run(qString: String, default: Boolean): ListBuffer[JeopardyResult] = {
      val query = new QueryParser("text", analyzer).parse(qString)
      var doc_score_list = new ListBuffer[JeopardyResult]()
      val hitsPerPage = 20
      val reader = DirectoryReader.open(luceneIndex)
      val searcher:IndexSearcher = new IndexSearcher(reader)
      if(!default) searcher.setSimilarity(new ClassicSimilarity())
      val docs = searcher.search(query, hitsPerPage)
      val hits = docs.scoreDocs

      var i = 0
      while (i < hits.length) {
        val docId = hits(i).doc
        val d = searcher.doc(docId)
        val objResultClass: JeopardyResult = new JeopardyResult()
        objResultClass.Title = d.get("title")
        objResultClass.Score = hits(i).score
        println("Hit:"+ (i+1) +" Title: " + objResultClass.Title + " DocScore: " + objResultClass.Score)
        doc_score_list += (objResultClass)
        i += 1
      }
      return doc_score_list
    }

    def GetDocument(title:String): String ={
      val query = new TermQuery(new Term("title", title))
      var doc_score_list = new ListBuffer[JeopardyResult]()
      val hitsPerPage = 10
      val reader = DirectoryReader.open(luceneIndex)
      val searcher:IndexSearcher = new IndexSearcher(reader)
      val docs = searcher.search(query, hitsPerPage)
      val hits = docs.scoreDocs

      var i = 0
      while (i < hits.length) {
        val docId = hits(i).doc
        val d = searcher.doc(docId)
        val objResultClass: JeopardyResult = new JeopardyResult()
        objResultClass.Title = d.get("text")
        objResultClass.Score = hits(i).score
        println("Hit:"+ (i+1) +" Title: " + objResultClass.Title + " DocScore: " + objResultClass.Score)
        doc_score_list += (objResultClass)
        i += 1
      }
      return doc_score_list(0).Title
    }
}
