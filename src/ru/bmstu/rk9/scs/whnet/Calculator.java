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
			Warehouse w = c.warehouse;
			w.consumersList.add(c);
		}

		for (Integer i : db.thirdLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForThirdLvlWH(w.consumersList, resourcesMap);
		}

		for (Integer i : db.secondLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.children, resourcesMap);
		}

		for (Integer i : db.firstLevelWarehousesIDList) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.children, resourcesMap);
		}

		// maps results for each wh to wh id
		Map<Integer, Map<Integer, ResultItem>> calcResultsMap = new HashMap<>();

		double c1 = db.c1;
		double c2 = db.c2;
		double c3 = db.c3;

		double T = db.T;

		for (Warehouse w : whNetMap.values()) {

			// maps results for each resource to resource id
			Map<Integer, ResultItem> resourceResultsMap = new HashMap<>();

			for (Resource resource : resourcesMap.values()) {

				double R = w.resourceIDsDemandsMap.get(resource.id);

				double q0 = -1;
				double ts0 = -1;
				double d0 = -1;

				switch (w.level) {
				case FIRST:
					switch (db.firstLvlSolveModelType) {
					case SINGLEPRODUCT:
						double C1 = calculateResourceStoringCost(c1, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, resource.Cs, T, C1);
						ts0 = calculateSingleProductTs0(R, resource.Cs, T, C1);
						d0 = calculateSingleProductCostFunc(R, resource.Cs, T, C1);
						break;
					case MULTIPRODUCT:
						double r = R / T;
						double Cs = resource.Cs;

						double denominatror = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							if (resource.supplierID != resourcesMap.get(i).supplierID)
								continue;
							double c_i = calculateResourceStoringCost(c1, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							denominatror += c_i * r_i;
						}

						ts0 = calculateMultiProductTs0(Cs, denominatror);
						q0 = r * ts0;

						double storageCost = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							if (resource.supplierID != resourcesMap.get(i).supplierID)
								continue;
							double c_i = calculateResourceStoringCost(c1, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							storageCost += (c_i * r_i * ts0 * T) / 2;
						}

						double supplyCost = Cs * T / ts0;

						d0 = storageCost + supplyCost;
						break;
					}
					break;
				case SECOND:
					switch (db.secondLvlSolveModelType) {
					case SINGLEPRODUCT:
						double C2 = calculateResourceStoringCost(c2, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, w.Cs, T, C2);
						ts0 = calculateSingleProductTs0(R, w.Cs, T, C2);
						d0 = calculateSingleProductCostFunc(R, w.Cs, T, C2);
						break;
					case MULTIPRODUCT:
						double r = R / T;
						double Cs = w.Cs;

						double denominatror = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c2, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							denominatror += c_i * r_i;
						}

						ts0 = calculateMultiProductTs0(Cs, denominatror);
						q0 = r * ts0;

						double storageCost = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c2, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							storageCost += (c_i * r_i * ts0 * T) / 2;
						}

						double supplyCost = Cs * T / ts0;

						d0 = storageCost + supplyCost;
						break;
					}
					break;
				case THIRD:
					switch (db.thirdLvlSolveModelType) {
					case SINGLEPRODUCT:
						double C3 = calculateResourceStoringCost(c3, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, w.Cs, T, C3);
						ts0 = calculateSingleProductTs0(R, w.Cs, T, C3);
						d0 = calculateSingleProductCostFunc(R, w.Cs, T, C3);
						break;
					case MULTIPRODUCT:
						double r = R / T;
						double Cs = w.Cs;

						double denominatror = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c3, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							denominatror += c_i * r_i;
						}

						ts0 = calculateMultiProductTs0(Cs, denominatror);
						q0 = r * ts0;

						double storageCost = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c3, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							storageCost += (c_i * r_i * ts0 * T) / 2;
						}

						double supplyCost = Cs * T / ts0;

						d0 = storageCost + supplyCost;
						break;
					}
					break;
				}

				resourceResultsMap.put(resource.id, new ResultItem(q0, ts0, d0));
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
				switch (w.level) {
				case FIRST:
					switch (db.secondLvlSolveModelType) {
					case SINGLEPRODUCT:
						total += result.d0;
						break;
					case MULTIPRODUCT:
						int resourcesSuppliedInOnePack = db.suppliersMap.get(r.supplierID).suppliedResources.size();
						total += result.d0 / resourcesSuppliedInOnePack;
						break;
					}
					break;
				case SECOND:
					switch (db.secondLvlSolveModelType) {
					case SINGLEPRODUCT:
						total += result.d0;
						break;
					case MULTIPRODUCT:
						total += result.d0 / w.resourceIDsDemandsMap.values().size();
						break;
					}
					break;
				case THIRD:
					switch (db.thirdLvlSolveModelType) {
					case SINGLEPRODUCT:
						total += result.d0;
						break;
					case MULTIPRODUCT:
						total += result.d0 / w.resourceIDsDemandsMap.values().size();
						break;
					}
					break;
				}
				System.out.println("optimal cost function value: " + result.d0);
				System.out.println("current total value: " + total);
				System.out.println("");
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
			Map<Integer, Warehouse> children, Map<Integer, Resource> resourcesMap) {

		Map<Integer, Double> demands = new HashMap<>();

		for (Resource r : resourcesMap.values()) {
			double demand = 0;
			for (Integer i : children.keySet()) {
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

	private static double calculateSingleProductTs0(double R, double Cs, double T, double C1) {
		return Math.sqrt(2 * T * Cs / (R * C1));
	}

	private static double calculateSingleProductCostFunc(double R, double Cs, double T, double C1) {
		return Math.sqrt(2 * R * T * Cs * C1);
	}

	private static double calculateMultiProductTs0(double Cs, double denominator) {
		return Math.sqrt(2 * Cs / denominator);
	}

	private static double calculateResourceStoringCost(double whWagePerVolume, double volumePerUnit) {
		double actualVolumePerUnit = Math.round(volumePerUnit);
		if (actualVolumePerUnit < volumePerUnit)
			actualVolumePerUnit += 1;
		return whWagePerVolume * actualVolumePerUnit;
	}
}
