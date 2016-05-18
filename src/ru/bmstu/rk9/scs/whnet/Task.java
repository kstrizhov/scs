package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.Map;

public class Task {

	protected int id;
	protected String name;
	protected Map<Integer, Double> resourceNorms;

	public Task(int id, String name) {
		this.id = id;
		this.name = name;
		this.resourceNorms = new HashMap<>();
	}

	public String getName() {
		return this.name;
	}

	public Map<Integer, Double> getResourceNorms() {
		return this.resourceNorms;
	}
}
