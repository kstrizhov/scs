package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.Map;

public class Consumer {

	protected int id;
	protected String name;
	protected Warehouse warehouse;
	protected Map<Integer, Task> tasksMap;
	protected Map<Integer, Double> tasksFreqenciesMap;

	public Consumer(int id, String name, Warehouse warehouse) {
		this.id = id;
		this.name = name;
		this.warehouse = warehouse;
		this.tasksMap = new HashMap<>();
		this.tasksFreqenciesMap = new HashMap<>();
	}

	public Map<Integer, Task> getTasksMap() {
		return this.tasksMap;
	}

	public Map<Integer, Double> getTasksFreqList() {
		return this.tasksFreqenciesMap;
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

	public boolean hasTasks() {
		return !this.tasksMap.isEmpty();
	}
}
