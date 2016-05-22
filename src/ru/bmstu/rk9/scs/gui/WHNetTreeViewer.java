package ru.bmstu.rk9.scs.gui;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.whnet.WHNetDatabase;
import ru.bmstu.rk9.scs.whnet.Warehouse;

public class WHNetTreeViewer extends TreeViewer implements Observer{

	public WHNetTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void update(Observable o, Object arg) {
		System.err.println("updated");
		this.setInput(getInitalInput());
		this.refresh();
	}
	
	private Warehouse getInitalInput() {
		WHNetDatabase db = DBHolder.getInstance().getWHNetDatabase();
		Warehouse root = new Warehouse(0, "root", 0, null, 0);
		HashMap<Integer, Warehouse> rootChild = new HashMap<>();
		rootChild.put(1, db.getWHNetMap().get(1));
		db.getWHNetMap().get(1).setParent(root);
		root.setChildren(rootChild);
		return root;
	}
}
