package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.Map;

public class Consumer {

	protected int id;
	protected String name;
	protected Warehouse warehouse;
	protected Map<Integer, Task> tasksList;
	protected Map<Integer, Double> tasksFreqenciesMap;

	public Consumer(int id, String name, Warehouse warehouse) {
		this.id = id;
		this.name = name;
		this.warehouse = warehouse;
		this.tasksList = new HashMap<>();
		this.tasksFreqenciesMap = new HashMap<>();
	}

	public Map<Integer, Task> getTasksList() {
		return this.tasksList;
	}

	public int getID() {
		return this.id;
	}

	public Warehouse getSupplyingWarehouse() {
		return this.warehouse;
	}

	public int getId() {
		return this.id;
	}
}
