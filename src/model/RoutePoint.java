package model;

/**
 * @author jinyul
 * current 
 * schedulePoint
 */

public class RoutePoint {
	public Point cur;
	
	public int start_time,waiting_time,end_time;
	
	public int accumulate_distance = 0,quantity = 0;
	
	public Task on_task;
	
	public RoutePoint() {}

	public RoutePoint(Point cur,int start_time,int waiting_time,int end_time,int quantity, int accumulate_distance,Task task) {
		this.cur = cur;
		this.start_time = start_time;
		this.end_time = end_time;
		this.waiting_time = waiting_time;
		this.quantity = quantity;
		this.accumulate_distance = accumulate_distance;
		this.on_task = task;
	}
	
	public String toString() {
		String on_task_str = null;
		if( on_task!=null ) 
			on_task_str = on_task.toString();
		return "curPoint:"+cur.toString()+"\tstartTime:"+timeToString(start_time)+"\tendTime:"+timeToString(end_time)+"\twaitingTime:"+timeToString(waiting_time)+
				"\tquantity:"+quantity+"\ttask:"+on_task_str;
	}
	
	private String timeToString(int time) {
		int hour =  time/60;
		int min = time - hour*60;
		return hour + "h," + min + "min";
	}
}
