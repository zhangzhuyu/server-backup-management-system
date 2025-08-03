
package com.ly.cloud.backup.util;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class PoiTlUtils {
//    public static void main(String[] args){
//        Map<String,Object> map = new HashMap<>();
//        ArrayList<String[]> strings = new ArrayList<>();
//        String[] heard = new String[]{"t1","t2"};
//        String[] row1 = new String[]{"r1","r2"};
//        String[] row2 = new String[]{"s1","s2"};
//        strings.add(row1);
//        strings.add(row2);
//        MiniTableRenderData miniTableRenderData = list2Table(heard, strings);
//        map.put("test",miniTableRenderData);
//        try {
//            CustomTableRenderPolicy customTableRenderPolicy = new CustomTableRenderPolicy();
//            ITableToolsMCImpl tableToolsMC = new ITableToolsMCImpl();
//            customTableRenderPolicy.setMergeCells(true);
//            customTableRenderPolicy.setiTableToolsMC(tableToolsMC);
//            createDocByTemplateName("test.docx","D:\\ruoyi\\resultPath\\test.docx",map,customTableRenderPolicy);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    /** 功能描述: 根据指定的模板和数据生成docx文件到指定的路径
     * @param templateName
     * @param docPath
     * @param data
     * @return: void
     * @Author:
     * @Date: 2020/2/20 14:31
     *//*

    public static void createDocByTemplateName(String templateName,
                                               String docPath,
                                               Object data) throws Exception{
        String docTemplatePath = PoiTlUtils.class.getClassLoader()
                .getResource("docTemplates/" + templateName).getPath(); // 模板文件的路径
        createDocByTemplate(docTemplatePath,docPath,data,null);
    }

    public static void createDocByTemplateName(String templateName,String docPath,
                                               Object data,CustomTableRenderPolicy customTableRenderPolicy) throws Exception{
        String docTemplatePath = PoiTlUtils.class.getClassLoader()
                .getResource("docTemplates/" + templateName).getPath(); // 模板文件的路径
        createDocByTemplate(docTemplatePath,docPath,data,customTableRenderPolicy);
    }

    public static void createDocByTemplate(String docTemplatePath,
                                           String docPath,
                                           Object data,CustomTableRenderPolicy customTableRenderPolicy) throws Exception{
        if(customTableRenderPolicy == null){
            customTableRenderPolicy = new CustomTableRenderPolicy();
            customTableRenderPolicy.setMergeCells(false);
        }

        // 自定义表格控件覆盖原有控件
        Configure config = Configure.newBuilder()
                .addPlugin('#', customTableRenderPolicy)
                .addPlugin('+', new CustomTableRenderPolicyWP()) // zidingyibaioghhe
                .addPlugin('>', new CustomListRenderPolicyWP())  // 自定义列表
                .build();

        // 生成报告doc文件
        XWPFTemplate template = XWPFTemplate
                .compile(docTemplatePath,config)
                .render(data);
        FileOutputStream out = new FileOutputStream(docPath);
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }


    public static MiniTableRenderData list2Table(
            String[] headerArr,
            List<String[]> datas){
        // 通用样式
        TableStyle tableStyle = new TableStyle();
        tableStyle.setAlign(STJc.Enum.forInt(2));

        RowRenderData header = null;
        if(headerArr != null){
            List<TextRenderData> headerDatas = new ArrayList();
            for (String headerText: headerArr){
                headerDatas.add(new TextRenderData(headerText));
            }
            header = new RowRenderData(headerDatas,"eeeeee"); // 表头
        }


        List<RowRenderData> rows = new ArrayList<>();
        for (String[] dataRow: datas){
            List<TextRenderData> rowData = new ArrayList();
            for (String cellData: dataRow){
                rowData.add(new TextRenderData( cellData)); // 每行
            }
            RowRenderData row = new RowRenderData(rowData,null); // 行数据
            rows.add(row);
        }


        MiniTableRenderData tableRenderData = new MiniTableRenderData(header,rows,MiniTableRenderData.WIDTH_A4_MEDIUM_FULL);

        return tableRenderData;
    }


    public static MiniTableRenderData list2Table4Pcr2(
            String[] headerArr,
            List<String[]> datas){
        // 通用样式
        TableStyle tableStyle = new TableStyle();
        tableStyle.setAlign(STJc.Enum.forInt(2));

        RowRenderData header = null;
        if(headerArr != null){
            List<TextRenderData> headerDatas = new ArrayList();
            for (String headerText: headerArr){
                headerDatas.add(new TextRenderData(headerText));
            }
            header = new RowRenderData(headerDatas,"d3dce9"); // 表头
        }

        String backgroundColor = "ffffff";
        List<RowRenderData> rows = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            String[] dataRow = datas.get(i);
            List<TextRenderData> rowData = new ArrayList();
            for (String cellData: dataRow){
                rowData.add(new TextRenderData( cellData)); // 每行
            }
            if (i == datas.size() - 1) backgroundColor = "d3dce9";
            RowRenderData row = new RowRenderData(rowData,backgroundColor); // 行数据
            rows.add(row);
        }
        MiniTableRenderData tableRenderData = new MiniTableRenderData(header,rows,MiniTableRenderData.WIDTH_A4_FULL);
        return tableRenderData;
    }


    */
    /** 功能描述: map 列表转表格
     * @param headerArr
     * @param cellKeyArr
     * @param mapLists
     * @return: com.deepoove.poi.data.MiniTableRenderData
     * @Author:
     * @Date: 2020/2/16 16:21
     *//*

    public static MiniTableRenderData listMap2Table(
            String[] headerArr, String[] cellKeyArr,
            List<Map<String,String>>... mapLists){

        List<Map<String,String>> mapList = new ArrayList<>();
        for (List<Map<String,String>> list: mapLists){
            mapList.addAll(list);
        }

        // 表头单元格样式
        Style style = new Style();
        style.setBold(true); // 加粗

        // 表头样式
        TableStyle headerStyle = new TableStyle();
        headerStyle.setAlign(STJc.Enum.forInt(2)); // 文字居中
        headerStyle.setBackgroundColor("eeeeee");


        // 内容样式
        TableStyle contentStyle = new TableStyle();
        contentStyle.setAlign(STJc.Enum.forInt(2)); // 文字居中


        CellRenderData cellRenderData = new CellRenderData();
        List<TextRenderData> headerDatas = new ArrayList();
        for (String headerText: headerArr){
            TextRenderData textRenderData = new TextRenderData(headerText,style);
            headerDatas.add(new TextRenderData(headerText,style));
        }
        RowRenderData header = new RowRenderData(headerDatas,"eeeeee"); // 表头
        header.setRowStyle(headerStyle);


        List<RowRenderData> rows = new ArrayList<>();
        for (Map<String,String> map: mapList){
            List<TextRenderData> rowData = new ArrayList();
            for (String cellKey: cellKeyArr){
                rowData.add(new TextRenderData(map.get(cellKey))); // 每行
            }
            RowRenderData row = new RowRenderData(rowData,null); // 行数据
            row.setRowStyle(contentStyle);
            rows.add(row);
        }


        MiniTableRenderData tableRenderData = new MiniTableRenderData(header,rows,MiniTableRenderData.WIDTH_A4_MEDIUM_FULL);
        tableRenderData.setNoDatadesc("无");

        return tableRenderData;
    }



    */
    /* 功能描述: String 列表转带序号的表格
     * @param headerArr 必须是偶数个 [序号 , 名字 ,  序号 , 名字]
     * @param strList
     * @return: com.deepoove.poi.data.MiniTableRenderData
     * @Author:
     * @Date: 2020/2/15 17:51
     *
     * 例子：  序号  名字   序号  名字
     *         1   张三   3    李四
     *         2   王五   4    赵柳
     *//*

    public static MiniTableRenderData listStr2TableWithNum(
            String[] headerArr,
            List<String> strList){

        Style style = new Style();
        style.setBold(true); // 加粗

        // 表头
        List<TextRenderData> headerDatas = new ArrayList();
        for (String headerText: headerArr){
            headerDatas.add(new TextRenderData(headerText,style));
        }
        RowRenderData header = new RowRenderData(headerDatas,"eeeeee"); // 表头


        // 内容
        // 算出来除去序号多少列
        int leiCount = headerArr.length / 2;
        if (strList.size() < leiCount){
            leiCount = strList.size();
        }

        // 算出来应该占多少行
        int hangCount = strList.size()%leiCount==0?strList.size() / leiCount:strList.size() / leiCount +1;

        List<RowRenderData> rows = new ArrayList<>();

        for (int hangNum = 1; hangNum <= hangCount; hangNum++) {
            int num = hangNum;
            List<TextRenderData> rowData = new ArrayList();
            for (int lieNum = 1; lieNum <= leiCount; lieNum++) {
                if (strList.size() >= num){
                    rowData.add(new TextRenderData(String.valueOf(num))); // 每行
                    rowData.add(new TextRenderData(strList.get(num - 1))); // 每行
                }else {
                    rowData.add(new TextRenderData("")); // 每行
                    rowData.add(new TextRenderData("")); // 每行
                }

                num += hangCount;
            }
            RowRenderData row = new RowRenderData(rowData, null); // 行数据
            rows.add(row);
        }


        MiniTableRenderData tableRenderData =
                new MiniTableRenderData(header,rows,MiniTableRenderData.WIDTH_A4_MEDIUM_FULL); // 列宽

        return tableRenderData;
    }




    */
    /* 功能描述: 文本列表  转 doc列表  带字体颜色
     * @param textList
     * @param color  "FFFFFF"
     * @param fontSize  9 - 小五
     * @return: com.deepoove.poi.data.NumbericRenderData
     * @Author:
     * @Date: 2020/2/13 20:40
     *//*

    public static NumbericRenderData list2DocList(List<String> textList,Style style){
        List<TextRenderData> list = new ArrayList<TextRenderData>();
        for (String text: textList){
            TextRenderData data = new TextRenderData(text);
            if (style != null){
                data.setStyle(style);
            }
            list.add(data);
        }
        return new NumbericRenderData(NumbericRenderData.FMT_DECIMAL,list);  // NumbericRenderData.FMT_DECIMAL 数字列表
    }

    public static NumbericRenderData list2DocList(List<String> textList,
                                                  String color,
                                                  int fontSize){
        Style style = new Style();
        style.setColor(color);
        style.setFontSize(fontSize);
        return list2DocList(textList,style);
    }

    public static NumbericRenderData list2DocList(List<String> textList){
        return list2DocList(textList,null);
    }



    public static Style createStyle(String fontFamily, int fontSize, String color){
        Style style = new Style();
        if (fontSize != 0) style.setFontSize(fontSize);
        if (fontFamily != null) style.setFontFamily(fontFamily);
        style.setColor(color);
        return style;
    } */


    /**
     * 浏览器下载excel
     * @param fileName
     * @param wb
     * @param response
     */

    /**
     * 文件流输出
     *
     * @param oldFileName
     * @param newFileName
     * @param data
     */
    public static String buildDocxFile(Configure configure, String oldFileName, String newFileName, Object data) {
        File file = new File(newFileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            XWPFTemplate template = XWPFTemplate.compile(oldFileName, configure).render(data);
            FileOutputStream out = new FileOutputStream(newFileName);
            template.write(out);
            out.flush();
            out.close();
            template.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFileName;
    }

    /**
     * 文件流输出到byte中
     *
     * @param templateFile 模板文件
     * @param configure 设置
     * @param data 模板文件数据
     */
    public static byte[] buildDocxFileForBytes(Configure configure, InputStream templateFile, Object data) {
        try {
            XWPFTemplate template = XWPFTemplate.compile(templateFile, configure).render(data);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            template.write(out);
            out.flush();
            byte[] docBytes = out.toByteArray();

            out.close();
            template.close();
            return docBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

 /*   public static void buildDocxFile(Configure configure, String oldFileName, String newFileName, Object data) {
        newFileName = "c:\\\\client\\templateB - 副本.docx";
        File file = new File(newFileName);
        try {
            if (file.exists()) {
//                file.delete();
                System.out.println("文档已存在");
            }else{

            }


//            XWPFTemplate template = XWPFTemplate.compile(newFileName).render(datas);
//            XWPFTemplate template = XWPFTemplate.compile(newFileName, configure).render(datas);
            System.out.println("data:"+data);
            XWPFTemplate template = XWPFTemplate.compile(newFileName, configure).render(data);
            FileOutputStream out = new FileOutputStream(newFileName);
            template.write(out);
            out.flush();
            out.close();
            template.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    /**
     * 浏览器下载docx
     *
     * @param oldFileName
     * @param newFileName
     * @param data
     * @param response
     */
    public static void buildDocxDocument(Configure configure, String oldFileName, String newFileName, Object data, HttpServletResponse response) {
        try {
            XWPFTemplate template = XWPFTemplate.compile(oldFileName, configure).render(data);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(newFileName, "utf-8"));
            response.flushBuffer();
            template.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
