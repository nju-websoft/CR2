package cr2.qrel;

import java.util.List;

import cr2.beans.KGEntity;

public class EntitySetSOS {
	
	/**
	 * The method aims to compute SOS(sum of salience) of an entity set.
	 * @param entities query entities with id and salience.
	 * @return SOS of input entities.
	 */
	public static double computeSOS(List<KGEntity> entities){
		if(entities == null)
			return 0;
		double totalWeight = 0;
		for(KGEntity kgEntity:entities){
			totalWeight += kgEntity.salience;
		}
		return totalWeight;
	}
}
