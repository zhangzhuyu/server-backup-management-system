package com.ly.cloud.backup.util;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//这个是模拟将多个字节数组一起打包生成一个压缩数组然后生成一个压缩文件（依赖Windows）
public class ListBytesToZipInWinUtil {
    public static void main(String[] args) throws IOException {

        Map<String,byte[]> map=new HashMap<>();
        map.put("1.html",getBytes(new File("F:/FtpServer/tmp/zzy/1.html")));
        map.put("cdc.sql",getBytes(new File("F:/FtpServer/tmp/zzy/cdc.sql")));
        map.put("second/second.txt",getBytes(new File("F:/FtpServer/tmp/zzy/second/second.txt")));
        System.out.println(listBytesToZip(map).length);

        //文件夹的后面必须要斜杠结尾
        String filepath="F:/FtpServer/";
        String filename="zzy.zip";
        fileToBytes(listBytesToZip(map),filepath,filename);
    }

    public static byte[] listBytesToZip(Map<String,byte[]> mapReporte) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ZipOutputStream zos = new ZipOutputStream(baos);

        Iterator<Map.Entry<String, byte[]>> iterator = mapReporte.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, byte[]> reporte = iterator.next();
            ZipEntry entry = new ZipEntry(reporte.getKey());

            entry.setSize(reporte.getValue().length);
            //这个方法及其里面的参数是决定目录结构的
            zos.putNextEntry(entry);

            zos.write(reporte.getValue());

        }
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

    public static byte[] getBytes(File file) throws IOException {
        FileInputStream fis=new FileInputStream(file);
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        byte[] b=new byte[1024];
        int len=-1;
        while ((len=fis.read(b))!=-1){
            bao.write(b,0,len);
        }
        return bao.toByteArray();
    }

    public static void fileToBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {

            file = new File(filePath + fileName);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
