package ru.bmstu.rk9.scs.gui;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import Jama.Matrix;
import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.lib.ExcelParser;
import ru.bmstu.rk9.scs.lib.Scheduler;
import ru.bmstu.rk9.scs.tp.Base;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Producer;
import ru.bmstu.rk9.scs.tp.Solver;
import ru.bmstu.rk9.scs.tp.Solver.Plan;

public class Application {

	protected Shell shell;
	private Text enterEpsilonText;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Application window = new Application();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setText("Система управления запасами складского комплекса РЖД");
		shell.setSize(800, 600);
		shell.setLayout(new FormLayout());

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(0, 571);
		fd_tabFolder.right = new FormAttachment(0, 794);
		fd_tabFolder.top = new FormAttachment(0);
		fd_tabFolder.left = new FormAttachment(0);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setEnabled(false);

		TabItem roadMetalTabItem = new TabItem(tabFolder, SWT.NONE);
		roadMetalTabItem.setText("Управление поставками щебня");

		Composite roadMetalTabComposite = new Composite(tabFolder, SWT.NONE);
		roadMetalTabItem.setControl(roadMetalTabComposite);
		GridLayout roadMetalGridLayout = new GridLayout(2, false);
		roadMetalGridLayout.marginLeft = 10;
		roadMetalGridLayout.marginRight = 10;
		roadMetalTabComposite.setLayout(roadMetalGridLayout);

		Label loadProducersInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadProducersInfoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		loadProducersInfoLabel.setText("Загрузить информацию о карьерах:");

		Button loadProducersInfoButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadProducersInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open");
				fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				DBHolder.getInstance().getDatabase().setProducersList(ExcelParser.parseProducersExcelFile(selected));
			}
		});
		loadProducersInfoButton.setText("Загрузить [..]");

		Label loadConsumersInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadConsumersInfoLabel.setText("Загрузить информацию о точках потребления:");

		Button loadConsumersInfoButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadConsumersInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open");
				fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				DBHolder.getInstance().getDatabase().setConsumersList(ExcelParser.parseConsumersExcelFile(selected));
			}
		});
		loadConsumersInfoButton.setText("Загрузить [..]");

		Label loadBasesInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadBasesInfoLabel.setText("Загрузить информацию о перевалочных базах щебня:");

		Button loadBasesInfoButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadBasesInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open");
				fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				DBHolder.getInstance().getDatabase().setBasesList(ExcelParser.parseBasesExcelFile(selected));
			}
		});
		loadBasesInfoButton.setText("Загрузить [..]");

		Label loadProdsConsDistanceMatrixLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadProdsConsDistanceMatrixLabel.setText("Загрузить матрицу расстояний \"карьеры - точки потребления\":");

		Button loadProdsConsDistanceMatrixButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadProdsConsDistanceMatrixButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open");
				fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				DBHolder.getInstance().getDatabase()
						.setProdsConsDistanceMatrix(ExcelParser.parseProdConsDistanceMatrixExcelFile(selected));
			}
		});
		loadProdsConsDistanceMatrixButton.setText("Загрузить [..]");

		Label loadProdsBasesDistanceMatrixLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadProdsBasesDistanceMatrixLabel.setText("Загрузить матрицу расстояний \"карьеры - базы\":");

		Button loadProdsBasesDistanceMatrixButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadProdsBasesDistanceMatrixButton.setText("Загрузить [..]");

		Label loadBasesConsDistanceMatrixLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadBasesConsDistanceMatrixLabel.setText("Загрузить матрицу расстояний \"базы - точки потребления\":");

		Button loadBasesConsDistanceMatrixButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadBasesConsDistanceMatrixButton.setText("Загрузить [..]");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label enterEpsilonLabel = new Label(roadMetalTabComposite, SWT.NONE);
		enterEpsilonLabel.setEnabled(false);
		enterEpsilonLabel.setText("Введите значение:");
		new Label(roadMetalTabComposite, SWT.NONE);

		enterEpsilonText = new Text(roadMetalTabComposite, SWT.BORDER);
		enterEpsilonText.setEnabled(false);
		enterEpsilonText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Button enterEpsilonButton = new Button(roadMetalTabComposite, SWT.NONE);
		enterEpsilonButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double eps = Double.parseDouble(enterEpsilonText.getText());
				DBHolder.getInstance().getDatabase().setEps(eps);

				ArrayList<ConsumptionPoint> consumers = DBHolder.getInstance().getDatabase().getConsumersList();
				for (ConsumptionPoint c : consumers) {
					double currentConsumption = c.getConsumption();
					c.setConsumption(currentConsumption + eps);
				}

				ArrayList<Producer> producers = DBHolder.getInstance().getDatabase().getProducersList();
				int numOfProducers = producers.size();
				double lastProducersProduction = producers.get(numOfProducers - 1).getProduction();
				int numOfConsumers = consumers.size();
				producers.get(numOfProducers - 1).setProduction(lastProducersProduction + eps * numOfConsumers);

			}
		});
		enterEpsilonButton.setEnabled(false);
		enterEpsilonButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		enterEpsilonButton.setText("Задать");

		Button useEpsilonCheckButton = new Button(roadMetalTabComposite, SWT.CHECK);
		useEpsilonCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				if (btn.getSelection()) {
					enterEpsilonLabel.setEnabled(true);
					enterEpsilonText.setEnabled(true);
					enterEpsilonButton.setEnabled(true);
					DBHolder.getInstance().getDatabase().setEpsUsed(true);
				} else {
					enterEpsilonLabel.setEnabled(false);
					enterEpsilonText.setEnabled(false);
					enterEpsilonButton.setEnabled(false);
					DBHolder.getInstance().getDatabase().setEpsUsed(false);
				}
			}
		});
		useEpsilonCheckButton.setText("Использовать дополнительные возмущения для исключения вырожденности задачи");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Button solveButton = new Button(roadMetalTabComposite, SWT.NONE);
		solveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Producer> producersList = DBHolder.getInstance().getDatabase().getProducersList();
				List<ConsumptionPoint> consumersList = DBHolder.getInstance().getDatabase().getConsumersList();

				Matrix C0 = DBHolder.getInstance().getDatabase().getProdsConsDistanceMatrix();

				Matrix isolatedC0 = Solver.isolateTransportationProblem(producersList, consumersList, C0);

				System.out.println("C0 after isolation");
				C0.print(C0.getColumnDimension(), 2);

				Plan plan = Solver.createBasicPlan(producersList, consumersList);

				plan.X0.print(plan.X0.getColumnDimension(), 2);

				Matrix result;

				if (isolatedC0 == null)
					result = Solver.solve(plan, C0);
				else
					result = Solver.solve(plan, isolatedC0);
			}
		});
		solveButton.setText("РЕШИТЬ");
		new Label(roadMetalTabComposite, SWT.NONE);

		TabItem warehouseNetTabItem = new TabItem(tabFolder, SWT.NONE);
		warehouseNetTabItem.setText("Управление складской сетью");

		Composite warehouseNetTabComposite = new Composite(tabFolder, SWT.NONE);
		warehouseNetTabItem.setControl(warehouseNetTabComposite);
		warehouseNetTabComposite.setLayout(null);

		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		MenuItem fileSubMenu = new MenuItem(menuBar, SWT.CASCADE);
		fileSubMenu.setText("File");

		Menu fileMenu = new Menu(fileSubMenu);
		fileSubMenu.setMenu(fileMenu);

		MenuItem newInstanceMenuItem = new MenuItem(fileMenu, SWT.NONE);
		newInstanceMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance();
				tabFolder.setEnabled(true);
			}
		});
		newInstanceMenuItem.setText("New instance");

		MenuItem openMenuItem = new MenuItem(fileMenu, SWT.NONE);
		openMenuItem.setText("Open");

		MenuItem closeMenuItem = new MenuItem(fileMenu, SWT.NONE);
		closeMenuItem.setText("Close");

		MenuItem helpSubMenu = new MenuItem(menuBar, SWT.CASCADE);
		helpSubMenu.setText("Help");

		Menu helpMenu = new Menu(helpSubMenu);
		helpSubMenu.setMenu(helpMenu);

		MenuItem testMenuItem = new MenuItem(helpMenu, SWT.NONE);
		testMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ArrayList<Producer> list = DBHolder.getInstance().getDatabase().getProducersList();
				ArrayList<ConsumptionPoint> list2 = DBHolder.getInstance().getDatabase().getConsumersList();
				ArrayList<Base> list3 = DBHolder.getInstance().getDatabase().getBasesList();
				for (Producer p : list) {
					System.out.println("id:" + p.getId());
					System.out.println("name: " + p.getName());
					System.out.println("prod: " + p.getProduction());
					System.out.println("cost: " + p.getCostPerUnit());
				}
				for (ConsumptionPoint p : list2) {
					System.out.println("id:" + p.getId());
					System.out.println("name: " + p.getName());
					System.out.println("cons: " + p.getConsumption());
					for (Month m : p.getMonthsList())
						System.out.println("months: " + m);
				}
				for (Base b : list3) {
					System.out.println("id:" + b.getId());
					System.out.println("name: " + b.getName());
					System.out.println("volume: " + b.getVolume());
				}
				Matrix m = DBHolder.getInstance().getDatabase().getProdsConsDistanceMatrix();
				m.print(m.getColumnDimension(), 2);
			}
		});
		testMenuItem.setText("Test button");

		MenuItem aboutHelpMenuItem = new MenuItem(helpMenu, SWT.NONE);
		aboutHelpMenuItem.setText("About");

		MenuItem howToUseMenuItem = new MenuItem(helpMenu, SWT.NONE);
		howToUseMenuItem.setText("How to use");

		MenuItem solveTestMenuItem = new MenuItem(helpMenu, SWT.NONE);
		solveTestMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Scheduler scheduler = new Scheduler();
				scheduler.schedule();
			}
		});
		solveTestMenuItem.setText("SOLVE TEST");

	}
}
