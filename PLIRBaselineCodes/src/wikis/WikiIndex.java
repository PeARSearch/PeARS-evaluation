/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class WikiIndex {

    public static void main(String[] ss) throws IOException {
        
        GetFiles gf = new GetFiles();
        gf.getCorpusFiles(ss[0]);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Path path = Paths.get("", "wiki_index", "test"); // change later
        Directory index = new SimpleFSDirectory(path);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        for (String file : gf.getFiles()) {
            parseText(file, w);
        }
        w.close();
    }

  
  /* main hack for indexing the documents
    
    */  
    private static void parseText(String fileName, IndexWriter w) throws FileNotFoundException, IOException {
        BufferedReader br = null;
        if (fileName.endsWith("txt")) {
            br = new BufferedReader(new FileReader(fileName));
        } else if (fileName.endsWith("gz")) {
            FileInputStream fin = new FileInputStream(fileName);
            BufferedInputStream in = new BufferedInputStream(fin);
            GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
            InputStreamReader reader = new InputStreamReader(gzIn);
            br = new BufferedReader(reader);
        } else {
            System.out.println("**UNKNOWN FILE FORMAT IS  ignored :" + fileName);
            return;
        }

        System.out.print("analysing " + fileName);

        StringBuilder documentStr = null;
        String line = "";
        String id;
        String url;
        String title;
        Document doc = null;
        int documentIndexed=0;
        while ((line = br.readLine()) != null) {
           // System.out.println("line: " + line);
            if (line.trim().startsWith("<doc")) {
                String[] split = line.split("\"");
                documentIndexed++;
                id = split[1];
                url = split[3];
                title = split[5];
                doc = new Document();
                documentStr= new StringBuilder();
                doc.add(new StringField("title", title, Field.Store.YES));
                doc.add(new StringField("url", url, Field.Store.YES));
                doc.add(new StringField("id", id, Field.Store.YES));
                
            } else if (line.startsWith("</doc")) {
                doc.add(new TextField("full_text", documentStr.toString(), Field.Store.YES));
                w.addDocument(doc);
            }else{
                documentStr.append(line);
            }

        }
        br.close();
        System.out.println(" .... analysed and indexed " + documentIndexed +" <doc> in this file.");
    }
}
