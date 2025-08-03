package com.ly.cloud.backup.config;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.ly.cloud.backup.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * @author SYC
 * @date 创建时间：2022年03月07日 09:37:35
 * @description openoffice word转pdf工具类(只支持doc转pdf)
 */
//@Component
@Configuration
public class WordToPdfConf {
    /**
     * *主机名
     */
    @Value("${openoffice.host:192.168.36.3}")
    private String host;
    /**
     * *端口号
     */
    @Value("${openoffice.port:8100}")
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(WordToPdfConf.class);

    /**
     * *只支持doc转pdf
     *
     * @param inputPath     格式：C://POIWord/
     * @param inputFileName 格式：demo.doc
     * @param outputPath    格式：C://POIWord/
     * @param outFileName   格式：demo.pdf
     * @return
     */
    public String wordToPDF(String inputPath, String inputFileName, String outputPath, String outFileName) throws ParseException {

        File inputFile = new File(inputPath + inputFileName);
        if (!inputFile.exists()) {
            // 找不到源文件, 则返回false
            throw new IllegalStateException("找不到要转换成pdf的word文档");
        }

        // 如果目标路径不存在, 则新建该路径
        File outputFile = new File(outputPath + outFileName);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }


        OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, port);
        try {
            connection.connect();
        } catch (ConnectException e) {
            System.out.println("libreOffice连接失败！请检查IP,端口");
            logger.error(e.getMessage(),e);
            return "libreOffice连接失败！可能服务没有启动,请检查IP,端口.";
        }
        DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                connection);
        converter.convert(inputFile, outputFile);
        connection.disconnect();
        System.out.println("---报告pdf完成生成---"+ DateUtil.getCurrentDate());
        return null;
    }

    /**
     * *只支持doc转pdf
     *
     * @param docBytes      格式：doc内容字节数组
     * @param fileInfo     格式：demo.doc
     * @return
     */
    public void wordToPDFForBytes(byte[] docBytes, HashMap<String, Object> fileInfo) {
        // 建立连接
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, port);
        try {
            connection.connect();
        } catch (ConnectException e) {
            logger.error("libreOffice连接失败！可能服务没有启动,请检查IP,端口!");
            logger.error(e.getMessage(), e);
        }

        try {
            // 将doc文件字节数组放入 字节数组输出流中
            ByteArrayInputStream inputStream = new ByteArrayInputStream(docBytes);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            DocumentFormat docDocumentFormat = (new DefaultDocumentFormatRegistry()).getFormatByFileExtension("doc");
            DocumentFormat pdfDocumentFormat = (new DefaultDocumentFormatRegistry()).getFormatByFileExtension("pdf");
            // 转换doc成pdf文档
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                    connection);
            converter.convert(inputStream, docDocumentFormat, outputStream, pdfDocumentFormat);
            // 获取转换完成后的pdf文件字节数组
            byte[] pdfBytes = outputStream.toByteArray();
            // 放入map里
            fileInfo.put("pdfBytes",pdfBytes);

            inputStream.close();
            outputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("---报告pdf完成生成---");
    }

    public static void main(String[] args) {
        OpenOfficeConnection connection = new SocketOpenOfficeConnection("192.168.36.3", 8100);
        try {
            connection.connect();
        } catch (ConnectException e) {
            System.out.println("libreOffice连接失败！请检查IP,端口");
            e.printStackTrace();
        }
        DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
        connection.disconnect();
    }
}
