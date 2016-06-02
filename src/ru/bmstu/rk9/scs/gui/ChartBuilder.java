package ru.bmstu.rk9.scs.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;
import ru.bmstu.rk9.scs.whnet.Warehouse;

public class ChartBuilder {

	private static final int divisionValue = 10000;

	public static class ChartFrame extends ChartComposite {

		XYSeriesCollection dataset;

		public ChartFrame(final Composite comp, final int style) {
			super(comp, style, null, ChartComposite.DEFAULT_WIDTH, ChartComposite.DEFAULT_HEIGHT, 0, 0,
					Integer.MAX_VALUE, Integer.MAX_VALUE, ChartComposite.DEFAULT_BUFFER_USED, true, true, true, true,
					true);
			addSWTListener(this);
		}
	}

	private static XYSeries createResouceStockData(ResultItem item) {
		String resName = item.getResource().getName();
		XYSeries series = new XYSeries(resName);

		double T = DBHolder.getInstance().getWHNetDatabase().getTimePeriod();

		double step = T / divisionValue;

		for (int i = 0; i <= divisionValue; i++) {
			double t = step * i;
			double r = item.getDemand() / T;
			double ts0 = item.getTs0();
			int n = (int) Math.floor(t / ts0);
			double q0 = item.getQ0();
			double q = q0 - r * (t - n * ts0);
			series.add(t, q);
		}

		return series;
	}

	public static XYSeriesCollection createWhResourcesStockData(Warehouse warehouse) {
		int whID = warehouse.getId();
		XYSeriesCollection dataset = new XYSeriesCollection();

		List<ResultItem> resultItems = DBHolder.getInstance().getWHNetDatabase().getResultsList();

		List<ResultItem> neededItems = new ArrayList<>();

		for (ResultItem i : resultItems)
			if (i.getWarehouse().getId() == whID)
				neededItems.add(i);

		for (ResultItem i : neededItems) {
			XYSeries data = createResouceStockData(i);
			dataset.addSeries(data);
		}

		return dataset;
	}

	public static XYSeriesCollection createWhTotalStockData(Warehouse warehouse) {
		int whID = warehouse.getId();
		XYSeries totalStockSeries = new XYSeries("Текущий уровень");

		List<ResultItem> resultItems = DBHolder.getInstance().getWHNetDatabase().getResultsList();

		List<ResultItem> neededItems = new ArrayList<>();

		for (ResultItem i : resultItems)
			if (i.getWarehouse().getId() == whID)
				neededItems.add(i);

		double T = DBHolder.getInstance().getWHNetDatabase().getTimePeriod();

		double step = T / divisionValue;

		for (int i = 0; i <= divisionValue; i++) {
			double t = step * i;
			double q = 0;

			for (ResultItem item : neededItems) {
				double r = item.getDemand() / T;
				double ts0 = item.getTs0();
				int n = (int) Math.floor(t / ts0);
				double q0 = item.getQ0();
				q += q0 - r * (t - n * ts0);
			}
			totalStockSeries.add(t, q);
		}

		XYSeries maxStockLvlSeries = new XYSeries("Макс. уровень");
		maxStockLvlSeries.add(0.0, warehouse.getVolume());
		maxStockLvlSeries.add(T, warehouse.getVolume());

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(totalStockSeries);
		dataset.addSeries(maxStockLvlSeries);

		return dataset;
	}

	public static void openChartFrame(XYSeriesCollection dataset, String chartTitle) {

		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		shell.setText("chart");
		shell.setSize(900, 500);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		shell.setLayout(layout);

		ChartFrame frame = createChartFrame(shell, dataset, chartTitle);
		GridData gd_frame = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		frame.setLayoutData(gd_frame);

		ChartTableViewer viewer = new ChartTableViewer(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		Table table = viewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd_table.widthHint = 200;
		table.setLayoutData(gd_table);
		viewer.createColumn();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				XYSeries series = (XYSeries) item.getData();
				XYSeriesCollection dataset = (XYSeriesCollection) frame.getChart().getXYPlot().getDataset();
				if (event.detail == SWT.CHECK)
					if (!item.getChecked()) {
						dataset.removeSeries(series);
					} else {
						dataset.addSeries(series);
					}
			}
		});

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(frame.dataset.getSeries());

		TableItem[] items = table.getItems();
		for (TableItem i : items)
			i.setChecked(true);

		viewer.refresh();

		Button selectAllButton = new Button(shell, SWT.NONE);
		selectAllButton.setText("Выбрать все");
		GridData gd_selectAll = new GridData(SWT.FILL, SWT.FILL, false, false);
		selectAllButton.setLayoutData(gd_selectAll);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();

				for (TableItem i : items) {
					if (!i.getChecked()) {
						i.setChecked(true);
						Event event = new Event();
						event.item = i;
						event.detail = SWT.CHECK;
						table.notifyListeners(SWT.Selection, event);
					}
				}
			}
		});

		Button deselectAllButton = new Button(shell, SWT.NONE);
		deselectAllButton.setText("Отменить все");
		GridData gd_deselectAll = new GridData(SWT.FILL, SWT.FILL, false, false);
		deselectAllButton.setLayoutData(gd_deselectAll);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getItems();

				for (TableItem i : items) {
					if (i.getChecked()) {
						i.setChecked(false);
						Event event = new Event();
						event.item = i;
						event.detail = SWT.CHECK;
						table.notifyListeners(SWT.Selection, event);
					}
				}
			}
		});

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static ChartFrame createChartFrame(Composite composite, XYSeriesCollection dataset, String chartTitle) {

		ChartFrame frame = new ChartFrame(composite, SWT.NONE);

		frame.dataset = dataset;

		final JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "Единиц продукции, шт.", "Время, мес.",
				dataset);
		chart.setBackgroundPaint(Color.white);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setStroke(new BasicStroke(2));

		double T = DBHolder.getInstance().getWHNetDatabase().getTimePeriod();

		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setRange(0.0, T);

		frame.setChart(chart);
		return frame;
	}
}
