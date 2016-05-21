package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.whnet.Warehouse;

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
		if (!parent.hasChildren())
			return new Object[] {};
		return parent.getChildren().values().toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Warehouse root = (Warehouse) inputElement;
		if (!root.hasChildren())
			return new Object[] {};
		return root.getChildren().values().toArray();
	}

	@Override
	public Object getParent(Object element) {
		Warehouse w = (Warehouse) element;
		return w.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		Warehouse w = (Warehouse) element;
		return w.hasChildren();
	}
}
