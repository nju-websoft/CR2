package cr2.oracle;

/**
 *  An abstract oracle.
 */
public abstract class AbstractOracle implements Oracle{
	
	public abstract byte Query(int source, int target);	
}
