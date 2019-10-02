/**
 * 
 *  worker class
 *
 */
public class Worker  implements Comparable<Worker>{ 

	/** identifier*/
	private String id;
	
	/** maximum working time per month */
	private int maxHoursPerMonth;	
	
	Worker(String id, int maxHoursPerMonth){
    	// assigning by constructor passed values 
    	this.id = id;
    	this.maxHoursPerMonth = maxHoursPerMonth;	
    }
	
	public int compareTo(Worker other){
        return this.id.compareTo(other.id);
    }
	
	// getters and setters (auto-generated)
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaxHoursPerMonth() {
		return maxHoursPerMonth;
	}

	public void setMaxHoursPerMonth(int maxHoursPerMonth) {
		this.maxHoursPerMonth = maxHoursPerMonth;
	}

}