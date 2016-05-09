package ru.bmstu.rk9.scs.lib;

public class DBHolder {
	private static DBHolder INSTANCE;

	private DBHolder() {
	}

	public static synchronized DBHolder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DBHolder();
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
