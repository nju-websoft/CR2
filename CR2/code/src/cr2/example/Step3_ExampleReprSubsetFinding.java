package cr2.example;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.beans.IntegerEdge;
import cr2.beans.KGEntity;
import cr2.qrel.AssociativeEntityFinding;

/**
 * This class is an example class for getting result of input entities after query relaxation.
 * 
 */
public class Step3_ExampleReprSubsetFinding {
	
	private static ExampleGraphAgent graphAgent = null;
	private static ExampleOracleAgent oracleAgent = null;
	private static Map<String,Integer> dictionary = null;
	
	public static void main(String[] args) throws Exception {
		// read entity-relation graph.
		Step1_ExampleTriplePreprocessor.main(null);
		Step2_ExampleOracleUsage.main(null);
		int[] resultIDs = getMostSalientSubset();
		List<String> relaxResult=new ArrayList<>();
		for(int id:resultIDs){
			for(Map.Entry<String, Integer> entry:dictionary.entrySet()){
				if(entry.getValue()==id)
					relaxResult.add(entry.getKey());
			}
		}
		System.out.print("associative entities: "+relaxResult.toString());
	}
	
	public static ExampleGraphAgent getGraphAgent() throws IOException {
		if(graphAgent == null) {
			DirectedMultigraph<Integer, IntegerEdge> graph = new DirectedMultigraph<>(IntegerEdge.class);
			List<String> allLine = Files.readAllLines(Paths.get("example/out_id_relation_triples"), Charset.defaultCharset());
			for (String line : allLine) {
				String[] spo = line.split(" ");
				Integer source = Integer.valueOf(spo[0]);
				Integer target = Integer.valueOf(spo[2]);

				graph.addVertex(source);
				graph.addVertex(target);
				graph.addEdge(source, target, new IntegerEdge(source, target, Integer.parseInt(spo[1])));
			}
			graphAgent = new ExampleGraphAgent(graph);
		}
		return graphAgent;
	}



	public static ExampleOracleAgent getOracleAgent() {
		if(oracleAgent == null) {
			oracleAgent = new ExampleOracleAgent();
		}
		return oracleAgent;
	}


	public static int[] getMostSalientSubset() throws Exception{
//		Step1_ExampleTriplePreprocessor.main(null);
//		Step2_ExampleOracleUsage.main(null);
		
		graphAgent = getGraphAgent();
		oracleAgent = getOracleAgent();
		dictionary = readDictionary();
		
		KGEntity[] queryEntities=new KGEntity[3];
		queryEntities[0]=new KGEntity(dictionary.get("Alice"), 0.5); // The salience depends on specific computing method
		queryEntities[1]=new KGEntity(dictionary.get("Paper01"), 0.8);
		queryEntities[2]=new KGEntity(dictionary.get("Dan"), 0.7);
		
		int diameter=3; // The diameter depends on user's needs.
		AssociativeEntityFinding finder=new AssociativeEntityFinding();
		int[] resultIDs=finder.bestFirst(graphAgent, oracleAgent, diameter, queryEntities);
		
		return resultIDs;
	}
	
	public static Map<String,Integer> readDictionary() throws IOException {
		if(dictionary == null) {
			dictionary = new TreeMap<>();
			List<String> allLine = Files.readAllLines(Paths.get("example/out_dict"), Charset.defaultCharset());
			for (String line : allLine) {
				String[] spo = line.split(",");
				Integer id = Integer.valueOf(spo[0]);
				String uri = spo[1];
				dictionary.put(uri, id);
			}
		}
		return dictionary;
	}
}
