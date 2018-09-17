package model;

/**
 * @author jinyul
 * generate_time
 * start_time
 * end_time
 * from_point
 * to_point
 * quantity
 */
public class Task {
	
	public int generate_time, start_time, end_time;
	
	public Point from_point, to_point;
	
	public int quantity;

	public boolean is_segment;
	
	public Task() {}
	
	public Task(Point from_point, Point to_point) {
		this.from_point = from_point;
		this.to_point = to_point;
	}
	
	public Task(int generate_time, int start_time, int end_time, Point from_point, Point to_point, int quantity) {
		this.generate_time = generate_time;
		this.start_time = start_time;
		this.end_time = end_time;
		this.from_point = from_point;
		this.to_point = to_point;
		this.quantity = quantity;
		is_segment = false;
	}


	public Task(int start_time, int end_time, Point from_point, Point to_point, int quantity) {
		this.start_time = start_time;
		this.end_time = end_time;
		this.from_point = from_point;
		this.to_point = to_point;
		this.quantity = quantity;
		is_segment = false;
	}

	public Task(int start_time, int end_time, Point from_point, Point to_point, int quantity, boolean is_segment) {
		this.start_time = start_time;
		this.end_time = end_time;
		this.from_point = from_point;
		this.to_point = to_point;
		this.quantity = quantity;
		this.is_segment = is_segment;
	}

	@Override
	public String toString() {
		return "\tgenerate_time = "+ timeToString(generate_time) +"\tstart_time = " + timeToString(start_time) + 
				"\tend_time = " + timeToString(end_time) + "\tfrom_point = " + from_point.toString() +
				"\tto_point = " + to_point.toString() + "\tquantity = " + quantity + "\tis_segment = " + is_segment;
	}
	
	private String timeToString(int time) {
		int hour = time/60;
		int min = time - hour*60;
		return hour + "h," + min + "min";
	}
}
