package cr2.beans;

/**
 * entity that contains its id and salience.
 */
public class KGEntity {
	public int id;
	public double salience;
	
	public KGEntity(){
		
	}
	
	public KGEntity(int id, double salience){
		this.id = id;
		this.salience = salience;
	}
}
