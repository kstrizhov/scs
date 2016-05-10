package ru.bmstu.rk9.scs.lib;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Producer;
import ru.bmstu.rk9.scs.tp.Solver;
import ru.bmstu.rk9.scs.tp.Solver.Plan;

public class Scheduler {

	class ScheduleItem {
		Month month;
		Matrix consumersSuppliesPlan;
		Matrix basesSuppliesPlan;
	}

	private ArrayList<ScheduleItem> consumersSuppliesList;
	private ArrayList<ScheduleItem> basesSuppliesList;

	public void schedule() {

		for (int i = 1; i <= 12; i++) {
			
			List<Producer> dbProducersList = DBHolder.getInstance().getDatabase().getProducersList();
			List<ConsumptionPoint> dbConsumersList = DBHolder.getInstance().getDatabase().getConsumersList();
			Matrix prodsConsCostMatrix = DBHolder.getInstance().getDatabase().getProdsConsDistanceMatrix();

			List<Producer> producers = new ArrayList<Producer>();
			for (int k = 0; k < dbProducersList.size(); k++) {
				producers.add(dbProducersList.get(k).copy());
			}
			List<ConsumptionPoint> consumers = new ArrayList<ConsumptionPoint>();
			for (int k = 0; k < dbConsumersList.size(); k++) {
				consumers.add(dbConsumersList.get(k).copy());
			}
			
			Month month = Month.of(i);
			ArrayList<ConsumptionPoint> currentConsumers = new ArrayList<ConsumptionPoint>();

			for (ConsumptionPoint c : consumers)
				if (c.getMonthsList().contains(month))
					currentConsumers.add(c);

			if (currentConsumers.isEmpty()) {
				System.err.println("NO SUPPLIES IN " + month);
				continue;
			}

			ArrayList<Integer> currentConsumersIdList = new ArrayList<Integer>();
			for (ConsumptionPoint c : currentConsumers)
				currentConsumersIdList.add(c.getId());

			Matrix C0 = new Matrix(producers.size(), currentConsumers.size());

			for (int i1 = 0; i1 < producers.size(); i1++)
				for (int j = 1; j <= currentConsumers.size(); j++) {
					if (!currentConsumersIdList.contains(j))
						continue;
					double value = prodsConsCostMatrix.get(i1, j - 1);
					C0.set(i1, j - 1, value);
				}

			System.out.println("~~~ SUBMATRIX C ~~~");
			C0.print(C0.getColumnDimension(), 2);

			Matrix isolatedC0 = Solver.isolateTransportationProblem(producers, currentConsumers, C0);

			Plan plan = Solver.createBasicPlan(producers, currentConsumers);
			
			Matrix result;

			if (isolatedC0 == null)
				result = Solver.solve(plan, C0);
			else
				result = Solver.solve(plan, isolatedC0);
			
			System.out.println("RESULT");
			result.print(result.getColumnDimension(), 2);

			System.out.println("MONTH: " + Month.of(i));
		}
	}
}
