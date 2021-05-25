/**
 * 
 */
package aas.controller;

import java.util.HashMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class AgentPattern {
	
	private String agentClass;
	
	private String agentName;
	
	private Integer startX;
	
	private Integer startY;
	
	
	private HashMap<String,String> parameters = new HashMap<>();
	
	
	/**
	 * Constructor
	 * @param agentClass
	 * @param agentName
	 * @param startX
	 * @param startY
	 */
	public AgentPattern(@Element(name="class") String agentClass
			, @Element(name="name") String agentName
			, @Element(name="x") Integer startX
			, @Element(name="y") Integer startY
			, @ElementMap(entry="parameter", key="key", attribute=true, inline=true, required=false) HashMap<String, String> parameters) {
		super();
		this.setAgentClass(agentClass);
		this.setAgentName(agentName);
		this.setStartX(startX);
		this.setStartY(startY);
		this.setParameters(parameters);
	}

	@Element(name="class")
	public String getAgentClass() {
		return agentClass;
	}
	
	protected void setAgentClass(String agentClass) {
		this.agentClass = agentClass.toLowerCase();
	}
	
	@Element(name="name")
	public String getAgentName() {
		return agentName;
	}
	
	protected void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	@Element(name="x")
	public Integer getStartX() {
		return startX;
	}
	
	protected void setStartX(Integer startX) {
		this.startX = startX;
	}
	
	@Element(name="y")
	public Integer getStartY() {
		return startY;
	}
	
	protected void setStartY(Integer startY) {
		this.startY = startY;
	}
	
	@ElementMap(entry="parameter", key="key", attribute=true, inline=true, required=false)
	protected HashMap<String, String> getParameters() {
		return parameters;
	}
	
	public String getParameter(String key) {
		if(!this.parameters.containsKey(key)) {
			throw new IllegalArgumentException("Key " + key + " is not defined for " + this.agentName);
		}
		
		return this.parameters.get(key);
	}
	
	public boolean hasParameter(String key) {
		return this.parameters.keySet().contains(key);
	}
	
	protected void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public Point getStart() {
		return new Point(this.startX, this.startY);
	}

}
