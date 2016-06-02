package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.tp.Base;

public class TPStockViewerComparator extends AbstractTableViewerComparator {

	@Override
	public int compare(Viewer viewer, Object element1, Object element2) {
		Base base1 = (Base) element1;
		Base base2 = (Base) element2;
		int comparationResult = 0;
		switch (propertyIndex) {
		case 0:
			Integer baseId1 = base1.getId();
			Integer baseId2 = base2.getId();
			comparationResult = baseId1.compareTo(baseId2);
			break;
		case 1:
			comparationResult = base1.getName().compareTo(base2.getName());
			break;
		case 2:
			Double stock1 = base1.getStock();
			Double stock2 = base2.getStock();
			comparationResult = stock1.compareTo(stock2);
			break;
		case 3:
			Double volume1 = base1.getVolume();
			Double volume2 = base2.getVolume();
			comparationResult = volume1.compareTo(volume2);
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
