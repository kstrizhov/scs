package ru.bmstu.rk9.scs.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ru.bmstu.rk9.scs.whnet.Consumer;
import ru.bmstu.rk9.scs.whnet.Resource;
import ru.bmstu.rk9.scs.whnet.Task;
import ru.bmstu.rk9.scs.whnet.Warehouse;
import ru.bmstu.rk9.scs.whnet.Warehouse.WHLevel;

public class WHNetTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Consumer) {
			Consumer c = (Consumer) parentElement;
			List<Task> list = new ArrayList<>();
			for (Integer i : c.getTasksFreqList().keySet())
				if (c.getTasksFreqList().get(i) != 0)
					list.add(c.getTasksMap().get(i));
			return list.toArray();
		}
		if (parentElement instanceof Task) {
			Task t = (Task) parentElement;
			List<Resource> list = new ArrayList<>();
			for (Integer i : t.getResourceNorms().keySet())
				if (t.getResourceNorms().get(i) != 0)
					list.add(t.getResourcesMap().get(i));
			return list.toArray();
		}
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
		if (inputElement instanceof Consumer) {
			Consumer root = (Consumer) inputElement;
			List<Task> list = new ArrayList<>();
			for (Integer i : root.getTasksFreqList().keySet())
				if (root.getTasksFreqList().get(i) != 0)
					list.add(root.getTasksMap().get(i));
			return list.toArray();
		}
		if (inputElement instanceof Task) {
			Task root = (Task) inputElement;
			List<Resource> list = new ArrayList<>();
			for (Integer i : root.getResourceNorms().keySet())
				if (root.getResourceNorms().get(i) != 0)
					list.add(root.getResourcesMap().get(i));
			return list.toArray();
		}
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
		if (element instanceof Task) {
			Task t = (Task) element;
			return t.getConsumer();
		}
		if (element instanceof Resource) {
			Resource r = (Resource) element;
			return r.getTask();
		}
		return new Object();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Consumer) {
			Consumer c = (Consumer) element;
			return c.hasTasks();
		}
		if (element instanceof Task) {
			Task t = (Task) element;
			return t.needsResources();
		}
		if (element instanceof Resource) {
			return false;
		}
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
