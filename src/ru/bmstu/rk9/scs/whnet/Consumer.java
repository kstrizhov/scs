package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.Map;

public class Consumer {

	protected int id;
	protected String name;
	protected int warehouseID;
	protected Map<Integer, Task> tasksList;
	protected Map<Integer, Double> tasksFreqenciesMap;

	public Consumer(int id, String name, int warehouseID) {
		this.id = id;
		this.name = name;
		this.warehouseID = warehouseID;
		this.tasksList = new HashMap<>();
		this.tasksFreqenciesMap = new HashMap<>();
	}

	public Map<Integer, Task> getTasksList() {
		return this.tasksList;
	}

	public int getID() {
		return this.id;
	}
}
