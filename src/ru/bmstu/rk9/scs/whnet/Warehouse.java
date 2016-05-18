package ru.bmstu.rk9.scs.whnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Warehouse {

	protected int id;
	protected String name;
	protected double volume;
	protected int parentID;
	protected List<Integer> childrensIDList = new ArrayList<Integer>();
	protected List<Consumer> consumersList = new ArrayList<Consumer>();

	protected Map<Integer, Double> resourceIDsDemandsMap;

	public enum WHLevel {
		FIRST, SECOND, THIRD
	}

	protected WHLevel level;

	public Warehouse(int id, String name, double volume, int parentID) {
		this.id = id;
		this.name = name;
		this.volume = volume;
		this.parentID = parentID;
	}

	public void print() {
		System.out.println("WAREHOUSE id: " + id + " name: " + name + " parentID: " + parentID);
	}
}
