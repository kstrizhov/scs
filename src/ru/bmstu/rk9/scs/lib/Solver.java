package ru.bmstu.rk9.scs.lib;

public class Solver {
	private static Solver INSTANCE;

	private Solver() {
	}

	public static synchronized Solver getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Solver();
			initDatabase();
		}
		return INSTANCE;
	}

	private Database database;
	
	public static synchronized void initDatabase() {
		INSTANCE.database = new Database();
	}

	public Database getDatabase() {
		return INSTANCE.database;
	}
}
