package ru.bmstu.rk9.scs.whnet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bmstu.rk9.scs.lib.WHNetDatabase;
import ru.bmstu.rk9.scs.whnet.Calculator.WHNetResultItem.SupplyType;

public class Calculator {

	public static class WHNetResultItem {

		private Warehouse warehouse;
		private Resource resource;

		private double demand;

		private double q0;
		private double ts0;
		private double d0;

		public enum SupplyType {
			SINGLE, MULTI;
		}

		private int numOfProductsInSupply;

		private SupplyType type;

		public WHNetResultItem(Warehouse warehouse, Resource resource, double demand, double q0, double ts0, double d0,
				SupplyType type, int numOfProductsInSupply) {
			this.warehouse = warehouse;
			this.resource = resource;
			this.demand = demand;
			this.q0 = q0;
			this.ts0 = ts0;
			this.d0 = d0;
			this.type = type;
			this.numOfProductsInSupply = numOfProductsInSupply;
		}

		public Warehouse getWarehouse() {
			return this.warehouse;
		}

		public Resource getResource() {
			return this.resource;
		}

		public double getQ0() {
			return this.q0;
		}

		public double getTs0() {
			return this.ts0;
		}

		public double getD0() {
			return this.d0;
		}

		public double getDemand() {
			return this.demand;
		}

		public SupplyType getType() {
			return this.type;
		}

		public int getNumOfProductsInSupply() {
			return this.numOfProductsInSupply;
		}
	}

	public static void calculateWHNet(WHNetDatabase db) {

		Map<Integer, Warehouse> whNetMap = db.getWHNetMap();
		Map<Integer, Resource> resourcesMap = db.getResourcesMap();

		for (Integer i : db.getThirdLevelWarehousesIDList()) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForThirdLvlWH(w.consumersList, resourcesMap);
		}

		for (Integer i : db.getSecondLevelWarehousesIDList()) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.children, resourcesMap);
		}

		for (Integer i : db.getFirstLevelWarehousesIDList()) {
			Warehouse w = whNetMap.get(i);
			w.resourceIDsDemandsMap = calculateDemandsForUpperWHLvls(whNetMap, w.children, resourcesMap);
		}

		List<WHNetResultItem> resultsList = db.getResultsList();

		double c1 = db.getC1();
		double c2 = db.getC2();
		double c3 = db.getC3();

		double T = db.getTimePeriod();

		for (Warehouse w : whNetMap.values()) {

			for (Resource resource : resourcesMap.values()) {

				double R = w.resourceIDsDemandsMap.get(resource.id);

				double q0 = -1;
				double ts0 = -1;
				double d0 = -1;

				SupplyType type = null;
				int numOfProductsInSupply = 0;

				switch (w.level) {
				case FIRST:
					switch (db.getFirstLvlSolveModelType()) {
					case SINGLEPRODUCT:
						double C1 = calculateResourceStoringCost(c1, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, resource.Cs, T, C1);
						ts0 = calculateSingleProductTs0(R, resource.Cs, T, C1);
						d0 = calculateSingleProductCostFunc(R, resource.Cs, T, C1);

						type = SupplyType.SINGLE;
						numOfProductsInSupply = 1;
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
							numOfProductsInSupply += 1;
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

						type = SupplyType.MULTI;
						break;
					}
					break;
				case SECOND:
					switch (db.getSecondLvlSolveModelType()) {
					case SINGLEPRODUCT:
						double C2 = calculateResourceStoringCost(c2, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, w.Cs, T, C2);
						ts0 = calculateSingleProductTs0(R, w.Cs, T, C2);
						d0 = calculateSingleProductCostFunc(R, w.Cs, T, C2);

						type = SupplyType.SINGLE;
						numOfProductsInSupply = 1;
						break;
					case MULTIPRODUCT:
						double r = R / T;
						double Cs = w.Cs;

						double denominatror = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c2, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							denominatror += c_i * r_i;
							numOfProductsInSupply += 1;
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

						type = SupplyType.MULTI;
						break;
					}
					break;
				case THIRD:
					switch (db.getThirdLvlSolveModelType()) {
					case SINGLEPRODUCT:
						double C3 = calculateResourceStoringCost(c3, resource.volumePerUnit);

						q0 = calculateSingleProductQ0(R, w.Cs, T, C3);
						ts0 = calculateSingleProductTs0(R, w.Cs, T, C3);
						d0 = calculateSingleProductCostFunc(R, w.Cs, T, C3);

						type = SupplyType.SINGLE;
						numOfProductsInSupply = 1;
						break;
					case MULTIPRODUCT:
						double r = R / T;
						double Cs = w.Cs;

						double denominatror = 0;
						for (Integer i : w.resourceIDsDemandsMap.keySet()) {
							double c_i = calculateResourceStoringCost(c3, resourcesMap.get(i).volumePerUnit);
							double r_i = w.resourceIDsDemandsMap.get(i) / T;
							denominatror += c_i * r_i;
							numOfProductsInSupply += 1;
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

						type = SupplyType.MULTI;
						break;
					}
					break;
				}

				resultsList.add(new WHNetResultItem(w, resource, R, q0, ts0, d0, type, numOfProductsInSupply));
			}
		}
	}

	private static Map<Integer, Double> calculateDemandsForThirdLvlWH(List<Consumer> consumersList,
			Map<Integer, Resource> resourcesMap) {

		Map<Integer, Double> demands = new HashMap<>();

		for (Resource r : resourcesMap.values()) {

			double demand = 0;

			for (Consumer c : consumersList)
				for (Task t : c.tasksMap.values()) {
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
