package ru.bmstu.rk9.scs.lib;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Point.Type;
import ru.bmstu.rk9.scs.tp.Producer;
import ru.bmstu.rk9.scs.tp.Solver;

public class TPScheduler {

	private class ScheduleItem {
		Month month;
		Matrix consumersSuppliesPlan;
		Matrix basesSuppliesPlan;

		private ScheduleItem(Month month, Matrix consumersSuppliesPlan, Matrix basesSuppliesPlan) {
			this.month = month;
			this.consumersSuppliesPlan = consumersSuppliesPlan;
			this.basesSuppliesPlan = basesSuppliesPlan;
		}
	}

	private ArrayList<Producer> producersList;
	private ArrayList<ConsumptionPoint> consumersList;

	private Matrix prodsConsDistanceMatrix;

	private ArrayList<ScheduleItem> consumersSuppliesList;
	private ArrayList<ScheduleItem> basesSuppliesList;

	public TPScheduler(TPDatabase db) {

		this.producersList = new ArrayList<Producer>();
		this.consumersList = new ArrayList<ConsumptionPoint>();

		List<Producer> dbProducersList = db.getProducersList();
		List<ConsumptionPoint> dbConsumersList = db.getConsumersList();
		Matrix prodsConsDistanceMatrix = db.getProdsConsDistanceMatrix();

		for (int i = 0; i < dbProducersList.size(); i++) {
			this.producersList.add(dbProducersList.get(i).copy());
		}

		for (int i = 0; i < dbConsumersList.size(); i++) {
			this.consumersList.add(dbConsumersList.get(i).copy());
		}

		this.prodsConsDistanceMatrix = prodsConsDistanceMatrix.copy();
	}

	public void schedule() {

		this.consumersSuppliesList = new ArrayList<ScheduleItem>();

		for (int k = 1; k <= 12; k++) {

			List<Producer> producers = new ArrayList<Producer>();
			for (int i = 0; i < this.producersList.size(); i++) {
				producers.add(this.producersList.get(i).copy());
			}
			List<ConsumptionPoint> consumers = new ArrayList<ConsumptionPoint>();
			for (int i = 0; i < this.consumersList.size(); i++) {
				consumers.add(this.consumersList.get(i).copy());
			}

			Month month = Month.of(k);

			ArrayList<ConsumptionPoint> currentConsumers = new ArrayList<ConsumptionPoint>();
			for (ConsumptionPoint c : consumers)
				if (c.getMonthsList().contains(month) && c.getConsumption() != 0)
					currentConsumers.add(c);

			if (currentConsumers.isEmpty()) {
				System.out.println("NO SUPPLIES IN " + month);
				continue;
			}

			System.out.println("BEFORE EPS");
			for (Producer p : producers)
				System.out.println("pr[" + p.getId() + "]: " + p.getProduction());
			System.out.println("");
			for (ConsumptionPoint c : consumers)
				System.out.println("cons[" + c.getId() + "]: " + c.getConsumption());
			System.out.println("");

			double eps = DBHolder.getInstance().getTPDatabase().getEps();
			addEpsToProdsAndCons(eps, producers, currentConsumers);

			System.out.println("AFTER EPS");
			for (Producer p : producers)
				System.out.println("pr[" + p.getId() + "]: " + p.getProduction());
			System.out.println("");
			for (ConsumptionPoint c : consumers)
				System.out.println("cons[" + c.getId() + "]: " + c.getConsumption());
			System.out.println("");

			ArrayList<Integer> currentConsumersIdList = new ArrayList<Integer>();
			for (ConsumptionPoint c : currentConsumers)
				currentConsumersIdList.add(c.getId());

			Matrix C0 = new Matrix(producers.size(), currentConsumers.size());
			ArrayList<Integer> columnIndices = new ArrayList<Integer>();
			for (int i = 0; i < producers.size(); i++)
				for (int j = 0; j < consumers.size(); j++) {
					int id = consumers.get(j).getId();
					if (currentConsumersIdList.contains(id))
						columnIndices.add(j);
				}
			for (int i = 0; i < producers.size(); i++)
				for (int j = 0; j < currentConsumers.size(); j++) {
					double value = this.prodsConsDistanceMatrix.get(i, columnIndices.get(j));
					C0.set(i, j, value);
				}

			Matrix solution = Solver.solve(producers, currentConsumers, C0);

			for (int i = 0; i < solution.getRowDimension(); i++)
				for (int j = 0; j < solution.getColumnDimension(); j++) {
					if (solution.get(i, j) != 0 && producers.get(i).getType() == Type.NATURAL
							&& currentConsumers.get(j).getType() == Type.NATURAL) {
						int id = currentConsumers.get(j).getId();

						double currentConsumption = Math.round(currentConsumers.get(j).getConsumption());;
						//TODO get rid of this loop
						for (ConsumptionPoint c : this.consumersList)
							if (c.getId() == id)
								currentConsumption = Math.round(c.getConsumption());

						double newConsumption = currentConsumption - Math.round(solution.get(i, j));
						System.out.println("currCons: " + currentConsumption);
						System.out.println("res: " + Math.round(solution.get(i, j)));
						System.out.println("newCons: " + newConsumption);
						//TODO get rid of this loop
						for (ConsumptionPoint c : this.consumersList)
							if (c.getId() == id)
								c.setConsumption(newConsumption);
					}
				}

			this.consumersSuppliesList.add(new ScheduleItem(month, solution, null));

			System.out.println("MONTH: " + Month.of(k));
		}
	}

	private void addEpsToProdsAndCons(double eps, List<Producer> producers, List<ConsumptionPoint> consumers) {
		for (ConsumptionPoint c : consumers) {
			double currentConsumption = c.getConsumption();
			c.setConsumption(currentConsumption + eps);
		}

		int numOfProducers = producers.size();
		double lastProducersProduction = producers.get(numOfProducers - 1).getProduction();
		int numOfConsumers = consumers.size();
		producers.get(numOfProducers - 1).setProduction(lastProducersProduction + eps * numOfConsumers);
	}
}
