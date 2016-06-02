package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public abstract class AbstractTableViewerComparator extends ViewerComparator {
	protected int propertyIndex;
	protected static final int DESCENDING = 1;
	protected int direction = 0;

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	public abstract int compare(Viewer viewer, Object element1, Object element2);
}
