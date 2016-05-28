package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;

public class WHNetTableViewerComparator extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public WHNetTableViewerComparator() {
		this.propertyIndex = 0;
		direction = -DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

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
			Double q01 = resultItem1.getQ0();
			Double q02 = resultItem2.getQ0();
			comparationResult = q01.compareTo(q02);
			break;
		case 5:
			Double ts01 = resultItem1.getTs0();
			Double ts02 = resultItem2.getTs0();
			comparationResult = ts01.compareTo(ts02);
			break;
		case 6:
			Double d01 = resultItem1.getD0();
			Double d02 = resultItem2.getD0();
			comparationResult = d01.compareTo(d02);
			break;
		default:
			comparationResult = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			comparationResult = -comparationResult;
		}
		return comparationResult;
	}
}
