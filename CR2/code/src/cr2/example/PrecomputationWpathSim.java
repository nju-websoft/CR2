package cr2.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * This class is used for precomputation Wpath similarity,
 * and store them for the step of ranking
 *
 */
public class PrecomputationWpathSim {

	private Integer totalEntities;
	private Set<String> entities;
	private Map<Integer, Integer> ontoClassMap;
	private Map<Integer, Integer> instanceClassMap;
	private List<String> vocabularies;
	private Set<Integer> allType= null;
	private int rootClass;
	private Map<Integer, List<Integer>> ontoTree = null;
	private Map<Integer, List<Integer>> childrenMap = null;
	private Map<Integer, Integer> typeCount = null;
	final static double k=0.4;
	
	PrecomputationWpathSim(Set<String> entities, Map<Integer, Integer> ontoClassMap, Map<Integer, Integer> instanceClassMap,
			List<String> vocabularies) {
		this.entities = entities;
		this.totalEntities = entities.size();
		this.ontoClassMap = ontoClassMap;
		this.instanceClassMap = instanceClassMap;
		this.vocabularies = vocabularies;
	}

	private void findRoot() {
		Set<Integer> keySet = ontoClassMap.keySet();
		Set<Integer> valueSet = new TreeSet<>();
		for (Integer key : keySet) {
			valueSet.add(ontoClassMap.get(key));
		}
		valueSet.removeAll(keySet);
		for (Integer key : valueSet) {
			rootClass = key;
		}
	}

	private void buildOntoTree(Integer node) {
		if (childrenMap.containsKey(node)) {
			for (Integer nodeChildren : childrenMap.get(node)) {
				List<Integer> ancestors = new ArrayList<Integer>();
				ancestors.add(node);
				ancestors.addAll(ontoTree.get(node));
				ontoTree.put(nodeChildren, ancestors);
				buildOntoTree(nodeChildren);
			}
		}
	}

	private void addAllAncestorCount(Integer typeId) {
		Integer num = typeCount.get(typeId);
		typeCount.put(typeId, num+1);
		 List<Integer> ancestors = ontoTree.get(typeId);
		 for(Integer ancestor : ancestors) {
			num = typeCount.get(ancestor);
			typeCount.put(ancestor, num+1);
		 }
	}
	
	private void calClassCount() {
		if(typeCount == null) {
			typeCount = new HashMap<Integer, Integer>();
			
			for(Integer type : allType) {
				typeCount.put(type, 0);
			}
		}
		
		for (String entity : entities) {
			Integer entityId = vocabularies.indexOf(entity);
			Integer entityTypeId;
			
			if(instanceClassMap.containsKey(entityId)) {
				entityTypeId = instanceClassMap.get(entityId);
			}else {
				entityTypeId = rootClass;
			}
			addAllAncestorCount(entityTypeId);
		}
	}

	private void buildChildren() {
		if (childrenMap == null) {
			childrenMap = new HashMap<Integer, List<Integer>>();
		}
		for (Integer key : ontoClassMap.keySet()) {
			Integer father = ontoClassMap.get(key);
			Integer child = key;
			if (childrenMap.containsKey(father)) {
				List<Integer> childrenList = childrenMap.get(father);
				childrenList.add(child);
				childrenMap.put(father, childrenList);
			} else {
				List<Integer> childrenList = new ArrayList<Integer>();
				childrenList.add(child);
				childrenMap.put(father, childrenList);
			}
		}
	}
	
	private double getIC(Integer type) {
		double ic=0;
		int fre = typeCount.get(type);
		ic = -Math.log((double)fre/(double)totalEntities);
		return ic;
	}
	
	private int getDistance(int typeId1, int typeId2) {
		int distance=0;
		Integer commonAncestor = getLCS(typeId1,typeId2);
		List<Integer> typeId1Ancestors = ontoTree.get(typeId1);
		List<Integer> typeId2Ancestors = ontoTree.get(typeId2);
		int len1 = 0;
		if(commonAncestor!=typeId1)
			len1 = typeId1Ancestors.indexOf(commonAncestor)+1;

		int len2 = 0;
		if(commonAncestor!=typeId2)
			len2 = typeId2Ancestors.indexOf(commonAncestor)+1;

		distance = len1 + len2;
		return distance;
	}
	
	private Integer getLCS(int typeId1, int typeId2) {
		List<Integer> typeId1Ancestors = ontoTree.get(typeId1);
		List<Integer> typeId2Ancestors = ontoTree.get(typeId2);
		List<Integer> tmpList1 = new ArrayList<Integer>();
		tmpList1.add(typeId1);
		for(Integer item : typeId1Ancestors) {
			tmpList1.add(item);
		}
		List<Integer> tmpList2 = new ArrayList<Integer>();
		tmpList2.add(typeId2);
		for(Integer item : typeId2Ancestors) {
			tmpList2.add(item);
		}
		tmpList1.retainAll(tmpList2);
		return tmpList1.get(0);
	}
	
	
	
	private double caclWpath(int typeId1, int typeId2) {
		double score = 0;
		int len = getDistance(typeId1,typeId2);
		int lcs = getLCS(typeId1,typeId2);
		double ic = getIC(lcs);
		score = 1 + len * Math.pow(k, ic);
		score = 1/score;
		return score;
	}
	
	public int calculateWpath() throws IOException {
		findRoot();
		buildChildren();
		if (ontoTree == null) {
			ontoTree = new HashMap<Integer, List<Integer>>();
		}

		List<Integer> rootAncestors = new ArrayList<Integer>();
		ontoTree.put(rootClass, rootAncestors);
		for (Integer rootChildren : childrenMap.get(rootClass)) {
			List<Integer> ancestors = new ArrayList<Integer>();
			ancestors.add(rootClass);
			ontoTree.put(rootChildren, ancestors);
			buildOntoTree(rootChildren);
		}
		allType = ontoTree.keySet();
		calClassCount();
		
		String outDir = "example";
		FileWriter fileWriter = new FileWriter(outDir+"/out_ief");
		for(Integer type : typeCount.keySet()) {
			fileWriter.write(type+" "+getIC(type)/Math.log(totalEntities)+ "\n");
			fileWriter.flush();
		}
		fileWriter.close();
		
		List<Integer> allTypeList = new ArrayList<>(allType);
		fileWriter = new FileWriter(outDir+"/out_wpath_score");
		for(int i=0;i<allTypeList.size();i++) {
			for(int j=i+1;j<allTypeList.size();j++) {
				double score = caclWpath(allTypeList.get(i),allTypeList.get(j));
				fileWriter.write(allTypeList.get(i)+" "+allTypeList.get(j)+" "+score+"\n");
				fileWriter.flush();	
			}
		}
		fileWriter.close();
		return rootClass;
	}
}
