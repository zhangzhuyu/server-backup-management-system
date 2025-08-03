package com.ly.cloud.backup.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author SYC
 * @Date: 2022/7/20 17:27
 * @Description
 */
public class CreateFileUtil {
    /**
     * 生成.json格式文件
     */
    public static byte[] createJsonFile(String jsonString, String filePath, String fileName) throws IOException {
        // 标记文件生成是否成功
        boolean flag = true;
        byte[] bytes = null;
                // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName;
        File  file = null;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // 格式化json字符串
            jsonString = JsonFormatTool.formatJson(jsonString);

            // 将格式化后的字符串写入文件
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Writer write = new OutputStreamWriter(fileOutputStream, "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
//        bytes = new byte[(int) file.length()];
        // 返回是否成功的标记
        Path path = Paths.get(fullPath);
        bytes = Files.readAllBytes(path);
        return bytes;
//        return flag;
    }

    /**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String filePath, String fileName) {
        fileName = filePath+fileName;
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
