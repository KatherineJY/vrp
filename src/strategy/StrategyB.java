package strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import model.Arrangment;
import model.FactoryMap;
import model.Point;
import model.Properties;
import model.RoutePoint;
import model.Task;
import model.Vehicle;

/**
 * simplify strategyA
 */

public class StrategyB {
	public static Arrangment arrangeVehicle(ArrayList<Task> tasks, FactoryMap map) {
		tasks.sort(new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				if (o1.end_time == o2.end_time)
					return 0;
				return o1.end_time > o2.end_time ? 1 : -1;
			}

		});
		//System.out.println("finished tasks sort");
		int t=0;
		List<Task> unfinished_tasks = new ArrayList<Task>();
		
		List<Vehicle> vehicles = new ArrayList<Vehicle>(Properties.VEHICLE_NUM);
		for (int i = 0; i < Properties.VEHICLE_NUM; i++)
			vehicles.add(new Vehicle(map));

		// initial arrange
		Iterator<Task> task_itr = tasks.iterator();
		Iterator<Vehicle> vehicle_itr = vehicles.iterator();
		while (task_itr.hasNext() && vehicle_itr.hasNext()) {
			Task tmp_task = task_itr.next();
			Vehicle tmp_vehicle = vehicle_itr.next();

			int end_time, driving_time = Point.getTime(map.getPark(), tmp_task.from_point, Properties.MAXX_SPEED);
			int accumulate_distance = Point.getDistance(map.getPark(), tmp_task.from_point);
			end_time = driving_time > tmp_task.start_time ? driving_time : tmp_task.start_time;
			RoutePoint routePoint = new RoutePoint(tmp_task.from_point, driving_time, end_time - driving_time, end_time,
					tmp_task.quantity, accumulate_distance, tmp_task);
			tmp_vehicle.routeAdd(routePoint);

			driving_time = Point.getTime(tmp_task.from_point, tmp_task.to_point, Properties.MAXX_SPEED);
			end_time += driving_time;
			accumulate_distance += Point.getDistance(tmp_task.from_point, tmp_task.to_point);
			routePoint = new RoutePoint(tmp_task.to_point, end_time, 0, end_time, 0, accumulate_distance, tmp_task);
			tmp_vehicle.routeAdd(routePoint);

			driving_time = Point.getTime(tmp_task.to_point, map.getPark(), Properties.MAXX_SPEED);
			end_time += driving_time;
			accumulate_distance += Point.getDistance(tmp_task.to_point, map.getPark());
			routePoint = new RoutePoint(map.getPark(), end_time, 0, end_time, 0, accumulate_distance, null);
			tmp_vehicle.routeAdd(routePoint);
			tmp_vehicle.finished_time = routePoint.end_time;
		//	System.out.println("finished arrange one task"+(++t));
		}

		while (task_itr.hasNext()) {
			
			Arrangment best_feasible_arrangment = new Arrangment();

			Task task = task_itr.next();
			for (int vehicle_idx = 0; vehicle_idx < Properties.VEHICLE_NUM; vehicle_idx++) {
				Vehicle vehicle = vehicles.get(vehicle_idx);
				//int route_num = vehicle.route.size();
				List<RoutePoint> new_route = new ArrayList<RoutePoint>();
				List<RoutePoint> pre_route = vehicle.getRoute();
				new_route = routeAppend(pre_route,task);
				//new_route = routeInsert(vehicle.route, route_num-1, route_num-1, task);
				if (new_route != null) {
					if( new_route.get(new_route.size()-1).end_time>Properties.WORK_END_TIME ) {
						t=1;
					}
					vehicle.setRoute(new_route);
					Arrangment new_arrangment = new Arrangment(vehicles);
					if (best_feasible_arrangment.vehicles.size() == 0
							|| best_feasible_arrangment.distance_sum > new_arrangment.distance_sum)
						best_feasible_arrangment = new_arrangment;
					vehicle.setRoute(pre_route);
				}

			}

			if (best_feasible_arrangment.vehicles.size() == 0) {
				unfinished_tasks.add(task);
			}
			else
				vehicles = best_feasible_arrangment.vehicles;
			//System.out.println("finished arrange one task"+(++t));
		}
		/*
		System.out.println("begin deal with unfinished task");
		for(Task task:unfinished_tasks) {
			Arrangment best_feasible_arrangment = new Arrangment();
			for (int vehicle_idx = 0; vehicle_idx < Properties.VEHICLE_NUM; vehicle_idx++) {
				Vehicle vehicle = vehicles.get(vehicle_idx);
				int route_num = vehicle.route.size();
				for (int i = 1; i < route_num; i++)
					for (int j = i; j < route_num; j++) {
						Vehicle new_vehicle_route = new Vehicle();
						new_vehicle_route = routeInsert(vehicle.route, i, j, task);
						if (new_vehicle_route != null) {
							Vehicle pre_vehicle= vehicle;
							vehicle = new_vehicle_route;
							Arrangment new_arrangment = new Arrangment(vehicles);
							if( best_feasible_arrangment.vehicles.size()==0 || best_feasible_arrangment.exceed_time_sum>new_arrangment.exceed_time_sum ||
									( best_feasible_arrangment.exceed_time_sum==new_arrangment.exceed_time_sum && best_feasible_arrangment.distance_sum > new_arrangment.distance_sum ))
								best_feasible_arrangment = new_arrangment;
							vehicle = pre_vehicle;
						}
					}
			}
			//System.out.println("finished arrange one task"+(++t));
		}*/
		System.out.println();
		Arrangment arrangment = new Arrangment(vehicles);
		arrangment.exceed_time_tasks = unfinished_tasks.size();
		return arrangment;
	}

	private static Vehicle routeInsert(final List<RoutePoint> route, int pickup, int delivery, Task task) {
		List<RoutePoint> new_route = new ArrayList<RoutePoint>();
		int route_num = route.size();
		int unfinished_task = 0;
		int exceed_time = 0;
		
		for (int i = 0; i < pickup; i++) {
			RoutePoint cur = route.get(i);
			new_route.add(cur);
			if( cur.on_task !=null && cur.on_task.to_point==cur.cur && cur.on_task.end_time < cur.end_time ) {
				unfinished_task++;
				exceed_time += cur.end_time - cur.on_task.end_time;
			}
		}
			
		RoutePoint pre = route.get(pickup - 1);

		if (pre.quantity + task.quantity > Properties.TRAILER_LIMIT)
			return null;

		int driving_time = Point.getTime(pre.cur, task.from_point, Properties.MAXX_SPEED);
		int start_time = pre.end_time + driving_time;
		int accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, task.from_point);
		int end_time = Math.max(start_time, task.start_time);
		if( end_time>Properties.WORK_END_TIME )
			return null;
		pre = new RoutePoint(task.from_point, start_time, end_time - start_time, end_time, pre.quantity + task.quantity,
				accumulate_distance, task);
		new_route.add(pre);

		for (int i = pickup; i < delivery; i++) {
			RoutePoint cur = route.get(i);
			start_time = pre.end_time + Point.getTime(pre.cur, cur.cur, Properties.MAXX_SPEED);
			end_time = start_time;
			int to_pickup = 0;

			if (cur.on_task.from_point.equals(cur.cur)) {
				to_pickup = cur.on_task.quantity;
				end_time = Math.max(end_time, cur.on_task.start_time);
			} else if (cur.on_task.to_point.equals(cur.cur)) {
				to_pickup = -cur.on_task.quantity;
				if (start_time > cur.on_task.end_time) {
					unfinished_task++;
					exceed_time += cur.on_task.end_time-start_time;
				}
			}

			if (pre.quantity + to_pickup > Properties.TRAILER_LIMIT)
				return null;
			if (end_time > Properties.WORK_END_TIME)
				return null;
			accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, cur.cur);
			RoutePoint new_cur = new RoutePoint(cur.cur, start_time, end_time - start_time, end_time,
					pre.quantity + to_pickup, accumulate_distance, cur.on_task);
			new_route.add(new_cur);
			pre = new_cur;
		}

		driving_time = Point.getTime(pre.cur, task.to_point, Properties.MAXX_SPEED);
		start_time = pre.end_time + driving_time;
		if (start_time > task.end_time) {
			unfinished_task++;
			exceed_time += task.end_time-start_time;
		}
		if (start_time > Properties.WORK_END_TIME)
			return null;
		accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, task.to_point);
		pre = new RoutePoint(task.to_point, start_time, 0, start_time, pre.quantity - task.quantity,
				accumulate_distance, task);
		new_route.add(pre);

		for (int i = delivery; i < route_num; i++) {
			RoutePoint cur = route.get(i);
			start_time = pre.end_time + Point.getTime(pre.cur, cur.cur, Properties.MAXX_SPEED);
			end_time = start_time;
			int to_pickup = 0;
			if (cur.on_task != null && cur.on_task.from_point.equals(cur.cur)) {
				to_pickup = cur.on_task.quantity;
				end_time = Math.max(end_time, cur.on_task.start_time);
			} else if (cur.on_task != null && cur.on_task.to_point.equals(cur.cur)) {
				to_pickup = -cur.on_task.quantity;
				if (start_time > cur.on_task.end_time) {
					exceed_time += start_time-cur.on_task.end_time;
					unfinished_task++;
				}
			}
			if (pre.quantity + to_pickup > Properties.TRAILER_LIMIT)
				return null;
			if( end_time > Properties.WORK_END_TIME )
				return null;
			accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, cur.cur);
			RoutePoint new_cur = new RoutePoint(cur.cur, start_time, end_time - start_time, end_time,
					pre.quantity + to_pickup, accumulate_distance, cur.on_task);
			new_route.add(new_cur);
			pre = new_cur;
		}

		return new Vehicle(new_route,unfinished_task,exceed_time);
	}
	
	private static List<RoutePoint> routeAppend(final List<RoutePoint> route, Task task) {
		List<RoutePoint> new_route = new ArrayList<RoutePoint>();
		int route_num = route.size();
		
		for (int i = 0; i < route_num-1; i++)
			new_route.add(route.get(i));
		RoutePoint pre = route.get(route_num - 2);
		
		if (pre.quantity + task.quantity > Properties.TRAILER_LIMIT)
			return null;

		int driving_time = Point.getTime(pre.cur, task.from_point, Properties.MAXX_SPEED);
		int start_time = pre.end_time + driving_time;
		int accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, task.from_point);
		int end_time = Math.max(start_time, task.start_time);
		pre = new RoutePoint(task.from_point, start_time, end_time - start_time, end_time, pre.quantity + task.quantity,
				accumulate_distance, task);
		new_route.add(pre);

		driving_time = Point.getTime(pre.cur, task.to_point, Properties.MAXX_SPEED);
		start_time = pre.end_time + driving_time;
		if (start_time > task.end_time)
			return null;
		if (start_time > Properties.WORK_END_TIME)
			return null;
		accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, task.to_point);
		pre = new RoutePoint(task.to_point, start_time, 0, start_time, pre.quantity - task.quantity,
				accumulate_distance, task);
		new_route.add(pre);
		
		RoutePoint end = route.get(route_num-1);
		end.accumulate_distance = pre.accumulate_distance + Point.getDistance(end.cur, pre.cur);
		end.start_time = end.end_time = pre.end_time + Point.getTime(end.cur,pre.cur, Properties.MAXX_SPEED);
		new_route.add(end);
		
		return new_route;
	}

	public static Arrangment dynamicArrange(Arrangment arrangment, Task task) {
		Arrangment best_feasible_arrangment = new Arrangment();
		for (int vehicle_idx = 0; vehicle_idx < Properties.VEHICLE_NUM; vehicle_idx++) {

			Vehicle vehicle = arrangment.vehicles.get(vehicle_idx);
			List<RoutePoint> route = vehicle.getRoute();
			int route_num = route.size();
			
			int after_idx = findNext(vehicle,task);
			for (int i = after_idx; i < route_num; i++) {
				if( route.get(i-1).quantity+task.quantity > Properties.TRAILER_LIMIT )
					continue;
				for (int j = i; j < route_num; j++) {
					Vehicle new_vehicle_route = new Vehicle();
					new_vehicle_route = routeInsert(route, i, j, task);
					if (new_vehicle_route != null) {
						arrangment.vehicles.set(vehicle_idx,new_vehicle_route);
						Arrangment new_arrangment = new Arrangment(arrangment.vehicles);
						if( best_feasible_arrangment.vehicles.size()==0 || best_feasible_arrangment.exceed_time_sum>new_arrangment.exceed_time_sum ||
								( best_feasible_arrangment.exceed_time_sum==new_arrangment.exceed_time_sum && best_feasible_arrangment.distance_sum > new_arrangment.distance_sum ))
							best_feasible_arrangment = new_arrangment;
						arrangment.vehicles.set(vehicle_idx,vehicle);
					}
				}
			}
		}
		return best_feasible_arrangment;
	}

	private static int findNext(Vehicle vehicle, Task task) {
		List<RoutePoint> route = vehicle.getRoute();
		int num = route.size(),i;
		for( i=1;i<num;i++ )
			if( route.get(i).end_time>task.start_time )
				break;
		if(i>=num) i = num-1;
		return i;
	}
}
