package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;

public class WHNetTableViewerComparator extends AbstractTableViewerComparator {

	@Override
	public int compare(Viewer viewer, Object element1, Object element2) {
		ResultItem resultItem1 = (ResultItem) element1;
		ResultItem resultItem2 = (ResultItem) element2;
		int comparationResult = 0;
		switch (propertyIndex) {
		case 0:
			Integer whId1 = resultItem1.getWarehouse().getId();
			Integer whId2 = resultItem2.getWarehouse().getId();
			comparationResult = whId1.compareTo(whId2);
			break;
		case 1:
			comparationResult = resultItem1.getWarehouse().getName().compareTo(resultItem2.getWarehouse().getName());
			break;
		case 2:
			Integer resourceId1 = resultItem1.getResource().getId();
			Integer resourceId2 = resultItem2.getResource().getId();
			comparationResult = resourceId1.compareTo(resourceId2);
			break;
		case 3:
			comparationResult = resultItem1.getResource().getName().compareTo(resultItem2.getResource().getName());
			break;
		case 4:
			Double demand1 = resultItem1.getDemand();
			Double demand2 = resultItem2.getDemand();
			comparationResult = demand1.compareTo(demand2);
			break;
		case 5:
			Double q01 = resultItem1.getQ0();
			Double q02 = resultItem2.getQ0();
			comparationResult = q01.compareTo(q02);
			break;
		case 6:
			Double ts01 = resultItem1.getTs0();
			Double ts02 = resultItem2.getTs0();
			comparationResult = ts01.compareTo(ts02);
			break;
		case 7:
			Double d01 = resultItem1.getD0();
			Double d02 = resultItem2.getD0();
			comparationResult = d01.compareTo(d02);
			break;
		case 8:
			comparationResult = resultItem1.getType().toString().compareTo(resultItem2.getType().toString());
			break;
		case 9:
			Integer numOfProdsInSupply1 = resultItem1.getNumOfProductsInSupply();
			Integer numOfProdsInSupply2 = resultItem2.getNumOfProductsInSupply();
			comparationResult = numOfProdsInSupply1.compareTo(numOfProdsInSupply2);
			break;
		default:
			comparationResult = 0;
		}
		if (direction == DESCENDING) {
			comparationResult = -comparationResult;
		}
		return comparationResult;
	}
}
