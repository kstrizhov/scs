package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculator {

	static class ResultItem {
		double q0;
		double ts0;
		double d0;

		public ResultItem(double q0, double ts0, double d0) {
			this.q0 = q0;
			this.ts0 = ts0;
			this.d0 = d0;
		}
	}

	public static void calculateWHNet(WHNetDatabase db) {
		Map<Integer, Warehouse> whNetMap = db.whNetMap;
		Map<Integer, Resource> resourcesMap = db.resourcesMap;

		for (Consumer c : db.consumersMap.values()) {
			int whID = c.warehouseID;
			whNetMap.get(whID).consumersList.add(c);
		}

		for (Integer i : db.thirdLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForThirdLvlWH(w.consumersList, resourcesMap);
		}

		for (Integer i : db.secondLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.childrensIDList, resourcesMap);
		}

		for (Integer i : db.firstLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.childrensIDList, resourcesMap);
		}

		Map<Integer, Map<Integer, ResultItem>> calcResultsMap = new HashMap<>();

		double c1 = db.c1;
		double c2 = db.c2;
		double c3 = db.c3;
		double cs1 = db.cs1;
		double cs2 = db.cs2;
		double cs3 = db.cs3;

		double T = db.T;

		for (Warehouse w : whNetMap.values()) {

			Map<Integer, ResultItem> resourceResultsMap = new HashMap<>();

			for (Resource r : resourcesMap.values()) {

				double R = w.resourceIDsDemandsMap.get(r.id);

				double q0 = -1;
				double ts0 = -1;
				double d0 = -1;

				switch (w.level) {
				case FIRST:
					q0 = calculateSingleProductQ0(R, cs1, T, c1);
					ts0 = caclulateSingleProductTs0(R, cs1, T, c1);
					d0 = caclulateSingleProductCostFunc(R, cs1, T, c1);
					break;
				case SECOND:
					q0 = calculateSingleProductQ0(R, cs2, T, c2);
					ts0 = caclulateSingleProductTs0(R, cs2, T, c2);
					d0 = caclulateSingleProductCostFunc(R, cs2, T, c2);
					break;
				case THIRD:
					q0 = calculateSingleProductQ0(R, cs3, T, c3);
					ts0 = caclulateSingleProductTs0(R, cs3, T, c3);
					d0 = caclulateSingleProductCostFunc(R, cs3, T, c3);
					break;
				}

				resourceResultsMap.put(r.id, new ResultItem(q0, ts0, d0));
			}

			calcResultsMap.put(w.id, resourceResultsMap);
		}

		double total = 0;

		for (Warehouse w : whNetMap.values()) {
			System.out.println("####-- WAREHOUSE[" + w.id + "] --####");
			System.out.println("level: " + w.level);
			System.out.println("");

			Map<Integer, ResultItem> resultsMap = calcResultsMap.get(w.id);
			for (Resource r : resourcesMap.values()) {
				ResultItem result = resultsMap.get(r.id);
				System.out.println("~~ resource[" + r.id + "] ~~");
				System.out.println("name: " + r.name);
				System.out.println("demand: " + w.resourceIDsDemandsMap.get(r.id));
				System.out.println("optimal delivery volume: " + result.q0);
				System.out.println("optimal delivery time period: " + result.ts0);
				System.out.println("optimal cost function value: " + result.d0);
				System.out.println("");
				total += result.d0;
			}
		}

		System.out.println("total cost function value: " + total);
	}

	private static Map<Integer, Double> calculateDemandsForThirdLvlWH(List<Consumer> consumersList,
			Map<Integer, Resource> resourcesMap) {

		Map<Integer, Double> demands = new HashMap<>();

		for (Resource r : resourcesMap.values()) {

			double demand = 0;

			for (Consumer c : consumersList)
				for (Task t : c.tasksList.values()) {
					demand += c.tasksFreqenciesMap.get(t.id) * t.resourceNorms.get(r.id);
				}

			demands.put(r.id, demand);
		}

		return demands;
	}

	private static Map<Integer, Double> calculateDemandsForUpperWHLvls(Map<Integer, Warehouse> whNetMap,
			List<Integer> childrensIDList, Map<Integer, Resource> resourcesMap) {

		Map<Integer, Double> demands = new HashMap<>();

		for (Resource r : resourcesMap.values()) {
			double demand = 0;
			for (Integer i : childrensIDList) {
				Warehouse w = whNetMap.get(i);
				demand += w.resourceIDsDemandsMap.get(r.id);
			}

			demands.put(r.id, demand);
		}

		return demands;
	}

	private static double calculateSingleProductQ0(double R, double Cs, double T, double C1) {
		return Math.sqrt(2 * R * Cs / (T * C1));
	}

	private static double caclulateSingleProductTs0(double R, double Cs, double T, double C1) {
		return Math.sqrt(2 * T * Cs / (R * C1));
	}

	private static double caclulateSingleProductCostFunc(double R, double Cs, double T, double C1) {
		return Math.sqrt(2 * R * T * Cs * C1);
	}
}
