package cr2.example;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.associations.AssociationFinder;
import cr2.associations.AssociationTree;
import cr2.beans.IntegerEdge;
import cr2.rank.AbstractRankMetric;
import cr2.rank.AssociationScore;
import cr2.rank.Cohesion;
import cr2.rank.RankAssociation;
import cr2.rank.Relevance;

/**
 * This class is an example for finding associations of entities and ranking them according to some metrics.
 *
 */
public class Step4_ExampleRanking {
	private static double alpha = 0.5;
	public static void main(String[] args) throws Exception {
		Step1_ExampleTriplePreprocessor.main(null);
		Step2_ExampleOracleUsage.main(null);
		int[] relaxResultID = Step3_ExampleReprSubsetFinding.getMostSalientSubset();
		List<Integer> relaxResult = new ArrayList<Integer>();
		for(int id : relaxResultID) {
			relaxResult.add(id);
		}

		ExampleGraphAgent graphAgent = Step3_ExampleReprSubsetFinding.getGraphAgent();
		ExampleOracleAgent oracleAgent = Step3_ExampleReprSubsetFinding.getOracleAgent();
		Map<String,Integer> dictionary = Step3_ExampleReprSubsetFinding.readDictionary();
		
		List<String> relaxEntities = new ArrayList<>();
		for(int id:relaxResultID){
			for(Map.Entry<String, Integer> entry:dictionary.entrySet()){
				if(entry.getValue()==id)
					relaxEntities.add(entry.getKey());
			}
		}
		System.out.println("associative entities: "+relaxEntities.toString());
		
		AssociationFinder finder = new AssociationFinder();
		List<AssociationTree> associations =finder.discovery(graphAgent, oracleAgent, 3, relaxResult);
		
		Map<AbstractRankMetric, Double> rankMetrics = new HashMap<AbstractRankMetric, Double>();
		rankMetrics.put(new Cohesion(graphAgent), alpha);
		rankMetrics.put(new Relevance(graphAgent), (1-alpha));
		
		RankAssociation rank = new RankAssociation();
		
		List<String> allLine = Files.readAllLines(Paths.get("example/ExampleNewsEntities"), Charset.defaultCharset());
		List<Integer> querys = new ArrayList<Integer>();
		for (String line : allLine) {
			querys.add(dictionary.get(line));
		}
		
		AssociationScore associationTemp = rank.rank(associations, rankMetrics, querys);
		DirectedMultigraph<Integer, IntegerEdge> associationGraph = associationTemp.getAssociationGraph();
		System.out.println("score:"+associationTemp.getScore());
		for(IntegerEdge edge:associationGraph.edgeSet()){
			String subject = getLabel(edge.getSource(), dictionary);
			String predicate = getLabel(edge.getEdge(), dictionary);
			String object = getLabel(edge.getTarget(), dictionary);
			String triple = subject+" - "+predicate+" - "+object;
			System.out.println(triple);
		}
	}
	
	private static String getLabel(Integer id, Map<String,Integer> dictionary) {
		String label="";
		for(Map.Entry<String, Integer> entry:dictionary.entrySet()){
			if(entry.getValue()==id)
				label = entry.getKey();
		}
		return label;
	}
}
