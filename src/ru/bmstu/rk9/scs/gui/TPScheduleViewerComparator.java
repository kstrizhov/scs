package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.tp.Scheduler.TPResultItem;

public class TPScheduleViewerComparator extends AbstractTableViewerComparator {

	@Override
	public int compare(Viewer viewer, Object element1, Object element2) {
		TPResultItem resultItem1 = (TPResultItem) element1;
		TPResultItem resultItem2 = (TPResultItem) element2;
		int comparationResult = 0;
		switch (propertyIndex) {
		case 0:
			Integer producerId1 = resultItem1.producer.getId();
			Integer producerId2 = resultItem2.producer.getId();
			comparationResult = producerId1.compareTo(producerId2);
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
