/**
 * 
 */
package aas.controller.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import aas.model.Agent;
import aas.model.Moveable;
import aas.model.StaticObject;
import aas.model.communication.Message;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class GeoJsonExport implements Export {
	
	private static final Logger LOGGER = Logger.getLogger(GeoJsonExport.class.getName());


	private String fileName;
	private Double referenceLatitude = 53.63099;
	private Double referenceLongitude = 10.00564;
	private HashMap<String, Vector<Point>> lines = new HashMap<>();
	private HashMap<String, Point[]> objects = new HashMap<>();
	
	/**
	 * 
	 */
	public GeoJsonExport(String fileName) {
		this.fileName = fileName;
	}

	public void addPostion(String name, long time, Point point) {
		if(!this.lines.containsKey(name)) {
			this.lines.put(name, new Vector<>());
		}
		this.lines.get(name).add(point);
	}

	@Override
	public void finish() {
		try {
		FileWriter writer = new FileWriter(this.fileName);
		writer.write("{ \"type\": \"FeatureCollection\",\n"
                     +"\"features\": [\n");
		
		String objectStrings = new String();
		for(String agentName : objects.keySet()) {
			objectStrings = objectStrings + this.getPolygon(agentName) + ",\n";
		}
		objectStrings = objectStrings.substring(0, objectStrings.length() - 2);
		writer.write(objectStrings + ",");
		
		String lineStrings = new String();
		for(String agentName : lines.keySet()) {
			lineStrings = lineStrings + this.getLineString(agentName) + ",\n";
		}
		lineStrings = lineStrings.substring(0, lineStrings.length() - 2);
		writer.write(lineStrings);
		
		writer.write("]}");
		writer.flush();
		writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not open file", e);
		}
	}
	
	private String getLineString(String agentName) {
		String line = new String();
		
		line = line + "{ \"type\": \"Feature\",\n"
				+ "\"geometry\": {\n"
				+ "\"type\": \"LineString\",\n"
				+ "\"coordinates\": [";
		
		for(Point point : lines.get(agentName)) {
			double latitude = this.convertYToLatitude(point.getY());
			double longitude = this.convertXToLongitude(point.getX(), latitude);
			line = line + "[" + longitude + "," + latitude + "],";
		}
		line = line.substring(0, line.length() -1 );
		
		line = line + "]},\n"
				+ "\"properties\": {\"name\":\"" + agentName + "\"}}";
		return line;
	}
	
	private String getPolygon(String agentName) {
		String line = new String();
		
		line = line + "{ \"type\": \"Feature\",\n"
				+ "\"geometry\": {\n"
				+ "\"type\": \"Polygon\",\n"
				+ "\"coordinates\": [[";
		
		for(Point point : objects.get(agentName)) {
			double latitude = this.convertYToLatitude(point.getY());
			double longitude = this.convertXToLongitude(point.getX(), latitude);
			line = line + "[" + longitude + "," + latitude + "],";
		}
		line = line.substring(0, line.length() -1 );
		
		line = line + "]]},\n"
				+ "\"properties\": {\"name\":\"" + agentName + "\"}}";
		return line;
	}
	
	private Double convertYToLatitude(double y) {
		return this.referenceLatitude + (y / 111111);
	}
	
	private Double convertXToLongitude(double x, double latitude) {
		return this.referenceLongitude + (x / 111111 * Math.cos(latitude));
	}

	@Override
	public void notify(int time, Agent agent, Message[] messages) {
		if(agent instanceof Moveable) {
			this.addPostion(agent.getFootprint().getName(), time, agent.getFootprint().getPosition());
		}
		
		if(agent instanceof StaticObject) {
			StaticObject objectAgent = (StaticObject) agent;
			if(!this.objects.containsKey(agent.getFootprint().getName())) {
				Point center = agent.getFootprint().getPosition();
				double width = objectAgent.getWidth();
				double length = objectAgent.getLength();
				Point p1 = new Point(center.getX() - width/2, center.getY() - length/2);
				Point p2 = new Point(center.getX() + width/2, center.getY() - length/2);
				Point p3 = new Point(center.getX() + width/2, center.getY() + length/2);
				Point p4 = new Point(center.getX() - width/2, center.getY() + length/2);
				this.objects.put(agent.getFootprint().getName(), new Point[] {p1,p4,p3,p2,p1});
			}
		}
	}

}
