package com.ly.cloud.backup.scheduled;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.TableStyle;
import com.deepoove.poi.util.BytePictureUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.common.enums.InspectionStrategyEnums;
import com.ly.cloud.backup.config.WordToPdfConf;
import com.ly.cloud.backup.util.DateUtil;
import com.ly.cloud.backup.util.PoiTlUtils;
import lombok.SneakyThrows;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author SYC
 * @Date: 2022/3/7 17:22
 * @Description 实现带返回值的线程
 */
@Component
//@DependsOn("wordToPdfConf")
//@Configuration
public class SchedulerRunnable implements Runnable  {


    @Autowired
    private WordToPdfConf wordToPdfUtil;

    @Value("${fileClientPath:/data/dc}")
    private String fileClientPath;
/*
    private String name;
    public SchedulerRunnable(String name) {
        this.name = name;
    }*/

    private static final String PDF = InspectionStrategyEnums.PDF.getValue();
    private static final String DOC = InspectionStrategyEnums.DOC.getValue();
    private static final String DOCX = InspectionStrategyEnums.DOCX.getValue();

    private static Style textStyle = new Style();

    private final static Style headTextStyle = new Style();
    private final static TableStyle headStyle = new TableStyle();
    private final static TableStyle rowStyle;
    private final static int thresholdValue = 300;
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00%");
    public static LocalDateTime reportTime = LocalDateTime.now();

    static {
        headTextStyle.setFontFamily("仿宋");
        headTextStyle.setFontSize(12);
        headTextStyle.setColor("000000");
        headTextStyle.setBold(true);
        headStyle.setAlign(STJc.LEFT);
        rowStyle = new TableStyle();
        rowStyle.setAlign(STJc.LEFT);
    }

    /**
     * 生成word格式的文档
     */
    public void generateWord() throws Exception {
        Configure config;
        ConfigureBuilder configureBuilder = new ConfigureBuilder();
        config = configureBuilder.build();
        String fileName = "templateB - 副本";
        String newFileName = "templateBnew.doc";
        String inputFileName = fileName + ".docx";
        String outputFileName = inputFileName.replace("docx","pdf");
        StringBuilder path = new StringBuilder(fileClientPath);
        path.append(fileName);
        path.append("."+DOCX.toLowerCase());
        List<ReportTemplate> reportTemplates = new ArrayList<>();
        ReportTemplate reportTemplate0 = new ReportTemplate();
        reportTemplate0.setTemplateName("AAAAAAAA");
        reportTemplate0.setConfigurationValue("AAAAAAAAAAAAAAAAA");
        reportTemplates.add(reportTemplate0);
  /*      Map<String, Object> result = (Map<String, Object>) reportTemplates.stream().filter(reportTemplate1->reportTemplate1.getMonth()==null)
               .collect(Collectors.toList());*/
        Map<String, Object> result = reportTemplates.stream().filter(reportTemplate -> reportTemplate.getSystemNumber() == null).collect(Collectors.toMap(reportTemplate -> reportTemplate.getTemplateName(),
                reportTemplate -> reportTemplate.getResult() == null ? "" : reportTemplate.getTemplateName().equals("departmentReportTitle") || reportTemplate.getTemplateName().equals("schoolName") ?
                        new TextRenderData(reportTemplate.getResult()) : new TextRenderData(reportTemplate.getResult(), textStyle)
                , (oldValue, newValue) -> newValue));



        Map<String, Object> systemQualityReportDetail = new HashMap<>();
        // 结果值部分
        LocalDateTime localDateTime = LocalDateTime.now();
        String exportTime = dateTimeFormatter.format(localDateTime);
        systemQualityReportDetail.put("exportTime", new TextRenderData(exportTime, textStyle));
        reportTemplates.stream().forEach(reportTemplate -> {
                if (reportTemplate.getTemplateName().equals("systemName")) {
                    systemQualityReportDetail.put(reportTemplate.getTemplateName(), new TextRenderData(reportTemplate.getResult()));
                } else {
                    systemQualityReportDetail.put(reportTemplate.getTemplateName(), new TextRenderData(reportTemplate.getResult(), textStyle));
                }
        });

        Map<String, Object> data = new HashMap<String, Object>() {
            {
                //本地图片
                put("NAME","清华大学");
                put("DATE", DateUtil.getCurrentStringDate());
                put("CPU","8核");
                put("NC","16G");
                put("YP","1T");
                put("XT","LINUX");
                put("JG","X86");
                put("IP","192.168.3.47");
                put("ZJM","SYC");
                put("JDK","1.8");
                put("fieldLengthChangesTableDetail",fieldLengthChangesDetail(systemQualityReportDetail));

                RowRenderData header = RowRenderData.build(new TextRenderData("FFFFFF", "姓名"), new TextRenderData("FFFFFF", "学历"));
                String cpu = "8核";
                String nc = "16G";
                String yp = "1T";
                String czxtxx = "LINUX";
                String czxtbb = "centOS7";
                String jglx = "X86";
                String ipdz = "192.168.3.47";
                String zjm = "X86";
                String yycxxx = "人事信息管理系统";
                String jdkljjbbh = "1.7";
                RowRenderData row0 = RowRenderData.build("硬件配置信息", null);
                RowRenderData row1 = RowRenderData.build("CPU", cpu);
                RowRenderData row2 = RowRenderData.build("内存", nc);
                RowRenderData row3 = RowRenderData.build("硬盘", yp);
                RowRenderData row4 = RowRenderData.build("操作系统信息", czxtxx);
                RowRenderData row5 = RowRenderData.build("操作系统版本", czxtbb);
                RowRenderData row6 = RowRenderData.build("架构类型", jglx);
                RowRenderData row7 = RowRenderData.build("IP地址", ipdz);
                RowRenderData row8 = RowRenderData.build("主机名", zjm);
                RowRenderData row9 = RowRenderData.build("应用程序信息", yycxxx);
                RowRenderData row10 = RowRenderData.build("JDK路径及版本号", jdkljjbbh);
                List<RowRenderData> rowRenderData = Arrays.asList(row0, row1, row2,
                row3, row4, row5,
                row6, row7, row8,
                row9, row10
                        );
                put("table", new MiniTableRenderData(header, rowRenderData));

//				//网路图片
                put("picture", new PictureRenderData(200, 250, ".png",
                        BytePictureUtils.getUrlByteArray("https://image.baidu.com/search/detail?ct=503316480&z=&tn=baiduimagedetail&ipn=d&word=%E5%A3%81%E7%BA%B8&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=-1&hd=undefined&latest=undefined&copyright=undefined&cs=2892804069,2700133627&os=1474595969,3374654221&simid=2892804069,2700133627&pn=1&rn=1&di=7060663421280190465&ln=1943&fr=&fmq=1526269427171_R&fm=&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&is=0,0&istype=2&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%253A%252F%252Fpic.jj20.com%252Fup%252Fallimg%252F1111%252F0Q91Q50307%252F1PQ9150307-8.jpg%26refer%3Dhttp%253A%252F%252Fpic.jj20.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Djpeg%3Fsec%3D1649412094%26t%3Df3cc37321099be2af6680669336023b8&rpstart=0&rpnum=0&adpicid=0&nojc=undefined&ctd=1646820097256^3_1366X704%1")));

            }
        };

        PoiTlUtils.buildDocxFile(config,path.toString(),fileClientPath+newFileName,data);
    }
    
    

    /**
     * 详情组装
     *
     * @param result
     * @param titles
     */
    public void detailConsumer(Map<String, Object> result, List<Map<String, Object>> Infos, ImmutableList<String> titles,
                               ImmutableList<String> keys, String templateName) {
        List<TextRenderData> textRenderDatas = new ArrayList<>();
        titles.stream().forEach(title -> textRenderDatas.add(new TextRenderData(title, headTextStyle)));
        RowRenderData header = new RowRenderData(textRenderDatas, "#E7E6E6");
        header.setRowStyle(headStyle);
        List<RowRenderData> rowRenderDatas = new ArrayList<>();
        MiniTableRenderData miniTableRenderData;
            int i = 0;
            RowRenderData row;
            for (Map<String, Object> info : Infos) {
                if (i <= thresholdValue) {
                    String[] values = new String[keys.size()];
                    for (String key : info.keySet()) {
                        if (keys.contains(key)) {
                            if ("null".equals(info.get(key)) || info.get(key) == null) {
                                values[keys.indexOf(key)] = "";
                            } else {
                                values[keys.indexOf(key)] = info.get(key) == null ? "" : String.valueOf(info.get(key));
                            }
                        }
                    }
                    row = RowRenderData.build(values);
                    row.setRowStyle(rowStyle);
                    rowRenderDatas.add(row);
                    i++;
                }
            }
        miniTableRenderData = new MiniTableRenderData(header, rowRenderDatas, MiniTableRenderData.WIDTH_A4_FULL);
        miniTableRenderData.setStyle(headStyle);
        result.put(templateName, miniTableRenderData);

    }

    public ReportTemplate sourceTableStatusDate(ReportTemplate reportTemplate, String titleName) {
        List<Map<String, Object>> dataDictionarys;
        Map<String, List<Map<String, Object>>> countMap;
        Consumer<Map<String, List<Map<String, Object>>>> detailConsumer = b -> {
            List<Map<String, Object>> datas = new ArrayList<>();
            for (String key : b.keySet()) {
                Map<String, Object> data2 = Maps.newHashMap();
                for (Map<String, Object> map : b.get(key)) {
                    data2.put("BYW", map.get("BYW"));
                    data2.put("BZW", map.get("BZW"));
                    data2.put("ZDYW", map.get("ZDYW"));
                    data2.put("ZDZW", map.get("ZDZW"));
                }
                datas.add(data2);
            }
            reportTemplate.setOthers(new ArrayList<>(datas));
        };
        return  reportTemplate;
    }

    /**
     * 字段长度变更详情
     *
     * @param result
     * @throws Exception
     * @return
     */
    public Object fieldLengthChangesDetail(Map<String, Object> result) throws Exception {
        ReportTemplate reportTemplate = new ReportTemplate();
        sourceTableStatusDate(reportTemplate, "fieldLengthChangesTableNum");
        List<Map<String, Object>> tableInfos = new ArrayList<>();
//        tableInfos = reportTemplate.getOthers();
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("A表标题1","A表内容1");
        objectObjectHashMap.put("A表标题2","A表内容2");
        objectObjectHashMap.put("A表标题3","A表内容3");
        objectObjectHashMap.put("A表标题4","A表内容4");
        System.out.println("objectObjectHashMap:"+objectObjectHashMap);
        tableInfos.add(objectObjectHashMap);
        System.out.println("tableInfos:"+tableInfos);
        System.out.println("result:"+result);
        System.out.println("tableInfos:"+tableInfos);
        detailConsumer(result, tableInfos, ImmutableList.of("业务表英文", "标准表中文", "业务字段英文", "标准字段中文", "业务表字段类型", "业务表字段长度", "标准表字段类型", "标准表字段长度"),
                ImmutableList.of("BYW", "BZW", "ZDYW", "ZDZW", "YWBZDLX", "YWBZDCD", "BZBZDLX", "BZBZDCD"), "fieldLengthChangesTableDetail");
        return reportTemplate;
    }

    /**
     * word转为pdf格式文件
     * @return
     */
    public WebResponse<?> wordToPdf() throws ParseException {
        String inFileName = "templateBnew.doc";
        String filePath = fileClientPath+inFileName;
        String outFileName = inFileName.replace(DOC,PDF);
        String replaceFilePath = filePath.replace(DOC,PDF);
        File file = new File(replaceFilePath);
        if (!file.exists()){
            wordToPdfUtil.wordToPDF(fileClientPath,inFileName,fileClientPath,outFileName);
            System.out.println("---巡检报告pdf正在生成中---"+ DateUtil.getCurrentDate());
        }else{
            System.out.println("---巡检报告pdf已存在，不能重复生成---"+ DateUtil.getCurrentDate());
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("---执行生成巡检报告操作---" + DateUtil.getCurrentDate());
        //生成word任务报告
        System.out.println("---巡检报告word正在生成中---" + DateUtil.getCurrentDate());
        this.generateWord();
        //word格式文档转换为pdf文档
        this.wordToPdf();
    }
}