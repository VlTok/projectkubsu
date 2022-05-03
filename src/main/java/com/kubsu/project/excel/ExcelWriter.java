package com.kubsu.project.excel;

import com.kubsu.project.models.Couple;
import com.kubsu.project.models.User;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ExcelWriter {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
    private static final Couple couple = new Couple();
    private static final List<Couple> coupleList = new ArrayList<>();
    private static final String SPECIAL_CASE = "Элективные дисциплины по физической культуре и спорту";
    private static final int COLUMN_WITH_TIME = 1;

    public static void readExcelFile(User user, String pathname) throws IOException, ParseException {
        File file = new File(pathname);
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        int rowEnd = sheet.getLastRowNum();
        int iteratorUnderLastCell = sheet.getRow(1).getLastCellNum();
        System.out.println("LastCellNum: " + iteratorUnderLastCell);
        int columnNum = 2;
        int coupleIterator=1;
        while (iteratorUnderLastCell > 1) {
            for (int rowNum = 2; rowNum < rowEnd; rowNum++) {
                Row rowFromExcel = sheet.getRow(rowNum);
                if (rowFromExcel == null) {
                    continue;
                }
                Cell cellFromExcel = rowFromExcel.getCell(columnNum);
                if (cellFromExcel != null) {
                    coupleIterator = readInformationFromCell(rowFromExcel,cellFromExcel, coupleIterator);
                }
                if (coupleIterator==5){
                    System.out.println("Couple: "+couple);
                    couple.setTitle(null);
                    couple.setTeacher(null);
                    couple.setAudience(null);
                    couple.setType(null);
                    coupleIterator=1;
                }
            }
            iteratorUnderLastCell--;
            columnNum++;
            coupleIterator=1;
        }
    }

    private static int readInformationFromCell(Row rowFromExcel,Cell cellFromExcel, int coupleIterator) throws ParseException {
        String informationIntoExcelCell;
        switch (cellFromExcel.getCellType()) {
            case STRING:
                informationIntoExcelCell = cellFromExcel.getRichStringCellValue().getString().trim();
                if (!informationIntoExcelCell.isEmpty()) {
                    System.out.println(informationIntoExcelCell);
                    coupleIterator = workWithStringCell(rowFromExcel,cellFromExcel,coupleIterator);
                    coupleIterator++;
                }
                break;
            case NUMERIC:
                informationIntoExcelCell = String.valueOf((int)cellFromExcel.getNumericCellValue()).trim();
                if (!informationIntoExcelCell.isEmpty()) {
                    System.out.println(informationIntoExcelCell);
                    coupleIterator = workWithNumericCell(rowFromExcel,cellFromExcel,coupleIterator);
                    coupleIterator++;
                }
                break;
            default:
                break;
        }
        return coupleIterator;
    }

    private static int workWithStringCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        String cellWithInfoAboutCouple = cellFromExcel.getRichStringCellValue().getString().trim();
        if (cellWithInfoAboutCouple.equalsIgnoreCase(SPECIAL_CASE) || cellWithInfoAboutCouple.equals(SPECIAL_CASE)){
            couple.setTitle(cellWithInfoAboutCouple);
            Cell cellWithTimeOfCouple = rowFromExcel.getCell(COLUMN_WITH_TIME);
            if (cellWithTimeOfCouple!=null) {
                if (DateUtil.isCellDateFormatted(cellWithTimeOfCouple) && cellWithTimeOfCouple.getDateCellValue() != null) {
                    String timeOfCouple = simpleDateFormat.format(cellWithTimeOfCouple.getDateCellValue());
                    couple.setTimeCouple(simpleDateFormat.parse(timeOfCouple));
                }
            }
            coupleIterator = 4;
            return coupleIterator;
        }
        return chooseCaseForSetInfoIntoPerson(rowFromExcel,cellWithInfoAboutCouple,coupleIterator);
    }

    private static int workWithNumericCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        String cellWithInfoAboutCouple = String.valueOf((int) cellFromExcel.getNumericCellValue()).trim();
        return chooseCaseForSetInfoIntoPerson(rowFromExcel,cellWithInfoAboutCouple,coupleIterator);
    }

    private static int chooseCaseForSetInfoIntoPerson(Row rowFromExcel, String cellWithInfoAboutCouple, int coupleIterator) throws ParseException {
        switch (coupleIterator){
            case 1:
                couple.setTitle(cellWithInfoAboutCouple);
                Cell cellWithTimeOfCouple = rowFromExcel.getCell(COLUMN_WITH_TIME);
                if (cellWithTimeOfCouple!=null) {
                    if (DateUtil.isCellDateFormatted(cellWithTimeOfCouple) && cellWithTimeOfCouple.getDateCellValue()!=null) {
                        String timeOfCouple = simpleDateFormat.format(cellWithTimeOfCouple.getDateCellValue());
                        System.out.println(timeOfCouple);
                        couple.setTimeCouple(simpleDateFormat.parse(timeOfCouple));
                    }
                }
                break;
            case 2:
                couple.setTeacher(cellWithInfoAboutCouple);
                break;
            case 3:
                couple.setType(cellWithInfoAboutCouple);
                break;
            case 4:
                couple.setAudience(cellWithInfoAboutCouple);
                break;
            default:
                break;
        }
        return coupleIterator;
    }

    private void updateExcelFile() throws IOException {
        File file = new File("C:/demo/employee.xls");
        // Read XSL file
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFCell cell = sheet.getRow(1).getCell(2);
        cell.setCellValue(cell.getNumericCellValue() * 2);

        cell = sheet.getRow(2).getCell(2);
        cell.setCellValue(cell.getNumericCellValue() * 2);

        cell = sheet.getRow(3).getCell(2);
        cell.setCellValue(cell.getNumericCellValue() * 2);

        inputStream.close();

        // Write File
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
    }
}
