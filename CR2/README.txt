CR2 is a tool for mining compact, representative and relevant entity relation subgraphs (ERGs).
/code contains the source code.
/example contains an example.
/example/ExampleGraph.pdf visualizes the example.
/example/ExampleTriples provides triple-structured data for the example.
/example/ExampleInstanceType provides entity types for the example.
/example/ExampleOntology provides the entity type subsumption hierarchy for the example.
/example/ExampleNewsEntities provides a set of input entities.
/cr2.jar the runnable jar file. To run the example correctly, you have to put the example folder and cr2.jar in the same directory.

To run the project, you may wish to input 
'java -jar cr2.jar'. 
(We have already set cr2.example.Step4_ExampleRanking.java as the main class, so users don't need to run step by step. 
The whole process is as follows. 
Step1_ExampleTriplePreprocessor: Preprocess. 
Step2_ExampleOracleUsage: Construct a distance oracle (also a kind of preprocessing). 
Step3_ExampleReprSubsetFinding: Identify a subset of the most salient input entities that are representable.
Step4_ExampleRanking: ERG ranking.