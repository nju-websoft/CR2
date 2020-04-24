package cr2.rank;

import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.associations.AssociationTree;
import cr2.beans.IntegerEdge;

/**
 * 
 * Calling the ranking functions to calculate the score of all association 
 * and return the top 1
 *
 */
public class RankAssociation {
	public AssociationScore rank(List<AssociationTree> associations, Map<AbstractRankMetric,Double> rankMetrics,List<Integer> quaryEntity) {
		double maxScore=-1;
		DirectedMultigraph<Integer, IntegerEdge> maxAssociationGraph =null;
		for(int i=0;i<associations.size();i++){
			DirectedMultigraph<Integer, IntegerEdge> associationGraph = AssociationTreeParser.parseTree(associations.get(i));
			double score = 0;
			for(AbstractRankMetric rankMetric:rankMetrics.keySet()) {
				score = score + rankMetric.calcScore(associationGraph,quaryEntity)*rankMetrics.get(rankMetric);
			}
			if(score>maxScore) {
				maxAssociationGraph = associationGraph;
				maxScore = score;
			}
		}
		AssociationScore associationScore = new AssociationScore();
		associationScore.setAssociationGraph(maxAssociationGraph);
		associationScore.setScore(maxScore);
		return associationScore;
	}
}
