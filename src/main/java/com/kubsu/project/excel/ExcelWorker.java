package com.kubsu.project.excel;

import com.kubsu.project.excel.helper.CellReferenceInfo;
import com.kubsu.project.excel.helper.ExcelCoupleInfo;
import com.kubsu.project.models.User;
import com.kubsu.project.models.dto.CoupleDto;
import com.kubsu.project.models.dto.ScheduleDto;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
public class ExcelWorker {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
    private static final String SPECIAL_CASE = "Элективные дисциплины по физической культуре и спорту";
    private static final String SPECIAL_TYPE = "лекция";
    private static final int COLUMN_WITH_TIME = 1;
    private static final int COLUMN_WITH_DAY_OF_WEEK = 0;
    private static final int ROW_WITH_GROUPS = 1;
    private static final int ROW_WITH_PARITY = 0;

    private static final CoupleDto coupleDto = new CoupleDto();
    private static final CellReferenceInfo cellReferenceInfo = new CellReferenceInfo();

    public static void readExcelFile(User user, String pathname) throws IOException, ParseException {
        File file = new File(pathname);
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        int rowEnd = sheet.getLastRowNum();
        System.out.println("LastRow: " + rowEnd);
        int iteratorUnderLastCell = sheet.getRow(1).getLastCellNum();
        System.out.println("LastCellNum: " + iteratorUnderLastCell);
        int columnNum = 2;
        int coupleIterator = 1;
        int dayOfWeekIterator = 0;
        int rowWithDayOfWeek = 0;
        List<CoupleDto> coupleList = new ArrayList<>();
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        List<CellReferenceInfo> cellReferenceInfoList = new ArrayList<>();
        Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap = new HashMap<>();
        while (iteratorUnderLastCell > 2) {
            for (int rowNum = 2; rowNum < rowEnd; rowNum++) {
                Row rowFromExcel = sheet.getRow(rowNum);
                if (rowFromExcel == null) {
                    continue;
                }
                Cell cellFromExcelForDayOfWeek;
                Cell cellFromExcelForGroup;
                Cell cellFromExcelForParity;
                dayOfWeekIterator++;
                if (dayOfWeekIterator == 1) {
                    rowWithDayOfWeek = rowNum;
                }
                Cell cellFromExcel = rowFromExcel.getCell(columnNum);
                if (cellFromExcel != null) {
                    coupleIterator = readInformationFromCell(rowFromExcel, cellFromExcel, coupleIterator);
                }
                if (coupleIterator == 5) {
                    cellFromExcelForDayOfWeek = sheet.getRow(rowWithDayOfWeek).getCell(COLUMN_WITH_DAY_OF_WEEK);
                    cellFromExcelForGroup = sheet.getRow(ROW_WITH_GROUPS).getCell(columnNum);
                    cellFromExcelForParity = sheet.getRow(ROW_WITH_PARITY).getCell(2);
                    System.out.println("Couple: " + coupleDto);
                    CoupleDto copyOfCouple = new CoupleDto();
                    CellReferenceInfo referenceInfo = CellReferenceInfo.builder().
                            cellTitleReference(cellReferenceInfo.getCellTitleReference()).cellTypeReference(cellReferenceInfo.getCellTypeReference()).
                            cellAudienceReference(cellReferenceInfo.getCellAudienceReference()).cellTeacherReference(cellReferenceInfo.getCellTeacherReference())
                            .build();
                    ExcelCoupleInfo excelCoupleInfo = new ExcelCoupleInfo();
                    copyOfCouple.setTitle(coupleDto.getTitle());
                    excelCoupleInfo.setTitle(coupleDto.getTitle());
                    copyOfCouple.setTimeCouple(coupleDto.getTimeCouple());
                    excelCoupleInfo.setTimeCouple(coupleDto.getTimeCouple());
                    if (coupleDto.getType() != null) {
                        copyOfCouple.setType(coupleDto.getType());
                        excelCoupleInfo.setType(coupleDto.getType());
                    }
                    if (coupleDto.getTeacher() != null) {
                        copyOfCouple.setTeacher(coupleDto.getTeacher());
                        excelCoupleInfo.setTeacher(coupleDto.getTeacher());
                    }
                    if (coupleDto.getAudience() != null) {
                        copyOfCouple.setAudience(coupleDto.getAudience());
                        excelCoupleInfo.setAudience(coupleDto.getAudience());
                    }
                    if (cellFromExcelForDayOfWeek != null && cellFromExcelForGroup != null && cellFromExcelForParity != null) {
                        excelCoupleInfo.setDayOfWeek(cellFromExcelForDayOfWeek.getRichStringCellValue().getString().trim());
                        excelCoupleInfo.setTeam(cellFromExcelForGroup.getRichStringCellValue().getString().trim());
                        excelCoupleInfo.setParity(cellFromExcelForParity.getRichStringCellValue().getString().trim());
                    }
                    cellReferenceInfoList.add(referenceInfo);
                    coupleList.add(copyOfCouple);
                    excelCoupleInfoMap.put(referenceInfo, excelCoupleInfo);
                    coupleIterator = 1;
                }
                if (dayOfWeekIterator == 28) {
                    ScheduleDto scheduleDto = new ScheduleDto();
                    cellFromExcelForDayOfWeek = sheet.getRow(rowWithDayOfWeek).getCell(COLUMN_WITH_DAY_OF_WEEK);
                    cellFromExcelForGroup = sheet.getRow(ROW_WITH_GROUPS).getCell(columnNum);
                    cellFromExcelForParity = sheet.getRow(ROW_WITH_PARITY).getCell(2);
                    if (cellFromExcelForDayOfWeek != null && cellFromExcelForGroup != null && cellFromExcelForParity != null) {
                        scheduleDto.setDayOfWeek(cellFromExcelForDayOfWeek.getRichStringCellValue().getString().trim());
                        scheduleDto.setTeam(cellFromExcelForGroup.getRichStringCellValue().getString().trim());
                        scheduleDto.setParity(cellFromExcelForParity.getRichStringCellValue().getString().trim());
                    }
                    scheduleDto.setCouples(coupleList);
                    scheduleDto.setAuthor(user);
                    System.out.println("Schedule: " + scheduleDto);
                    scheduleDtoList.add(scheduleDto);
                    coupleList = new ArrayList<>();
                    dayOfWeekIterator = 0;
                }
            }
            iteratorUnderLastCell--;
            columnNum++;
            coupleIterator = 1;
            dayOfWeekIterator = 0;
        }
        workbook.close();
        System.out.println("CellReferenceInfoSet: " + cellReferenceInfoList);
        System.out.println("ExcelCoupleInfoMap: " + excelCoupleInfoMap);
        System.out.println("ScheduleList: "+ scheduleDtoList);
        boolean haveErrors = findErrorsIntoExcel(cellReferenceInfoList, excelCoupleInfoMap, file);
    }

    private static boolean findErrorsIntoExcel(List<CellReferenceInfo> cellReferenceInfoList, Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap, File file) throws IOException {
        boolean haveErrors = false;
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.RED.getIndex());
        cell.setCellStyle(style);
        for (int i=0;i<cellReferenceInfoList.size();i++) {
            CellReferenceInfo referenceInfo1 = cellReferenceInfoList.get(i);
            ExcelCoupleInfo excelCoupleInfo1 = excelCoupleInfoMap.get(referenceInfo1);
            if (excelCoupleInfo1.getTitle().equals(SPECIAL_CASE)|| excelCoupleInfo1.getTitle().equalsIgnoreCase(SPECIAL_CASE)){
                continue;
            }
            for (int j=i+1;j<cellReferenceInfoList.size();j++){
                CellReferenceInfo referenceInfo2 = cellReferenceInfoList.get(j);
                ExcelCoupleInfo excelCoupleInfo2 = excelCoupleInfoMap.get(referenceInfo2);
                if (excelCoupleInfo2.getTitle().equals(SPECIAL_CASE)|| excelCoupleInfo2.getTitle().equalsIgnoreCase(SPECIAL_CASE)){
                    continue;
                }
                if (excelCoupleInfo1.getDayOfWeek().equals(excelCoupleInfo2.getDayOfWeek()) &&
                        excelCoupleInfo1.getTimeCouple().equals(excelCoupleInfo2.getTimeCouple()) &&
                        excelCoupleInfo1.getAudience().equals(excelCoupleInfo2.getAudience()) &&
                        excelCoupleInfo1.getParity().equals(excelCoupleInfo2.getParity())){
                    if (excelCoupleInfo1.getTitle().equals(excelCoupleInfo2.getTitle()) &&
                            excelCoupleInfo1.getType().equals(excelCoupleInfo2.getType()) &&
                            excelCoupleInfo1.getTeacher().equals(excelCoupleInfo2.getTeacher()) &&
                            (excelCoupleInfo1.getTitle().equals(SPECIAL_TYPE) || excelCoupleInfo1.getTitle().equalsIgnoreCase(SPECIAL_CASE)) &&
                            (excelCoupleInfo2.getTitle().equals(SPECIAL_TYPE) || excelCoupleInfo2.getTitle().equalsIgnoreCase(SPECIAL_CASE))){
                        continue;
                    }else if (excelCoupleInfo1.getTitle().equals(SPECIAL_TYPE) ||
                            excelCoupleInfo1.getTitle().equalsIgnoreCase(SPECIAL_CASE)) {

                    }else if (excelCoupleInfo2.getTitle().equals(SPECIAL_TYPE) ||
                            excelCoupleInfo2.getTitle().equalsIgnoreCase(SPECIAL_CASE)){

                    }else {

                    }
                }
            }
        }
        return haveErrors;
    }

    private static int readInformationFromCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        String informationIntoExcelCell;
        switch (cellFromExcel.getCellType()) {
            case STRING:
                informationIntoExcelCell = cellFromExcel.getRichStringCellValue().getString().trim();
                if (!informationIntoExcelCell.isEmpty()) {
                    System.out.println(informationIntoExcelCell);
                    coupleIterator = workWithStringCell(rowFromExcel, cellFromExcel, coupleIterator);
                    coupleIterator++;
                }
                break;
            case NUMERIC:
                informationIntoExcelCell = String.valueOf((int) cellFromExcel.getNumericCellValue()).trim();
                if (!informationIntoExcelCell.isEmpty()) {
                    System.out.println(informationIntoExcelCell);
                    coupleIterator = workWithNumericCell(rowFromExcel, cellFromExcel, coupleIterator);
                    coupleIterator++;
                }
                break;
            default:
                break;
        }
        return coupleIterator;
    }

    private static int workWithStringCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        CellReference cellRef = new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex());
        System.out.print(cellRef.formatAsString());
        String cellWithInfoAboutCouple = cellFromExcel.getRichStringCellValue().getString().trim();
        if (cellWithInfoAboutCouple.equalsIgnoreCase(SPECIAL_CASE) || cellWithInfoAboutCouple.equals(SPECIAL_CASE)) {
            cellReferenceInfo.setCellTitleReference(new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex()));
            coupleDto.setTitle(cellWithInfoAboutCouple);
            Cell cellWithTimeOfCouple = rowFromExcel.getCell(COLUMN_WITH_TIME);
            if (cellWithTimeOfCouple != null) {
                if (DateUtil.isCellDateFormatted(cellWithTimeOfCouple) && cellWithTimeOfCouple.getDateCellValue() != null) {
                    String timeOfCouple = simpleDateFormat.format(cellWithTimeOfCouple.getDateCellValue());
                    coupleDto.setTimeCouple(simpleDateFormat.parse(timeOfCouple));
                }
            }
            coupleDto.setTeacher(null);
            coupleDto.setAudience(null);
            coupleDto.setType(null);
            cellReferenceInfo.setCellTeacherReference(null);
            cellReferenceInfo.setCellTypeReference(null);
            cellReferenceInfo.setCellAudienceReference(null);
            coupleIterator = 4;
            return coupleIterator;
        }
        return chooseCaseForSetInfoIntoPerson(rowFromExcel, cellFromExcel, cellWithInfoAboutCouple, coupleIterator);
    }

    private static int workWithNumericCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        String cellWithInfoAboutCouple = String.valueOf((int) cellFromExcel.getNumericCellValue()).trim();
        return chooseCaseForSetInfoIntoPerson(rowFromExcel, cellFromExcel, cellWithInfoAboutCouple, coupleIterator);
    }

    private static int chooseCaseForSetInfoIntoPerson(Row rowFromExcel, Cell cellFromExcel, String cellWithInfoAboutCouple, int coupleIterator) throws ParseException {
        switch (coupleIterator) {
            case 1:
                cellReferenceInfo.setCellTitleReference(new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex()));
                coupleDto.setTitle(cellWithInfoAboutCouple);
                Cell cellWithTimeOfCouple = rowFromExcel.getCell(COLUMN_WITH_TIME);
                if (cellWithTimeOfCouple != null) {
                    if (DateUtil.isCellDateFormatted(cellWithTimeOfCouple) && cellWithTimeOfCouple.getDateCellValue() != null) {
                        String timeOfCouple = simpleDateFormat.format(cellWithTimeOfCouple.getDateCellValue());
                        System.out.println(timeOfCouple);
                        coupleDto.setTimeCouple(simpleDateFormat.parse(timeOfCouple));
                    }
                }
                break;
            case 2:
                cellReferenceInfo.setCellTeacherReference(new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex()));
                coupleDto.setTeacher(cellWithInfoAboutCouple);
                break;
            case 3:
                cellReferenceInfo.setCellTypeReference(new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex()));
                coupleDto.setType(cellWithInfoAboutCouple);
                break;
            case 4:
                cellReferenceInfo.setCellAudienceReference(new CellReference(rowFromExcel.getRowNum(), cellFromExcel.getColumnIndex()));
                coupleDto.setAudience(cellWithInfoAboutCouple);
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
