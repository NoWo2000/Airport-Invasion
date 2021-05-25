/**
 * 
 */
package aas.unit.model.util;

import static org.junit.Assert.fail;

import javax.sound.midi.MidiSystem;

import org.junit.Test;

import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class PointTest {

	/**
	 * Test method for {@link aas.model.util.Point#getDirection(aas.model.util.Point)}.
	 */
	@Test
	public void testGetDirection() {
		//1. Arrange
		Point point = new Point (0.0, 0.0);
		Point p0 = new Point (1.0, 0.0);
		Point p45 = new Point (1.0, 1.0);
		Point p90 = new Point (0.0, 1.0);
		Point p135 = new Point (-1.0, 1.0);
		Point p180 = new Point (-1.0, 0.0);
		Point p225 = new Point (-1.0, -1.0);
		Point p270 = new Point (0.0, -1.0);
		Point p315 = new Point (1.0, -1.0);
		
		//2.Act & Assert
		assert(Math.abs(point.getDirection(p0)) < 1.0);
		assert(Math.abs(point.getDirection(p45) - 45.0) < 1.0);
		assert(Math.abs(point.getDirection(p90) - 90.0) < 1.0);
		assert(Math.abs(point.getDirection(p135) - 135.0) < 1.0);
		assert(Math.abs(point.getDirection(p180) - 180.0) < 1.0);
		assert(Math.abs(point.getDirection(p225) - 225.0) < 1.0);
		assert(Math.abs(point.getDirection(p270) - 270.0) < 1.0);
		assert(Math.abs(point.getDirection(p315) - 315.0) < 1.0);
		
	}

	/**
	 * Test method for {@link aas.model.util.Point#getDistance(aas.model.util.Point)}.
	 */
	@Test
	public void testGetDistance() {
		//1. Arrange
		Point point = new Point (0.0, 0.0);
		Point p0 = new Point (0.0, 0.0); 
		Point p1 = new Point (1.0, 0.0);
		Point pMinus1 = new Point (-1.0, 0.0);
		Point p141 = new Point (1.0, 1.0);
		
		//2.Act & Assert
		assert(point.getDistance(p0) < 1.0);
		assert(point.getDistance(p1) - 1.0 < 1.0);
		assert(point.getDistance(pMinus1) - 1.0 < 1.0);
		assert(point.getDistance(p141) - 1.41 < 1.0);
	}

	/**
	 * Test method for {@link aas.model.util.Point#translate(double, double)}.
	 */
	@Test
	public void testTranslate() {
		//1. Arrange
		Point point = new Point (0.0, 0.0);
		double dx = 1.0;
		double dy = 1.0;
		
		//2. Act
		point.translate(dx, dy);
		
		//3. Assert
		assert(Math.abs(point.getX() - dx) < 1.0);
		assert(Math.abs(point.getY() - dy) < 1.0);
	}

	/**
	 * Test method for {@link aas.model.util.Point#moveTo(aas.model.util.Point, aas.model.util.Point, double)}.
	 */
	@Test
	public void testMoveTo() {
		//1. Arrange
		Point start = new Point (0.0, 0.0);
		Point target = new Point (1.0, 1.0);
		double speedMax = Double.MAX_VALUE;
		double speed1 = 1.0;
				
		//2. Act
		Point s1Point = start.moveTo(start, target, speed1);
		Point sMaxPoint = start.moveTo(start, target, speedMax);
				
		//3. Assert
		assert(s1Point.getDistance(target) < start.getDistance(target) - speed1);
		assert(sMaxPoint.equals(target));
	}

	
	@Test
	public void testValueOf() {
		//1. Arrange
		Point point1 = new Point(1.0, 2.0);
		
		//2. Act
		Point point2 = Point.valueOf(point1.toString());
		
		//3. Assert
		assert(point2.equals(point1));
		assert(Math.abs(point2.getX() - point1.getX()) < 1.0);
		assert(Math.abs(point2.getY() - point1.getY()) < 1.0);
	}
	
	
}
