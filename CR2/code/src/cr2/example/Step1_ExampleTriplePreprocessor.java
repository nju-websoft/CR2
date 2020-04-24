package cr2.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.graph.SimpleGraph;

import cr2.beans.SimpleEdge;

/**
 * This class is an example class for preprocessing of the entity-relations
 * graph's triples. The expected result of this class contains a dictionary of
 * URI<-->Id, and triples described by Id corresponding to the original triple
 * file. Id numbers from entities, types and properties must be separable
 * (required in mining stage).
 * 
 */
public class Step1_ExampleTriplePreprocessor {

	public static void main(String[] args) throws IOException {
		List<String> allLine = Files.readAllLines(Paths.get("example/ExampleTriples"), Charset.defaultCharset());

		Set<String> properties = new TreeSet<>();
		Set<String> entities = new TreeSet<>();

		for (String line : allLine) {
			String[] triple = line.split(",");
			entities.add(triple[0]);
			properties.add(triple[1]);
			entities.add(triple[2]);
		}
		List<String> vocabularies = new ArrayList<>();
		int propertyRange, entityRange, typeRange;

		vocabularies.addAll(properties);
		propertyRange = vocabularies.size() - 1;

		vocabularies.addAll(entities);
		entityRange = vocabularies.size() - 1;
		Map<Integer, Integer> instanceClassMap = new HashMap<Integer, Integer>();
		String outDir = "example";
		FileWriter fileWriter = new FileWriter(outDir + "/out_id_type_triples");
		List<String> allLine2 = Files.readAllLines(Paths.get("example/ExampleInstanceType"), Charset.defaultCharset());
		for (String line : allLine2) {
			String[] triple = line.split(",");
			Integer instance = vocabularies.indexOf(triple[0]);
			Integer type = vocabularies.indexOf(triple[2]);
			if (type == -1) {
				vocabularies.add(triple[2]);
				type = vocabularies.size() - 1;
			}
			instanceClassMap.put(instance, type);
			fileWriter.write(instance + " " + type + "\n");
			fileWriter.flush();
		}
		fileWriter.close();

		Map<Integer, Integer> ontoClassMap = new HashMap<Integer, Integer>();
		allLine2 = Files.readAllLines(Paths.get("example/ExampleOntology"), Charset.defaultCharset());
		for (String line : allLine2) {
			String[] triple = line.split(",");
			Integer subTypeId = vocabularies.indexOf(triple[0]);
			Integer fatherTypeId = vocabularies.indexOf(triple[2]);
			if (subTypeId == -1) {
				vocabularies.add(triple[0]);
				subTypeId = vocabularies.size() - 1;
			}

			if (fatherTypeId == -1) {
				vocabularies.add(triple[2]);
				fatherTypeId = vocabularies.size() - 1;
			}
			ontoClassMap.put(subTypeId, fatherTypeId);
		}

		PrecomputationWpathSim preCalculateWpath = new PrecomputationWpathSim(entities, ontoClassMap, instanceClassMap,
				vocabularies);
		int rootTypeId = preCalculateWpath.calculateWpath();
		typeRange = vocabularies.size() - 1;

		Map<String, Integer> dictionary = new TreeMap<>();
		for (int i = 0, len = vocabularies.size(); i < len; i++) {
			dictionary.put(vocabularies.get(i), i);
		}

		fileWriter = new FileWriter(outDir + "/out_id_relation_triples");

		SimpleGraph<Integer, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);

		for (String line : allLine) {
			String[] triple = line.split(",");

			fileWriter.write(dictionary.get(triple[0]) + " " + dictionary.get(triple[1]) + " "
					+ dictionary.get(triple[2]) + "\n");
			fileWriter.flush();

			graph.addVertex(dictionary.get(triple[0]));
			graph.addVertex(dictionary.get(triple[2]));
			graph.addEdge(dictionary.get(triple[0]), dictionary.get(triple[2]));
		}
		fileWriter.close();

		fileWriter = new FileWriter(outDir + "/out_dict");
		for (int i = 0, len = vocabularies.size(); i < len; i++) {
			fileWriter.write(i + "," + vocabularies.get(i) + "\n");
			fileWriter.flush();
		}
		fileWriter.close();

		fileWriter = new FileWriter(outDir + "/out_undirected_graph");
		for (SimpleEdge edges : graph.edgeSet()) {

			Integer source = (Integer) edges.getSource();
			Integer target = (Integer) edges.getTarget();

			fileWriter.write(source + " " + target + "\n");
			fileWriter.flush();
		}
		fileWriter.close();

		fileWriter = new FileWriter(outDir + "/out_id_range");
		fileWriter.write("property:" + 0 + "-" + propertyRange + "\n");
		fileWriter.write("entity:" + (propertyRange + 1) + "-" + entityRange + "\n");
		fileWriter.write("type:" + (entityRange + 1) + "-" + typeRange + "\n");
		fileWriter.write("rootTypeId:" + rootTypeId + "\n");
		fileWriter.flush();
		fileWriter.close();
		System.out.println("property range: 0-" + propertyRange + ", entity range: " + (propertyRange + 1) + "-"
				+ entityRange + ", type range: " + (entityRange + 1) + "-" + typeRange);
	}

}
