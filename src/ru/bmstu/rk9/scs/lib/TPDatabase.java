package ru.bmstu.rk9.scs.lib;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import ru.bmstu.rk9.scs.tp.Base;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Producer;
import ru.bmstu.rk9.scs.tp.Scheduler.TPResultItem;

public class TPDatabase {

	private ArrayList<Producer> producersList;
	private ArrayList<ConsumptionPoint> consumersList;
	private ArrayList<Base> basesList;

	public ArrayList<Producer> getProducersList() {
		return producersList;
	}

	public void setProducersList(ArrayList<Producer> producersList) {
		this.producersList = producersList;
	}

	public ArrayList<ConsumptionPoint> getConsumersList() {
		return consumersList;
	}

	public void setConsumersList(ArrayList<ConsumptionPoint> consumersList) {
		this.consumersList = consumersList;
	}

	public ArrayList<Base> getBasesList() {
		return basesList;
	}

	public void setBasesList(ArrayList<Base> basesList) {
		this.basesList = basesList;
	}

	private Matrix prodsConsDistanceMatrix;
	private Matrix prodsBasesDistanceMatrix;
	private Matrix basesConsDistanceMatrix;

	public Matrix getProdsConsDistanceMatrix() {
		return prodsConsDistanceMatrix;
	}

	public void setProdsConsDistanceMatrix(Matrix prodsConsDistanceMatrix) {
		this.prodsConsDistanceMatrix = prodsConsDistanceMatrix;
	}

	public Matrix getProdsBasesDistanceMatrix() {
		return prodsBasesDistanceMatrix;
	}

	public void setProdsBasesDistanceMatrix(Matrix prodsBasesDistanceMatrix) {
		this.prodsBasesDistanceMatrix = prodsBasesDistanceMatrix;
	}

	public Matrix getBasesConsDistanceMatrix() {
		return basesConsDistanceMatrix;
	}

	public void setBasesConsDistanceMatrix(Matrix basesConsDistanceMatrix) {
		this.basesConsDistanceMatrix = basesConsDistanceMatrix;
	}

	private double tariff;

	public double getTariff() {
		return tariff;
	}

	public void setTariff(double tariff) {
		this.tariff = tariff;
	}

	private double eps = 0.000001;

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	private List<TPResultItem> resultsList;

	public List<TPResultItem> getResultsList() {
		return this.resultsList;
	}

	public void setResultsList(List<TPResultItem> resultsList) {
		this.resultsList = resultsList;
	}
}
