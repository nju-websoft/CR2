package cr2.rank;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 *  Using singleton pattern loads resources for ranking 
 *
 */
public class LoadResource {

	private Map<Integer, Integer> instanceClassMap;
	private Map<String, Double> typePairWpathSim;
	private Map<Integer, Double> iefMap;
	private Integer rootTypeId;
	private static LoadResource instance = null;

	
	
	
	public Integer getRootTypeId() {
		return rootTypeId;
	}

	public Map<Integer, Double> getIcMap() {
		return iefMap;
	}

	public Map<Integer, Integer> getInstanceClassMap() {
		return instanceClassMap;
	}

	public void setInstanceClassMap(Map<Integer, Integer> instanceClassMap) {
		this.instanceClassMap = instanceClassMap;
	}

	public Map<String, Double> getTypePairWpathSim() {
		return typePairWpathSim;
	}

	public void setTypePairWpathSim(Map<String, Double> typePairWpathSim) {
		this.typePairWpathSim = typePairWpathSim;
	}

	public static LoadResource getInstance() {
		if (instance == null) {
			try {
				instance = new LoadResource();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}

	private LoadResource() throws IOException {
		instanceClassMap = new HashMap<Integer, Integer>();
		List<String> allLine = Files.readAllLines(Paths.get("example/out_id_type_triples"), Charset.defaultCharset());
		for (String line : allLine) {
			String[] triple = line.split(" ");
			Integer instance = Integer.parseInt(triple[0]);
			Integer type = Integer.parseInt(triple[1]);
			instanceClassMap.put(instance, type);
		}
		
		typePairWpathSim = new HashMap<String, Double>();
		allLine = Files.readAllLines(Paths.get("example/out_wpath_score"), Charset.defaultCharset());
		for (String line : allLine) {
			String[] triple = line.split(" ");
			Integer type1 = Integer.parseInt(triple[0]);
			Integer type2 = Integer.parseInt(triple[1]);
			Double score = Double.parseDouble(triple[2]);
			String key = "";
			if(type1<type2) {
				key = type1 +"-" + type2;
			}else {
				key = type2 +"-" + type1;
			}
			typePairWpathSim.put(key, score);
		}
		
		iefMap = new HashMap<Integer, Double>();
		allLine = Files.readAllLines(Paths.get("example/out_ief"), Charset.defaultCharset());
		for (String line : allLine) {
			String[] triple = line.split(" ");
			Integer type = Integer.parseInt(triple[0]);
			Double ief = Double.parseDouble(triple[1]);
			iefMap.put(type, ief);
		}
		
		allLine = Files.readAllLines(Paths.get("example/out_id_range"), Charset.defaultCharset());
		for (String line : allLine) {
			String[] triple = line.split(":");
			if(triple[0].equals("rootTypeId")) {
				rootTypeId = Integer.parseInt(triple[1]);
				break;
			}			
		}
	}
}
