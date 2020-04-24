package cr2.rank;

import org.jgrapht.graph.DirectedMultigraph;

import cr2.beans.IntegerEdge;


public class AssociationScore {
	private DirectedMultigraph<Integer, IntegerEdge> associationGraph;
	private double score;
	private int rank;
	public DirectedMultigraph<Integer, IntegerEdge> getAssociationGraph() {
		return associationGraph;
	}
	public void setAssociationGraph(DirectedMultigraph<Integer, IntegerEdge> associationGraph) {
		this.associationGraph = associationGraph;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
}
