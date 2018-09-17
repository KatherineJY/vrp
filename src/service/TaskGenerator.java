package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import model.FactoryMap;
import model.PointType;
import model.Properties;
import model.Task;

public class TaskGenerator {

	static private int task_cycle[] = { 30, 45, 60, 90, 120, 150, 240 };
	
	static public ArrayList<Task> generateStaticTasks(FactoryMap map) {
		Random rand = new Random();
		ArrayList<Task> tasks = new ArrayList<Task>();
		for (Task task : map.all_trans_tasks) {
			int task_cycle_index = rand.nextInt(task_cycle.length);
			int cycle = task_cycle[task_cycle_index];
			int quantity = 0, start_time = 0;
			if (task.from_point.point_type == PointType.ASSMBLY_END) {
				quantity = 1;
				start_time = cycle;
			} else
				quantity = rand.nextInt(10) + 1;

			for (int j = Properties.WORK_START_TIME + start_time; j + cycle
					- 1 < Properties.WORK_END_TIME; j += cycle) {
				if (task.to_point.point_type == PointType.PRODUCT_DEPOT)
					tasks.add(new Task(j, Properties.WORK_END_TIME, task.from_point, task.to_point, quantity));
				else
					tasks.add(new Task(j, j + cycle - 1, task.from_point, task.to_point, quantity));
			}
		}

		return tasks;
	}

	static public ArrayList<Task> spiltTask(ArrayList<Task> tasks) {
		ArrayList<Task> afterSpiltTasks = new ArrayList<Task>();
		for (Task task : tasks)
			if (task.quantity > Properties.TRAILER_LIMIT) {
				Task tmp = task;
				int current_type_num = (int) (tmp.quantity / Properties.TRAILER_LIMIT);
				for (int i = 1; i <= current_type_num; i++)
					afterSpiltTasks.add(new Task(task.start_time, task.end_time, tmp.from_point, tmp.to_point,
							Properties.TRAILER_LIMIT, true));
				tmp.quantity -= Properties.TRAILER_LIMIT * current_type_num;
				if (tmp.quantity != 0.0)
					afterSpiltTasks.add(
							new Task(task.start_time, task.end_time, tmp.from_point, tmp.to_point, tmp.quantity, true));
			} else
				afterSpiltTasks.add(task);
		return afterSpiltTasks;
	}

	static public ArrayList<Task> spiltTask(Task task) {
		ArrayList<Task> afterSpiltTasks = new ArrayList<Task>();

		if (task.quantity > Properties.TRAILER_LIMIT) {
			Task tmp = task;
			int current_type_num = (int) (tmp.quantity / Properties.TRAILER_LIMIT);
			for (int i = 1; i <= current_type_num; i++)
				afterSpiltTasks.add(new Task(task.start_time, task.end_time, tmp.from_point, tmp.to_point,
						Properties.TRAILER_LIMIT, true));
			tmp.quantity -= Properties.TRAILER_LIMIT * current_type_num;
			if (tmp.quantity != 0.0)
				afterSpiltTasks.add(
						new Task(task.start_time, task.end_time, tmp.from_point, tmp.to_point, tmp.quantity, true));
		} else
			afterSpiltTasks.add(task);
		return afterSpiltTasks;
	}

	public static ArrayList<Task> generateDynamicTasks(FactoryMap map) {
		Random rand = new Random();
		int dynamic_tasks = rand.nextInt(23) + 1;
		ArrayList<Task> tasks = new ArrayList<Task>();
		while (dynamic_tasks > 0) {
			dynamic_tasks--;

			int trans_tasks_num = map.all_trans_tasks.size();
			Task trans_task = map.all_trans_tasks.get(rand.nextInt(trans_tasks_num));
			int time = Properties.WORK_END_TIME, task_cycle_index, cycle = Properties.WORK_END_TIME;
			while (time + cycle >= Properties.WORK_END_TIME) {
				time = rand.nextInt(23 * 60) + Properties.WORK_START_TIME;
				task_cycle_index = rand.nextInt(task_cycle.length);
				cycle = task_cycle[task_cycle_index];
			}
			int quantity = rand.nextInt(10) + 1;

			Task dynamic_task = new Task(time, time + cycle, trans_task.from_point, trans_task.to_point, quantity);
			tasks.add(dynamic_task);
		}
		tasks.sort(new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.start_time == o2.start_time)
					return 0;
				return o1.start_time > o2.start_time ? 1 : -1;
			}
		});

		return tasks;
	}

	public static void print_static_task(ArrayList<Task> static_tasks) {
		System.out.println();
		System.out.println("=====================================================");
		System.out.println("========== the information of Static Tasks ==========");
		System.out.println("=====================================================");
		System.out.println();
		System.out.println("NUM = " + static_tasks.size());
		System.out.println();
		for (Task task : static_tasks)
			System.out.println(task.toString());
		System.out.println();
		System.out.println("=====================================================");
		System.out.println("========== the information of Static Tasks ==========");
		System.out.println("=====================================================");
		System.out.println();
	}

}
