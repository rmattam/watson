package edu.arizona.cs

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, StringField, TextField}
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.document.Document
import org.apache.lucene.store.RAMDirectory

import scala.io.Source

class Index {
    private var luceneIndex = new RAMDirectory()
    private val analyzer = new StandardAnalyzer()
    private var writer:IndexWriter = null

    def Create():Unit = {
      luceneIndex = new RAMDirectory()
    }

    def Open():Unit = {
      val config = new IndexWriterConfig(analyzer)
      writer = new IndexWriter(luceneIndex, config)
    }

    def Close(): Unit ={
      writer.close()
    }

    def Add(document: WikiDoc): Unit ={
      writer.addDocument(CreateDocument(document))
    }

    private def CreateDocument(document: WikiDoc): Document = {
      val doc: Document = new Document()
      doc.add(new TextField("title", document.Title, Field.Store.YES))
      doc.add(new StringField("para-1", document.Content(0), Field.Store.YES))
      doc.add(new StringField("text", document.Content.slice(0, document.Content.length).mkString(" "), Field.Store.YES))
      return doc
    }
}
