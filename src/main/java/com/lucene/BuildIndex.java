package com.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.Date;

/**
 * lucene 创建对应索引
 * User: liyangli
 * Date: 2016/2/16
 * Time: 09:43
 */
public class BuildIndex {


    public static void main(String[] args) throws Exception{
        //开始启动进行写入索引文件
        Directory dir = FSDirectory.open(Paths.get(""));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);
        Document doc = new Document();
        Field pathField = new StringField("time","hello12323", Field.Store.YES);
         doc.add(pathField);
        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
          // New index, so we just add the document (no old document can be there):
          System.out.println("adding " );
          writer.addDocument(doc);
        } else {
          // Existing index (an old copy of this document may have been indexed) so
          // we use updateDocument instead to replace the old one matching the exact
          // path, if present:
//          System.out.println("updating " + file);
          writer.updateDocument(new Term("path", ""), doc);
        }
    }
}
