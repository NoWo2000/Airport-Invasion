/**
 * 
 */
package aas.model.util;

import java.util.Arrays;

/**
 * @author schier
 *
 */
public class Point {
	
	private double x = 0.0;
	private double y = 0.0;
	
	public Point() {
		super();
	}
	
	
	/**
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getDirection(Point target) {
		double radian = Math.atan2(target.getY() - this.getY(), target.getX() - this.getX());
		if(radian < 0) {
			radian = radian + 2 * Math.PI;
		}
		
		return Math.toDegrees(radian);
	}
	
	public double getDistance(Point target) {
		if(target == null) {
			throw new NullPointerException("target must not be null");
		}
		
		double x2 = Math.pow(target.getX() - this.getX(), 2.0);
		double y2 = Math.pow(target.getY() - this.getY(), 2.0);
		return Math.sqrt(x2+y2);
	}
	
	public void translate(double dx, double dy) {
		this.setX(x + dx);
		this.setY(y + dy);
	}
	
	public Point moveTo(Point start, Point target, double maximumSpeed) {
		double distance = start.getDistance(target);
		double speed = Math.min(distance, maximumSpeed);
		double direction = Math.toRadians(start.getDirection(target));
		double dy = speed * Math.cos(direction);
		double dx = speed * Math.sin(direction);
		Point newPosition = new Point(start.getX(), start.getY());
		newPosition.translate(dx,dy);
		return newPosition;
	}
	
	


	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		
		if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
		
		Point point = (Point) object;
		return (Math.abs(point.getX() - this.getX()) < Double.MIN_NORMAL) 
				&& (Math.abs(point.getX() - this.getX()) < Double.MIN_NORMAL);
		
	}


	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	public static Point valueOf(final String pointString) {
		String stringValue = pointString.replace("Point [x=", "");
		stringValue = stringValue.replace(" y=", "");
		stringValue = stringValue.replace("]", "");
		String[] tokens = stringValue.split(",");
		if(Math.abs(tokens.length - 2) > 0) {
			return null;
		}
		
		Double x = Double.valueOf(tokens[0]);
		Double y = Double.valueOf(tokens[1]);
		return new Point(x, y);
	}

}
