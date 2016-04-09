/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.text.NumberFormatter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class WikiQuery {

    static int maxHits = 20;

    public static void main(String[] qu) throws IOException, InstantiationException, IllegalAccessException, ParseException {

        maxHits = Integer.parseInt(qu[1]);
        Path path = Paths.get("", "wiki_index", "test");
        Directory index = new SimpleFSDirectory(path);//RAMDirectory();
        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        StandardAnalyzer analyzer = new StandardAnalyzer();

        PrintWriter pw = new PrintWriter(new FileWriter("results_verbose.txt"));
        PrintWriter pwShort = new PrintWriter(new FileWriter("results_short.txt"));

        BufferedReader br = new BufferedReader(new FileReader(qu[0]));
        ArrayList<Integer> hitList = new ArrayList<Integer>();
        System.out.print("Be patient... queries are analysed now ");
        int counter =0;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            String[] split = line.split("::");
            int hit = runQuery(analyzer, searcher, split[0].trim(), split[1].trim(), pw, pwShort);
            hitList.add(hit);
            if(counter++%50==0){
                System.out.print(".");
            }
        }
        System.out.println("");
        System.out.println("Done!");
        pw.close();
        pwShort.close();
   
        System.out.println("Number of queries: " + hitList.size());
        System.out.println("Number of queries answered correctly: " + (hitList.size() - Collections.frequency(hitList, -1)) + " when cut-off point is set to " + maxHits);
        System.out.println("For detailed performance see the generated performance report files");

        makePerformanceReport(hitList);
        
       

    }

    private static int runQuery(StandardAnalyzer analyzer, IndexSearcher searcher, String queryStr, String answer, PrintWriter pw, PrintWriter pwShort) throws ParseException, IOException {
        Query query = new QueryParser("full_text", analyzer).parse(QueryParser.escape(queryStr));
        TopScoreDocCollector collector = TopScoreDocCollector.create(maxHits);
        searcher.search(query, collector);
        //System.out.println("Query\t" + queryStr + "\tTotal number of hits: " + collector.topDocs().totalHits);

        TopDocs docs = searcher.search(query, maxHits);
        ScoreDoc[] hits = docs.scoreDocs;

        pw.println("Query: " + queryStr);
        int queryHit = -1;

        //boolean found=false;
        for (int i = 0; i < hits.length; ++i) {

            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            pw.println("\t" + (i + 1) + ". " + d.get("id") + " " + d.get("url") + "\t" + d.get("title"));
            if (d.get("title").trim().equalsIgnoreCase(answer.trim())) {
                queryHit = i+1;
            }
        }
        pw.println("\t#summary\t:hit_rank:" + queryHit + "\tq:" + queryStr + "\texp_ans:" + answer);
        pwShort.println("\t#summary\t:hit_rank:" + queryHit + "\tq:" + queryStr + "\texp_ans:" + answer);
        return queryHit;
        
    }

    /**
     * 
     * @param hitList: the results obtained from running queries against index files (-1 for not found)
     * that is, the size of hitList is equal to the number of queries.
     * @throws IOException 
     */
    private static void makePerformanceReport(ArrayList<Integer> hitList) throws IOException {
        PrintWriter pwPatN = new PrintWriter(new FileWriter("unfav-patn-cutoff-" + maxHits + ".txt"));
        PrintWriter pwNAP = new PrintWriter(new FileWriter("fav-nap-cutoff-" + maxHits + ".txt"));
        pwPatN.println("n\tP@n");
        pwNAP.println("n\tnap");
        NumberFormat nf = new DecimalFormat("0.000000#");
       // double prev = 0.0;
        for (int i = 1; i < maxHits + 1; i++) {
            int pAtThisI = 0;
            double nap = 0.0;
            for (int j = 0; j < hitList.size(); j++) {
                Integer retrieveRank = hitList.get(j);
                if (retrieveRank != -1 && retrieveRank <= i) {
                    pAtThisI++;
                    nap += (1.0 / retrieveRank);
                   
                }

            }
            pwPatN.println(i + "\t" + nf.format(pAtThisI * 1.0 / hitList.size()));
            pwNAP.println(i + "\t" + nf.format(nap / hitList.size()));
           
        }
        pwPatN.close();
        pwNAP.close();
    }

}
