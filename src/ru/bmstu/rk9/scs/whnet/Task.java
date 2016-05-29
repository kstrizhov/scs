package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.Map;

public class Task {

	protected int id;
	protected String name;
	protected Consumer consumer;
	protected Map<Integer, Resource> resourcesMap;
	protected Map<Integer, Double> resourceNorms;

	public Task(int id, String name) {
		this.id = id;
		this.name = name;
		this.resourcesMap = new HashMap<>();
		this.resourceNorms = new HashMap<>();
	}

	public String getName() {
		return this.name;
	}

	public Map<Integer, Resource> getResourcesMap() {
		return this.resourcesMap;
	}

	public Map<Integer, Double> getResourceNorms() {
		return this.resourceNorms;
	}

	public Consumer getConsumer() {
		return this.consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public int getId() {
		return this.id;
	}

	public boolean needsResources() {
		return !this.resourcesMap.isEmpty();
	}
}
