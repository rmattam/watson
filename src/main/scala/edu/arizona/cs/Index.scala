package edu.arizona.cs

import java.nio.file.Paths

import edu.stanford.nlp.simple
import org.apache.lucene.analysis.core.WhitespaceAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, StringField, TextField}
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig, Term}
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.similarities.ClassicSimilarity
import org.apache.lucene.search.{IndexSearcher, TermQuery}
import org.apache.lucene.store.FSDirectory
import collection.JavaConverters._

import scala.collection.mutable.ListBuffer

class Index(val file:String, val bm25:Boolean, val lemma:Boolean) {
    private val luceneIndex: FSDirectory = FSDirectory.open(Paths.get(file))
    private val analyzer = if (lemma) new WhitespaceAnalyzer() else new StandardAnalyzer()
    private var writer:IndexWriter = null

    private def Open(mode: OpenMode):Unit = {
      val config = if (bm25) new IndexWriterConfig(analyzer) else new IndexWriterConfig(analyzer).setSimilarity(new ClassicSimilarity())
      config.setOpenMode(mode)
      writer = new IndexWriter(luceneIndex, config)
    }

    def Create(): Unit ={
      Open(OpenMode.CREATE)
    }

    def Open(): Unit ={
      Open(OpenMode.CREATE_OR_APPEND)
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

      var para_1 = document.Content(0)
      var content = document.Content.slice(0, document.Content.length).mkString(" ")

      if (lemma){
        val para1_doc = new simple.Document(document.Content(0))
        val content_doc = new simple.Document(document.Content.slice(0, document.Content.length).mkString(" "))
        para_1 = ""
        for(sentence <- para1_doc.sentences().asScala){
          para_1 += " " + sentence.lemmas().asScala.mkString(" ").toLowerCase()
        }

        content = ""
        for(sentence <- content_doc.sentences().asScala){
          content += " " + sentence.lemmas().asScala.mkString(" ").toLowerCase()
        }
      }

      doc.add(new StringField("title", document.Title, Field.Store.YES))
      doc.add(new TextField("para-1", para_1, Field.Store.YES))
      doc.add(new TextField("text", content, Field.Store.YES))
      return doc
    }

    def Run(qString: String): ListBuffer[JeopardyResult] = {
      val query = new QueryParser("text", analyzer).parse(qString)
      var doc_score_list = new ListBuffer[JeopardyResult]()
      val hitsPerPage = 20
      val reader = DirectoryReader.open(luceneIndex)
      val searcher:IndexSearcher = new IndexSearcher(reader)
      if(!bm25) searcher.setSimilarity(new ClassicSimilarity())
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
