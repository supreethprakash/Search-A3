import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.*;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class AuthorRankwithQuery {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		//File path = new File("//Volumes//Supreeth//IUB Classes//3rd Sem//Search//Assignment 3//assignment3//author_index");
		File dir = new File("//Volumes//Supreeth//IUB Classes//3rd Sem//Search//Assignment 3//assignment3//author_index");
		Directory directory = FSDirectory.open(dir);
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);

		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("content", analyzer);
		
		FileInputStream fileStream = new FileInputStream("//Volumes//Supreeth//IUB Classes//3rd Sem//Search//Assignment 3//assignment3//author.net");
		BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
		String strLine;
		ArrayList<String> fileContents = new ArrayList<String>();
		Graph<Integer, String> graph1 = new DirectedSparseGraph<Integer, String> ();
		HashMap<Integer, String> authorList = new HashMap<Integer, String>();
		

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  // Print the content on the console
		  fileContents.add(strLine);
		 }
		
		for(int i = 1; i <=  Integer.parseInt(fileContents.get(0).split("     ")[1].replace(" ","")); i++){
			
			graph1.addVertex(Integer.parseInt(fileContents.get(i).split(" ")[0]));
			authorList.put(i, fileContents.get(i).split(" ")[0]);
		}
		
		for(int i = Integer.parseInt(fileContents.get(0).split("     ")[1].replace(" ","")) + 2; i < fileContents.size(); i++ ){
			int firstAuthor = Integer.parseInt(fileContents.get(i).split(" ")[0]);
			int secondAuthor = Integer.parseInt(fileContents.get(i).split(" ")[1]);
			String authorToAuthor = firstAuthor + "->" + secondAuthor;
			graph1.addEdge(authorToAuthor, firstAuthor, secondAuthor, EdgeType.DIRECTED);
		}
		
		searcher.setSimilarity(new BM25Similarity());
    	String queryString = "Information Retrieval";
		Query query = parser.parse(QueryParser.escape(queryString));
		TopDocs results = searcher.search(query, 300);
		ScoreDoc[] score = results.scoreDocs;
		HashMap<String, Double> authorScores = new HashMap<String, Double>();
		
		for(int i=0;i<score.length;i++){	
			Document doc=searcher.doc(score[i].doc);
			String dId = doc.get("authorid");
			if(authorScores.containsKey(dId)) {
				double firstScore = authorScores.get(dId);
				authorScores.put(dId, (double) score[i].score + firstScore);
			} else {
			authorScores.put(dId, (double) score[i].score);
			}
		}
		
		/* Calculate the score for each author
		 */
		
		double sumOfAllScores = 0.0;
		for (double eachScore : authorScores.values()) {
			sumOfAllScores += eachScore;
		}
		
		double[] priorProb = new double[2001];
		int indexCounter = 0;
		
		for (double eachScore : authorScores.values()) {
			priorProb[indexCounter] = eachScore/sumOfAllScores;
			indexCounter++;
		}
		System.out.println(priorProb.length);
		
		Transformer<Integer, Double> vertex_prior = 
	            new Transformer<Integer, Double>()
	            {            
	         @Override
	                 public Double transform(Integer v) 
	                 {                        
	                     return (double) priorProb[v];            
	                 }           
	            };
		
	    PageRankWithPriors<Integer, String> PRP = new PageRankWithPriors<Integer, String>(graph1, vertex_prior, 0.85);
	    PRP.getVertexPriors();
	    PRP.setMaxIterations(30);
	    PRP.evaluate();
	  
	    HashMap<String, Double> pageRankValues = new HashMap<String, Double>();  
	    for(int i = 1; i <  Integer.parseInt(fileContents.get(0).split("     ")[1].replace(" ","")); i++){
	    	pageRankValues.put(authorList.get(i), PRP.getVertexScore(Integer.parseInt(authorList.get(i))));
		}
	    
	    
	    LinkedList list = new LinkedList(pageRankValues.entrySet());
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       }
	       
	       int counter = 0;
	       
	       Set set = sortedHashMap.entrySet();
	       
	       // Get an iterator
	       Iterator i = set.iterator();
	       counter = 0;
	       // Display elements
	       System.out.println("The top 10 Authors with highest PageRank Values are:\n");
	       while(i.hasNext()) {
	    	  counter++;
	          Map.Entry me = (Map.Entry)i.next();
	          System.out.print("\t"+ me.getKey() + ": ");
	          System.out.println("\t"+ me.getValue());
	          if(counter == 10) {
	        	  break;
	          }
	       }
	       
	}
}
