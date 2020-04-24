package cr2.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import cr2.agent.GraphAgent;
import cr2.agent.OracleAgent;
import cr2.beans.AlgLoggerBean;
import cr2.util.CombinationUtil;
import cr2.util.TimeCounter;

/**
 * This class is the main entry of discovery and mining algorithm.
 *
 */
public class AssociationFinder {
	
	public final static long ILLEGAL_QUERYENTITIES_TIMEFLAG = -2;
	//expected association number. If the discovery progress find more association number than this,it would return immediately.
	public int associationLimit = 1000000;
	public int thingId = Integer.MAX_VALUE;
	public static Logger logger = Logger.getLogger("RunningLog");
	public AlgLoggerBean recorder=new AlgLoggerBean();
	
	private PruningStrategy runingStrategy = PruningStrategy.PRN_1;	

	public PruningStrategy getRuningStrategy() {
		return runingStrategy;
	}

	public void setRuningStrategy(PruningStrategy runingStrategy) {
		this.runingStrategy = runingStrategy;
	}

	public void resetRecorder(){recorder=new AlgLoggerBean();}
	
	/**
	 * this method firstly enumerates all the paths from different query entity,then find out their common 
	 * node. Finally accord their common nodes (in each path) to construction association tree.
	 * @param graphAgent answer the neighborhood query.
	 * @param oracle answer the distance query.
	 * @param delta  diameter constraint.
	 * @param queryEntities query entity ids.
	 * @return association tree set.
	 */
	public List<AssociationTree> discovery(GraphAgent graphAgent,OracleAgent oracleAgent,int delta,List<Integer> queryEntities){
		resetRecorder();
		recorder.runingStrategy = this.runingStrategy;
		recorder.delta = delta;
		recorder.queryEntityNumber = queryEntities.size();
		
		for(int queryEntityID:queryEntities) {
			recorder.queryEntityIDs+=queryEntityID+",";		
		}
		List<AssociationTree> result = new ArrayList<>();		
		
		//step 1. enumeration
		List<Map<Integer,List<Path>>> 
				allEnumeratedPathForQueryEntities = new ArrayList<>(queryEntities.size());		
		long startTimeStub = System.currentTimeMillis();		
		try{		
			for(int queryEntity:queryEntities){ 
				Map<Integer,List<Path>> pathesFromQueryEntities = new HashMap<>();				
				if(runingStrategy.equals(PruningStrategy.PRN))		
					pathesFromQueryEntities = PathEnumerator.enumeratePathesViaPRN(this.recorder,queryEntity, graphAgent, oracleAgent, delta, queryEntities);
				else if(runingStrategy.equals(PruningStrategy.PRN_1))
					pathesFromQueryEntities = PathEnumerator.enumeratePathesViaPRN_1(this.recorder,queryEntity, graphAgent, oracleAgent, delta, queryEntities);
				else if(runingStrategy.equals(PruningStrategy.BSC))
					pathesFromQueryEntities = PathEnumerator.enumeratePathesViaBSC(this.recorder,queryEntity, graphAgent, oracleAgent, delta, queryEntities);
				else{
					logger.info("Illegal Runing State setting");
					//System.err.println("Illegal Runing State setting");
					System.exit(3);
				}
				allEnumeratedPathForQueryEntities.add(pathesFromQueryEntities);	
			}
			recorder.semipathCombinationTime = System.currentTimeMillis()-startTimeStub;
		}catch(IllegalArgumentException e){
			recorder.semipathCombinationTime = ILLEGAL_QUERYENTITIES_TIMEFLAG;//setting time illegal
			return null;
		}	
		
		Set<List<Integer>> canonicalCodeSet = new HashSet<>();		
		TimeCounter tc = new TimeCounter();
		
		//step 2. find out common nodes.
		HashSet<Integer> finalCommonIds = null;		
		for(Map<Integer, List<Path>> queryEntityEntryMap : allEnumeratedPathForQueryEntities){			
			HashSet<Integer> idSetCopy = new HashSet<>();//figure out why  we have to copy instead of use itself.
			
			int totalPathesNumber = 0;
			for(Entry<Integer,List<Path>> entry: queryEntityEntryMap.entrySet()){
				idSetCopy.add(entry.getKey());
				totalPathesNumber+=entry.getValue().size();
			}								
			recorder.queryPathesNumber+=totalPathesNumber+",";
			
			if(finalCommonIds == null)finalCommonIds = idSetCopy;
			else finalCommonIds.retainAll(idSetCopy);
		}
		
		//step 3. construction.
		for(Integer instanceId : finalCommonIds){
			List<List<Path>> currentCommonIdQENodes = new ArrayList<>(queryEntities.size());			
			for(Map<Integer, List<Path>> qeNodeMap : allEnumeratedPathForQueryEntities)
				currentCommonIdQENodes.add(qeNodeMap.get(instanceId));			
			try{
				CombinationUtil.CombinateAndGenerateAssociationResult(associationLimit,tc, result,canonicalCodeSet, currentCommonIdQENodes, delta, queryEntities);	
			}catch(Throwable t){				
				if(t instanceof OutOfMemoryError){
					System.err.println("out of memory:"+result.size());
					System.exit(1);
				}
				t.printStackTrace(); System.exit(2);	
			}
		}
		
		recorder.runingTime = System.currentTimeMillis()-startTimeStub;
		recorder.validAssociationNumber = result.size();		
		recorder.checkingTime = tc.checkTime;
		recorder.constructTime = tc.constructTime;		
		return result;		
	}
}
