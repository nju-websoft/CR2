package cr2.qrel;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import cr2.agent.OracleAgent;
import cr2.beans.KGEntity;
import cr2.beans.PVertex;
import cr2.example.ExampleGraphAgent;

public class AssociativeEntityFinding{

	/**
	 * The method aims to find a most salient successful sub-query Qmax of queryEntities that 
	 * entities in Qmax have association under specific graph and diameter.
	 * @param graphAgent answer the neighborhood query.
	 * @param oracle answer the distance query.
	 * @param delta  diameter constraint.
	 * @param queryEntities query entities with id and salience.
	 * @return array after query relaxation
	 * @throws Exception
	 */
	public int[] bestFirst(ExampleGraphAgent graphAgent, OracleAgent oracleAgent, int delta, KGEntity[] queryEntities) throws Exception {

		int vertexMax=10000000; //It depends on the number of vertex in the graph.If the number is larger than this value,please modify it.
		int pathDist=delta/2; //search scope
		boolean[][] explored=new boolean[queryEntities.length][vertexMax]; //explored vertices from each starting vertex
		boolean[] checked=new boolean[vertexMax]; //vertices checked by OptWithCert
		
		Queue<PVertex> PQ=new PriorityQueue<PVertex>(1,PVertex.cmp); //element:vertex and its starting vertex and its priority
		
		//compute priority
		double[] priority=new double[queryEntities.length];
		for(int i=0;i<queryEntities.length-1;i++){
			for(int j=i+1;j<queryEntities.length;j++){
				int dist=oracleAgent.queryDistance(queryEntities[i].id, queryEntities[j].id);
				if(dist<=delta){
					priority[i] += queryEntities[j].salience;
					priority[j] += queryEntities[i].salience;
				}
			}
		}
		for(int i=0;i<queryEntities.length;i++){
			priority[i] += queryEntities[i].salience;
			PQ.add(new PVertex(queryEntities[i].id,i,priority[i]));
			explored[i][queryEntities[i].id]=true;
		}
		
		List<KGEntity> Qmax=new ArrayList<>(); // an associative subset with the largest sum of salience.
		
		double currentPriority = 0;
		while(!PQ.isEmpty()){
			PVertex v=PQ.poll();
			if(v.priority<=currentPriority) //cannot be better
				break;
			else{
				if(!checked[v.vid]){ //to avoid repeats
					//call sub-procedure OptWithCert
					List<KGEntity> Qv=SubQuery.OptWithCert(graphAgent, oracleAgent, delta, queryEntities, v.vid, Qmax);
					if(Qv != null){
						double newPriority = EntitySetSOS.computeSOS(Qv);
						if(newPriority>currentPriority){
							Qmax=Qv;
							currentPriority = newPriority;
						}
					}
					checked[v.vid]=true;
				}
				int length=oracleAgent.queryDistance(v.vid, queryEntities[v.svid].id);
				if(length<pathDist){
					if(v.priority <= currentPriority) //cannot be better, to avoid time for expanding neighbors
						break;
					List<int[]> neighborEdges=graphAgent.getNeighborInfo(v.vid);
					for(int[] edge:neighborEdges){ //continue to search neighbors
						int v1=edge[0];
						int length1=oracleAgent.queryDistance(v1, queryEntities[v.svid].id);
						if(!explored[v.svid][v1] && length1==length+1){ //only explore a shortest path
							double prv1 = queryEntities[v.svid].salience;
							for(KGEntity sv:queryEntities){
								if(sv.id==queryEntities[v.svid].id)
									continue;
								int dist=length+1+oracleAgent.queryDistance(v1, sv.id);
								if(dist<=delta)
									prv1 += sv.salience;
							}
							if(prv1<=currentPriority){ //no need to enqueue
								explored[v.svid][v1]=true;
								continue;
							}
							PQ.add(new PVertex(v1,v.svid,prv1));
							explored[v.svid][v1]=true;
						}
					}
				}
			}
		}
		if(Qmax.size()==0){
			return null;
		}
		else{
			int[] result=new int[Qmax.size()];
			for(int i=0;i<Qmax.size();i++)
				result[i]=Qmax.get(i).id;
			return result;
		}
	}

}
