/**
 * 
 */
package aas.model;

/**
 * Static objects have a certain outlines
 * @author schier
 *
 */
//TODO replace getWidth and getLength with Point[] getOutline();
public interface StaticObject {

	/**
	 * Getter
	 * @return the width in meters
	 */
	double getWidth();
	
	/**
	 * Getter
	 * @return the length in meters
	 */
	double getLength();
	
}
