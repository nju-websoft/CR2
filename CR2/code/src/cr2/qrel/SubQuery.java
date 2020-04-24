package cr2.qrel;

import java.util.ArrayList;
import java.util.List;

import cr2.agent.OracleAgent;
import cr2.beans.KGEntity;
import cr2.example.ExampleGraphAgent;

public class SubQuery {
	/**
	 * The method aims to find the largest associative subset of query entities certified by certificate better than Qknown.
	 * @param graphAgent answer the neighborhood query.
	 * @param oracleAgent answer the distance query.
	 * @param delta diameter constraint.
	 * @param queryEntities query entities with id and salience.
	 * @param certificate the entity that may prove the existence of relationship subgraphs for a subset of query entities.
	 * @param Qknown an already known associative sub-query.
	 * @return a more salient associative subset than Qknown if exists, otherwise null.
	 */
	public static List<KGEntity> OptWithCert(ExampleGraphAgent graphAgent,
			OracleAgent oracleAgent,int delta,KGEntity[] queryEntities,int certificate,List<KGEntity> Qknown){
		
		List<KGEntity> VQ1=new ArrayList<>(); //dist(v,c)<=ceil(D/2)
		List<KGEntity> VQ2=new ArrayList<>(); //dist(v,c)=ceil(D/2)
		List<KGEntity> VQ3=new ArrayList<>(); //dist(v,c)<ceil(D/2)
		
		int pathDist=(delta+1)/2;
		for(KGEntity kgEntity:queryEntities){
			int d=oracleAgent.queryDistance(kgEntity.id, certificate);
			if(d<pathDist){
				VQ1.add(kgEntity);
				VQ3.add(kgEntity);
			}
			else if(d==pathDist){
				VQ1.add(kgEntity);
				VQ2.add(kgEntity);
			}
		}
		
		if(Qknown!=null && EntitySetSOS.computeSOS(VQ1)>EntitySetSOS.computeSOS(Qknown)){
			List<KGEntity> maxWeightEntities=null;
			if(delta%2==0 || VQ2.size()<=1)
				maxWeightEntities=VQ1;
			else{
				List<int[]> neighborEdges=graphAgent.getNeighborInfo(certificate);
				double maxWeight = 0;
				for(int[] edge:neighborEdges){
					List<KGEntity> Vcurrent=new ArrayList<>(); 
					Vcurrent.addAll(VQ3);
					int c1=edge[0];
					for(KGEntity kgEntity:VQ2){
						int dist=oracleAgent.queryDistance(c1, kgEntity.id);
						if(dist<pathDist){ //c has a neighbor c1 that dist(v,c1)<=ceil(D/2)-1
							Vcurrent.add(kgEntity);
						}
					}
					if(EntitySetSOS.computeSOS(Vcurrent)>maxWeight){
						maxWeight=EntitySetSOS.computeSOS(Vcurrent);
						maxWeightEntities=Vcurrent;
					}
					if(Vcurrent.size()==VQ1.size()) //find common vertex and no better sub-query
						break;
				}
			}
			if(maxWeightEntities!=null&&maxWeightEntities.size()>1)
				return maxWeightEntities;
			else
				return null; //no successful sub-query
		}
		else
			return null; //no better successful sub-query
	}
}
