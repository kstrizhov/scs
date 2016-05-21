package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ru.bmstu.rk9.scs.whnet.Consumer;
import ru.bmstu.rk9.scs.whnet.Warehouse;

public class DataLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		Display d = Display.getDefault();
		if (element instanceof Warehouse) {
			Warehouse w = (Warehouse) element;
			switch (w.getLevel()) {
			case FIRST:
				return new Image(d, "./images/wh1.png");
			case SECOND:
				return new Image(d, "./images/wh2.png");
			case THIRD:
				return new Image(d, "./images/wh3.png");
			}
		}
		if (element instanceof Consumer)
			return new Image(d, "./images/resource.png");
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Warehouse) {
			Warehouse w = (Warehouse) element;
			return "Warehouse (id: " + w.getId() + ")";
		}
		if (element instanceof Consumer) {
			Consumer c = (Consumer) element;
			return "Consumer (id: " + c.getId() + ")";
		}
		return "qqq";
	}
}
