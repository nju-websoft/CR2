package cr2.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.agent.GraphAgent;
import cr2.beans.IntegerEdge;

/**
 * This class is an implementation of the example graph agent. Meanwhile, it is an example of memory-based graph agent implementation.
 *
 */
public class ExampleGraphAgent implements GraphAgent{

	public DirectedMultigraph<Integer, IntegerEdge> graph;
	public ExampleGraphAgent(DirectedMultigraph<Integer, IntegerEdge> graph){
		this.graph=graph;
	}
	@Override
	public List<int[]> getNeighborInfo(Integer id) {
		List<int[]> result = new ArrayList<>();
		Set<IntegerEdge> allEdges =	graph.edgesOf(id);
		for(IntegerEdge ie : allEdges){			
			int[] info = new int[2];//info[0] = neighbor;info[1] = interEdge
			if(ie.getSource()==id){
				info[0] = ie.getTarget();
				info[1] = ie.getEdge();
			}else{
				info[0] = ie.getSource();
				info[1] =-ie.getEdge();//inversed
			}
			result.add(info);			
		}		
		return result;
	}

}
