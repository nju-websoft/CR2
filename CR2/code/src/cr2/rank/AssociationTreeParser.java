package cr2.rank;

import java.util.Iterator;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.associations.AssociationNode;
import cr2.associations.AssociationTree;
import cr2.beans.IntegerEdge;

/**
 * 
 * parsing the association tree to  triple
 *
 */
public class AssociationTreeParser {
	
	
	public static DirectedMultigraph<Integer, IntegerEdge> parseTree(AssociationTree tree){
		
		DirectedMultigraph<Integer, IntegerEdge> graph = new DirectedMultigraph<>(IntegerEdge.class);
		Iterator<AssociationNode> iterator = tree.getDFSIterator();		
		while(iterator.hasNext()){
			AssociationNode node = iterator.next();
			if(node.getFather()==null) continue;
			
			int subjectId,predicateId,objectId;
			if(node.getRelation()<0){
				subjectId = node.getId();
				predicateId = -node.getRelation();
				objectId = node.getFather().getId();				
			}else{
				subjectId =  node.getFather().getId();
				predicateId = node.getRelation();
				objectId = node.getId();			
			}
			if(!graph.containsVertex(subjectId))
				graph.addVertex(subjectId);
			if(!graph.containsVertex(objectId))
				graph.addVertex(objectId);
			graph.addEdge(subjectId, objectId,new IntegerEdge(subjectId, objectId, predicateId));
		}
		return graph;
	}
}