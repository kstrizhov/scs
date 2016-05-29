package ru.bmstu.rk9.scs.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Jama.Matrix;
import ru.bmstu.rk9.scs.whnet.Consumer;
import ru.bmstu.rk9.scs.whnet.Resource;
import ru.bmstu.rk9.scs.whnet.Supplier;
import ru.bmstu.rk9.scs.whnet.Task;
import ru.bmstu.rk9.scs.whnet.Warehouse;
import ru.bmstu.rk9.scs.whnet.Warehouse.WHLevel;

public class WHNetDataParser {

	private final static int initialIndex = -1;
	private final static int neededSheetIndex = 0;
	private final static int noParentWHID = 0;

	public static void parseWarehousesData(String filePath) {

		Map<Integer, Warehouse> whNetMap = new HashMap<Integer, Warehouse>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int volumeColumnIndex = initialIndex;
			int parentIdColumnIndex = initialIndex;
			int csIdColumnIndex = initialIndex;

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();

			while (headRowIterator.hasNext()) {
				Cell cell = headRowIterator.next();
				if (cell.getStringCellValue().equals("id"))
					idColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("name"))
					nameColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("volume"))
					volumeColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("parent_id"))
					parentIdColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("cs"))
					csIdColumnIndex = cell.getColumnIndex();
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					double volume = row.getCell(volumeColumnIndex).getNumericCellValue();
					int parentID = (int) row.getCell(parentIdColumnIndex).getNumericCellValue();
					double Cs = row.getCell(csIdColumnIndex).getNumericCellValue();
					if (parentID == noParentWHID)
						whNetMap.put(id, new Warehouse(id, name, volume, null, Cs));
					else
						whNetMap.put(id, new Warehouse(id, name, volume, whNetMap.get(parentID), Cs));
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
					break;
				}
			}
			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setChildren(whNetMap);

		setWHLevels(whNetMap);

		for (Warehouse w : whNetMap.values())
			switch (w.getLevel()) {
			case FIRST:
				DBHolder.getInstance().getWHNetDatabase().firstLevelWarehousesIDList.add(w.getId());
				break;
			case SECOND:
				DBHolder.getInstance().getWHNetDatabase().secondLevelWarehousesIDList.add(w.getId());
				break;
			case THIRD:
				DBHolder.getInstance().getWHNetDatabase().thirdLevelWarehousesIDList.add(w.getId());
				break;
			}
		DBHolder.getInstance().getWHNetDatabase().whNetMap = whNetMap;
	}

	private static void setChildren(Map<Integer, Warehouse> whNetMap) {
		for (Warehouse w : whNetMap.values()) {
			if (w.getParent() == null)
				continue;
			Warehouse parent = whNetMap.get(w.getParent().getId());
			parent.getChildren().put(w.getId(), w);
		}
	}

	private static void setWHLevels(Map<Integer, Warehouse> whNetMap) {
		for (Warehouse w : whNetMap.values()) {
			if (w.getParent() == null)
				w.setLevel(WHLevel.FIRST);
			else if ((w.getParent() != null) && !w.getChildren().isEmpty())
				w.setLevel(WHLevel.SECOND);
			else
				w.setLevel(WHLevel.THIRD);
		}
	}

	public static void parseConsumersData(String filePath) {

		Map<Integer, Warehouse> whNetMap = DBHolder.getInstance().getWHNetDatabase().whNetMap;
		Map<Integer, Consumer> consumersMap = new HashMap<Integer, Consumer>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int whIDColumnIndex = initialIndex;

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();

			while (headRowIterator.hasNext()) {
				Cell cell = headRowIterator.next();
				if (cell.getStringCellValue().equals("id"))
					idColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("name"))
					nameColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("wh_id"))
					whIDColumnIndex = cell.getColumnIndex();
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					int warehouseID = (int) row.getCell(whIDColumnIndex).getNumericCellValue();
					consumersMap.put(id, new Consumer(id, name, whNetMap.get(warehouseID)));
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
					break;
				}
			}
			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Consumer c : consumersMap.values()) {
			Warehouse w = c.getSupplyingWarehouse();
			w.getConsumersList().add(c);
		}

		DBHolder.getInstance().getWHNetDatabase().consumersMap = consumersMap;
	}

	public static void parseTasksFrequenciesData(String filePath) {

		int numOfConsumers = DBHolder.getInstance().getWHNetDatabase().consumersMap.size();
		int numOfTasks = 0;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();
			headRowIterator.next(); // fist cell is empty

			while (headRowIterator.hasNext()) {
				headRowIterator.next();
				numOfTasks++;
			}

			Map<Integer, Consumer> consumersMap = DBHolder.getInstance().getWHNetDatabase().consumersMap;

			for (int i = 1; i <= numOfConsumers; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j <= numOfTasks; j++) {
					double taskFrequency = row.getCell(j).getNumericCellValue();
					Task task = new Task(j, "task" + j);
					consumersMap.get(i).getTasksMap().put(task.getId(), task);
					consumersMap.get(i).getTasksFreqMap().put(task.getId(), taskFrequency);
					task.setConsumer(consumersMap.get(i));
				}
			}

			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseTasksResourceNormsData(String filePath) {

		Map<Integer, Consumer> consumersMap = DBHolder.getInstance().getWHNetDatabase().consumersMap;
		Map<Integer, Resource> resourcesMap = DBHolder.getInstance().getWHNetDatabase().resourcesMap;
		int numOfResources = 0;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();
			headRowIterator.next(); // fist cell is empty

			while (headRowIterator.hasNext()) {
				headRowIterator.next();
				numOfResources++;
			}

			for (Consumer c : consumersMap.values()) {

				Map<Integer, Task> tasksMap = c.getTasksMap();
				int numOfTasks = tasksMap.size();

				for (int i = 1; i <= numOfTasks; i++) {
					Row row = sheet.getRow(i);
					for (int j = 1; j <= numOfResources; j++) {
						double needsThisResource = row.getCell(j).getNumericCellValue();
						Resource resource = new Resource(j);
						tasksMap.get(i).getResourceNorms().put(resource.getId(), needsThisResource);
						tasksMap.get(i).getResourcesMap().put(resource.getId(), resource);
						resource.setTask(tasksMap.get(i));
						if (!resourcesMap.containsKey(resource.getId()))
							resourcesMap.put(resource.getId(), resource);
					}
				}
			}

			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseResourcesData(String filePath) {

		Map<Integer, Supplier> suppliersMap = new HashMap<>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int volumePerUnitColumnIndex = initialIndex;
			int supplierIDColumnIndex = initialIndex;
			int csIdColumnIndex = initialIndex;

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();

			while (headRowIterator.hasNext()) {
				Cell cell = headRowIterator.next();
				if (cell.getStringCellValue().equals("id"))
					idColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("name"))
					nameColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("volume_per_unit"))
					volumePerUnitColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("supplier_id"))
					supplierIDColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("cs"))
					csIdColumnIndex = cell.getColumnIndex();
			}

			Map<Integer, Consumer> consumersMap = DBHolder.getInstance().getWHNetDatabase().getConsumersMap();

			for (Consumer c : consumersMap.values()) {

				Map<Integer, Task> tasksMap = c.getTasksMap();

				for (Task t : tasksMap.values()) {

					Map<Integer, Resource> resourcesMap = t.getResourcesMap();

					Iterator<Row> rowIterator = sheet.iterator();
					rowIterator.next();

					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();
						try {
							int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
							String name = row.getCell(nameColumnIndex).getStringCellValue();
							double volumePerUnit = row.getCell(volumePerUnitColumnIndex).getNumericCellValue();
							int supplierID = (int) row.getCell(supplierIDColumnIndex).getNumericCellValue();
							double Cs = row.getCell(csIdColumnIndex).getNumericCellValue();

							resourcesMap.get(id).setName(name);
							resourcesMap.get(id).setVolumePerUnit(volumePerUnit);
							resourcesMap.get(id).setSupplierID(supplierID);
							resourcesMap.get(id).setCs(Cs);
						} catch (Exception e) {
							System.err.println("Error: " + e.getMessage());
							e.printStackTrace();
							break;
						}
					}
				}
			}

			Map<Integer, Resource> resourcesMap = DBHolder.getInstance().getWHNetDatabase().getResourcesMap();

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					double volumePerUnit = row.getCell(volumePerUnitColumnIndex).getNumericCellValue();
					int supplierID = (int) row.getCell(supplierIDColumnIndex).getNumericCellValue();
					double Cs = row.getCell(csIdColumnIndex).getNumericCellValue();

					resourcesMap.get(id).setName(name);
					resourcesMap.get(id).setVolumePerUnit(volumePerUnit);
					resourcesMap.get(id).setSupplierID(supplierID);
					resourcesMap.get(id).setCs(Cs);

					if (!suppliersMap.containsKey(supplierID)) {
						Supplier supplier = new Supplier(supplierID, "supplier" + supplierID);
						supplier.getSuppliedResources().add(resourcesMap.get(id));
						suppliersMap.put(supplierID, supplier);
					} else
						suppliersMap.get(supplierID).getSuppliedResources().add(resourcesMap.get(id));

				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
					e.printStackTrace();
					break;
				}
			}
			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DBHolder.getInstance().getWHNetDatabase().suppliersMap = suppliersMap;
	}

	public static void readNormalDistributionValues(String filePath) {

		int numOfRows = 0;
		int numOfColumns = 0;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			numOfRows = sheet.getLastRowNum();

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();
			while (headRowIterator.hasNext()) {
				numOfColumns += 1;
				headRowIterator.next();
			}

			Matrix values = new Matrix(numOfRows, numOfColumns);
			double[] headerValues = new double[numOfColumns];
			double[] firstColumnValues = new double[numOfRows];

			for (int j = 1; j <= numOfColumns; j++) {
				Row row = sheet.getRow(0);
				double value = row.getCell(j).getNumericCellValue();
				headerValues[j - 1] = value;
			}

			for (int i = 1; i <= numOfRows; i++) {
				Row row = sheet.getRow(i);
				double value = row.getCell(0).getNumericCellValue();
				firstColumnValues[i - 1] = value;
			}

			for (int i = 1; i <= numOfRows; i++)
				for (int j = 1; j <= numOfColumns; j++) {
					Row row = sheet.getRow(i);
					double value = row.getCell(j).getNumericCellValue();
					values.set(i - 1, j - 1, value);
				}

			workBook.close();
			fileInputStream.close();

			DBHolder.getInstance().getWHNetDatabase().normalDistributionValues = values;
			DBHolder.getInstance().getWHNetDatabase().normalDistributionHeaderValues = headerValues;
			DBHolder.getInstance().getWHNetDatabase().normalDistributionFirstColumnValues = firstColumnValues;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
