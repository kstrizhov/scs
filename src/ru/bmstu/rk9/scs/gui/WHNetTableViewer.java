package ru.bmstu.rk9.scs.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import ru.bmstu.rk9.scs.lib.StringSymbolSetSearchTool;
import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;

public class WHNetTableViewer extends TableViewer {

	private String searchText;

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public WHNetTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void createColumns() {
		String[] titles = { "wh_id", "wh_name", "res_id", "res_name", "demand", "q0", "ts0", "d0" };
		int[] bounds = { 60, 100, 60, 100, 100, 100, 100, 100 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Integer.toString(item.getWarehouse().getId());
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ResultItem item = (ResultItem) cell.getElement();
				String cellText = item.getWarehouse().getName();
				cell.setText(cellText);
				if (searchText != null && searchText.length() > 0) {
					int intRangesCorrectSize[] = StringSymbolSetSearchTool.getSearchTermOccurrences(searchText,
							cellText);
					List<StyleRange> styleRange = new ArrayList<StyleRange>();
					for (int i = 0; i < intRangesCorrectSize.length / 2; i++) {
						int start = intRangesCorrectSize[i];
						int length = intRangesCorrectSize[++i];
						Color color = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
						StyleRange myStyledRange = new StyleRange(start, length, null, color);

						styleRange.add(myStyledRange);
					}
					cell.setStyleRanges(styleRange.toArray(new StyleRange[styleRange.size()]));
				} else {
					cell.setStyleRanges(null);
				}

				super.update(cell);
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Integer.toString(item.getResource().getId());
			}
		});

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ResultItem item = (ResultItem) cell.getElement();
				String cellText = item.getResource().getName();
				cell.setText(cellText);
				if (searchText != null && searchText.length() > 0) {
					int intRangesCorrectSize[] = StringSymbolSetSearchTool.getSearchTermOccurrences(searchText,
							cellText);
					List<StyleRange> styleRange = new ArrayList<StyleRange>();
					for (int i = 0; i < intRangesCorrectSize.length / 2; i++) {
						int start = intRangesCorrectSize[i];
						int length = intRangesCorrectSize[++i];
						Color color = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
						StyleRange myStyledRange = new StyleRange(start, length, null, color);

						styleRange.add(myStyledRange);
					}
					cell.setStyleRanges(styleRange.toArray(new StyleRange[styleRange.size()]));
				} else {
					cell.setStyleRanges(null);
				}

				super.update(cell);
			}
		});

		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Double.toString(item.getDemand());
			}
		});

		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Double.toString(item.getQ0());
			}
		});

		col = createTableViewerColumn(titles[6], bounds[6], 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Double.toString(item.getTs0());
			}
		});

		col = createTableViewerColumn(titles[7], bounds[7], 7);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultItem item = (ResultItem) element;
				return Double.toString(item.getD0());
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

	private WHNetTableViewer getViewer() {
		return this;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WHNetTableViewerComparator comparator = (WHNetTableViewerComparator) getViewer().getComparator();
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				getViewer().getTable().setSortDirection(dir);
				getViewer().refresh();
			}
		};
		return selectionAdapter;
	}
}
