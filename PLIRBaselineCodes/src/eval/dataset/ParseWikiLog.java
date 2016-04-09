/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eval.dataset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class ParseWikiLog {

    // the data file can be downloaded from https://dumps.wikimedia.org/enwiki/20151201/enwiki-20151201-pages-logging.xml.gz 
    public static void main(String[] ss) throws FileNotFoundException,  ParserConfigurationException,   IOException{
        FileInputStream fin = new FileInputStream("data/enwiki-20151201-pages-logging.xml.gz");
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fin);
        InputStreamReader reader = new InputStreamReader(gzIn);
        BufferedReader br = new BufferedReader(reader);
        PrintWriter pw = new PrintWriter(new FileWriter("data/user_page.txt"));
        pw.println("#list of user names and pages that they have edited, deleted or created. These info are mined from logitems of enwiki-20150304-pages-logging.xml.gz");
        TreeMap<String, Set<String>> userPageList = new TreeMap();
        TreeSet<String> pageList = new TreeSet();
        int counterEntry = 0;
        String currentUser = null;
        String currentPage = null;
        try {
            for (String line = br.readLine(); line!=null; line = br.readLine()) {
                
            if (line.trim().equals("</logitem>")) {
                counterEntry++;
                if (currentUser != null && currentPage != null) {
                    updateMap(userPageList, currentUser, currentPage);
                    pw.println(currentUser+"\t"+currentPage);
                    pageList.add(currentPage);
                }
                currentUser = null;
                currentPage = null;
            }else if (line.trim().startsWith("<username>")) {
                currentUser = line.trim().split(">")[1].split("<")[0].replace(" ", "_");
                
            }else if (line.trim().startsWith("<logtitle>")) {
                String content = line.trim().split(">")[1].split("<")[0];
                if(content.split(":").length==1){
                    currentPage= content.replace(" ", "_");
                }
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(ParseWikiLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        pw.println("#analysed " + counterEntry +" entries of wikipesia log file");
        pw.println("#gathered a list of unique user of size " + userPageList.size() );
        pw.println("#gathered a list of pages of size " + pageList.size() );
        pw.close();
        gzIn.close();
        
        PrintWriter pwUser = new PrintWriter(new FileWriter("data/user_list_page_edited.txt"));
        pwUser.println("#list of unique users and pages that they have edited, extracted from logitems of enwiki-20150304-pages-logging.xml.gz");
        for (String user : userPageList.keySet()) {
            pwUser.print(user);
            Set<String> getList = userPageList.get(user);
            for (String page : getList) {
                pwUser.print("\t" + page);
            }
            pwUser.println();
        }
        pwUser.close();
        
        PrintWriter pwPage = new PrintWriter(new FileWriter("data/all_pages.txt"));
        pwPage.println("#list of the unique pages that are extracted from enwiki-20150304-pages-logging.xml.gz");
        for (String page : pageList) {
            pwPage.println(page);
        }
        pwPage.close();
        System.out.println("#analysed " + counterEntry +" entries of wikipesia log file");
        System.out.println("#gathered a list of unique user of size " + userPageList.size() );
        System.out.println("#gathered a list of pages of size " + pageList.size() );
    }
    
    private static void updateMap(TreeMap<String, Set<String>> userPageMap, String user, String page){
        if(userPageMap.containsKey(user)){
            userPageMap.get(user).add(page);
        }else{
            Set<String> pageSet = new TreeSet();
            pageSet.add(page);
             userPageMap.put(user,pageSet);
        }
        
        
    }
}


