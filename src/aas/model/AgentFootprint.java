/**
 * 
 */
package aas.model;

import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class AgentFootprint {
	
	private Integer id;
	private AgentRole role;
	private String type;
	private String name;
	private Point position;
	
	
	
	/**
	 * Constructor
	 * @param id - the agent id
	 * @param role - the role of the agent
	 * @param type - the agent type
	 * @param name - the name of the agent
	 * @param position - the location
	 */
	public AgentFootprint(Integer id, AgentRole role, String type, String name, Point position) {
		super();
		this.id = id;
		this.role = role;
		this.type = type;
		this.name = name;
		this.position = position;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public AgentRole getRole() {
		return role;
	}
	public void setRole(AgentRole role) {
		this.role = role;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Point getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "[" + this.id + "] " + this.role + "." + this.type + "." + this.name;  
	}
	
	
	
	

	

}
