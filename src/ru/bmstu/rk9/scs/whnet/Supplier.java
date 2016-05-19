package ru.bmstu.rk9.scs.whnet;

import java.util.ArrayList;
import java.util.List;

public class Supplier {

	protected int id;
	protected String name;
	protected List<Resource> suppliedResources;

	public Supplier(int id, String name) {
		this.id = id;
		this.name = name;
		this.suppliedResources = new ArrayList<>();
	}

	public String getName() {
		return this.name;
	}
}
