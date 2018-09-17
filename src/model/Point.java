package model;

public class Point {
	public int x;
	public int y;
	public PointType point_type;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;	
	}
	
	public Point(int x, int y, PointType point_type) {
		this.x = x;
		this.y = y;
		this.point_type = point_type;
	}

	public static int getDistance( Point p1, Point p2 ) {
		return (int) Math.ceil(Math.sqrt( (p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y) ));
	}
	
	public static int getTime( Point p1, Point p2, int speed ) {
		return (int) Math.ceil(getDistance(p1, p2)/speed);
	}
	
	static Point getPoint( Point from_point, Point to_point, int speed, int time ) {
		double driving_distance = speed * time;
		double full_distance = getDistance(from_point, to_point);
		int x,y;
		x = (int) (from_point.x + driving_distance/full_distance*(to_point.x-from_point.x));
		y = (int) (from_point.y + driving_distance/full_distance*(to_point.y-from_point.y));
		return new Point(x,y);
	}
	
	@Override
	public String toString() {
		return "("+x+","+ y+")";
	}

	public boolean equals(int x, int y) {
		if( this.x==x && this.y==y )
			return true;
		return false;
	}
	
	public boolean equals(Point p){
		if( this.x==p.x && this.y==p.y )
			return true;
		return false;
	}
}
