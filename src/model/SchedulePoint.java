package model;

/**
 * @author jinyul
 * describe the status of a vehicle in a piece of time
 * start_time
 * end_time
 * status
 * from_point
 * to_point                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
 * task 
 * vehicle
 */

public class SchedulePoint {
	
	//if end_time is below zero, it means the vehicle stopped
	public int start_time, end_time;
	
	public Status status;
	
	//if from_point==end_point, it means the vehicle is waiting for loading or unloading or stopped
	public Point from_point, to_point;
	
	public int accumulate_distance = 0, remain_capacity;
	
	public Task task;
	
	public SchedulePoint() {}
	
	public SchedulePoint(int start_time, int end_time, Status status, Point from_point,Point to_point, 
			int remain_capacity, Task task, int accumulate_distance) {
		this.start_time = start_time;
		this.end_time = end_time;
		this.status = status;
		this.from_point = from_point;
		this.to_point = to_point;
		this.task = task;
		this.remain_capacity = remain_capacity;
		this.accumulate_distance = accumulate_distance;
	}
	
}
