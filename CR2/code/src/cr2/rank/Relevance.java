package cr2.rank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.beans.IntegerEdge;
import cr2.example.ExampleGraphAgent;
import cr2.rank.AbstractRankMetric;
/**
 * The method aims to calculate subgraph score by the relevance to the news content.
 */
public class Relevance extends AbstractRankMetric{


	public Relevance(ExampleGraphAgent graphAgent) throws IOException {
		super(graphAgent);
	}
	
	private double cosineSimilarity(double vec1[],double vec2[]) {
		double score=0;
		double score1=0;
		double score2=0;
		double score3=0;
		for(int i=0;i<vec1.length;i++) {
			score1 = score1+vec1[i]*vec2[i];
			score2 = score2+vec1[i]*vec1[i];
			score3 = score3+vec2[i]*vec2[i];
		}
		score2 = Math.sqrt(score2);
		score3 = Math.sqrt(score3);
		if(score2==0||score3==0) {
			return 0;
		}
		score = score1/(score2*score3);
		return score;
	}
	
	@Override
	public double calcScore(DirectedMultigraph<Integer, IntegerEdge> associationGraph, List<Integer> queryEntity) {
		
		
		if(associationGraph.edgeSet().size()==0) {
			return 0;
		}
		
		double score = 0;
		Set<Integer> entitySet = new TreeSet<>();
		for(IntegerEdge edge:associationGraph.edgeSet()){
			Integer subject = edge.getSource();
			Integer object = edge.getTarget();
			entitySet.add(subject);
			entitySet.add(object);
		}
		List<Integer> entityList = new ArrayList<Integer>();
		entityList.addAll(entitySet);
		for(Integer entity : entityList) {
			for(Integer query : queryEntity) {
				double vec1[] = new double[typeNum];
				double vec2[] = new double[typeNum];
				int index1 = typeIndex.indexOf(getType(entity));
				int index2 = typeIndex.indexOf(getType(query));
			
				vec1[index1] = 1;
				vec2[index2] = 1;
				List<int[]> neighbors1 = graphAgent.getNeighborInfo(entity);
				List<int[]> neighbors2 = graphAgent.getNeighborInfo(query);
				Map<Integer,Integer> neighborTypeCount1 = new HashMap<Integer,Integer>();
				Map<Integer,Integer> neighborTypeCount2 = new HashMap<Integer,Integer>();
				
				for(int[] neighbor1 : neighbors1) {
					int neighborId = neighbor1[0];
					int neighborType = getType(neighborId);
					if(neighborTypeCount1.containsKey(neighborType)){
						int num = neighborTypeCount1.get(neighborType);
						neighborTypeCount1.put(neighborType, num+1);
					}else {
						neighborTypeCount1.put(neighborType, 1);
					}
				}
				
				for(int[] neighbor2 : neighbors2) {
					int neighborId = neighbor2[0];
					int neighborType = getType(neighborId);
					if(neighborTypeCount2.containsKey(neighborType)){
						int num = neighborTypeCount2.get(neighborType);
						neighborTypeCount2.put(neighborType, num+1);
					}else {
						neighborTypeCount2.put(neighborType, 1);
					}
				}
				for(Integer type : neighborTypeCount1.keySet()) {
					double ief = iefMap.get(type);
					double tf = (double)neighborTypeCount1.get(type)/(double)neighbors1.size();
					int index = typeIndex.indexOf(type);
					vec1[index] = tf*ief;
				}
				for(Integer type : neighborTypeCount2.keySet()) {
					double ief = iefMap.get(type);
					double tf = (double)neighborTypeCount2.get(type)/(double)neighbors2.size();
					int index = typeIndex.indexOf(type);
					vec2[index] = tf*ief;
				}
				
				score += cosineSimilarity(vec1,vec2);
			}
		}
		
		score = score / (double)(entityList.size()*queryEntity.size());
		return score;
	}
	
	
}
