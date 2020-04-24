package cr2.rank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.beans.IntegerEdge;
import cr2.example.ExampleGraphAgent;
/**
 * The method aims to calculate subgraph score by semantic cohesion 
 */
public class Cohesion extends AbstractRankMetric{

	

	public Cohesion(ExampleGraphAgent graphAgent) throws IOException {
		super(graphAgent);
		// TODO Auto-generated constructor stub
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
		
		for(int i=0;i<entityList.size();i++) {
			for(int j=i+1;j<entityList.size();j++) {
				Integer type1 = getType(entityList.get(i));
				Integer type2 = getType(entityList.get(j));
				String id="";
				if(type1 == type2) {
					score += 1;
					continue;
				}
				if(type1 < type2) {
					id = type1 + "-" + type2;
				}else {
					id = type2 + "-" + type1;
				}
				double wpath = typePairWpathSim.get(id);
				score += wpath;
			}		
		}
		double num = entityList.size() * (entityList.size()-1);
		num = num/2;
		score = score/num;
		return score;
	}
}
