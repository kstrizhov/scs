package ru.bmstu.rk9.scs.tp;

public class Base extends Point {

	private double volume;
	private double stock;

	public Base(int id, String name, double volume) {
		this.id = id;
		this.name = name;
		this.volume = volume;
		this.stock = 0;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Base copy() {
		return new Base(id, name, volume);
	}

	public double getStock() {
		return stock;
	}

	public void setStock(double stock) {
		this.stock = stock;
	}
}
