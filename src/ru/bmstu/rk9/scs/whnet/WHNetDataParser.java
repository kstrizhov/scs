package ru.bmstu.rk9.scs.whnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ru.bmstu.rk9.scs.lib.DBHolder;
import ru.bmstu.rk9.scs.whnet.Warehouse.WHLevel;

public class WHNetDataParser {

	private final static int initialIndex = -1;
	private final static int neededSheetIndex = 0;
	private final static int firstLevelWHID = 0;

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
					whNetMap.put(id, new Warehouse(id, name, volume, parentID));
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
			switch (w.level) {
			case FIRST:
				DBHolder.getInstance().getWHNetDatabase().firstLevelWarehousesIDList.add(w.id);
				break;
			case SECOND:
				DBHolder.getInstance().getWHNetDatabase().secondLevelWarehousesIDList.add(w.id);
				break;
			case THIRD:
				DBHolder.getInstance().getWHNetDatabase().thirdLevelWarehousesIDList.add(w.id);
				break;
			}
		DBHolder.getInstance().getWHNetDatabase().whNetMap = whNetMap;
	}

	private static void setChildren(Map<Integer, Warehouse> whNetMap) {
		for (Warehouse w : whNetMap.values()) {
			if (w.parentID == firstLevelWHID)
				continue;
			Warehouse parent = whNetMap.get(w.parentID);
			parent.childrensIDList.add(w.id);
		}
	}

	private static void setWHLevels(Map<Integer, Warehouse> whNetMap) {
		for (Warehouse w : whNetMap.values()) {
			if (w.parentID == firstLevelWHID)
				w.level = WHLevel.FIRST;
			else if ((w.parentID != firstLevelWHID) && !w.childrensIDList.isEmpty())
				w.level = WHLevel.SECOND;
			else
				w.level = WHLevel.THIRD;
		}
	}

	public static void parseConsumersData(String filePath) {

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
					consumersMap.put(id, new Consumer(id, name, warehouseID));
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

			Map<Integer, Task> tasksMap = DBHolder.getInstance().getWHNetDatabase().tasksMap;

			for (int i = 1; i <= numOfTasks; i++) {
				tasksMap.put(i, new Task(i, "task" + i));
			}

			Map<Integer, Consumer> consumersMap = DBHolder.getInstance().getWHNetDatabase().consumersMap;

			for (int i = 1; i <= numOfConsumers; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j <= numOfTasks; j++) {
					double taskFrequency = row.getCell(j).getNumericCellValue();
					Task task = tasksMap.get(j);
					consumersMap.get(i).tasksList.put(task.id, task);
					consumersMap.get(i).tasksFreqenciesMap.put(task.id, taskFrequency);
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

		int numOfTasks = DBHolder.getInstance().getWHNetDatabase().tasksMap.size();
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

			Map<Integer, Resource> resourcesMap = DBHolder.getInstance().getWHNetDatabase().resourcesMap;

			for (int i = 1; i <= numOfResources; i++) {
				resourcesMap.put(i, new Resource(i, "resource" + i));
			}

			Map<Integer, Task> tasksMap = DBHolder.getInstance().getWHNetDatabase().tasksMap;

			for (int i = 1; i <= numOfTasks; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j <= numOfResources; j++) {
					double needsThisResource = row.getCell(j).getNumericCellValue();
					Resource resource = resourcesMap.get(j);
					tasksMap.get(i).resourceNorms.put(resource.id, needsThisResource);
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
}
