package ru.bmstu.rk9.scs.whnet;

public class Resource {

	protected int id;
	protected String name;

	public Resource(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
