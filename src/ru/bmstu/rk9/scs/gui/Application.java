package ru.bmstu.rk9.scs.gui;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
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
import org.jfree.data.xy.XYSeriesCollection;

import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.lib.TPDataParser;
import ru.bmstu.rk9.scs.lib.WHNetDataParser;
import ru.bmstu.rk9.scs.lib.WHNetDataWriter;
import ru.bmstu.rk9.scs.lib.WHNetDatabase;
import ru.bmstu.rk9.scs.lib.WHNetDatabase.SolveModelType;
import ru.bmstu.rk9.scs.tp.Scheduler;
import ru.bmstu.rk9.scs.whnet.Calculator;
import ru.bmstu.rk9.scs.whnet.Calculator.WHNetResultItem;
import ru.bmstu.rk9.scs.whnet.Warehouse;

public class Application {

	protected Shell shell;
	private Text setC3Text;
	private Text setC2Text;
	private Text setC1Text;
	private Text timePeriodText;
	private Table table;
	private Text filterText;
	private Text totalText;
	private Table tpScheduleTable;
	private Table tpStockTable;
	private Text setEpsText;
	private Text sumText;

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
		shell.setSize(1100, 660);
		shell.setLayout(new FormLayout());

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(0, 640);
		fd_tabFolder.right = new FormAttachment(0, 1090);
		fd_tabFolder.top = new FormAttachment(0);
		fd_tabFolder.left = new FormAttachment(0);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setEnabled(false);

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

		final Menu treePopupMenu = new Menu(tree);

		final MenuItem whResourcesStockChartMenuItem = new MenuItem(treePopupMenu, SWT.CASCADE);
		whResourcesStockChartMenuItem.setText("Построить графики запасов ресурсов");

		final MenuItem whTotalStockChartMenuItem = new MenuItem(treePopupMenu, SWT.CASCADE);
		whTotalStockChartMenuItem.setText("Построить график общего уровня запас склада");

		tree.setMenu(treePopupMenu);

		treePopupMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent e) {
				boolean enabled = false;

				if (tree.getSelection()[0].getData() instanceof Warehouse) {
					enabled = true;
				}

				whResourcesStockChartMenuItem.setEnabled(enabled);
				whTotalStockChartMenuItem.setEnabled(enabled);
			}

			@Override
			public void menuHidden(MenuEvent e) {
			}
		});

		whResourcesStockChartMenuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(tree.getSelection()[0].getData() instanceof Warehouse))
					return;

				final Warehouse wh = (Warehouse) tree.getSelection()[0].getData();

				XYSeriesCollection dataset = ChartBuilder.createWhResourcesStockData(wh);
				ChartBuilder.openChartFrame(dataset, "Уровни запасов ресурсов, поставляемых складом №" + wh.getId());
			}
		});

		whTotalStockChartMenuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(tree.getSelection()[0].getData() instanceof Warehouse))
					return;

				final Warehouse wh = (Warehouse) tree.getSelection()[0].getData();

				XYSeriesCollection dataset = ChartBuilder.createWhTotalStockData(wh);
				ChartBuilder.openChartFrame(dataset, "Общий уровень запасов склада №" + wh.getId());
			}
		});

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

		Button setTimePeriodButton = new Button(warehouseNetTabComposite, SWT.NONE);
		setTimePeriodButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		setTimePeriodButton.setText("Задать");
		setTimePeriodButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				double T = Double.parseDouble(timePeriodText.getText());
				DBHolder.getInstance().getWHNetDatabase().setTimePeriod(T);
			}
		});
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Label chooseFirstLvlModelLaybel = new Label(warehouseNetTabComposite, SWT.NONE);
		chooseFirstLvlModelLaybel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		chooseFirstLvlModelLaybel.setText("Используемая модель управления запасами");
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Composite radioBtnnGroupsComposite = new Composite(warehouseNetTabComposite, SWT.NONE);
		radioBtnnGroupsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 2));
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
			}
		});
		caclulcateWHNetButton.setText("Рассчитать");
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button reportButton = new Button(warehouseNetTabComposite, SWT.NONE);
		reportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Save");
				fileDialog.setFilterPath("/");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				HSSFWorkbook wb = WHNetDataWriter.createWorkbookFromTable(tableViewer);
				WHNetDataWriter.dumpWorkbookToAFile(wb, selected, shell);
			}
		});
		reportButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		reportButton.setText("Отчет");

		Label filterLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		filterLabel.setText("Фильтр:");

		filterText = new Text(warehouseNetTabComposite, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(warehouseNetTabComposite, SWT.NONE);
		new Label(warehouseNetTabComposite, SWT.NONE);

		Button dbClearButton = new Button(warehouseNetTabComposite, SWT.NONE);
		dbClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBHolder.getInstance().getWHNetDatabase().clear();
				tree.removeAll();
				tableViewer.getTable().removeAll();
			}
		});
		dbClearButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		dbClearButton.setText("Очистить БД");

		Label totalLabel = new Label(warehouseNetTabComposite, SWT.NONE);
		totalLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		totalLabel.setText("Затраты:");

		totalText = new Text(warehouseNetTabComposite, SWT.BORDER);
		totalText.setEditable(false);
		totalText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		TabItem roadMetalTabItem = new TabItem(tabFolder, SWT.NONE);
		roadMetalTabItem.setText("Управление поставками щебня");

		Composite roadMetalTabComposite = new Composite(tabFolder, SWT.NONE);
		roadMetalTabItem.setControl(roadMetalTabComposite);
		GridLayout roadMetalGridLayout = new GridLayout(6, false);
		roadMetalGridLayout.marginLeft = 10;
		roadMetalGridLayout.marginRight = 10;
		roadMetalTabComposite.setLayout(roadMetalGridLayout);

		Label loadProducersInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadProducersInfoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
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
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label loadConsumersInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadConsumersInfoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
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
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label loadBasesInfoLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadBasesInfoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
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
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label loadProdsConsDistanceMatrixLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadProdsConsDistanceMatrixLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
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
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label loadBasesConsDistanceMatrixLabel = new Label(roadMetalTabComposite, SWT.NONE);
		loadBasesConsDistanceMatrixLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		loadBasesConsDistanceMatrixLabel.setText("Загрузить матрицу расстояний \"базы - точки потребления\":");

		Button loadBasesConsDistanceMatrixButton = new Button(roadMetalTabComposite, SWT.NONE);
		loadBasesConsDistanceMatrixButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open");
				fileDialog.setFilterPath("/home/kirill/diplom_info/data");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				DBHolder.getInstance().getTPDatabase()
						.setBasesConsDistanceMatrix(TPDataParser.parseBasesConsDistanceMatrixExcelFile(selected));
			}
		});
		loadBasesConsDistanceMatrixButton.setText("Загрузить [..]");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label setEpsLabel = new Label(roadMetalTabComposite, SWT.NONE);
		setEpsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		setEpsLabel.setText("Задать новое значение epsilon:");

		setEpsText = new Text(roadMetalTabComposite, SWT.BORDER);
		setEpsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button setEpsButton = new Button(roadMetalTabComposite, SWT.NONE);
		setEpsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double eps = Double.parseDouble(setEpsText.getText());
				DBHolder.getInstance().getTPDatabase().setEps(eps);
			}
		});
		setEpsButton.setText("Задать");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label tpScheduleLabel = new Label(roadMetalTabComposite, SWT.NONE);
		tpScheduleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		tpScheduleLabel.setText("Календарный план перевозок");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Label basesStockLabel = new Label(roadMetalTabComposite, SWT.NONE);
		basesStockLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		basesStockLabel.setText("Запас щебня на перевалочных базах");
		new Label(roadMetalTabComposite, SWT.NONE);

		TPScheduleTableViewer tpScheduleTableViewer = new TPScheduleTableViewer(roadMetalTabComposite,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tpScheduleTable = tpScheduleTableViewer.getTable();
		GridData gd_tpScheduleTableViewer = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_tpScheduleTableViewer.widthHint = 730;
		gd_tpScheduleTableViewer.heightHint = 190;
		gd_tpScheduleTableViewer.horizontalSpan = 4;
		tpScheduleTable.setLayoutData(gd_tpScheduleTableViewer);
		tpScheduleTableViewer.createColumns();
		tpScheduleTable.setHeaderVisible(true);
		tpScheduleTable.setLinesVisible(true);
		tpScheduleTableViewer.setContentProvider(new ArrayContentProvider());

		TPStockTableViewer tpStockTableViewer = new TPStockTableViewer(roadMetalTabComposite,
				SWT.BORDER | SWT.FULL_SELECTION);
		tpStockTable = tpStockTableViewer.getTable();
		tpStockTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		tpStockTableViewer.createColumns();
		tpStockTable.setHeaderVisible(true);
		tpStockTable.setLinesVisible(true);
		tpStockTableViewer.setContentProvider(new ArrayContentProvider());
		tpStockTableViewer.setComparator(new TPStockViewerComparator());

		Button solveButton = new Button(roadMetalTabComposite, SWT.NONE);
		solveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Scheduler scheduler = new Scheduler(DBHolder.getInstance().getTPDatabase());
				scheduler.schedule();
				String total = Double.toString(scheduler.getTotal());
				sumText.setText(total);
				tpScheduleTableViewer.setInput(DBHolder.getInstance().getTPDatabase().getResultsList());
				tpScheduleTableViewer.refresh();
				tpStockTableViewer.setInput(DBHolder.getInstance().getTPDatabase().getBasesList());
				tpStockTableViewer.refresh();
			}
		});
		solveButton.setText("Расчет");
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Button writeScheduleReportButton = new Button(roadMetalTabComposite, SWT.NONE);
		writeScheduleReportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Save");
				fileDialog.setFilterPath("/");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				HSSFWorkbook wb = WHNetDataWriter.createWorkbookFromTable(tpScheduleTableViewer);
				WHNetDataWriter.dumpWorkbookToAFile(wb, selected, shell);
			}
		});
		GridData gd_scheduleReportBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_scheduleReportBtn.widthHint = 215;
		writeScheduleReportButton.setLayoutData(gd_scheduleReportBtn);
		writeScheduleReportButton.setText("Выгрузить план перевозок");
		new Label(roadMetalTabComposite, SWT.NONE);

		Label sumLabel = new Label(roadMetalTabComposite, SWT.NONE);
		sumLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		sumLabel.setText("Стоимость снабжения:");

		sumText = new Text(roadMetalTabComposite, SWT.BORDER);
		GridData gd_sumText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_sumText.widthHint = 130;
		sumText.setLayoutData(gd_sumText);
		sumText.setEditable(false);
		new Label(roadMetalTabComposite, SWT.NONE);
		new Label(roadMetalTabComposite, SWT.NONE);

		Button writeStockReportButton = new Button(roadMetalTabComposite, SWT.NONE);
		writeStockReportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Save");
				fileDialog.setFilterPath("/");
				String[] filterExtensions = { "*.xls" };
				fileDialog.setFilterExtensions(filterExtensions);
				String selected = fileDialog.open();
				HSSFWorkbook wb = WHNetDataWriter.createWorkbookFromTable(tpStockTableViewer);
				WHNetDataWriter.dumpWorkbookToAFile(wb, selected, shell);
			}
		});
		GridData gd_stockReportBtn = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_stockReportBtn.widthHint = 215;
		writeStockReportButton.setLayoutData(gd_stockReportBtn);
		writeStockReportButton.setText("Выгрузить отчет о запасах");
		new Label(roadMetalTabComposite, SWT.NONE);

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
				WHNetDataParser.readNormalDistributionValues("./data/normal_distribution_values.xls");
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

				Warehouse w = DBHolder.getInstance().getWHNetDatabase().getWHNetMap().get(1);

				ChartBuilder.openChartFrame(ChartBuilder.createWhResourcesStockData(w), "test");
			}
		});
		testMenuItem.setText("Test button");

		MenuItem aboutHelpMenuItem = new MenuItem(helpMenu, SWT.NONE);
		aboutHelpMenuItem.setText("About");

		MenuItem howToUseMenuItem = new MenuItem(helpMenu, SWT.NONE);
		howToUseMenuItem.setText("How to use");

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
			WHNetResultItem r = (WHNetResultItem) i.getData();
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
