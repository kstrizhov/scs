package ru.bmstu.rk9.scs.lib;

import Jama.Matrix;

public class RandomValueCalculator {

	public static double calculateRandomValue(double P, Matrix values, double[] header, double[] column)
			throws IndexOutOfBoundsException {

		double p = 1 - P;
		double upperP;
		double lowerP;

		int i0 = 0;
		int j0 = 0;

		int upperI = -1;
		int upperJ = -1;

		int lowerI = -1;
		int lowerJ = -1;

		double a;

		double[] packed = values.getRowPackedCopy();

		int numOfRows = values.getRowDimension();
		int numOfColumns = values.getColumnDimension();

		label: for (int i = 0; i < numOfRows; i++)
			for (int j = 0; j < numOfColumns; j++) {
				if (packed[i * numOfColumns + j] == p) {
					i0 = i;
					j0 = j;
					break label;
				}
				if (packed[i * numOfColumns + j + 1] > p) {
					upperP = packed[i * numOfColumns + j + 1];
					lowerP = packed[i * numOfColumns + j];
					a = (p - lowerP) / (upperP - lowerP);

					for (int i1 = 0; i1 < numOfRows; i1++)
						for (int j1 = 0; j1 < numOfColumns; j1++)
							if (values.get(i1, j1) == upperP) {
								upperI = i;
								upperJ = j;
							}

					for (int i1 = 0; i1 < numOfRows; i1++)
						for (int j1 = 0; j1 < numOfColumns; j1++)
							if (values.get(i1, j1) == lowerP) {
								lowerI = i;
								lowerJ = j;
							}

					double y = column[lowerI] + header[lowerJ];
					double z = column[upperI] + header[upperJ];

					return a * (z - y) + y;
				}
			}

		return column[i0] + header[j0];
	}
}
