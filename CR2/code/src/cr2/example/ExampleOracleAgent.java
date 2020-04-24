package cr2.example;

import cr2.agent.OracleAgent;
import cr2.oracle.DefaultOracle;

/**
 * This class is an implementation of the example oracle agent.Meanwhile, it is an example of memory-based oracle agent implementation.
 *
 */
public class ExampleOracleAgent implements OracleAgent{
	private DefaultOracle oracle;
	
	public ExampleOracleAgent(){
		oracle=new DefaultOracle();
		oracle.LoadIndex("example/oracle");
	}
	
	@Override
	public int queryDistance(int source, int target) {
		return oracle.Query(source, target);
	}
	
}
