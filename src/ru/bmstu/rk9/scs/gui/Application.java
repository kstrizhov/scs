package ru.bmstu.rk9.scs.gui;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import Jama.Matrix;
import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.lib.TPDataParser;
import ru.bmstu.rk9.scs.lib.WHNetDataParser;
import ru.bmstu.rk9.scs.lib.WHNetDatabase;
import ru.bmstu.rk9.scs.lib.WHNetDatabase.SolveModelType;
import ru.bmstu.rk9.scs.tp.Base;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Producer;
import ru.bmstu.rk9.scs.tp.Solver;
import ru.bmstu.rk9.scs.tp.Scheduler;
import ru.bmstu.rk9.scs.whnet.Calculator;
import ru.bmstu.rk9.scs.whnet.Calculator.ResultItem;
import ru.bmstu.rk9.scs.whnet.Warehouse;

public class Application {

	protected Shell shell;
	private Text enterEpsilonText;
	private Text setC3Text;
	private Text setC2Text;
	private Text setC1Text;
	private Text timePeriodText;
	private Table table;
	private Text filterText;
	private Text totalText;

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
		shell.setSize(1100, 640);
		shell.setLayout(new FormLayout());

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(0, 630);
		fd_tabFolder.right = new FormAttachment(0, 1090);
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
				DBHolder.getInstance().getTPDatabase().setProducersList(TPDataParser.parseProducersExcelFile(selected));
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
				DBHolder.getInstance().getTPDatabase().setConsumersList(TPDataParser.parseConsumersExcelFile(selected));
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
				DBHolder.getInstance().getTPDatabase().setBasesList(TPDataParser.parseBasesExcelFile(selected));
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
				DBHolder.getInstance().getTPDatabase()
						.setProdsConsDistanceMatrix(TPDataParser.parseProdConsDistanceMatrixExcelFile(selected));
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
				DBHolder.getInstance().getTPDatabase().setEps(eps);
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
					DBHolder.getInstance().getTPDatabase().setEpsUsed(true);
				} else {
					enterEpsilonLabel.setEnabled(false);
					enterEpsilonText.setEnabled(false);
					enterEpsilonButton.setEnabled(false);
					DBHolder.getInstance().getTPDatabase().setEpsUsed(false);
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
				List<Producer> producersList = DBHolder.getInstance().getTPDatabase().getProducersList();
				List<ConsumptionPoint> consumersList = DBHolder.getInstance().getTPDatabase().getConsumersList();

				Matrix C0 = DBHolder.getInstance().getTPDatabase().getProdsConsDistanceMatrix();

				Matrix solution = Solver.solve(producersList, consumersList, C0);
			}
		});
		solveButton.setText("РЕШИТЬ");
		new Label(roadMetalTabComposite, SWT.NONE);

		TabItem warehouseNetTabItem = new TabItem(tabFolder, SWT.NONE);
		warehouseNetTabItem.setText("Управление складской сетью");

		Composite warehouseNetTabComposite = new Composite(tabFolder, SWT.NONE);
		warehouseNetTabItem.setControl(warehouseNetTabComposite);
		GridLayout warehouseNetGridLayout = new GridLayout(5, false);
		warehouseNetGridLayout.marginLeft = 10;
		warehouseNetGridLayout.marginRight = 10;
		warehouseNetGridLayout.horizontalSpacing = 8;
		warehouseNetTabComposite.setLayout(warehouseNetGridLayout);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label treeLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		treeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		treeLabel.setText("Структура складской сети");

		Label lblLoadwhnetlabel = new Label(warehouseNetTabComposite, SWT.NONE);
		lblLoadwhnetlabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLoadwhnetlabel.setText("Зарузить информацию о складской сети");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button loadWHNetButton = new Button(warehouseNetTabComposite, SWT.NONE);
		loadWHNetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loadWHNetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				// fileDialog.setText("Open");
				// fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				// String[] filterExtensions = { "*.xls" };
				// fileDialog.setFilterExtensions(filterExtensions);
				// String selected = fileDialog.open();
				String selected = "/home/kirill/diplom_info/data/wh_net1/whnet.xls";
				WHNetDataParser.parseWarehousesData(selected);
				DBHolder.getInstance().getWHNetDatabase().setChanged();
				DBHolder.getInstance().getWHNetDatabase().notifyObservers();
			}
		});
		loadWHNetButton.setText("Загрузить [..]");

		WHNetTreeViewer treeViewer = new WHNetTreeViewer(warehouseNetTabComposite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		Tree tree = treeViewer.getTree();
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 5);
		gd_tree.widthHint = 346;
		tree.setLayoutData(gd_tree);
		tree.setLinesVisible(true);
		DBHolder.getInstance().getWHNetDatabase().addObserver(treeViewer);
		treeViewer.setContentProvider(new WHNetTreeContentProvider());
		treeViewer.setLabelProvider(new WHNetTreeLabelProvider());

		Label loadWHNetConsumersInfoLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		loadWHNetConsumersInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		loadWHNetConsumersInfoLabel.setText("Загрузить информацию о потребителях");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button loadWHNetConsumersInfoButton = new Button(warehouseNetTabComposite, SWT.NONE);
		loadWHNetConsumersInfoButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loadWHNetConsumersInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				// fileDialog.setText("Open");
				// fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				// String[] filterExtensions = { "*.xls" };
				// fileDialog.setFilterExtensions(filterExtensions);
				// String selected = fileDialog.open();
				String selected = "/home/kirill/diplom_info/data/wh_net1/consumers.xls";
				WHNetDataParser.parseConsumersData(selected);
				DBHolder.getInstance().getWHNetDatabase().setChanged();
				DBHolder.getInstance().getWHNetDatabase().notifyObservers();
			}
		});
		loadWHNetConsumersInfoButton.setText("Загрузить [..]");

		Label loadTasksInfoLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		loadTasksInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		loadTasksInfoLabel.setText("Загрузить информацию о проводимых работах потребителей");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button loadTasksInfoButton = new Button(warehouseNetTabComposite, SWT.NONE);
		loadTasksInfoButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loadTasksInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				// fileDialog.setText("Open");
				// fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				// String[] filterExtensions = { "*.xls" };
				// fileDialog.setFilterExtensions(filterExtensions);
				// String selected = fileDialog.open();
				String selected = "/home/kirill/diplom_info/data/wh_net1/task_freq.xls";
				WHNetDataParser.parseTasksFrequenciesData(selected);
				DBHolder.getInstance().getWHNetDatabase().setChanged();
				DBHolder.getInstance().getWHNetDatabase().notifyObservers();
			}
		});
		loadTasksInfoButton.setText("Загрузить [..]");

		Label loadTasksNormsInfoLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		loadTasksNormsInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		loadTasksNormsInfoLabel.setText("Загрузить нормы материалов для проводимых работ");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button loadTasksNormsButton = new Button(warehouseNetTabComposite, SWT.NONE);
		loadTasksNormsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loadTasksNormsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				// fileDialog.setText("Open");
				// fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				// String[] filterExtensions = { "*.xls" };
				// fileDialog.setFilterExtensions(filterExtensions);
				// String selected = fileDialog.open();
				String selected = "/home/kirill/diplom_info/data/wh_net1/res_norms.xls";
				WHNetDataParser.parseTasksResourceNormsData(selected);
				DBHolder.getInstance().getWHNetDatabase().setChanged();
				DBHolder.getInstance().getWHNetDatabase().notifyObservers();
			}
		});
		loadTasksNormsButton.setText("Загрузить [..]");

		Label loadResourcesInfoLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		loadResourcesInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		loadResourcesInfoLabel.setText("Загрузить информацию о поставляемых материалах");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button loadResourcesInfoButton = new Button(warehouseNetTabComposite, SWT.NONE);
		loadResourcesInfoButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loadResourcesInfoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				// fileDialog.setText("Open");
				// fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				// String[] filterExtensions = { "*.xls" };
				// fileDialog.setFilterExtensions(filterExtensions);
				// String selected = fileDialog.open();
				String selected = "/home/kirill/diplom_info/data/wh_net1/resources.xls";
				WHNetDataParser.parseResourcesData(selected);
				DBHolder.getInstance().getWHNetDatabase().setChanged();
				DBHolder.getInstance().getWHNetDatabase().notifyObservers();
			}
		});
		loadResourcesInfoButton.setText("Загрузить [..]");

		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label resultsLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		resultsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		resultsLabel.setText("Результаты расчетов");

		Label setC1Label = new Label(warehouseNetTabComposite, SWT.NONE);
		setC1Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		setC1Label.setText("Задать стоимость хранения продукции на складе 1 уровня");

		setC1Text = new Text(warehouseNetTabComposite, SWT.BORDER);
		setC1Text.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Button setC1Button = new Button(warehouseNetTabComposite, SWT.NONE);
		setC1Button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		setC1Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double c1 = Double.parseDouble(setC1Text.getText());
				DBHolder.getInstance().getWHNetDatabase().setC1(c1);
			}
		});
		setC1Button.setText("Задать");

		WHNetTableViewer tableViewer = new WHNetTableViewer(warehouseNetTabComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 8);
		gd_table.widthHint = 361;
		table.setLayoutData(gd_table);
		tableViewer.createColumns();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setComparator(new WHNetTableViewerComparator());
		tableViewer.addFilter(new WHNetTableViewerFilter());

		Label setC2Label = new Label(warehouseNetTabComposite, SWT.NONE);
		setC2Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		setC2Label.setText("Задать стоимость хранения продукции на складе 2 уровня");

		setC2Text = new Text(warehouseNetTabComposite, SWT.BORDER);
		setC2Text.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Button setC2Button = new Button(warehouseNetTabComposite, SWT.NONE);
		setC2Button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		setC2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double c2 = Double.parseDouble(setC2Text.getText());
				DBHolder.getInstance().getWHNetDatabase().setC2(c2);
			}
		});
		setC2Button.setText("Задать");

		Label setC3Label = new Label(warehouseNetTabComposite, SWT.NONE);
		setC3Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		setC3Label.setText("Задать стоимость хранения продукции на складе 3 уровня");

		setC3Text = new Text(warehouseNetTabComposite, SWT.BORDER);
		setC3Text.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Button setC3Button = new Button(warehouseNetTabComposite, SWT.NONE);
		setC3Button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		setC3Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double c3 = Double.parseDouble(setC3Text.getText());
				DBHolder.getInstance().getWHNetDatabase().setC3(c3);
			}
		});
		setC3Button.setText("Задать");

		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label timePeriodLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		timePeriodLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		timePeriodLabel.setText("Период времени T, мес.");

		timePeriodText = new Text(warehouseNetTabComposite, SWT.BORDER);
		timePeriodText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Button btnNewButton = new Button(warehouseNetTabComposite, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton.setText("Задать");
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label chooseFirstLvlModelLaybel = new Label(warehouseNetTabComposite, SWT.NONE);
		chooseFirstLvlModelLaybel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		chooseFirstLvlModelLaybel.setText("Используемая модель управления запасами");
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Composite radioBtnnGroupsComposite = new Composite(warehouseNetTabComposite, SWT.NONE);
		radioBtnnGroupsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		radioBtnnGroupsComposite.setLayout(new GridLayout(3, false));

		Group grpI = new Group(radioBtnnGroupsComposite, SWT.NONE);
		grpI.setText(" I уровень");
		grpI.setBounds(0, 0, 66, 66);
		grpI.setLayout(new GridLayout(1, false));

		Button firstLvlOneProdModelButton = new Button(grpI, SWT.RADIO);
		firstLvlOneProdModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setFirstLvlSolveModelType(SolveModelType.SINGLEPRODUCT);
			}
		});
		firstLvlOneProdModelButton.setSelection(true);
		firstLvlOneProdModelButton.setBounds(0, 0, 112, 22);
		firstLvlOneProdModelButton.setText("Однопродукт.");

		Button firstLvlMultiprodModelButton = new Button(grpI, SWT.RADIO);
		firstLvlMultiprodModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setFirstLvlSolveModelType(SolveModelType.MULTIPRODUCT);
			}
		});
		firstLvlMultiprodModelButton.setBounds(0, 0, 112, 22);
		firstLvlMultiprodModelButton.setText("Многопродукт.");

		Group grpII = new Group(radioBtnnGroupsComposite, SWT.NONE);
		grpII.setText(" II уровень");
		grpII.setBounds(0, 0, 66, 66);
		grpII.setLayout(new GridLayout(1, false));

		Button secondLvlOneProdModelButton = new Button(grpII, SWT.RADIO);
		secondLvlOneProdModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setSecondLvlSolveModelType(SolveModelType.SINGLEPRODUCT);
			}
		});
		secondLvlOneProdModelButton.setSelection(true);
		secondLvlOneProdModelButton.setBounds(0, 0, 112, 22);
		secondLvlOneProdModelButton.setText("Однопродукт.");

		Button secondLvlMultiprodModelButton = new Button(grpII, SWT.RADIO);
		secondLvlMultiprodModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setSecondLvlSolveModelType(SolveModelType.MULTIPRODUCT);
			}
		});
		secondLvlMultiprodModelButton.setBounds(0, 0, 112, 22);
		secondLvlMultiprodModelButton.setText("Многопродукт.");

		Group grpIII = new Group(radioBtnnGroupsComposite, SWT.NONE);
		grpIII.setText(" III уровень");
		grpIII.setBounds(0, 0, 66, 66);
		grpIII.setLayout(new GridLayout(1, false));

		Button thirdLvlOneProdModelButton = new Button(grpIII, SWT.RADIO);
		thirdLvlOneProdModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setThirdLvlSolveModelType(SolveModelType.SINGLEPRODUCT);
			}
		});
		thirdLvlOneProdModelButton.setSelection(true);
		thirdLvlOneProdModelButton.setBounds(0, 0, 112, 22);
		thirdLvlOneProdModelButton.setText("Однопродукт.");

		Button thirdLvlMultiprodModelButton = new Button(grpIII, SWT.RADIO);
		thirdLvlMultiprodModelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().setThirdLvlSolveModelType(SolveModelType.MULTIPRODUCT);
			}
		});
		thirdLvlMultiprodModelButton.setBounds(0, 0, 112, 22);
		thirdLvlMultiprodModelButton.setText("Многопродукт.");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button caclulcateWHNetButton = new Button(warehouseNetTabComposite, SWT.NONE);
		caclulcateWHNetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		caclulcateWHNetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WHNetDatabase db = DBHolder.getInstance().getWHNetDatabase();
				Calculator.calculateWHNet(db);
				treeViewer.refresh();
				tableViewer.setInput(DBHolder.getInstance().getWHNetDatabase().getResultsList());
				tableViewer.refresh();

				double total = calculateTotal(tableViewer);
				totalText.setText(Double.toString(total));

				db.clear();
			}
		});
		caclulcateWHNetButton.setText("Рассчитать");
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label filterLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		filterLabel.setText("Фильтр:");

		filterText = new Text(warehouseNetTabComposite, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label totalLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		totalLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		totalLabel.setText("Затраты:");

		totalText = new Text(warehouseNetTabComposite, SWT.BORDER);
		totalText.setEditable(false);
		totalText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				WHNetTableViewerFilter filter = (WHNetTableViewerFilter) tableViewer.getFilters()[0];
				filter.setSearchText(filterText.getText());
				tableViewer.setSearchText(filterText.getText());
				tableViewer.refresh();
				double total = calculateTotal(tableViewer);
				totalText.setText(Double.toString(total));
			}
		});

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
				ArrayList<Producer> list = DBHolder.getInstance().getTPDatabase().getProducersList();
				ArrayList<ConsumptionPoint> list2 = DBHolder.getInstance().getTPDatabase().getConsumersList();
				ArrayList<Base> list3 = DBHolder.getInstance().getTPDatabase().getBasesList();
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
				Matrix m = DBHolder.getInstance().getTPDatabase().getProdsConsDistanceMatrix();
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
				Scheduler scheduler = new Scheduler(DBHolder.getInstance().getTPDatabase());
				scheduler.schedule();
			}
		});
		solveTestMenuItem.setText("SOLVE TEST");

		MenuItem loadNetDataMenuItem = new MenuItem(helpMenu, SWT.NONE);
		loadNetDataMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// String s1 = "/home/kirill/diplom_info/data/wh_net/whnet.xls";
				// WHNetDataParser.parseWarehousesData(s1);
				// String s2 = "/home/kirill/diplom_info/data/wh_net/consumers.xls";
				// WHNetDataParser.parseConsumersData(s2);
				// String s3 = "/home/kirill/diplom_info/data/wh_net/task_freq.xls";
				// WHNetDataParser.parseTasksFrequenciesData(s3);
				// String s4 = "/home/kirill/diplom_info/data/wh_net/res_norms.xls";
				// WHNetDataParser.parseTasksResourceNormsData(s4);
				// String s5 = "/home/kirill/diplom_info/data/wh_net/resources.xls";
				// WHNetDataParser.parseResourcesData(s5);
				// //
				// String s1 = "/home/kirill/diplom_info/data/wh_net1/whnet.xls";
				// WHNetDataParser.parseWarehousesData(s1);
				// String s2 = "/home/kirill/diplom_info/data/wh_net1/consumers.xls";
				// WHNetDataParser.parseConsumersData(s2);
				// String s3 = "/home/kirill/diplom_info/data/wh_net1/task_freq.xls";
				// WHNetDataParser.parseTasksFrequenciesData(s3);
				// String s4 = "/home/kirill/diplom_info/data/wh_net1/res_norms.xls";
				// WHNetDataParser.parseTasksResourceNormsData(s4);
				// String s5 = "/home/kirill/diplom_info/data/wh_net1/resources.xls";
				// WHNetDataParser.parseResourcesData(s5);

				String s1 = "/home/kirill/diplom_info/data/data_whnet/whnet.xls";
				WHNetDataParser.parseWarehousesData(s1);
				String s2 = "/home/kirill/diplom_info/data/data_whnet/consumers.xls";
				WHNetDataParser.parseConsumersData(s2);
				String s3 = "/home/kirill/diplom_info/data/data_whnet/task_freq.xls";
				WHNetDataParser.parseTasksFrequenciesData(s3);
				String s4 = "/home/kirill/diplom_info/data/data_whnet/res_norms.xls";
				WHNetDataParser.parseTasksResourceNormsData(s4);
				String s5 = "/home/kirill/diplom_info/data/data_whnet/resources.xls";
				WHNetDataParser.parseResourcesData(s5);

				treeViewer.setInput(getInitalInput());
				treeViewer.expandAll();
			}
		});
		loadNetDataMenuItem.setText("LOAD NET DATA");

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

	private double calculateTotal(WHNetTableViewer viewer) {

		double total = 0;

		Table table = viewer.getTable();
		TableItem[] tableItems = table.getItems();

		for (TableItem i : tableItems) {
			ResultItem r = (ResultItem) i.getData();
			switch (r.getType()) {
			case SINGLE:
				total += r.getD0();
				break;
			case MULTI:
				total += r.getD0() / r.getNumOfProductsInSupply();
				break;
			}
		}

		return total;
	}
}
