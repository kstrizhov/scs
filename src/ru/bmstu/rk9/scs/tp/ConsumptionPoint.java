package ru.bmstu.rk9.scs.tp;

import java.time.Month;
import java.util.ArrayList;

public class ConsumptionPoint extends Point {

	private double consumption;
	private ArrayList<Month> monthsList;
	private Base base;

	public ConsumptionPoint(int id, String name, double consumption, ArrayList<Month> months) {
		this.id = id;
		this.name = name;
		this.setConsumption(consumption);
		this.monthsList = months;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public ArrayList<Month> getMonthsList() {
		return monthsList;
	}

	public void setMonthsList(ArrayList<Month> months) {
		this.monthsList = months;
	}

	public ConsumptionPoint copy() {
		return new ConsumptionPoint(id, name, consumption, monthsList);
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}
}
