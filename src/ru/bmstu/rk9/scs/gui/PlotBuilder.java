package ru.bmstu.rk9.scs.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class PlotBuilder {

	public static class PlotFrame extends ChartComposite {

		public PlotFrame(final Composite comp, final int style) {
			super(comp, style, null, ChartComposite.DEFAULT_WIDTH, ChartComposite.DEFAULT_HEIGHT, 0, 0,
					Integer.MAX_VALUE, Integer.MAX_VALUE, ChartComposite.DEFAULT_BUFFER_USED, true, true, true, true,
					true);
			addSWTListener(this);
		}
	}

	public static XYSeries createData(ResultItem item) {
		String whName = item.getWarehouse().getName();
		String resName = item.getResource().getName();
		XYSeries series = new XYSeries(whName + ": " + resName);

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

	public static XYSeriesCollection createDataset(List<ResultItem> items) {
		XYSeriesCollection dataset = new XYSeriesCollection();

		for (ResultItem i : items) {
			XYSeries data = createData(i);
			dataset.addSeries(data);
		}

		return dataset;
	}

	public static XYSeriesCollection createStockData(Warehouse warehouse) {
		String whName = warehouse.getName();
		int whID = warehouse.getId();
		XYSeries series = new XYSeries(whName);

		Map<Integer, ResultItem> itemMap = new HashMap<>();

		List<ResultItem> items = DBHolder.getInstance().getWHNetDatabase().getResultsList();

		for (ResultItem i : items)
			if (i.getWarehouse().getId() == whID)
				itemMap.put(i.getResource().getId(), i);

		double T = DBHolder.getInstance().getWHNetDatabase().getTimePeriod();

		double step = T / 10000;

		for (int i = 0; i <= 10000; i++) {
			double t = step * i;
			double q = 0;

			for (ResultItem item : itemMap.values()) {
				double r = item.getDemand() / T;
				double ts0 = item.getTs0();
				int n = (int) Math.floor(t / ts0);
				double q0 = item.getQ0();
				q += q0 - r * (t - n * ts0);
			}
			series.add(t, q);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		return dataset;
	}

	public static void drawChart(XYSeriesCollection dataset) {

		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		shell.setText("chart");
		shell.setSize(800, 400);
		shell.setLayout(new FillLayout());

		createPlot(shell, dataset);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static PlotFrame createPlot(Composite composite, XYSeriesCollection dataset) {

		PlotFrame frame = new PlotFrame(composite, SWT.NONE);

		final JFreeChart chart = ChartFactory.createXYLineChart("qqq", "sss", "ddd", dataset);
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
