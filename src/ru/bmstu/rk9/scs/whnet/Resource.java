package ru.bmstu.rk9.scs.whnet;

public class Resource {

	protected int id;
	protected String name;
	protected double volumePerUnit;
	protected int supplierID;
	protected double Cs;

	protected Task task;

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

	public void setName(String name) {
		this.name = name;
	}

	public void setVolumePerUnit(double volumePerUnit) {
		this.volumePerUnit = volumePerUnit;
	}

	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}

	public void setCs(double Cs) {
		this.Cs = Cs;
	}

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public int getId() {
		return this.id;
	}
}
