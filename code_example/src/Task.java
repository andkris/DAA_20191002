import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Task class
 *
 */
public class Task  implements Comparable<Task>{ 

		
	private String id;
	
	/** flight number */
	private String dateFromStr;

	/** task duration*/
	private int duration;
	
	
	// additional information creating together with the object, while 

	
	private int taskStartTime;
	
	private int taskEndTime;
	
	// getters and setters ------------------
	
	private int taskStartHourInDay;
	
	private Date dateFrom;
	
	
	
	/** Constructor creating the task Object */
	Task(String id, String dateFlightFromStr, int duration) throws ParseException{
		// assign mane data to object
		this.id = id;
		
		this.dateFromStr = dateFlightFromStr;
		
		this.duration = duration;
	
		
		// format additional task object, witch later will be used for schedule construction
		
		DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date dateFrom = formatter.parse(dateFromStr);
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(dateFrom);   // assigns calendar to given date 
		calendar.add(Calendar.MINUTE, -1*this.duration); 
		this.taskStartHourInDay = calendar.get(Calendar.HOUR_OF_DAY);
		this.dateFrom = calendar.getTime();
		
		long diff = dateFrom.getTime() - Config.getDateFrom().getTime(); 
		this.taskStartTime =  (int) (TimeUnit.MILLISECONDS.toMinutes(diff));
		this.taskEndTime =  (int) (TimeUnit.MILLISECONDS.toMinutes(diff)) + this.duration ; 

	}
	
	
	
	public int compareTo(Task other){
		 return this.id.compareTo(other.id);
	}
	
	public static class Comparators {
        public static Comparator<Task> STARTTIME = new Comparator<Task>() {
            public int compare(Task o1, Task o2) {
                return o1.getTaskStartTime()  - o2.getTaskStartTime();
            }
        };
    }

	@Override
	public String toString() {
		return "Task [id=" + id + ", dateFromStr=" + dateFromStr + ", duration=" + duration + ", taskStartTime="
				+ taskStartTime + ", taskEndTime=" + taskEndTime + ", workingPeriodFrom=" 
				+ ", taskStartHourInDay=" + taskStartHourInDay + ", dateFrom=" + dateFrom + "]";
	}
	
	/** method used to sort tasks by ID*/

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDateFromStr() {
		return dateFromStr;
	}

	public void setDateFromStr(String dateFromStr) {
		this.dateFromStr = dateFromStr;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getTaskStartTime() {
		return taskStartTime;
	}

	public void setTaskStartTime(int taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	
	public int getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(int taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	public int getTaskStartHourInDay() {
		return taskStartHourInDay;
	}

	public void setTaskStartHourInDay(int taskStartHourInDay) {
		this.taskStartHourInDay = taskStartHourInDay;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

}