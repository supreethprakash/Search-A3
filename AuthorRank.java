import java.awt.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class AuthorRank {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
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

	    PageRank<Integer, String> PR = new PageRank<Integer, String>(graph1, 0.85);
	    PR.setMaxIterations(30);
	    PR.evaluate();
	    
	    HashMap<String, Double> pageRankValues = new HashMap<String, Double>();
	    
	    for(int i = 1; i <  Integer.parseInt(fileContents.get(0).split("     ")[1].replace(" ","")); i++){
	    	pageRankValues.put(authorList.get(i), PR.getVertexScore(Integer.parseInt(authorList.get(i))));
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
		//Close the input stream
		br.close();
	}

}
