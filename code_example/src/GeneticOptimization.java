import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * 
 * Genetic optimization class
 *
 */
public class GeneticOptimization {

	/* GA parameters */

	private static final double mutationRate = 1.5;

	private static final int tournamentSize = 3; 

	private static final int elitism = 5; 

	/** one GA iteration */
	public static Population evolvePopulation(Population pop) {

		// creating population
		Population newPopulation = new Population(pop.size());
		
		// saving elitism
		for (int i = 0; i < elitism; i++) {
			newPopulation.saveShedule(i, pop.getFittest(i));
		}
		
		// obtaining new individuals
		for (int j = elitism; j < 10; j++) {
			Schedule indiv1 = tournamentSelection(pop);
			Schedule indiv2 = tournamentSelection(pop);
			Schedule newIndiv = crossoverAndMutate(indiv1, indiv2);
			newIndiv.createScheduleAfterMutation();
			newPopulation.saveShedule(j, newIndiv);
		}

		// sorting by target function
		Collections.sort(newPopulation.getShedules(), Schedule.Comparators.TARGET);
		return newPopulation;
	}

	/**
	 */
	private static Schedule crossoverAndMutate(Schedule indiv1, Schedule indiv2) {

		List<Task> tasks = indiv1.getTasks();
		int numberOfWorkers = indiv1.getWorkers().size();
		
		Map<Worker, ArrayList<Task>> workerTasks = new TreeMap<Worker, ArrayList<Task>>();
		for (Worker worker : indiv1.getWorkers()) {
			workerTasks.put(worker, new ArrayList<Task>());
		}
		// create crossover between indices (p1 and p2)
		int minVal = 20;
		int p1 = (int) ((indiv1.getTasks().size() - minVal) * Math.random());
		int p2 = (int) (p1 + (int) ((indiv1.getTasks().size() - p1 - minVal) * Math.random()));

		Map<Task, Worker> indiv1TasksWorkers = new TreeMap<Task, Worker>();
		for (Worker worker : indiv1.getWorkers()) {
			for (Task task : indiv1.getWorkerTasks().get(worker)) {
				indiv1TasksWorkers.put(task, worker);
			}
		}
		Map<Task, Worker> indiv2TasksWorkers = new TreeMap<Task, Worker>();
		for (Worker worker : indiv2.getWorkers()) {
			for (Task task : indiv2.getWorkerTasks().get(worker)) {
				indiv2TasksWorkers.put(task, worker);
			}
		}
		// crossover
		for (int i = 0; i < tasks.size(); i++) {
			if (i < p1 || i > p2) {
				Worker worker = indiv1TasksWorkers.get(tasks.get(i));
				if (worker != null) {
					workerTasks.get(worker).add(tasks.get(i));
				} else { // add to random worker
					int workerIndex = (int) (Math.random() * numberOfWorkers);
					worker = indiv1.getWorkers().get(workerIndex);
					workerTasks.get(worker).add(tasks.get(i));
				}
			} else {
				Worker worker = indiv2TasksWorkers.get(tasks.get(i));
				if (worker != null) {
					workerTasks.get(worker).add(tasks.get(i));
				} else {// add to random worker
					int workerIndex = (int) (Math.random() * numberOfWorkers);
					worker = indiv1.getWorkers().get(workerIndex);
					workerTasks.get(worker).add(tasks.get(i));
				}
			}

		}
		// mutation
		if (Math.random() < mutationRate) {
			for (int i = 0; i < 5; i++) {
				int taskIndex = (int) (Math.random() * indiv1.getTasks().size());
				Worker worker = null;
				for (Entry<Worker, ArrayList<Task>> entry : workerTasks.entrySet()) {
					if (entry.getValue().contains(tasks.get(taskIndex))) {
						worker = entry.getKey();
						break;
					}
				}
				if (worker != null) { //if the task was assigned
					workerTasks.get(worker).remove(tasks.get(taskIndex));
				}
				int workerIndex = (int) (Math.random() * indiv1.getWorkers().size());
				worker = indiv1.getWorkers().get(workerIndex);
				workerTasks.get(worker).add(tasks.get(taskIndex));
			}
		}

		Schedule newSol = new Schedule(indiv1.getTasks(), indiv1.getWorkers(), workerTasks);
		
		return newSol;
	}

	/**
	 * tournament selection
	 */
	private static Schedule tournamentSelection(Population pop) {

		// make tournament population
		Population tournament = new Population(tournamentSize);

		// choose random solution
		for (int i = 0; i < tournamentSize; i++) {
			int randomId = (int) (Math.random() * pop.size());
			tournament.saveShedule(i, pop.getShedule(randomId));
		}

		// get fittest solution from tournament
		Schedule fittest = tournament.getFittest();
		return fittest;
	}

}
