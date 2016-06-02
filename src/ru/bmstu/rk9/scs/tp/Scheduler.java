package ru.bmstu.rk9.scs.tp;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.lib.TPDatabase;
import ru.bmstu.rk9.scs.tp.Point.Type;

public class Scheduler {

	private ArrayList<Producer> producersList;
	private ArrayList<ConsumptionPoint> consumersList;

	private Matrix prodsConsDistanceMatrix;

	private List<TPResultItem> resultsList = new ArrayList<>();

	public Scheduler(TPDatabase db) {

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
				continue;
			}

			double eps = DBHolder.getInstance().getTPDatabase().getEps();
			addEpsToProdsAndCons(eps, producers, currentConsumers);

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

			for (int i = 0; i < solution.getRowDimension(); i++) {
				for (int j = 0; j < solution.getColumnDimension(); j++) {
					if (solution.get(i, j) != 0 && producers.get(i).getType() == Type.NATURAL
							&& currentConsumers.get(j).getType() == Type.NATURAL) {
						int id = currentConsumers.get(j).getId();

						double currentConsumption = Math.round(currentConsumers.get(j).getConsumption());
						;
						// TODO get rid of this loop
						for (ConsumptionPoint c : this.consumersList)
							if (c.getId() == id)
								currentConsumption = Math.round(c.getConsumption());

						double newConsumption = currentConsumption - Math.round(solution.get(i, j));

						// TODO get rid of this loop
						for (ConsumptionPoint c : this.consumersList)
							if (c.getId() == id)
								c.setConsumption(newConsumption);

						double value = Math.floor(solution.get(i, j) * 100) / 100;
						PointAmount amount = new PointAmount(
								DBHolder.getInstance().getTPDatabase().getConsumersList().get(j), value);
						TPResultItem item = new TPResultItem(
								DBHolder.getInstance().getTPDatabase().getProducersList().get(i), Month.of(k), amount);
						resultsList.add(item);
					}
				}
			}
		}

		try {
			setBasesToConsumers(DBHolder.getInstance().getTPDatabase().getConsumersList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			setBasesToConsumers(this.consumersList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Base> bases = DBHolder.getInstance().getTPDatabase().getBasesList();

		for (Base b : bases)
			for (ConsumptionPoint p : this.consumersList)
				if (p.getBase() == b)
					b.setStock(b.getStock() + p.getConsumption());

		for (Base b : bases)
			System.err.println(b.name + ": " + b.getStock());

		DBHolder.getInstance().getTPDatabase().setResultsList(resultsList);
	}

	private void setBasesToConsumers(List<ConsumptionPoint> consumers) throws Exception {
		Matrix distances = DBHolder.getInstance().getTPDatabase().getBasesConsDistanceMatrix();
		List<Base> bases = DBHolder.getInstance().getTPDatabase().getBasesList();

		int numOfBases = distances.getRowDimension();
		int numOfConsumers = distances.getColumnDimension();

		if (consumers.size() != numOfConsumers) {
			throw new Exception("Consumers list doesn't match distances matrix!");
		}

		for (int i = 0; i < numOfConsumers; i++)
			consumers.get(i).setBase(bases.get(0));

		for (int i = 0; i < numOfConsumers; i++) {
			for (int j = 0; j < numOfBases; j++) {
				if (distances.get(j, i) < distances.get(consumers.get(i).getBase().getId() - 1, i))
					consumers.get(i).setBase(bases.get(j));
			}
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

	public class PointAmount {
		public ConsumptionPoint point;
		public double amount;

		public PointAmount(ConsumptionPoint point, double amount) {
			this.point = point;
			this.amount = amount;
		}
	}

	public class TPResultItem {
		public Producer producer;
		public Month month;
		public PointAmount pointAmount;

		public TPResultItem(Producer producer, Month month, PointAmount amount) {
			this.producer = producer;
			this.month = month;
			this.pointAmount = amount;
		}
	}
}
