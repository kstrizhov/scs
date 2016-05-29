package ru.bmstu.rk9.scs.lib;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ru.bmstu.rk9.scs.gui.WHNetTableViewer;

public class WHNetDataWriter {

	public static HSSFWorkbook createWorkbookFromTable(WHNetTableViewer viewer) {

		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet("report");

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setBorderTop(CellStyle.BORDER_THIN);
		headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
		headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
		headerStyle.setBorderRight(CellStyle.BORDER_THIN);
		headerStyle.setAlignment(CellStyle.ALIGN_CENTER);

		Table table = viewer.getTable();
		TableColumn[] columns = table.getColumns();
		int rowIndex = 0;
		int cellIndex = 0;
		HSSFRow header = sheet.createRow((short) rowIndex++);
		for (TableColumn column : columns) {
			HSSFCell cell = header.createCell(cellIndex++);
			cell.setCellValue(column.getText());
			cell.setCellStyle(headerStyle);
		}

		TableItem[] items = viewer.getTable().getItems();

		String[] tableColumnNames = new String[columns.length];
		for (int i = 0; i < columns.length; i++)
			tableColumnNames[i] = table.getColumn(i).getText();

		for (TableItem item : items) {
			HSSFRow row = sheet.createRow((short) rowIndex++);
			cellIndex = 0;

			for (int i = 0; i < columns.length; i++) {
				HSSFCell cell = row.createCell(cellIndex++);

				HSSFCellStyle cellStyle = wb.createCellStyle();
				short ha = CellStyle.ALIGN_CENTER;
				cellStyle.setAlignment(ha);
				cell.setCellStyle(cellStyle);

				String text = item.getText(i);
				cell.setCellValue(text);
			}
		}

		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn((short) i);
		}

		return wb;
	}

	public static void dumpWorkbookToAFile(HSSFWorkbook wb, String filename, Shell shell) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			wb.write(fos);
			fos.close();
			MessageDialog.openInformation(shell, "Запись отчета: успешно", "Отчет сохранен в файл:\n\n" + filename);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			String msg = ioe.getMessage();
			MessageDialog.openError(shell, "Запись отчета: ошибка", "Невозможно записать отчет в файл:\n\n" + msg);
		}
	}
}
