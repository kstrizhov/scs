package ru.bmstu.rk9.scs.gui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.data.xy.XYSeries;

public class ChartTableViewer extends TableViewer {

	public ChartTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void createColumn() {
		String title = "Графики";
		int bound = 200;

		TableViewerColumn col = createTableViewerColumn(title, bound);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				XYSeries item = (XYSeries) element;
				return (String) item.getKey();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
}
