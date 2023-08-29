package net.argus.school.plugin.jobs;

public class Job {
	
	private String name;
	private int id, capability;
	
	public Job(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public Job(String name, int id, int capability) {
		this.name = name;
		this.id = id;
		this.capability = capability;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public int getCapability() {
		return capability;
	}
	
	public void setCapability(int capability) {
		this.capability = capability;
	}
}
