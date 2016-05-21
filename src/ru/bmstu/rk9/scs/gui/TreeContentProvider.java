package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.whnet.Consumer;
import ru.bmstu.rk9.scs.whnet.Warehouse;
import ru.bmstu.rk9.scs.whnet.Warehouse.WHLevel;

public class TreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Warehouse parent = (Warehouse) parentElement;
		if (parent.getLevel() == WHLevel.THIRD) {
			return parent.getConsumersList().toArray();
		}
		if (!parent.hasChildren())
			return new Object[] {};
		return parent.getChildren().values().toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Warehouse root = (Warehouse) inputElement;
		if (root.getLevel() == WHLevel.THIRD)
			return root.getConsumersList().toArray();
		if (!root.hasChildren())
			return new Object[] {};
		return root.getChildren().values().toArray();
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Warehouse) {
			Warehouse w = (Warehouse) element;
			return w.getParent();
		}
		if (element instanceof Consumer) {
			Consumer c = (Consumer) element;
			return c.getSupplyingWarehouse();
		}
		return new Object();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Consumer)
			return false;
		Warehouse w = (Warehouse) element;
		switch (w.getLevel()) {
		case FIRST:
		case SECOND:
			return w.hasChildren();
		case THIRD:
			return w.hasConsumers();
		}
		return false;
	}
}
