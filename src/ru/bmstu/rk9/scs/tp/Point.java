package ru.bmstu.rk9.scs.tp;

public abstract class Point {

	enum Type {
		NATURAL, FICTIVE
	}

	protected Type type = Type.NATURAL;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	protected int id;
	protected String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
