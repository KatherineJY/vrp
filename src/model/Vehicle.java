package model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinyul
 *
 */

public class Vehicle {
	
	private List<RoutePoint> route;
	
	public int exceed_task_num = 0;
	
	public int exceed_task_time = 0;
	
	public int running_time = 0;
	
	public int waiting_time = 0;
	
	public int finished_time = 0;
	
	public int quantity;
	
	public Vehicle() {
		quantity = Properties.TRAILER_LIMIT;
		route = new ArrayList<RoutePoint>();
	};
	
	public Vehicle(FactoryMap map) {
		quantity = Properties.TRAILER_LIMIT;
		route = new ArrayList<RoutePoint>();
		RoutePoint first_point = new RoutePoint(map.getPark(),0, 0, 0, 0,0,null);
		//RoutePoint end_point = new RoutePoint(map.getPark(),0, 0, 0, 0,0,null);
		route.add(first_point);
		//route.add(end_point);
	}
	
	public Vehicle(List<RoutePoint> route, int unfinished_task, int exceed_time) {
		quantity = Properties.TRAILER_LIMIT;
		exceed_task_num = unfinished_task;
		exceed_task_time = exceed_time;
		this.route = route;
		finished_time = route.get(route.size()-1).end_time;
		int last = 0;
		for(RoutePoint rp:route) {
			running_time = rp.start_time - last;
			waiting_time += rp.waiting_time;
			last = rp.end_time;
		}
	}

	public Vehicle clone() {
		Vehicle new_veh= new Vehicle();
		for(RoutePoint rp:route)
			new_veh.route.add( new RoutePoint(rp.cur, rp.start_time, rp.waiting_time, rp.end_time, rp.quantity, 
					rp.accumulate_distance, rp.on_task) );
		new_veh.exceed_task_num = exceed_task_num;
		new_veh.exceed_task_time = exceed_task_time;
		new_veh.finished_time = finished_time;
		new_veh.running_time = running_time;
		new_veh.waiting_time = waiting_time;
		return new_veh;
		
	}

	public void setRoute(List<RoutePoint> route) {
		this.route = route;
		finished_time = route.get(route.size()-1).end_time;
		int last = 0;
		for(RoutePoint rp:route) {
			running_time = rp.start_time - last;
			waiting_time += rp.waiting_time;
			last = rp.end_time;
		}
	}

	public List<RoutePoint> getRoute() {
		return route;
	}

	public void routeAdd(RoutePoint route_point) {
		this.route.add(route_point);
		finished_time = route_point.end_time;
	}
}
