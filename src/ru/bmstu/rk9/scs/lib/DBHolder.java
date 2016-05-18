package ru.bmstu.rk9.scs.lib;

import ru.bmstu.rk9.scs.whnet.WHNetDatabase;

public class DBHolder {
	private static DBHolder INSTANCE;

	private DBHolder() {
	}

	public static synchronized DBHolder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DBHolder();
			initTPDatabase();
			initWHNetDatabase();
		}
		return INSTANCE;
	}

	private TPDatabase tpDatabase;
	
	public static synchronized void initTPDatabase() {
		INSTANCE.tpDatabase = new TPDatabase();
	}

	public TPDatabase getTPDatabase() {
		return INSTANCE.tpDatabase;
	}
	
	private WHNetDatabase whNetDatabase;
	
	public static synchronized void initWHNetDatabase() {
		INSTANCE.whNetDatabase = new WHNetDatabase();
	}

	public WHNetDatabase getWHNetDatabase() {
		return INSTANCE.whNetDatabase;
	}
}
