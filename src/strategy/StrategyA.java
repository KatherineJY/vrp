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

public class StrategyA {

	public static Arrangment arrangeVehicle(ArrayList<Task> tasks, FactoryMap map) {
		//Test 2 
		//remove sort
		tasks.sort(new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				if (o1.end_time == o2.end_time)
					return 0;
				return o1.end_time > o2.end_time ? 1 : -1;
			}

		});

//		System.out.println("after sort:");
//		for( Task task:tasks )
//			System.out.println(task.toString());

//		System.out.println("after sort:");
//		for( int i=0;i<tasks.size();i++ )
//			System.out.println(tasks.get(i).toString());

//		System.out.println("after sort:");
//		for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();)
//			System.out.println(iterator.next().toString());

		int unfinished_task_num = 0;
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
			tmp_vehicle.route.add(routePoint);

			driving_time = Point.getTime(tmp_task.from_point, tmp_task.to_point, Properties.MAXX_SPEED);
			end_time += driving_time;
			accumulate_distance += Point.getDistance(tmp_task.from_point, tmp_task.to_point);
			routePoint = new RoutePoint(tmp_task.to_point, end_time, 0, end_time, 0, accumulate_distance, tmp_task);
			tmp_vehicle.route.add(routePoint);

			driving_time = Point.getTime(tmp_task.to_point, map.getPark(), Properties.MAXX_SPEED);
			end_time += driving_time;
			accumulate_distance += Point.getDistance(tmp_task.to_point, map.getPark());
			routePoint = new RoutePoint(map.getPark(), end_time, 0, end_time, 0, accumulate_distance, null);
			tmp_vehicle.route.add(routePoint);

		}

//		for (Iterator<Vehicle> iterator = vehicles.iterator(); iterator.hasNext();) {
//			Vehicle veh = iterator.next();
//			System.out.println("vehicle " + "\tsize=" + veh.route.size());
//			for (RoutePoint routePoint : veh.route)
//				System.out.println("\t" + routePoint.toString());
//		}

//		for(int i=0;i<tmp;i++) {
//			System.out.println("vehicle "+i+"\tsize="+vehicles.get(i).route.size());
//			for(RoutePoint routePoint:vehicles.get(i).route)
//				System.out.println("\t"+routePoint.toString());
//		}

		while (task_itr.hasNext()) {
			Arrangment best_feasible_arrangment = new Arrangment();

			Task task = task_itr.next();
			for (int vehicle_idx = 0; vehicle_idx < Properties.VEHICLE_NUM; vehicle_idx++) {
				Vehicle vehicle = vehicles.get(vehicle_idx);
				int route_num = vehicle.route.size();
//				System.out.println(route_num);
				for (int i = 1; i < route_num; i++)
					for (int j = i; j < route_num; j++) {
						List<RoutePoint> new_route = new ArrayList<RoutePoint>();
						new_route = routeInsert(vehicle.route, i, j, task);
						if (new_route != null) {
							List<RoutePoint> pre_route = vehicle.route;
							vehicle.route = new_route;
							Arrangment new_arrangment = new Arrangment(vehicles);
							if( best_feasible_arrangment.vehicles.size()==0 || best_feasible_arrangment.distance_sum>new_arrangment.distance_sum )
								best_feasible_arrangment = new_arrangment;
							vehicle.route = pre_route;
						}
					}
			}

			if ( best_feasible_arrangment.vehicles.size()==0)
				unfinished_task_num += 1;
			else vehicles = best_feasible_arrangment.vehicles;
		}
		System.out.println();
		Arrangment arrangment = new Arrangment(vehicles);
		arrangment.exceed_time_tasks = unfinished_task_num;
		return arrangment;

	}

	private static List<RoutePoint> routeInsert(final List<RoutePoint> route, int pickup, int delivery, Task task) {
		List<RoutePoint> new_route = new ArrayList<RoutePoint>();
		int route_num = route.size();

		for (int i = 0; i < pickup; i++)
			new_route.add(route.get(i));
		RoutePoint pre = route.get(pickup - 1);

		if (pre.quantity + task.quantity > Properties.TRAILER_LIMIT)
			return null;

		int driving_time = Point.getTime(pre.cur, task.from_point, Properties.MAXX_SPEED);
		int start_time = pre.end_time + driving_time;
		int accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, task.from_point);
		int end_time = Math.max(start_time, task.start_time);
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
				if (start_time > cur.on_task.end_time)
					return null;
			}

			if (pre.quantity + to_pickup > Properties.TRAILER_LIMIT)
				return null;
			accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, cur.cur);
			RoutePoint new_cur = new RoutePoint(cur.cur, start_time, end_time - start_time, end_time,
					pre.quantity + to_pickup, accumulate_distance, cur.on_task);
			new_route.add(new_cur);
			pre = new_cur;
		}

		driving_time = Point.getTime(pre.cur, task.to_point, Properties.MAXX_SPEED);
		start_time = pre.end_time + driving_time;
		if (start_time > task.end_time)
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
			if (cur.on_task!=null && cur.on_task.from_point.equals(cur.cur)) {
				to_pickup = cur.on_task.quantity;
				end_time = Math.max(end_time, cur.on_task.start_time);
			} else if (cur.on_task!=null && cur.on_task.to_point.equals(cur.cur)) {
				to_pickup = -cur.on_task.quantity;
				if (start_time > cur.on_task.end_time)
					return null;
			}
			if (pre.quantity + to_pickup > Properties.TRAILER_LIMIT)
				return null;
			accumulate_distance = pre.accumulate_distance + Point.getDistance(pre.cur, cur.cur);
			RoutePoint new_cur = new RoutePoint(cur.cur, start_time, end_time - start_time, end_time,
					pre.quantity + to_pickup, accumulate_distance, cur.on_task);
			new_route.add(new_cur);
			pre = new_cur;
		}

		return new_route;
	}

}
