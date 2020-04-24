package cr2.agent;

/**
 * This interface provide a checking mechanism( answer whether a given id of an uri is a property).
 * 
 */
public interface RelationChecker {
	
	public boolean isIdRelation(int id);
	public boolean isIdRelation(String id);
}
