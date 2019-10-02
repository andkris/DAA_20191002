import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Schedule {

    private List<Task> tasks;

    private List<Worker> workers;
    
    private Map<Worker, ArrayList<Task>> workerTasks;

    private List<Task> skipedTaskList = new ArrayList<Task>();

    // constructon to create initial schedule 
    Schedule(List<Task> tasks, List<Worker> workers) {
        this.tasks = tasks;
    	Collections.sort(this.tasks, Task.Comparators.STARTTIME);
        this.workers = workers;   
        
        workerTasks = new TreeMap<Worker, ArrayList<Task>>();
        for (Worker worker : workers) {
            workerTasks.put(worker, new ArrayList<Task>());
        }
        
    }
    // constructon to create schedule in genetic optimization stage
    Schedule(List<Task> tasks, List<Worker> workers, Map<Worker, ArrayList<Task>> workerTasks) {
        this.tasks = tasks;
    	Collections.sort(this.tasks, Task.Comparators.STARTTIME);
        this.workers = workers;   
        this.workerTasks = workerTasks;     
        
        skipedTaskList = new ArrayList<Task>(tasks);
        for (ArrayList<Task> eachWorkerTasks : workerTasks.values()) {
            skipedTaskList.removeAll(eachWorkerTasks);
        }
    }
    
	public void generateShedule() {
	    	
	        skipedTaskList = new ArrayList<Task>();
	
	        // make random list of tasks
	        List<Task> taskList = new ArrayList<Task>(tasks);
	        Collections.shuffle(taskList, new Random());
	        int numberOfWorkers = workers.size();
	        
	        
	        // make list of workers
	        List<Worker> workerList = new ArrayList<Worker>(workers);
	
	        // iterating by randimized tasks lists
	        for (Task task : taskList) {
	            
	        	int workerIndex = (int) (Math.random() * numberOfWorkers);
				Worker worker = workers.get(workerIndex);
				workerTasks.get(worker).add(task);
	        }
        createScheduleAfterMutation();
    }
    
    

	public void createScheduleAfterMutation() {
    	
        // create schedule for workers
        for (Entry<Worker, ArrayList<Task>> entry : workerTasks.entrySet()) {
            Worker worker = entry.getKey();
            ArrayList<Task> initialWorkerTasks = entry.getValue();
            
            long seed = System.nanoTime();
            
            workerTasks.put(worker, new ArrayList<Task>());
            //initialWorkerTasks.addAll(skipedTaskList);
            Collections.sort(initialWorkerTasks,Task.Comparators.STARTTIME);
            for (Task task : initialWorkerTasks) {
            	boolean isBusy = false;
                
                // check task's do not intersect with already assigned tasks
                for (Task workerTask : workerTasks.get(worker)) {
                    if (tasksIntersects(task, workerTask) ) {
                        isBusy = true;
                        break;
                    }
                }
            	// try to add, and check the worker constraints still valid 
                // (2 free days period / max working hours per day / max conseq working days)
                List<Task> workerTasksTmp = (List<Task>) workerTasks.get(worker).clone();
                workerTasksTmp.add(task);

                if(!workerIsAbleToPerformTask(workerTasksTmp)){
                	isBusy = true;               	
                }

                if (isBusy && !skipedTaskList.contains(task)) {
                	skipedTaskList.add(task);
                } else {
                    // worker can perform the task
                    workerTasks.get(worker).add(task);
                    if(skipedTaskList.contains(task)) {
                    	skipedTaskList.remove(task);
                    }
                }
                
            }
    
		}
        
}
    // check two tasks interesects 
    public static boolean tasksIntersects(Task task1, Task task2) {
        if ((task1.getTaskStartTime() >= task2.getTaskStartTime() && task1.getTaskStartTime() <= task2.getTaskEndTime())
                || (task1.getTaskEndTime() >= task2.getTaskStartTime() && task1.getTaskEndTime() <= task2.getTaskEndTime())
                || (task1.getTaskStartTime() >= task2.getTaskStartTime() && task1.getTaskEndTime() <= task2.getTaskEndTime())
                || (task2.getTaskStartTime() >= task1.getTaskStartTime() && task2.getTaskEndTime() <= task1.getTaskEndTime())) { 
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean workerIsAbleToPerformTask(List<Task> workerTasks) {
    	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    	Collections.sort(workerTasks, Task.Comparators.STARTTIME);
    
    	int workingDaysInSeq = 0;
    	int beforeTaskDay = 0;
    	int startTimeInMinutes = 0;
    	
    	for (Task task : workerTasks){
    		calendar = GregorianCalendar.getInstance();
    		calendar.setTime(task.getDateFrom());   // assigns calendar to given date 
    		if (calendar.get(calendar.DAY_OF_MONTH) == beforeTaskDay){
    			// secound task in day
    			int totalWorkingMinutesInDay = (calendar.get(calendar.HOUR_OF_DAY) * 60 + calendar.get(calendar.MINUTE) )  -  startTimeInMinutes;
    			if (totalWorkingMinutesInDay > 8*60){ // MAXIMUM 8 HOURS PER DAY
    				return false;
    			}
    		}else{
    			if(calendar.get(calendar.DAY_OF_MONTH) - beforeTaskDay == 1){
    				workingDaysInSeq++;
    				if(workingDaysInSeq > 5){   // MAX 5 WORKING DAYS IN SEQ
    					return false;
    				}
    				beforeTaskDay = calendar.get(calendar.DAY_OF_MONTH);
    			}else{
    				workingDaysInSeq = 1;
    				// FREE DAYS IN SEQ (MIN 2)
    				if(calendar.get(calendar.DAY_OF_MONTH) - beforeTaskDay < 3){
    					return false;
    				}
    			}
    			
    			beforeTaskDay = calendar.get(calendar.DAY_OF_MONTH); 
    			startTimeInMinutes = calendar.get(calendar.HOUR_OF_DAY) * 60 + calendar.get(calendar.MINUTE) -  task.getDuration();
    		}
    	}
    	return true;
    }
    

    public int getTargetValue() {

    	int target = 0;
    	
        for (Task task : skipedTaskList) {
        	target += 10000;  
        }
        
        for (Worker worker :workers) {
        	if(worker.getMaxHoursPerMonth() < totalWorkingTime(this.workerTasks.get(worker))){
        		target +=  totalWorkingTime(this.workerTasks.get(worker)) - worker.getMaxHoursPerMonth();
        	}
        }
        return target;
    }


    public  int totalWorkingTime(List<Task> workerTasks) {
    	int totalWorkingTime = 0;
    	
    	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    	Collections.sort(workerTasks, Task.Comparators.STARTTIME);
    
    	int beforeTaskDay = 0;
    	int startTimeInMinutes = 0;
    	
    	for (Task task : workerTasks){
    		calendar = GregorianCalendar.getInstance();
    		calendar.setTime(task.getDateFrom());   // assigns calendar to given date 
    		if (calendar.get(calendar.DAY_OF_MONTH) == beforeTaskDay){
    			// secound task in day
    			totalWorkingTime += (calendar.get(calendar.HOUR_OF_DAY) * 60 + calendar.get(calendar.MINUTE) )  -  startTimeInMinutes;
    		}else{
    			beforeTaskDay = calendar.get(calendar.DAY_OF_MONTH); 
    			startTimeInMinutes = calendar.get(calendar.HOUR_OF_DAY) * 60 + calendar.get(calendar.MINUTE) -  task.getDuration();
    		}
    	}
    	totalWorkingTime += (calendar.get(calendar.HOUR_OF_DAY) * 60 + calendar.get(calendar.MINUTE) )  -  startTimeInMinutes;
    	return totalWorkingTime / 60;
    }
    
    
    
    
    /**
     * Comparator'ius for sort shedules by value of Target function
     */
    public static class Comparators {

        public static Comparator<Schedule> TARGET = new Comparator<Schedule>() {
            public int compare(Schedule o1, Schedule o2) {
                return o1.getTargetValue() - o2.getTargetValue();
            }
        };
    }



	public List<Task> getSkipedTaskList() {
		return skipedTaskList;
	}


	public void setSkipedTaskList(List<Task> skipedTaskList) {
		this.skipedTaskList = skipedTaskList;
	}


	@Override
	public String toString() {
		return "Shedule [tasks=" + tasks + ", workers=" + workers + "]";
	}


	public List<Task> getTasks() {
		return tasks;
	}


	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}


	public List<Worker> getWorkers() {
		return workers;
	}


	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}


	public Map<Worker, ArrayList<Task>> getWorkerTasks() {
		return workerTasks;
	}


	public void setWorkerTasks(Map<Worker, ArrayList<Task>> workerTasks) {
		this.workerTasks = workerTasks;
	}
	
	
}
