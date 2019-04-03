
package edu.arizona.cs

import org.apache.lucene.document.Document

//remove if not needed
import scala.collection.JavaConversions._

class JeopardyResult {
  var Title: String = ""
  var Score: Double = 0
}

class LuceneResult {
  var Doc: Document = _
  var Score: Double = 0
}
