package ru.bmstu.rk9.scs.gui;

import java.time.Month;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import ru.bmstu.rk9.scs.tp.Scheduler.TPResultItem;

public class TPPlanTableViewer extends TableViewer {

	public TPPlanTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void createColumns() {
		String[] titles = { "prod_id", "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
				"DEC" };
		int[] bounds = { 60, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				return Integer.toString(item.producer.getId());
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.JANUARY)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.FEBRUARY)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.MARCH)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.APRIL)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.MAY)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[6], bounds[6], 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.JUNE)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[7], bounds[7], 7);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.JULY)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[8], bounds[8], 8);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.AUGUST)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[9], bounds[9], 9);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.SEPTEMBER)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[10], bounds[10], 10);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.OCTOBER)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[11], bounds[11], 11);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.NOVEMBER)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
			}
		});

		col = createTableViewerColumn(titles[12], bounds[12], 12);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TPResultItem item = (TPResultItem) element;
				if (item.month == Month.DECEMBER)
					return item.pointAmount.point.getId() + " (" + item.pointAmount.amount + ")";
				else
					return "-";
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

	private TPPlanTableViewer getViewer() {
		return this;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TPScheduleViewerComparator comparator;
				if (getViewer().getComparator() == null)
					comparator = new TPScheduleViewerComparator();
				else
					comparator = (TPScheduleViewerComparator) getViewer().getComparator();
				getViewer().setComparator(comparator);
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				getViewer().getTable().setSortDirection(dir);
				getViewer().refresh();
			}
		};
		return selectionAdapter;
	}
}
