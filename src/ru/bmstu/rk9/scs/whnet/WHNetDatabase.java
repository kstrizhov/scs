package ru.bmstu.rk9.scs.whnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WHNetDatabase {

	protected Map<Integer, Warehouse> whNetMap;

	protected List<Integer> firstLevelWarehousesIDList;
	protected List<Integer> secondLevelWarehousesIDList;
	protected List<Integer> thirdLevelWarehousesIDList;

	protected Map<Integer, Consumer> consumersMap;

	protected Map<Integer, Task> tasksMap;

	protected Map<Integer, Resource> resourcesMap;

	protected Map<Integer, Supplier> suppliersMap;
	protected Map<Integer, Integer> resourcesSuppliersMap;

	protected double c1 = 100;
	protected double c2 = 500;
	protected double c3 = 1000;

	protected double T = 2;

	public enum SolveModelType {
		SINGLEPRODUCT, MULTIPRODUCT
	}

	protected SolveModelType firstLvlSolveModelType = SolveModelType.SINGLEPRODUCT;
	protected SolveModelType secondLvlSolveModelType = SolveModelType.SINGLEPRODUCT;
	protected SolveModelType thirdLvlSolveModelType = SolveModelType.SINGLEPRODUCT;

	public WHNetDatabase() {
		this.whNetMap = new HashMap<Integer, Warehouse>();
		this.firstLevelWarehousesIDList = new ArrayList<Integer>();
		this.secondLevelWarehousesIDList = new ArrayList<Integer>();
		this.thirdLevelWarehousesIDList = new ArrayList<Integer>();
		this.consumersMap = new HashMap<Integer, Consumer>();
		this.tasksMap = new HashMap<Integer, Task>();
		this.resourcesMap = new HashMap<Integer, Resource>();
		this.suppliersMap = new HashMap<>();
		this.resourcesSuppliersMap = new HashMap<>();
	}

	public Map<Integer, Consumer> getConsumersMap() {
		return this.consumersMap;
	}

	public Map<Integer, Task> getTasksMap() {
		return this.tasksMap;
	}

	public Map<Integer, Resource> getResourcesMap() {
		return this.resourcesMap;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public void setC2(double c2) {
		this.c2 = c2;
	}

	public void setC3(double c3) {
		this.c3 = c3;
	}

	public void setTimePeriod(double T) {
		this.T = T;
	}

	public void setFirstLvlSolveModelType(SolveModelType firstLvlSolveModelType) {
		this.firstLvlSolveModelType = firstLvlSolveModelType;
	}

	public void setSecondLvlSolveModelType(SolveModelType secondLvlSolveModelType) {
		this.secondLvlSolveModelType = secondLvlSolveModelType;
	}

	public void setThirdLvlSolveModelType(SolveModelType thirdLvlSolveModelType) {
		this.thirdLvlSolveModelType = thirdLvlSolveModelType;
	}

	public void clear() {
		this.whNetMap = new HashMap<Integer, Warehouse>();
		this.firstLevelWarehousesIDList = new ArrayList<Integer>();
		this.secondLevelWarehousesIDList = new ArrayList<Integer>();
		this.thirdLevelWarehousesIDList = new ArrayList<Integer>();
		this.consumersMap = new HashMap<Integer, Consumer>();
		this.tasksMap = new HashMap<Integer, Task>();
		this.resourcesMap = new HashMap<Integer, Resource>();
		this.suppliersMap = new HashMap<>();
		this.resourcesSuppliersMap = new HashMap<>();
	}
}
