package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import ru.bmstu.rk9.scs.tp.Base;

public class TPStockTableViewer extends TableViewer {

	public TPStockTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void createColumns() {
		String[] titles = { "base_id", "base_name", "stock", "volume" };
		int[] bounds = { 70, 110, 60, 60 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Base item = (Base) element;
				return Integer.toString(item.getId());
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Base item = (Base) element;
				return item.getName();
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Base item = (Base) element;
				return Double.toString(item.getStock());
			}
		});

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Base item = (Base) element;
				return Double.toString(item.getVolume());
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	private TPStockTableViewer getViewer() {
		return this;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TPStockViewerComparator comparator = (TPStockViewerComparator) getViewer().getComparator();
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				getViewer().getTable().setSortDirection(dir);
				getViewer().refresh();
			}
		};
		return selectionAdapter;
	}
}
