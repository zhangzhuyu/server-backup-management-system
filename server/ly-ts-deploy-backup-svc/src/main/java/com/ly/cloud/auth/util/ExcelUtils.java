package com.ly.cloud.auth.util;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;

import java.util.List;

/**
 * Excel工具类
 *
 *
 */
public class ExcelUtils {

    /**
     * 验证EXCEL文件
     *
     * @param fileName
     * @return
     */
    public static boolean validateExcel(String fileName) {
        if (fileName == null || !(isExcelXls(fileName) || isExcelXlsx(fileName))) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为2003版本excel，是则返回true
     *
     * @param filePath
     * @return
     */
    public static boolean isExcelXls(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 判断是否为2007版本excel，是则返回true
     *
     * @param filePath
     * @return
     */
    public static boolean isExcelXlsx(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 判断row是否为空
     *
     * @param row
     * @return
     */
    public static boolean isRowEmpty(Row row) {
        if (null == row) {
            return true;
        }
        //第一个列位置
        int firstCellNum = row.getFirstCellNum();
        //最后一列位置
        int lastCellNum = row.getLastCellNum();
        //空列数量
        int nullCellNum = 0;
        for (int c = firstCellNum; c < lastCellNum; c++) {
            Cell cell = row.getCell(c);
            if (null == cell || CellType.STRING == cell.getCellType()) {
                nullCellNum ++;
                continue;
            }
            cell.setCellType(CellType.STRING);
            String cellValue = cell.getStringCellValue().trim();
            if (StringUtils.isEmpty(cellValue)) {
                nullCellNum ++;
            }
        }
        //所有列都为空
        if (nullCellNum == (lastCellNum - firstCellNum)) {
            return true;
        }
        return false;
    }

    /**
     * 生成Excel导入模板
     * @param title 列标题数组
     * @param downData 下拉框数据
     * @param downColumns 下拉框列下标数组
     */
    public static XSSFWorkbook createExcelTemplate(String[] title, List<String[]> downData, String[] downColumns){
        // 创建工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 表头样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 字体样式
        XSSFFont fontStyle = workbook.createFont();
        fontStyle.setFontName("微软雅黑");
        fontStyle.setFontHeightInPoints((short)12);
        style.setFont(fontStyle);
        // 新建sheet
        XSSFSheet sheet1 = workbook.createSheet("Sheet1");
        XSSFSheet sheet2 = workbook.createSheet("Sheet2");
        // 生成sheet1内容
        // 第一个sheet的第一行为标题
        XSSFRow rowFirst = sheet1.createRow(0);
        // 写标题
        for(int i=0;i<title.length;i++){
            // 获取第一行的每个单元格
            XSSFCell cell = rowFirst.createCell(i);
            // 设置每列的列宽
            sheet1.setColumnWidth(i, 4000);
            // 加样式
            cell.setCellStyle(style);
            // 往单元格里写数据
            cell.setCellValue(title[i]);
        }
        // 设置下拉框数据
        String[] arr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int index = 0;
        XSSFRow row = null;
        for(int i=0;i<downColumns.length;i++){
            // 获取下拉对象
            String[] dlData = downData.get(i);
            int columnNum = Integer.parseInt(downColumns[i]);
            // 255以内的下拉
            /*if(dlData.length<255){*/
                // 255以内的下拉,参数分别是：作用的sheet、下拉内容数组、起始行、终止行、起始列、终止列
                sheet1.addValidationData(setDataValidation(sheet1, dlData, 1, 500, columnNum ,columnNum));
            /*} else {
                // 255以上的下拉,
                // 1、设置有效性
                // Sheet2第A1到A5000作为下拉列表来源数据
                String strFormula = "Sheet2!$"+arr[index]+"$1:$"+arr[index]+"$5000";
                // 设置每列的列宽
                sheet2.setColumnWidth(i, 4000);
                // 设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
                //下拉列表元素很多的情况
                sheet1.addValidationData(SetDataValidation(strFormula, 1, 50000, columnNum, columnNum));
                //2、生成sheet2内容
                for(int j=0;j<dlData.length;j++){
                    if(index==0){ //第1个下拉选项，直接创建行、列
                        // 创建数据行
                        row = sheet2.createRow(j);
                        // 设置每列的列宽
                        sheet2.setColumnWidth(j, 4000);
                        // 设置对应单元格的值
                        row.createCell(0).setCellValue(dlData[j]);
                    } else { //非第1个下拉选项
                        int rowCount = sheet2.getLastRowNum();
                        // 前面创建过的行，直接获取行，创建列
                        if(j<=rowCount){
                            // 获取行，创建列
                            // 设置对应单元格的值
                            sheet2.getRow(j).createCell(index).setCellValue(dlData[j]);
                        } else { //未创建过的行，直接创建行、创建列
                            // 设置每列的列宽
                            sheet2.setColumnWidth(j, 4000);
                            // 创建行、创建列
                            // 设置对应单元格的值
                            sheet2.createRow(j).createCell(index).setCellValue(dlData[j]);
                        }
                    }
                }
                index++;
            }*/
        }
        return workbook;
    }

    /**
     *
     * 下拉列表元素超过255的设置方法
     * @param strFormula
     * @param firstRow   起始行
     * @param endRow     终止行
     * @param firstCol   起始列
     * @param endCol     终止列
     * @return HSSFDataValidation
     */
    private static HSSFDataValidation SetDataValidation(String strFormula, int firstRow, int endRow, int firstCol, int endCol) {
        // 设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(regions,constraint);
        dataValidation.createErrorBox("Error", "Error");
        dataValidation.createPromptBox("", null);
        return dataValidation;
    }

    /**
     * 下拉列表元素255以内的设置方法
     * @param sheet
     * @param textList
     * @param firstRow
     * @param endRow
     * @param firstCol
     * @param endCol
     * @return DataValidation
     */
    private static DataValidation setDataValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        //加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        constraint.setExplicitListValues(textList);
        //设置数据有效性加载在哪个单元格上。四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow,firstCol, endCol);
        //数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        return data_validation;
    }

    /**
     * 生成指定表名和标题的HSSFWorkbook（单表）
     * @param sheetName  表名
     * @param title   标题数组
     * @return
     */
    public static HSSFWorkbook createHSSFWorkbook(String sheetName, String[] title) {
        // 新建文档实例
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 在文档中添加表单
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 创建单元格格式，并设置居中
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        //标题字体式格式
        HSSFFont fontStyle = workbook.createFont();
        fontStyle.setFontName("宋体");
        //fontStyle.setBold(true);
        fontStyle.setFontHeightInPoints((short) 14);
        fontStyle.setColor(Font.COLOR_NORMAL);
        style.setFont(fontStyle);
        //创建第一行，用于填充标题
        HSSFRow titleRow = sheet.createRow(0);
        //填充表头标题
        for (int i = 0; i < title.length; i++) {
            sheet.setColumnWidth(i, 18 * 256);
            //创建单元格
            HSSFCell cell = titleRow.createCell(i);
            //设置单元格内容
            cell.setCellValue(title[i]);
            //设置单元格样式
            cell.setCellStyle(style);
        }
        return workbook;
    }

    /**
     * Excel表格添加批注
     * @return
     */
    public static void setCommentsOfPoiCell(int row,int col, Cell poiCell,Row poiRow,String columnComment) {
        //增加批注
        if (StringUtils.isNotBlank(columnComment) && !"null".equals(columnComment)) {
            ClientAnchor anchor = new XSSFClientAnchor();
            //dx1、dy1、dx2、dy2 四个参数是坐标点
            //dx1、dy1为起始单元格的x、y坐标，dx2、dy2为结束单元格的x、y坐标
            anchor.setDx1(0);
            anchor.setDx2(0);
            anchor.setDy1(0);
            anchor.setDy2(0);
            //col1、row1、col2、row2 四个参数是编辑和显示批注时的大小
            //col1、row1为单元格的起始列、行，col2、row2为单元格的终止列、行
            anchor.setCol1(col);
            anchor.setRow1(row);
            anchor.setCol2(col + 1);
            anchor.setRow2(row + 1);
            Drawing draw = poiRow.getSheet().createDrawingPatriarch();
            Comment commentOfCell = draw.createCellComment(anchor);
            commentOfCell.setString(new XSSFRichTextString(columnComment));
            poiCell.setCellComment(commentOfCell);
        }
    }
}