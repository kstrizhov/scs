package ru.bmstu.rk9.scs.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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

	public static class ChartFrame extends ChartComposite {

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

		double step = T / 1000;

		for (int i = 0; i <= 1000; i++) {
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
		XYSeries totalStockSeries = new XYSeries("Текущий уровень запасов склада");

		List<ResultItem> resultItems = DBHolder.getInstance().getWHNetDatabase().getResultsList();

		List<ResultItem> neededItems = new ArrayList<>();

		for (ResultItem i : resultItems)
			if (i.getWarehouse().getId() == whID)
				neededItems.add(i);

		double T = DBHolder.getInstance().getWHNetDatabase().getTimePeriod();

		double step = T / 10000;

		for (int i = 0; i <= 10000; i++) {
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

		XYSeries maxStockLvlSeries = new XYSeries("Максимальный уровень запасов");
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
		shell.setSize(800, 400);
		shell.setLayout(new FillLayout());

		createChartFrame(shell, dataset, chartTitle);
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
