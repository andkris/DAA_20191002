import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
Code for students, to understand how the Genetic optimization technique can be 
customized for rostering problem. There code is not optimized for the data set 
example presented together. 
 * 
 * Hard constraints (not parametrized):
 * 	MAXIMUM __ HOURS PER DAY
 *  MAX __ WORKING DAYS IN SEQ
 *  FREE DAYS IN SEQ (MIN __)
 *  
 */
public class Main {
		
	public static void main(String[] args) throws IOException, ParseException, InterruptedException, ExecutionException{
		
		Config.prepareData();
		
		// getting initial data
		List<Task> tasks = UtilityClass.getTasks("data/tasks.txt");		
		
		List<Worker> workers = UtilityClass.getWorkers("data/workers.txt");
		
		Population newPop = new Population(10, true, tasks, workers); 
		
		// Writing results
		System.out.println("Total tasks: " + tasks.size() );
		
		System.out.println("Total workers: " + workers.size() );
		
		
		Schedule fittest = newPop.getFittest();
		
		System.out.println("Total skiped tasks (before optimization): " + fittest.getSkipedTaskList().size() );


		// GA OPTIMIZATION START -----------
		int sameIterationCount = 1;
		int lastTarget = newPop.getFittest(0).getTargetValue();
        while (sameIterationCount < 20) {
        	  
        	  newPop = GeneticOptimization.evolvePopulation(newPop);
        	  
	          Schedule bestShedule = newPop.getFittest();
        	  if(lastTarget != bestShedule.getTargetValue()){
				  sameIterationCount = 1;
				  lastTarget = bestShedule.getTargetValue();
				  fittest = bestShedule;
			  }
			  
        	  System.out.println("[Not assigned taskes: " + bestShedule.getSkipedTaskList().size() + " / Overtime: " + bestShedule.getTargetValue()+ "] Same iteration count: " + sameIterationCount + " target value: " + bestShedule.getTargetValue() + " mean: " + newPop.getMean());
 			  sameIterationCount++;
		 
		}
    	
    	System.out.println("Total skiped tasks (after optimization): " + fittest.getSkipedTaskList().size() );
    	// GA OPTIMIZATION END -----------
		
		// printed results
		UtilityClass.printResults(fittest, "results");
		
    }
   
}