package ru.bmstu.rk9.scs.whnet;

public class Resource {

	protected int id;
	protected String name;
	protected double volumePerUnit;
	protected int supplierID;
	protected double Cs;

	public Resource(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Resource(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
}
