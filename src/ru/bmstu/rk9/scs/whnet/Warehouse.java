package ru.bmstu.rk9.scs.whnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warehouse {

	protected int id;
	protected String name;
	protected double volume;
	protected Warehouse parent;
	protected double Cs;

	protected Map<Integer, Warehouse> children = new HashMap<>();
	protected List<Consumer> consumersList = new ArrayList<Consumer>();

	protected Map<Integer, Double> resourceIDsDemandsMap;

	public enum WHLevel {
		FIRST, SECOND, THIRD
	}

	protected WHLevel level;

	public Warehouse(int id, String name, double volume, Warehouse parent, double Cs) {
		this.id = id;
		this.name = name;
		this.volume = volume;
		this.parent = parent;
		this.Cs = Cs;
	}

	public WHLevel getLevel() {
		return this.level;
	}

	public void print() {
		System.out.println("WAREHOUSE id: " + id + " name: " + name + " parentID: " + parent.id);
	}

	public Warehouse getParent() {
		return this.parent;
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public Map<Integer, Warehouse> getChildren() {
		return this.children;
	}

	public String getName() {
		return this.name;
	}
}
