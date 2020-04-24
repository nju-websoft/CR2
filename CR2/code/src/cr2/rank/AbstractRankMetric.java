package cr2.rank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.beans.IntegerEdge;
import cr2.example.ExampleGraphAgent;
/**
 * An abstract for ranking methods
 *
 */
public abstract class AbstractRankMetric {
	
	protected Map<Integer, Integer> instanceClassMap;
	protected Map<String, Double> typePairWpathSim;
	protected Map<Integer, Double> iefMap;
	protected ExampleGraphAgent graphAgent;
	protected List<Integer> typeIndex;
	protected Integer typeNum;
	protected Integer rootTypeId; 
	public abstract double calcScore(DirectedMultigraph<Integer, IntegerEdge> associationGraph,List<Integer> queryEntity);
	protected AbstractRankMetric(ExampleGraphAgent graphAgent) throws IOException{
		this.graphAgent = graphAgent;
		if(instanceClassMap == null) {
			instanceClassMap = LoadResource.getInstance().getInstanceClassMap();
			typePairWpathSim = LoadResource.getInstance().getTypePairWpathSim();
			iefMap = LoadResource.getInstance().getIcMap();
			typeNum = iefMap.size();
			typeIndex = new ArrayList<Integer>();
			for(Integer type : iefMap.keySet()) {
				typeIndex.add(type);
			}
			rootTypeId = LoadResource.getInstance().getRootTypeId();
		}
	}
	protected Integer getType(Integer id){
		if(instanceClassMap.containsKey(id)) {
			return instanceClassMap.get(id);
		}else {
			return rootTypeId;
		}
		
	}
}