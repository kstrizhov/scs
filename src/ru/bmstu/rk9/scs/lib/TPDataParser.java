package ru.bmstu.rk9.scs.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Jama.Matrix;
import ru.bmstu.rk9.scs.tp.Base;
import ru.bmstu.rk9.scs.tp.ConsumptionPoint;
import ru.bmstu.rk9.scs.tp.Producer;

public class TPDataParser {

	private final static int initialIndex = -1;
	private final static int neededSheetIndex = 0;

	public static ArrayList<Producer> parseProducersExcelFile(String filePath) {

		ArrayList<Producer> producersList = new ArrayList<Producer>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int productionColumnIndex = initialIndex;
			int costColumnIndex = initialIndex;

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();

			while (headRowIterator.hasNext()) {
				Cell cell = headRowIterator.next();
				if (cell.getStringCellValue().equals("id"))
					idColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("name"))
					nameColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("production"))
					productionColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("cost"))
					costColumnIndex = cell.getColumnIndex();
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					double production = row.getCell(productionColumnIndex).getNumericCellValue();
					double costPerUnit = row.getCell(costColumnIndex).getNumericCellValue();
					producersList.add(new Producer(id, name, production, costPerUnit));
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

		return producersList;
	}

	public static ArrayList<ConsumptionPoint> parseConsumersExcelFile(String filePath) {

		ArrayList<ConsumptionPoint> consumersList = new ArrayList<ConsumptionPoint>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int consumptionColumnIndex = initialIndex;
			int monthsListColumnIndex = initialIndex;

			Row headRow = sheet.getRow(0);
			Iterator<Cell> headRowIterator = headRow.cellIterator();

			while (headRowIterator.hasNext()) {
				Cell cell = headRowIterator.next();
				if (cell.getStringCellValue().equals("id"))
					idColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("name"))
					nameColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("consumption"))
					consumptionColumnIndex = cell.getColumnIndex();
				if (cell.getStringCellValue().equals("months"))
					monthsListColumnIndex = cell.getColumnIndex();
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					double consumption = row.getCell(consumptionColumnIndex).getNumericCellValue();
					String monthsString = row.getCell(monthsListColumnIndex).getStringCellValue();
					ArrayList<Month> monthsList = parseMonthsString(monthsString);
					consumersList.add(new ConsumptionPoint(id, name, consumption, monthsList));
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

		return consumersList;
	}

	// TODO redo this parse method!!
	public static ArrayList<Month> parseMonthsString(String monthsString) throws Exception {
		ArrayList<Month> monthsList = new ArrayList<Month>();
		String delimiter = "[-]+";
		String[] months = monthsString.split(delimiter);

		if (months.length > 2) {
			throw new Exception("Wrong data: incorrect \"months\" record!");
		}

		int firstMonthId = Integer.parseInt(months[0]);
		int lastMonthID = Integer.parseInt(months[1]);

		for (int i = firstMonthId; i <= lastMonthID; i++)
			monthsList.add(Month.of(i));

		return monthsList;
	}

	public static ArrayList<Base> parseBasesExcelFile(String filePath) {

		ArrayList<Base> basesList = new ArrayList<Base>();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			int idColumnIndex = initialIndex;
			int nameColumnIndex = initialIndex;
			int volumeColumnIndex = initialIndex;

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
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				try {
					int id = (int) row.getCell(idColumnIndex).getNumericCellValue();
					String name = row.getCell(nameColumnIndex).getStringCellValue();
					double volume = row.getCell(volumeColumnIndex).getNumericCellValue();
					basesList.add(new Base(id, name, volume));
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

		return basesList;
	}

	public static Matrix parseProdConsDistanceMatrixExcelFile(String filePath) {

		int numOfProducers = DBHolder.getInstance().getTPDatabase().getProducersList().size();
		int numOfConsumers = DBHolder.getInstance().getTPDatabase().getConsumersList().size();

		Matrix prodsConsDistanceMatrix = new Matrix(numOfProducers, numOfConsumers);

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			for (int i = 1; i < numOfProducers + 1; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j < numOfConsumers + 1; j++) {
					double distance = row.getCell(j).getNumericCellValue();
					prodsConsDistanceMatrix.set(i - 1, j - 1, distance);
				}
			}

			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prodsConsDistanceMatrix;
	}

	public static Matrix parseProdBasesDistanceMatrixExcelFile(String filePath) {

		int numOfProducers = DBHolder.getInstance().getTPDatabase().getProducersList().size();
		int numOfBases = DBHolder.getInstance().getTPDatabase().getBasesList().size();

		Matrix prodsBasesDistanceMatrix = new Matrix(numOfProducers, numOfBases);

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			for (int i = 1; i < numOfProducers + 1; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j < numOfBases + 1; j++) {
					double distance = row.getCell(j).getNumericCellValue();
					prodsBasesDistanceMatrix.set(i - 1, j - 1, distance);
				}
			}

			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prodsBasesDistanceMatrix;
	}

	public static Matrix parseBasesConsDistanceMatrixExcelFile(String filePath) {

		int numOfBases = DBHolder.getInstance().getTPDatabase().getBasesList().size();
		int numOfConsumers = DBHolder.getInstance().getTPDatabase().getConsumersList().size();

		Matrix basesConsDistanceMatrix = new Matrix(numOfBases, numOfConsumers);

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			Workbook workBook = new HSSFWorkbook(fileInputStream);
			Sheet sheet = workBook.getSheetAt(neededSheetIndex);

			for (int i = 1; i < numOfBases + 1; i++) {
				Row row = sheet.getRow(i);
				for (int j = 1; j < numOfConsumers + 1; j++) {
					double distance = row.getCell(j).getNumericCellValue();
					basesConsDistanceMatrix.set(i - 1, j - 1, distance);
				}
			}

			workBook.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return basesConsDistanceMatrix;
	}
}