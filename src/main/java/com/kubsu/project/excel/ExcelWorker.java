package com.kubsu.project.excel;

import com.kubsu.project.excel.helper.CellReferenceInfo;
import com.kubsu.project.excel.helper.ExcelCoupleInfo;
import com.kubsu.project.models.Couple;
import com.kubsu.project.models.Schedule;
import com.kubsu.project.models.User;
import com.kubsu.project.models.dto.CoupleDto;
import com.kubsu.project.models.dto.ScheduleDto;
import lombok.Getter;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Objects.nonNull;

@Getter
public class ExcelWorker {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
    private static final String SPECIAL_CASE = "Элективные дисциплины по физической культуре и спорту";
    private static final String SPECIAL_TYPE_1 = "лекция";
    private static final String SPECIAL_TYPE_2 = "п/з";
    private static final int COLUMN_WITH_TIME = 1;
    private static final int COLUMN_WITH_DAY_OF_WEEK = 0;
    private static final int ROW_WITH_GROUPS = 1;
    private static final int ROW_WITH_PARITY = 0;
    private static final int COLUMN_WITH_PARITY = 2;
    private static final int INFO_ABOUT_COUPLE_ITERATOR = 5;
    private static final int INFO_ABOUT_SCHEDULE_ITERATOR = 32;

    private static final CoupleDto coupleDto = new CoupleDto();
    private static final CellReferenceInfo cellReferenceInfo = new CellReferenceInfo();

    public static Set<String> readExcelFile(List<Schedule> scheduleListFromDb, List<Schedule> scheduleListForAddIntoDb, User user, String uploadFilePathname, String fileWithErrorsInfoPathname, String fileWithWarningsInfoPathname, Set<String> someWarnings) throws IOException, ParseException {
        File file = new File(uploadFilePathname);

        List<ScheduleDto> scheduleDtoListForAddIntoDb = new ArrayList<>();
        Set<String> errorsInfoForFirstStep = new HashSet<>();
        Set<String> errorsInfoForSecondStep = new HashSet<>();
        int sheetIterator = 0;
        while (sheetIterator < 2) {
            FileInputStream inputStream = new FileInputStream(file);
            ZipSecureFile.setMinInflateRatio(0);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(sheetIterator);
            int rowEnd = sheet.getLastRowNum()+51;
            System.out.println("LastRow: " + rowEnd);
            int iteratorUnderLastCell = sheet.getRow(1).getLastCellNum();
            System.out.println("LastCellNum: " + iteratorUnderLastCell);
            List<CellReferenceInfo> cellReferenceInfoList = new ArrayList<>();
            Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap = new HashMap<>();
            List<CoupleDto> coupleList = new ArrayList<>();
            int columnNum = 2;
            int coupleIterator = 1;
            int dayOfWeekIterator = 0;
            int rowWithDayOfWeek = 0;
            while (iteratorUnderLastCell > 2) {
                for (int rowNum = 2; rowNum < rowEnd; rowNum++) {
                    Row rowFromExcel = sheet.getRow(rowNum);
                    if (!nonNull(rowFromExcel)) {
                        continue;
                    }
                    dayOfWeekIterator++;
                    if (dayOfWeekIterator == 1) {
                        rowWithDayOfWeek = rowNum;
                    }
                    Cell cellFromExcel = rowFromExcel.getCell(columnNum);
                    if (nonNull(cellFromExcel)) {
                        coupleIterator = readInformationFromCell(rowFromExcel, cellFromExcel, coupleIterator);
                    }
                    if (coupleIterator == INFO_ABOUT_COUPLE_ITERATOR) {
                        setDataForCoupleFromExcel(sheet, cellReferenceInfoList,
                                excelCoupleInfoMap, coupleList,
                                rowWithDayOfWeek, columnNum);
                        coupleIterator = 1;
                    }
                    if (dayOfWeekIterator == INFO_ABOUT_SCHEDULE_ITERATOR) {
                        setDataForScheduleFromExcel(sheet, user,
                                scheduleDtoListForAddIntoDb,
                                coupleList, rowWithDayOfWeek, columnNum);
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

            if (findErrorsIntoExcel(cellReferenceInfoList, excelCoupleInfoMap, file, errorsInfoForFirstStep, sheetIterator)) {
                writeErrorsOrWarningsIntoFile(errorsInfoForFirstStep, fileWithErrorsInfoPathname);
                return errorsInfoForFirstStep;
            }
            System.out.println("errorsInfoList: " + errorsInfoForFirstStep);

            if (scheduleListFromDb.size() != 0) {
                List<ExcelCoupleInfo> couplesInfoFromDb = new ArrayList<>();
                addNewInformationIntoCouplesInfo(couplesInfoFromDb, scheduleListFromDb);
                if (findErrorsIntoExcelWithDb(cellReferenceInfoList, excelCoupleInfoMap,
                        couplesInfoFromDb, file, errorsInfoForSecondStep, sheetIterator)) {
                    writeErrorsOrWarningsIntoFile(errorsInfoForSecondStep, fileWithErrorsInfoPathname);
                    return errorsInfoForSecondStep;
                }
            }
            if (findWarningsIntoExcel(cellReferenceInfoList, excelCoupleInfoMap, file, someWarnings, sheetIterator)) {
                writeErrorsOrWarningsIntoFile(someWarnings, fileWithWarningsInfoPathname);
            }
            sheetIterator++;
        }
        System.out.println("ScheduleDtoListForAddIntoDb: "+scheduleDtoListForAddIntoDb);
        if (errorsInfoForFirstStep.size() == 0 && errorsInfoForSecondStep.size() == 0) {
            addNewInfoIntoSchedule(scheduleListForAddIntoDb, scheduleDtoListForAddIntoDb);
        }
        return new HashSet<>();
    }

    private static void setDataForCoupleFromExcel(XSSFSheet sheet, List<CellReferenceInfo> cellReferenceInfoList,
                                                  Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap, List<CoupleDto> coupleList,
                                                  int rowWithDayOfWeek, int columnNum) {
        Cell cellFromExcelForDayOfWeek = sheet.getRow(rowWithDayOfWeek).getCell(COLUMN_WITH_DAY_OF_WEEK);
        Cell cellFromExcelForGroup = sheet.getRow(ROW_WITH_GROUPS).getCell(columnNum);
        Cell cellFromExcelForParity = sheet.getRow(ROW_WITH_PARITY).getCell(COLUMN_WITH_PARITY);
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
        if (nonNull(coupleDto.getType())) {
            copyOfCouple.setType(coupleDto.getType());
            excelCoupleInfo.setType(coupleDto.getType());
        }
        if (nonNull(coupleDto.getTeacher())) {
            copyOfCouple.setTeacher(coupleDto.getTeacher());
            excelCoupleInfo.setTeacher(coupleDto.getTeacher());
        }
        if (nonNull(coupleDto.getAudience())) {
            copyOfCouple.setAudience(coupleDto.getAudience());
            excelCoupleInfo.setAudience(coupleDto.getAudience());
        }
        if (nonNull(cellFromExcelForDayOfWeek) && nonNull(cellFromExcelForGroup) && nonNull(cellFromExcelForParity)) {
            excelCoupleInfo.setDayOfWeek(cellFromExcelForDayOfWeek.getRichStringCellValue().getString().trim());
            excelCoupleInfo.setTeam(cellFromExcelForGroup.getRichStringCellValue().getString().trim());
            excelCoupleInfo.setParity(cellFromExcelForParity.getRichStringCellValue().getString().trim());
        }
        cellReferenceInfoList.add(referenceInfo);
        coupleList.add(copyOfCouple);
        excelCoupleInfoMap.put(referenceInfo, excelCoupleInfo);
    }

    private static void setDataForScheduleFromExcel(XSSFSheet sheet, User user,
                                                    List<ScheduleDto> scheduleDtoListForAddIntoDb,
                                                    List<CoupleDto> coupleList, int rowWithDayOfWeek, int columnNum) {
        ScheduleDto scheduleDto = new ScheduleDto();
        Cell cellFromExcelForDayOfWeek = sheet.getRow(rowWithDayOfWeek).getCell(COLUMN_WITH_DAY_OF_WEEK);
        Cell cellFromExcelForGroup = sheet.getRow(ROW_WITH_GROUPS).getCell(columnNum);
        Cell cellFromExcelForParity = sheet.getRow(ROW_WITH_PARITY).getCell(COLUMN_WITH_PARITY);
        if (cellFromExcelForDayOfWeek != null && cellFromExcelForGroup != null && cellFromExcelForParity != null) {
            scheduleDto.setDayOfWeek(cellFromExcelForDayOfWeek.getRichStringCellValue().getString().trim());
            scheduleDto.setTeam(cellFromExcelForGroup.getRichStringCellValue().getString().trim());
            scheduleDto.setParity(cellFromExcelForParity.getRichStringCellValue().getString().trim());
        }
        scheduleDto.setCouples(coupleList);
        scheduleDto.setAuthor(user);
        scheduleDtoListForAddIntoDb.add(scheduleDto);
    }

    private static int readInformationFromCell(Row rowFromExcel, Cell cellFromExcel, int coupleIterator) throws ParseException {
        String informationIntoExcelCell;
        switch (cellFromExcel.getCellType()) {
            case STRING:
                informationIntoExcelCell = cellFromExcel.getRichStringCellValue().getString().trim();
                if (!informationIntoExcelCell.isEmpty()) {
                    coupleIterator = workWithStringCell(rowFromExcel, cellFromExcel, coupleIterator);
                    coupleIterator++;
                }
                break;
            case NUMERIC:
                informationIntoExcelCell = String.valueOf((int) cellFromExcel.getNumericCellValue()).trim();
                if (!informationIntoExcelCell.isEmpty()) {
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

    private static boolean findErrorsIntoExcel(List<CellReferenceInfo> cellReferenceInfoList, Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap, File file, Set<String> errorsInfo, int sheetIterator) throws IOException {
        boolean haveErrors = false;
        FileInputStream inputStream = new FileInputStream(file);
        ZipSecureFile.setMinInflateRatio(0);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(sheetIterator);
        for (int i = 0; i < cellReferenceInfoList.size(); i++) {
            CellReferenceInfo referenceInfo1 = cellReferenceInfoList.get(i);
            ExcelCoupleInfo excelCoupleInfo1 = excelCoupleInfoMap.get(referenceInfo1);
            if (isPhysicalEducation(excelCoupleInfo1)) {
                continue;
            }
            for (int j = i + 1; j < cellReferenceInfoList.size(); j++) {
                CellReferenceInfo referenceInfo2 = cellReferenceInfoList.get(j);
                ExcelCoupleInfo excelCoupleInfo2 = excelCoupleInfoMap.get(referenceInfo2);
                if (isPhysicalEducation(excelCoupleInfo2)) {
                    continue;
                }
                haveErrors = checkErrorsIntoExcelCells(workbook, sheet, excelCoupleInfo1, referenceInfo1, excelCoupleInfo2, referenceInfo2, errorsInfo, haveErrors);
            }
        }
        try (OutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return haveErrors;
    }

    private static boolean checkErrorsIntoExcelCells(XSSFWorkbook workbook, XSSFSheet sheet,
                                                     ExcelCoupleInfo excelCoupleInfo1, CellReferenceInfo referenceInfo1,
                                                     ExcelCoupleInfo excelCoupleInfo2, CellReferenceInfo referenceInfo2,
                                                     Set<String> errorsInfo, boolean haveErrors) {
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.LEAST_DOTS);
        if (isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParity(excelCoupleInfo1, excelCoupleInfo2)) {
            if (isLectures(excelCoupleInfo1, excelCoupleInfo2) || isPractices(excelCoupleInfo1, excelCoupleInfo2)) {
                return haveErrors;
            } else if (isLecture(excelCoupleInfo1)) {
                setStyleForCellsWithError(sheet, style, referenceInfo2, errorsInfo);
                return true;
            } else if (isLecture(excelCoupleInfo2)) {
                setStyleForCellsWithError(sheet, style, referenceInfo1, errorsInfo);
                return true;
            } else {
                setStyleForCellsWithError(sheet, style, referenceInfo2, errorsInfo);
                setStyleForCellsWithError(sheet, style, referenceInfo1, errorsInfo);
                return true;
            }
        }
        return haveErrors;
    }

    private static boolean findWarningsIntoExcel(List<CellReferenceInfo> cellReferenceInfoList, Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap, File file, Set<String> warningsInfo, int sheetIterator) throws IOException {
        boolean haveWarnings = false;
        FileInputStream inputStream = new FileInputStream(file);
        ZipSecureFile.setMinInflateRatio(0);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(sheetIterator);
        for (int i = 0; i < cellReferenceInfoList.size(); i++) {
            CellReferenceInfo referenceInfo1 = cellReferenceInfoList.get(i);
            ExcelCoupleInfo excelCoupleInfo1 = excelCoupleInfoMap.get(referenceInfo1);
            if (isPhysicalEducation(excelCoupleInfo1)) {
                continue;
            }
            for (int j = i + 1; j < cellReferenceInfoList.size(); j++) {
                CellReferenceInfo referenceInfo2 = cellReferenceInfoList.get(j);
                ExcelCoupleInfo excelCoupleInfo2 = excelCoupleInfoMap.get(referenceInfo2);
                if (isPhysicalEducation(excelCoupleInfo2)) {
                    continue;
                }
                haveWarnings = checkWarningsIntoExcelCells(workbook, sheet, excelCoupleInfo1, referenceInfo1, excelCoupleInfo2, referenceInfo2, warningsInfo, haveWarnings);
            }
        }
        try (OutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return haveWarnings;
    }

    private static boolean checkWarningsIntoExcelCells(XSSFWorkbook workbook, XSSFSheet sheet,
                                                       ExcelCoupleInfo excelCoupleInfo1, CellReferenceInfo referenceInfo1,
                                                       ExcelCoupleInfo excelCoupleInfo2, CellReferenceInfo referenceInfo2,
                                                       Set<String> warningsInfo, boolean haveWarnings) {
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.LEAST_DOTS);
        if (isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParityAndTeacherButOtherTitle(excelCoupleInfo1, excelCoupleInfo2)) {
            setStyleForCellsWithWarning(sheet, style, referenceInfo1, warningsInfo);
            setStyleForCellsWithWarning(sheet, style, referenceInfo2, warningsInfo);
            return true;
        }
        return haveWarnings;
    }

    private static void setStyleForCellsWithError(XSSFSheet sheet, CellStyle style, CellReferenceInfo referenceInfo, Set<String> errorsInfo) {
        getCellsWithError(sheet, style, referenceInfo.getCellTitleReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTeacherReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTypeReference());
        getCellsWithError(sheet, style, referenceInfo.getCellAudienceReference());
        String error = String.format("При выполнении первого этапа проверки в ячейках: %s %s %s %s обнаружена ошибка в аудитории",
                referenceInfo.getCellTitleReference().formatAsString(),
                referenceInfo.getCellTeacherReference().formatAsString(),
                referenceInfo.getCellTypeReference().formatAsString(),
                referenceInfo.getCellAudienceReference().formatAsString());
        errorsInfo.add(error);
    }

    private static void setStyleForCellsWithWarning(XSSFSheet sheet, CellStyle style, CellReferenceInfo referenceInfo, Set<String> errorsInfo) {
        getCellsWithError(sheet, style, referenceInfo.getCellTitleReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTeacherReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTypeReference());
        getCellsWithError(sheet, style, referenceInfo.getCellAudienceReference());
        String error = String.format("При выполнении первого этапа проверки в ячейках: %s %s %s %s обнаружена опасность в наименовании пар",
                referenceInfo.getCellTitleReference().formatAsString(),
                referenceInfo.getCellTeacherReference().formatAsString(),
                referenceInfo.getCellTypeReference().formatAsString(),
                referenceInfo.getCellAudienceReference().formatAsString());
        errorsInfo.add(error);
    }

    private static void getCellsWithError(XSSFSheet sheet, CellStyle style, CellReference cellReference) {
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());
        cell.setCellStyle(style);
    }

    private static void writeErrorsOrWarningsIntoFile(Set<String> errorsInfo, String fileWithErrorsInfoPathname) throws IOException {
        File fileWithErrorsInfo = new File(fileWithErrorsInfoPathname);
        FileWriter fos = new FileWriter(fileWithErrorsInfo);
        BufferedWriter bufferedWriter = new BufferedWriter(fos);
        for (String item : errorsInfo) {
            bufferedWriter.write(item);
            bufferedWriter.write("\r\n");
        }
        bufferedWriter.close();
    }

    private static void addNewInformationIntoCouplesInfo(List<ExcelCoupleInfo> couplesInfoFromDb, List<Schedule> scheduleListFromDb) {
        for (Schedule schedule : scheduleListFromDb) {
            for (Couple couple : schedule.getCouples()) {
                ExcelCoupleInfo coupleInfoFromDb = ExcelCoupleInfo.builder()
                        .title(couple.getTitle())
                        .type(couple.getType())
                        .audience(couple.getAudience())
                        .timeCouple(couple.getTimeCouple())
                        .teacher(couple.getTeacher())
                        .dayOfWeek(schedule.getDayOfWeek())
                        .team(schedule.getTeam())
                        .parity(schedule.getParity()).build();
                couplesInfoFromDb.add(coupleInfoFromDb);
            }
        }
    }

    private static boolean findErrorsIntoExcelWithDb(List<CellReferenceInfo> cellReferenceInfoList, Map<CellReferenceInfo, ExcelCoupleInfo> excelCoupleInfoMap,
                                                     List<ExcelCoupleInfo> couplesInfoFromDb, File file, Set<String> errorsInfo, int sheetIterator) throws IOException {
        boolean haveErrorsWithDb = false;
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(sheetIterator);
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.LESS_DOTS);
        for (ExcelCoupleInfo coupleInfoFromDb : couplesInfoFromDb) {
            if (isPhysicalEducation(coupleInfoFromDb)) {
                continue;
            }
            for (CellReferenceInfo referenceInfo : cellReferenceInfoList) {
                ExcelCoupleInfo excelCoupleInfo = excelCoupleInfoMap.get(referenceInfo);
                if (isPhysicalEducation(excelCoupleInfo)) {
                    continue;
                }
                haveErrorsWithDb = checkErrorsIntoExcelCellsSecondStep(workbook, sheet, coupleInfoFromDb, excelCoupleInfo, referenceInfo, errorsInfo, haveErrorsWithDb);
            }
        }
        try (OutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return haveErrorsWithDb;
    }

    private static boolean checkErrorsIntoExcelCellsSecondStep(XSSFWorkbook workbook, XSSFSheet sheet,
                                                               ExcelCoupleInfo coupleInfoFromDb, ExcelCoupleInfo excelCoupleInfo,
                                                               CellReferenceInfo referenceInfo, Set<String> errorsInfo, boolean haveErrors) {
        CellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.LEAST_DOTS);
        if (isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParity(coupleInfoFromDb, excelCoupleInfo)) {
            if (isLectures(coupleInfoFromDb,excelCoupleInfo)||isPractices(coupleInfoFromDb,excelCoupleInfo)){
                if (isSameGroup(coupleInfoFromDb,excelCoupleInfo)){
                    setStyleForCellsWithErrorForSecondStep(sheet, style, referenceInfo, errorsInfo,coupleInfoFromDb);
                    return true;
                }else {
                    return haveErrors;
                }
            }
            setStyleForCellsWithErrorForSecondStep(sheet, style, referenceInfo, errorsInfo, coupleInfoFromDb);
            return true;
        }
        return haveErrors;
    }

    private static void setStyleForCellsWithErrorForSecondStep(XSSFSheet sheet, CellStyle style, CellReferenceInfo referenceInfo, Set<String> errorsInfo, ExcelCoupleInfo coupleInfo) {
        getCellsWithError(sheet, style, referenceInfo.getCellTitleReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTeacherReference());
        getCellsWithError(sheet, style, referenceInfo.getCellTypeReference());
        getCellsWithError(sheet, style, referenceInfo.getCellAudienceReference());
        String error = String.format("При выполнении второго этапа проверки в ячейках: %s %s %s %s обнаружена ошибка в аудитории. В веб-интерфейсе есть похожая пара: %s %s %s %s %s для группы %s и день недели %s %s",
                referenceInfo.getCellTitleReference().formatAsString(),
                referenceInfo.getCellTeacherReference().formatAsString(),
                referenceInfo.getCellTypeReference().formatAsString(),
                referenceInfo.getCellAudienceReference().formatAsString(),
                coupleInfo.getTitle(),
                coupleInfo.getTeacher(),
                coupleInfo.getType(),
                coupleInfo.getTimeCouple(),
                coupleInfo.getAudience(),
                coupleInfo.getTeam(),
                coupleInfo.getDayOfWeek(),
                coupleInfo.getParity()
        );
        errorsInfo.add(error);
    }

    private static boolean isSameGroup(ExcelCoupleInfo excelCoupleInfo1, ExcelCoupleInfo excelCoupleInfo2) {
        return excelCoupleInfo1.getTeam().equals(excelCoupleInfo2.getTeam());
    }

    private static boolean isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParityAndTeacherButOtherTitle(ExcelCoupleInfo excelCoupleInfo1, ExcelCoupleInfo excelCoupleInfo2) {
        return isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParity(excelCoupleInfo1, excelCoupleInfo2) &&
                excelCoupleInfo1.getTeacher().equals(excelCoupleInfo2.getTeacher()) &&
                !excelCoupleInfo1.getTitle().equals(excelCoupleInfo2.getTitle());
    }

    private static boolean isHaveEqualsDayOfWeekAndTimeCoupleAndAudienceAndParity(ExcelCoupleInfo excelCoupleInfo1, ExcelCoupleInfo excelCoupleInfo2) {
        return excelCoupleInfo1.getDayOfWeek().equals(excelCoupleInfo2.getDayOfWeek()) &&
                excelCoupleInfo1.getTimeCouple().compareTo(excelCoupleInfo2.getTimeCouple()) == 0 &&
                excelCoupleInfo1.getAudience().equals(excelCoupleInfo2.getAudience()) &&
                excelCoupleInfo1.getParity().equals(excelCoupleInfo2.getParity());
    }

    private static boolean isLectures(ExcelCoupleInfo excelCoupleInfo1, ExcelCoupleInfo excelCoupleInfo2) {
        return excelCoupleInfo1.getType().equals(excelCoupleInfo2.getType()) &&
                excelCoupleInfo1.getTeacher().equals(excelCoupleInfo2.getTeacher()) &&
                (excelCoupleInfo1.getType().trim().equals(SPECIAL_TYPE_1.trim()) || excelCoupleInfo1.getType().trim().equalsIgnoreCase(SPECIAL_TYPE_1.trim())) &&
                (excelCoupleInfo2.getType().trim().equals(SPECIAL_TYPE_1.trim()) || excelCoupleInfo2.getType().trim().equalsIgnoreCase(SPECIAL_TYPE_1.trim()));
    }

    private static boolean isPractices(ExcelCoupleInfo excelCoupleInfo1, ExcelCoupleInfo excelCoupleInfo2){
        return excelCoupleInfo1.getType().equals(excelCoupleInfo2.getType()) &&
                excelCoupleInfo1.getTeacher().equals(excelCoupleInfo2.getTeacher()) &&
                (excelCoupleInfo1.getType().trim().equals(SPECIAL_TYPE_2.trim()) || excelCoupleInfo1.getType().trim().equalsIgnoreCase(SPECIAL_TYPE_2.trim())) &&
                (excelCoupleInfo2.getType().trim().equals(SPECIAL_TYPE_2.trim()) || excelCoupleInfo2.getType().trim().equalsIgnoreCase(SPECIAL_TYPE_2.trim()));
    }

    private static boolean isLecture(ExcelCoupleInfo excelCoupleInfo) {
        return excelCoupleInfo.getType().trim().equals(SPECIAL_TYPE_1.trim()) ||
                excelCoupleInfo.getType().trim().equalsIgnoreCase(SPECIAL_TYPE_1.trim());
    }

    private static boolean isPhysicalEducation(ExcelCoupleInfo excelCoupleInfo) {
        return excelCoupleInfo.getTitle().equals(SPECIAL_CASE) || excelCoupleInfo.getTitle().equalsIgnoreCase(SPECIAL_CASE);
    }

    private static void addNewInfoIntoSchedule(List<Schedule> scheduleListForAddIntoDb, List<ScheduleDto> scheduleDtoListForAddIntoDb) {
        for (ScheduleDto scheduleDto : scheduleDtoListForAddIntoDb) {
            if (scheduleDto.getCouples().size() != 0) {
                Schedule schedule = new Schedule();
                schedule.setDayOfWeek(scheduleDto.getDayOfWeek().trim());
                schedule.setTeam(scheduleDto.getTeam().trim());
                schedule.setParity(scheduleDto.getParity().trim());
                schedule.setAuthor(scheduleDto.getAuthor());
                List<Couple> couples = new ArrayList<>();
                for (CoupleDto coupleDtoIterator : scheduleDto.getCouples()) {
                    Couple couple = new Couple();
                    couple.setTitle(coupleDtoIterator.getTitle().trim());
                    couple.setTimeCouple(coupleDtoIterator.getTimeCouple());
                    if (nonNull(coupleDtoIterator.getType())) {
                        couple.setType(coupleDtoIterator.getType().trim());
                    }
                    if (nonNull(coupleDtoIterator.getAudience())) {
                        couple.setAudience(coupleDtoIterator.getAudience().trim());
                    }
                    if (nonNull(coupleDtoIterator.getTeacher())) {
                        couple.setTeacher(coupleDtoIterator.getTeacher().trim());
                    }
                    couples.add(couple);
                }
                schedule.setCouples(couples);
                scheduleListForAddIntoDb.add(schedule);
            }
        }
    }
}
