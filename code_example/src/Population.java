import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Population {
	
	private List<Schedule> shedules;
	
	private int populationSize; 
	
	public List<Schedule> getShedules() {
        return shedules;
    }

    public void setShedules(List<Schedule> shedules) {
        this.shedules = shedules;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

	public Population(int populationSize, boolean initialise, List<Task> tasks, List<Worker> workers) {
		this.populationSize = populationSize;
		
		shedules = new ArrayList<Schedule>();
		
		if (initialise) {
	          for (int i = 0; i < this.populationSize; i++) {
	        	  Schedule newShedule = new Schedule(tasks, workers);
	        	  newShedule.generateShedule();
	              saveShedule(i, newShedule);
	          }
		}
	}
	
	
	public Population(int populationSize) {
		this.populationSize = populationSize;
		shedules = new ArrayList<Schedule>();
	}

	public Schedule getShedule(int index) {
        return shedules.get(index);
    }
	
    public void saveShedule(int index, Schedule indiv) {
        shedules.add(index, indiv); 
    }
    
    public int size() {
        return shedules.size();
    }

    public Schedule getFittest(int index){
    	Collections.sort(shedules, Schedule.Comparators.TARGET);
    	return  shedules.get(index);
    }
    
    public Schedule getFittest(){
    	Collections.sort(shedules, Schedule.Comparators.TARGET);
    	return  shedules.get(0);
    }
    public double getMean(){
    	double mean = 0;
    	for (Schedule shedule : shedules) {
    		mean = mean + shedule.getTargetValue();
    	}
    	mean = mean/shedules.size();
    	return  mean;
    }
}
