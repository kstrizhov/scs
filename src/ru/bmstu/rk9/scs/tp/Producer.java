package ru.bmstu.rk9.scs.tp;

public class Producer extends Point {

	private double production;
	private double costPerUnit;

	public Producer(int id, String name, double production, double costPerUnit) {
		this.id = id;
		this.name = name;
		this.production = production;
		this.costPerUnit = costPerUnit;
	}

	public double getProduction() {
		return production;
	}

	public void setProduction(double production) {
		this.production = production;
	}

	public double getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(double costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public Producer copy() {
		return new Producer(id, name, production, costPerUnit);
	}
}
