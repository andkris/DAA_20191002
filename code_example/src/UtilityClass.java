import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UtilityClass {
		 
	
	public static List<Task> getTasks(String tasksFile) throws IOException, ParseException{
		List<Task> tasks = new ArrayList<Task>();
		BufferedReader br = new BufferedReader(new FileReader(tasksFile));
		String line;
		line = br.readLine();// skip first line (header)
        while ((line = br.readLine()) != null) {
        	String parts[] = line.split("\\t");
        	
        	String identifier = parts[0];
        	String dateFromStr = parts[1];
        	int duration = Integer.parseInt(parts[2]);
        
        	// creating task object
        	Task task = new Task(identifier, dateFromStr, duration);
        	
        	// adding task object to task List
        	tasks.add(task);
        }
        br.close();
        return tasks;	
	}
	
	
	

	public static List<Worker> getWorkers(String workersFile) throws NumberFormatException, IOException {
		List<Worker> workers = new ArrayList<Worker>();
		BufferedReader br = new BufferedReader(new FileReader(workersFile));
		String line;
		line = br.readLine();// eliminuojame pirma eilute su headeriu
        while ((line = br.readLine()) != null) {
        	String parts[] = line.split("\\t");
        	
        	String identifier = parts[0];
        	int maxWorkingHours = Integer.parseInt(parts[1]);
        	
        	// creating task object
        	Worker worker = new Worker(identifier, maxWorkingHours);
        	
        	// adding task object to task List
        	workers.add(worker);
        }
        br.close();
        return workers;	
	}
	
	

	public static void printResults(Schedule shedule, String directory) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer;
		for(Worker worker : shedule.getWorkers()){
			writer = new PrintWriter(directory + "/" + worker.getId() + ".txt", "UTF-8");
			writer.println("Total working hours: " + shedule.totalWorkingTime(shedule.getWorkerTasks().get(worker)));
			
			List<Task> workerTasks =  shedule.getWorkerTasks().get(worker); 
			Collections.sort(workerTasks, Task.Comparators.STARTTIME);
			
			for(Task task : workerTasks){
				writer.println(task);
			}
			writer.close();
		}
		
	}
	
	
}
