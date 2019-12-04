package com.test.excelTojson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

/**
 * altered by Evan on 2019-3-4
 */
public class EXCELUtil {

    /**
     * 将EXCEL数据封装成JSON列表
     *
     * @param filePath 文件路径
     * @return
     * @throws JSONException
     */
    public static List<JSONObject> readFile(String filePath) throws JSONException {
        // 校验文件路径是否为空
        if (StringUtils.isEmpty(filePath)) return null;

        Workbook wb;
        Sheet sheet;
        Row row;
        String cellData;

        // 读取Excel
        wb = readExcel(filePath);
        // 用来存放每一行数据的列表
        List<JSONObject> jsonObjectList = new ArrayList<>();

        if (wb != null) {
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rowNum = sheet.getPhysicalNumberOfRows();
            //获取第一行（通常第一行为列名）
            row = sheet.getRow(0);
            //获取最大列数
            int colNum = row.getPhysicalNumberOfCells();
            //用来存放列名
            List<String> columnNameList = new ArrayList<>();
            //循环列，将列名取出放入List
            for (int i = 0; i < colNum; i++) {
                // 获取列的值，放入列名列表内
                String columnName = row.getCell(i).getStringCellValue();
                columnNameList.add(columnName);
            }

            //循环行，从第二行开始取行数据（因为第一行为列名）
            for (int i = 1; i < rowNum; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    List<String> dataList = new ArrayList<>();
                    //循环列，将行数据取出放入List
                    for (int j = 0; j < colNum; j++) {
                        cellData = getCellFormatValue(row.getCell(j)).toString();
                        dataList.add(cellData);
                    }
                    // 创建行数据JSON对象
                    JSONObject jsonData = new JSONObject();
                    for (int k = 0; k < dataList.size(); k++) {
                        // 按照 列名：行数据 的格式存放
                        jsonData.put(columnNameList.get(k), dataList.get(k));
                    }
                    jsonObjectList.add(jsonData);
                } else {
                    break;
                }
            }
        }
        return jsonObjectList;
    }


    /**
     * 读取EXCEL
     *
     * @param filePath
     * @return
     */
    public static Workbook readExcel(String filePath) {
        if (filePath == null) return null;
        // 第一版从本地文件读取
        URL httpUrl;
        InputStream is = null;
        try {
            //httpUrl = new URL(filePath);
            //URLConnection urlConnection = httpUrl.openConnection();
            is = new FileInputStream(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Workbook wb = null;

        String extString = filePath.substring(filePath.lastIndexOf("."));
        try {
            if (".xls".equals(extString)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                wb = new XSSFWorkbook(is);
            } else {
                wb = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 从row中取出数据
     *过滤了字符串，数字，布尔，时间类型
     * @param cell
     * @return
     */
    public static Object getCellFormatValue(Cell cell) {
        Object cellValue;
        if (cell != null) {
            //判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                case Cell.CELL_TYPE_BOOLEAN: {
                    cellValue = cell.getBooleanCellValue();
                    cellValue.toString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }}


