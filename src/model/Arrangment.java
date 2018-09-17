package model;

import java.util.ArrayList;
import java.util.List;

public class Arrangment {
	
	public List<Vehicle> vehicles = new ArrayList<Vehicle>();
	
	public int exceed_time_tasks = 0;
	
	public int exceed_time_sum = 0;
	
	public int distance_sum = 0;
	
	public Arrangment() {}
	
	public Arrangment(List<Vehicle> vehicles,int num,int sum) {
		this.vehicles = vehicles;
		this.exceed_time_tasks = num;
		this.distance_sum = sum;
	}

	public Arrangment(List<Vehicle> vehicles) {
		for( Vehicle vehicle:vehicles ) {
			this.vehicles.add(vehicle.clone());
			List<RoutePoint> route = vehicle.getRoute();
			distance_sum += route.get(route.size()-1).accumulate_distance;
			exceed_time_tasks += vehicle.exceed_task_num;
			exceed_time_sum += vehicle.exceed_task_time;
		}
	}

	public static void print(Arrangment arrangment) {
		System.out.println();
		System.out.println("====================================================");
		System.out.println("========== the information of ArrangMent ==========");
		System.out.println("====================================================");
		System.out.println();
		System.out.println("num of unfinished tasks =\t" + arrangment.exceed_time_tasks);
		System.out.println();
		System.out.println("sum of distance =\t" + arrangment.distance_sum);
		System.out.println();
		int idx = 0;
		for (Vehicle vehicle:arrangment.vehicles) {
			System.out.println("Vehicle "+idx);
			List<RoutePoint> route = vehicle.getRoute();
			for( RoutePoint routePoint:route )
				System.out.println("\t"+routePoint.toString());
			idx++;
		}
		System.out.println();
		System.out.println("====================================================");
		System.out.println("========== the information of ArrangMent ==========");
		System.out.println("====================================================");
		System.out.println();
		
	}
}
