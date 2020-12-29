package com.ustc.plugins.service;

import com.ustc.plugins.annotation.Excel;
import com.ustc.plugins.annotation.Title;
import com.ustc.plugins.annotation.TitleEnum;
import com.ustc.plugins.model.DefaultTitleInfo;
import com.ustc.plugins.model.TitleInfo;
import com.ustc.plugins.style.HyperLinkStyle;
import com.ustc.plugins.style.IConvert;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/9/24<br>
 * filename: ExcelService<br>
 * <p>
 * description:<br>
 * excel服务类
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
@Log4j
@Data
public class ExcelService {

    private ApplicationContext applicationContext;

    private static Pattern linePattern = Pattern.compile("\\r\\n|\\n");

    /**
     * 将文件转化成数据对象
     *
     * @param file 文件对象
     * @return 结果集合
     */
    public <T> List<T> readExcel(File file, Class<T> tClass, IFilterData<T> filterData) throws Exception {
        try (
                FileInputStream fis = new FileInputStream(file)
        ) {
            Workbook workbook = new HSSFWorkbook();
            String xls = ".xls", xlsx = ".xlsx";
            if (file.isFile() && file.exists()) {
                if (file.getName().endsWith(xls)) {
                    workbook = new HSSFWorkbook(fis);
                } else if (file.getName().endsWith(xlsx)) {
                    workbook = new XSSFWorkbook(fis);
                }
            }
            Excel excel = tClass.getAnnotation(Excel.class);
            TitleEnum type = excel.type();
            if (TitleEnum.index == type) {
                return readDataByIndex(workbook, excel.titleRow(), tClass, filterData);
            } else {
                return readDataByTitle(workbook, excel.titleRow(), tClass, filterData);
            }
        }
    }


    /**
     * 根据标题定义来读取数据
     *
     * @param workbook      文件对象
     * @param titleRowIndex 标题行
     * @param tClass        类对象
     * @param filterData    过滤方法
     * @param <T>           泛型
     * @return 数据集
     * @throws Exception 异常
     */
    private <T> List<T> readDataByTitle(Workbook workbook, int titleRowIndex, Class<T> tClass, IFilterData<T> filterData) throws Exception {
        List<T> result = new ArrayList<>();
        List<TitleInfo> titleInfos = getTitleInfoByTitle(tClass);
        int sheets = workbook.getNumberOfSheets();
        for (int i = 0; i < sheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Row titleRow = sheet.getRow(titleRowIndex);
            checkTitleByTitle(titleRow, titleInfos);
            Map<Integer, TitleInfo> indexOfMethod = getIndexOfTitleInfo(titleRow, titleInfos);
            readSheet(sheet, titleRowIndex, tClass, indexOfMethod, result, filterData);
        }
        return result;
    }


    /**
     * 读sheet数据
     *
     * @param sheet         SHEET
     * @param titleRowIndex 标题行
     * @param tClass        类
     * @param indexOfMethod 方法
     * @param result        结果
     * @param filterData    过滤数据方法
     * @param <T>           泛型
     * @throws Exception 异常
     */
    private <T> void readSheet(Sheet sheet, int titleRowIndex, Class<T> tClass, Map<Integer, TitleInfo> indexOfMethod, List<T> result, IFilterData<T> filterData) throws Exception {
        int rowNum = sheet.getLastRowNum();
        for (int j = titleRowIndex + 1; j <= rowNum; j++) {
            Row tmp = sheet.getRow(j);
            StringBuilder sb = new StringBuilder();
            if (tmp != null) {
                T data = readData(tClass, tmp, indexOfMethod, sb);
                if (data != null && filterData.filter(data, result, sb) != null) {
                    result.add(data);
                }
            }
        }
    }

    /**
     * 根据坐标读取数据
     *
     * @param workbook      文件对象
     * @param titleRowIndex 标题行
     * @param tClass        类对象
     * @param filterData    过滤方法
     * @param <T>           泛型
     * @return 数据集
     * @throws Exception 异常
     */
    private <T> List<T> readDataByIndex(Workbook workbook, int titleRowIndex, Class<T> tClass, IFilterData<T> filterData) throws Exception {
        List<T> result = new ArrayList<>();
        List<TitleInfo> titleInfos = getTitleInfoByTitle(tClass);
        int sheets = workbook.getNumberOfSheets();
        for (int i = 0; i < sheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Row titleRow = sheet.getRow(titleRowIndex);
            checkTitleByIndex(titleRow, titleInfos);
            Map<Integer, TitleInfo> indexOfMethod = getIndexOfTitleInfo(titleInfos);
            readSheet(sheet, titleRowIndex, tClass, indexOfMethod, result, filterData);
        }
        return result;
    }

    /**
     * 检查标题信息
     *
     * @param row        行
     * @param titleInfos 标题信息
     * @throws Exception 异常
     */
    private void checkTitleByIndex(Row row, List<TitleInfo> titleInfos) throws Exception {
        if (row == null) {
            throw new Exception("标题未定义！");
        }
        for (TitleInfo titleInfo : titleInfos) {
            Cell cell = row.getCell(titleInfo.getColIndex());
            String cellValue = getCellValue(cell);
            if (StringUtils.isEmpty(cellValue) || !cellValue.equals(titleInfo.getTitle())) {
                throw new Exception("标题定义不一致！");
            }
        }
    }

    /**
     * 检查标题信息
     *
     * @param row        行
     * @param titleInfos 标题信息
     * @throws Exception 异常
     */
    private void checkTitleByTitle(Row row, List<TitleInfo> titleInfos) throws Exception {
        if (row == null) {
            throw new Exception("标题未定义！");
        }
        int numberOfCells = row.getPhysicalNumberOfCells();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < numberOfCells; i++) {
            titles.add(getCellValue(row.getCell(i)));
        }
        for (TitleInfo titleInfo : titleInfos) {

            if (titleInfo.isNeed()) {
                boolean match = false;
                if (titleInfo.isRegexp()) {
                    for (String title : titles) {
                        if (matches(titleInfo.getTitle(), title)) {
                            match = true;
                            break;
                        }
                    }
                } else {
                    match = titles.contains(titleInfo.getTitle());
                }

                if (!match) {
                    throw new Exception(titleInfo.getTitle() + "未定义！");
                }
            }

        }
    }

    /**
     * 读取各标题的rowindex
     *
     * @param titleInfos 标题信息
     * @return map
     */
    private Map<Integer, TitleInfo> getIndexOfTitleInfo(List<TitleInfo> titleInfos) {
        Map<Integer, TitleInfo> result = new HashMap<>(titleInfos.size());
        titleInfos.forEach(ele -> result.put(ele.getColIndex(), ele));
        return result;
    }

    /**
     * 读取各标题的rowindex
     *
     * @param titleInfos 标题信息
     * @return map
     */
    private Map<Integer, TitleInfo> getIndexOfTitleInfo(Row row, List<TitleInfo> titleInfos) {
        int physicalNumberOfCells = row.getPhysicalNumberOfCells();
        Map<Integer, TitleInfo> result = new HashMap<>(physicalNumberOfCells);
        for (TitleInfo titleInfo : titleInfos) {
            Map<String, Integer> countMap = new HashMap<>(1);
            for (int i = 0; i < physicalNumberOfCells; i++) {
                Cell cell = row.getCell(i);
                String cellValue = getCellValue(cell).trim();
                if (countMap.containsKey(cellValue)) {
                    countMap.put(cellValue, countMap.get(cellValue) + 1);
                } else {
                    countMap.put(cellValue, 1);
                }
                if (titleInfo.getSort().equals(countMap.get(cellValue))) {
                    if (titleInfo.isRegexp()) {
                        if (matches(titleInfo.getTitle(), cellValue)) {
                            if (titleInfo.isRelated()) {
                                result.put(i + titleInfo.getRelatedOffset(), titleInfo);
                            } else {
                                result.put(i, titleInfo);
                            }
                            break;
                        }
                    } else if (titleInfo.getTitle().equals(cellValue)) {
                        if (titleInfo.isRelated()) {
                            result.put(i + titleInfo.getRelatedOffset(), titleInfo);
                        } else {
                            result.put(i, titleInfo);
                        }
                        break;
                    }
                }
            }

        }

        return result;
    }

    /**
     * 读数据并转化为对象
     *
     * @param row     excel行数据
     * @param infoMap 标题信息集合
     * @return 对象T
     * @throws Exception 异常
     */
    private <T> T readData(Class<T> tClass, Row row, Map<Integer, TitleInfo> infoMap, StringBuilder sb) throws Exception {
        T newInstanceOfT = tClass.newInstance();
        Set<Map.Entry<Integer, TitleInfo>> entries = infoMap.entrySet();
        for (Map.Entry<Integer, TitleInfo> next : entries) {
            Integer index = next.getKey();
            Cell cell = row.getCell(index);
            String value = getCellValue(cell);
            TitleInfo titleInfo = next.getValue();
            if (titleInfo.isNeed() && StringUtils.isEmpty(value)) {
                sb.append(titleInfo.getTitle()).append("不能为空！").append("\r\n");
            } else {
                titleInfo.setValue(titleInfo.convert(value), newInstanceOfT);
            }
        }
        return newInstanceOfT;
    }

    /**
     * 获取cell的值
     *
     * @param cell cell
     * @return 值
     */
    private String getCellValue(Cell cell) {
        String value;
        if (cell != null) {
            // 以下是判断数据的类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        if (date != null) {
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        } else {
                            value = "";
                        }
                    } else {
                        String formatType = cell.getCellStyle().getDataFormatString();
                        if (formatType != null && formatType.contains("%")) {
                            int baifenshuxiaoshuwei = 0;
                            if (formatType.contains(".")) {
                                baifenshuxiaoshuwei = formatType.substring(formatType.indexOf("."), formatType.indexOf("%")).length() - 1;
                                value = "#.";
                                for (int weishu = 0; weishu < baifenshuxiaoshuwei; weishu++) {
                                    value += "0";
                                }
                                value = new DecimalFormat(value).format(cell.getNumericCellValue() * 100) + "%";
                                if (value.startsWith(".")) {
                                    value = "0" + value;
                                }
                            } else {
                                value = Math.round(cell.getNumericCellValue() * 100) + "%";
                            }
                        } else {
                            value = new DecimalFormat("0").format(cell.getNumericCellValue());
                        }

                    }
                    break;
                // 字符串
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                // Boolean
                case Cell.CELL_TYPE_BOOLEAN:
                    value = cell.getBooleanCellValue() + "";
                    break;
                // 公式
                case Cell.CELL_TYPE_FORMULA:
                    try {
                        value = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        value = String.valueOf(cell.getRichStringCellValue());
                    }
                    break;
                // 空值
                case Cell.CELL_TYPE_BLANK:
                    value = "";
                    break;
                // 故障
                case Cell.CELL_TYPE_ERROR:
                    value = "非法字符";
                    break;
                default:
                    value = "未知类型";
                    break;
            }
            return removeLineSeparate(value).trim();
        }
        return "";
    }


    /**
     * excel导出方法
     *
     * @param total     总数
     * @param file      导出文件
     * @param fetchData 自定义截取数据方法
     * @param <T>       泛型
     */
    public <T> void exportExcel(int total, File file, IFetchData<T> fetchData) {
        if (total == 0) {
            return;
        }
        try (
                FileOutputStream fos = new FileOutputStream(file)
        ) {
            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            Sheet sheet = wb.createSheet("Sheet1");
            writeDataToSheet(wb, sheet, total, fetchData);
            log.info("excel生成成功！");
            wb.write(fos);
        } catch (Exception es) {
            es.printStackTrace();
        }
    }

    /**
     * @param data 导出数据方法
     * @param file 输出文件
     * @param <T>  泛型
     */
    public <T> void exportExcel(List<T> data, File file) {
        if (data.isEmpty()) {
            return;
        }
        try (
                FileOutputStream fos = new FileOutputStream(file)
        ) {
            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            Sheet sheet = wb.createSheet("Sheet1");
            writeDataToSheet(wb, sheet, data.size(), (cycle, size) -> {
                int startIndex = cycle * size;
                int endIndex = Math.min((cycle + 1) * size, data.size());
                return Collections.synchronizedList(data.subList(startIndex, endIndex));
            });
            log.info("excel生成成功！");
            wb.write(fos);
        } catch (Exception es) {
            es.printStackTrace();
        }
    }


    /**
     * 写数据到sheet中
     *
     * @param wb    wb对象
     * @param sheet sheet对象
     * @param <T>   泛型
     */
    private <T> void writeDataToSheet(SXSSFWorkbook wb, Sheet sheet, int total, IFetchData<T> fetchData) {
        try {
            int size = 10000;
            int cycle = Math.floorDiv(total, size) + 1;
            Map<Integer, List<TitleInfo>> titleInfo = new HashMap<>(1);

            for (int i = 0; i < cycle; i++) {
                List<T> subData = fetchData.subData(i, size);
                Class<?> aClass = subData.get(0).getClass();
                Excel excel = aClass.getAnnotation(Excel.class);
                if (i == 0) {
                    Class<?> titleInfoClass = excel.titleInfo();
                    if (titleInfoClass.equals(DefaultTitleInfo.class)) {
                        List<TitleInfo> titleInfoByTitle = getTitleInfoByTitle(aClass);
                        titleInfo.put(0, titleInfoByTitle);
                    } else {
                        titleInfo = getTitleInfoByTitleInfo(titleInfoClass);
                    }
                    writeTitlesToSheet(wb, sheet, titleInfo, excel.type());
                }
                int startIndex = i * size + titleInfo.size();
                List<TitleInfo> dataTitle = getTitleInfoByTitle(aClass);
                writeRowsToSheet(wb, sheet, subData, dataTitle, startIndex);
            }
            for (int i = 0; i < titleInfo.get(titleInfo.size() - 1).size(); i++) {
                sheet.autoSizeColumn(i);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 写数据到excel中
     *
     * @param wb         wb对象
     * @param sheet      sheet 对象
     * @param data       数据对象
     * @param titleInfos 标题信息
     * @param <T>        泛型对象
     */
    private <T> void writeRowsToSheet(SXSSFWorkbook wb, Sheet sheet, List<T> data, List<TitleInfo> titleInfos, int startIndex) {

        AtomicInteger rowIndex = new AtomicInteger(startIndex);

        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        dataFont.setColor(IndexedColors.BLACK.index);

        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        dataStyle.setFont(dataFont);
        setBorder(dataStyle, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);

        data.forEach(ele -> {
            Row dataRow = sheet.createRow(rowIndex.getAndAdd(1));
            AtomicInteger colIndex = new AtomicInteger(0);
            titleInfos.forEach(titleInfo -> {
                Cell cell = dataRow.createCell(titleInfo.getColIndex() == -1 ? colIndex.getAndAdd(1) : titleInfo.getColIndex());
                Object cellData = titleInfo.getValue(ele);
                if (cellData != null) {
                    if (cellData instanceof Date) {
                        cell.setCellValue(titleInfo.convert(dateFormat((Date) cellData, "yyyy-MM-dd HH:mm:ss")));
                    } else {
                        cell.setCellValue(titleInfo.convert(cellData.toString()));
                    }
                    if (titleInfo.getHyperlink() != null) {
                        cell.setCellFormula(titleInfo.setHyperLinkStyle(cellData.toString()));
                    }
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(dataStyle);
            });
        });
    }

    /**
     * 写标题
     *
     * @param wb     wb对象
     * @param sheet  sheet
     * @param titles 标题
     */
    private void writeTitlesToSheet(SXSSFWorkbook wb, Sheet sheet, Map<Integer, List<TitleInfo>> titles, TitleEnum titleEnum) {
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setColor(IndexedColors.BLACK.index);

        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        titleStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, HSSFCellStyle.BORDER_THIN, HSSFColor.BLACK.index);
        AtomicBoolean multi = new AtomicBoolean(titles.size() > 1);

        titles.forEach((key, val) -> {
            Row titleRow = sheet.createRow(key);
            AtomicInteger colIndexIncrement = new AtomicInteger(0);
            val.forEach(titleInfo -> {
                if (multi.get()) {
                    Integer rowIndex = titleInfo.getRowIndex();
                    Integer colIndex = titleInfo.getColIndex();
                    Cell cell = titleRow.createCell(colIndex);
                    cell.setCellValue(titleInfo.getTitle());
                    cell.setCellStyle(titleStyle);
                    CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex + titleInfo.getRowspan() - 1, colIndex, colIndex + titleInfo.getColspan() - 1);
                    sheet.addMergedRegion(region);
                } else {
                    Cell cell;
                    if (TitleEnum.index.equals(titleEnum)) {
                        cell = titleRow.createCell(titleInfo.getColIndex());
                    } else {
                        cell = titleRow.createCell(colIndexIncrement.getAndAdd(1));
                    }
                    cell.setCellValue(titleInfo.getTitle());
                    cell.setCellStyle(titleStyle);
                }
            });
        });

    }

    /**
     * 设置边框属性
     *
     * @param style  样式
     * @param border 边框
     * @param color  着色
     */
    private void setBorder(CellStyle style, short border, short color) {
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setTopBorderColor(color);
        style.setLeftBorderColor(color);
        style.setRightBorderColor(color);
        style.setBottomBorderColor(color);
    }

    /**
     * 获取标题信息
     *
     * @param aClass 类对象
     * @param <T>    泛型
     * @return 标题信息
     * @throws NoSuchMethodException 异常
     */
    private <T> List<TitleInfo> getTitleInfoByTitle(Class<T> aClass) throws NoSuchMethodException {
        Field[] fields = aClass.getDeclaredFields();
        List<TitleInfo> titles = new ArrayList<>();
        for (Field field : fields) {
            Title annotation = field.getAnnotation(Title.class);
            if (annotation != null) {
                int rowIndex = annotation.rowIndex();
                String fieldName = field.getName();
                TitleInfo titleInfo = new TitleInfo();
                titleInfo.setTitle(annotation.value());
                titleInfo.setSort(annotation.sort());
                titleInfo.setNeed(annotation.need());
                titleInfo.setField(field);
                titleInfo.setRegexp(annotation.regexp());
                titleInfo.setRelated(annotation.related());
                titleInfo.setRelatedOffset(annotation.relatedOffset());
                titleInfo.setColIndex(annotation.colIndex());
                titleInfo.setRowIndex(rowIndex);
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method = aClass.getMethod(getMethodName);
                titleInfo.setGetMethod(method);
                String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method setMethod = aClass.getMethod(setMethodName, String.class);
                titleInfo.setSetMethod(setMethod);
                if (!StringUtils.isEmpty(annotation.hyperlink())) {
                    HyperLinkStyle style = (HyperLinkStyle) applicationContext.getBean(annotation.hyperlink());
                    titleInfo.setHyperlink(style);
                }
                if (!StringUtils.isEmpty(annotation.convert())) {
                    IConvert convert = (IConvert) applicationContext.getBean(annotation.convert());
                    titleInfo.setConvert(convert);
                }
                titles.add(titleInfo);
            }
        }
        return titles;
    }

    /**
     * 获取标题信息
     *
     * @param aClass 类对象
     * @return 标题信息
     */
    private Map<Integer, List<TitleInfo>> getTitleInfoByTitleInfo(Class<?> aClass) {
        Field[] fields = aClass.getDeclaredFields();
        Map<Integer, List<TitleInfo>> map = new HashMap<>(1);
        for (Field field : fields) {
            com.ustc.plugins.annotation.TitleInfo annotation = field.getAnnotation(com.ustc.plugins.annotation.TitleInfo.class);
            if (annotation != null) {
                int rowIndex = annotation.rowIndex();
                TitleInfo titleInfo = new TitleInfo();
                titleInfo.setTitle(annotation.value());
                titleInfo.setColIndex(annotation.colIndex());
                titleInfo.setRowIndex(rowIndex);
                titleInfo.setColspan(annotation.colspan());
                titleInfo.setRowspan(annotation.rowspan());
                map.computeIfAbsent(rowIndex, k -> new ArrayList<>());
                map.get(rowIndex).add(titleInfo);
            }
        }
        return map;
    }

    /**
     * 去掉所有的换行分隔符
     *
     * @param content 文本内容
     * @return 替换后文本内容
     */
    private String removeLineSeparate(String content) {
        if (content == null) {
            return null;
        }
        Matcher matcher = linePattern.matcher(content);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return content.replace("|", "");
    }

    /**
     * 格式化日期
     *
     * @param date   日期
     * @param format 格式化语句
     * @return 格式化日期
     */
    private String dateFormat(Date date, String format) {
        try {
            if (date == null) {
                return "";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            log.error(e);
            return "";
        }
    }


    /**
     * 正则表达式匹配
     *
     * @param regex   正则表达式
     * @param content 内容
     * @return true 匹配
     */
    private boolean matches(String regex, String content) {
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }
}
