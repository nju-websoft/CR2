package cr2.agent;

public interface OracleAgent {
	/**
	 * 
	 * This interface is an enclosure of distance oracle(from database or memory). Given two vertexes, any class implements this should answer
	 * the distance between these vertexes.
	 * 
	 */
	public int queryDistance(int source,int target);
}