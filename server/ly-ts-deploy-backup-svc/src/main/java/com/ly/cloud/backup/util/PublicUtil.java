package com.ly.cloud.backup.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.odiszapc.nginxparser.*;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.vo.NginxProfileVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-03-09
 * @version: 1.0
 */
public class PublicUtil {

    private static final Logger logger = LoggerFactory.getLogger(PublicUtil.class);

    /**
     * 获取linux文件类型
     * “-”表示普通文件；“d”表示目录；“l”表示链接文件；“p”表示管理文件；“b”表示块设备文件；“c”表示字符设备文件；“s”表示套接字文件；
     */
    public static String getFileType(char type) {
        String fileType = "";
        switch (type) {
            case '-':
                fileType = "1";
                break;
            case 'd':
                fileType = "2";
                break;
            case 'l':
                fileType = "3";
                break;
            case 'p':
                fileType = "4";
                break;
            case 'b':
                fileType = "5";
                break;
            case 'c':
                fileType = "6";
                break;
            case 's':
                fileType = "7";
                break;
        }
        return fileType;
    }

    /**
     * 根据月份英文缩写字符获取数字月份
     */
    public static String getFileMonth(String month) {
        String fileMonth = "";
        switch (month) {
            case "Jan":
                fileMonth = "01";
                break;
            case "Feb":
                fileMonth = "02";
                break;
            case "Mar":
                fileMonth = "03";
                break;
            case "Apr":
                fileMonth = "04";
                break;
            case "May":
                fileMonth = "05";
                break;
            case "Jun":
                fileMonth = "06";
                break;
            case "Jul":
                fileMonth = "07";
                break;
            case "Aug":
                fileMonth = "08";
                break;
            case "Sep":
                fileMonth = "09";
                break;
            case "Oct":
                fileMonth = "10";
                break;
            case "Nov":
                fileMonth = "11";
                break;
            case "Dec":
                fileMonth = "12";
                break;
        }
        return fileMonth;
    }

    /**
     * 获取nginx配置信息，limit_conn_status 与  limit_conn_log_level的值 以及各 server 下的 limit_conn one 与 limit_conn perserver 值
     */
    public static List<NginxProfileVo> getNginxProfileInfo(InputStream inputStream) {
        try {
            //根据路径读取整个配置文件
            NgxConfig ngxConfig = NgxConfig.read(inputStream);

            // 返回数据
            List<NginxProfileVo> nginxProfileVos = new ArrayList<>();

            //找到http{}   因为不是单独的一条数据所以使用findBlock
            NgxBlock httpBlock = ngxConfig.findBlock("http");

            // 读取http下的limit_conn_status 与  limit_conn_log_level的值;
            NgxParam statusParam = httpBlock.findParam("limit_conn_status");
            NgxParam logLevelParam = httpBlock.findParam("limit_conn_log_level");

            //找到http{}   因为不是单独的一条数据所以使用findBlock
            List<NgxEntry> ngxServerBlock = ngxConfig.findAll(NgxConfig.BLOCK, "http", "server");
            for (NgxEntry serverEntry : ngxServerBlock) {

                NginxProfileVo nginxProfileVo = new NginxProfileVo();
                if (statusParam != null) {
                    nginxProfileVo.setLimit_conn_status(statusParam.getValue());
                }
                if (logLevelParam != null) {
                    nginxProfileVo.setLimit_conn_log_level(logLevelParam.getValue());
                }

                NgxBlock ngxBlock = (NgxBlock) serverEntry;
                NgxParam listenerParam = ngxBlock.findParam("Listen");
                if (listenerParam == null) {
                    listenerParam = ngxBlock.findParam("listen");
                }
                nginxProfileVo.setPort(listenerParam.getValue());

                List<NgxEntry> limitConns = ngxBlock.findAll(NgxConfig.PARAM, "limit_conn");
                for (NgxEntry serverBlockEntry : limitConns) {
                    NgxParam serverBlock = (NgxParam) serverBlockEntry;
                    String[] params = serverBlock.getValue().split(" ");
                    if (params[0].equals("one")) {
                        nginxProfileVo.setLimit_conn_one(params[1]);
                    } else {
                        nginxProfileVo.setLimit_conn_perserver(params[1]);
                    }
                }
                nginxProfileVos.add(nginxProfileVo);
            }

            String content = new NgxDumper(ngxConfig).dump();
            logger.info("{}", content);
            return nginxProfileVos;
        } catch (Exception e) {
            logger.warn("write nginx.conf to file catch IOException!", e);
            throw new RuntimeException("读取配置文件错误");
        }
//        return null;
    }

    /**
     * 修改nginx配置信息，limit_conn_status 与  limit_conn_log_level的值 以及各 server 下的 limit_conn one 与 limit_conn perserver 值
     */
    public static byte[] updateNginxProfileInfo(InputStream inputStream, List<NginxProfileVo> nginxProfileVos) {
        try {

            //根据路径读取整个配置文件
            NgxConfig ngxConfig = NgxConfig.read(inputStream);

            //找到http{}   因为不是单独的一条数据所以使用findBlock
            NgxBlock httpBlock = ngxConfig.findBlock("http");

            // 读取http下的limit_conn_status 与  limit_conn_log_level的值;
            NgxParam statusParam = httpBlock.findParam("limit_conn_status");
            NgxParam logLevelParam = httpBlock.findParam("limit_conn_log_level");


            //找到所有的server{}   因为不是单独的一条数据所以使用findBlock
            List<NgxEntry> ngxServerBlock = httpBlock.findAll(NgxConfig.BLOCK, "server");

            // 先删除所有server 再添加要增加的
            if (ngxServerBlock != null) {
                httpBlock.removeAll(ngxServerBlock);
            }

            NgxParam addParam1 = new NgxParam();
            if (StringUtils.isNotEmpty(nginxProfileVos.get(0).getLimit_conn_status())) {
                if (statusParam != null) {
                    httpBlock.remove(statusParam);
                }
                addParam1.addValue("limit_conn_status " + nginxProfileVos.get(0).getLimit_conn_status() + ";\n");
            }
            if (StringUtils.isNotEmpty(nginxProfileVos.get(0).getLimit_conn_log_level())) {
                if (logLevelParam != null) {
                    httpBlock.remove(logLevelParam);
                }
                addParam1.addValue("limit_conn_log_level " + nginxProfileVos.get(0).getLimit_conn_log_level());
            }
            // 添加进server 中
            if (StringUtils.isNotEmpty(addParam1.getValue())) {
                httpBlock.addEntry(addParam1);
            }

            for (NginxProfileVo nginxProfileVo : nginxProfileVos) {
                for (NgxEntry serverEntry : ngxServerBlock) {
                    NgxBlock serverBlock = (NgxBlock) serverEntry;
                    if (serverBlock != null) {
                        NgxParam listenerParam = serverBlock.findParam("Listen");
                        if (listenerParam == null) {
                            listenerParam = serverBlock.findParam("listen");
                        }
                        if (listenerParam.getValue().equals(nginxProfileVo.getPort())) {

                            //找到所有的location{}   因为不是单独的一条数据所以使用findBlock
                            List<NgxEntry> ngxLocationBlock = serverBlock.findAll(NgxConfig.BLOCK, "location");

                            // 先删除所有的Location
                            if (ngxLocationBlock != null) {
                                serverBlock.removeAll(ngxLocationBlock);
                            }

                            List<NgxEntry> limitConns = serverBlock.findAll(NgxConfig.PARAM, "limit_conn");

                            NgxParam limit_conn_one = null;
                            NgxParam limit_conn_perserver = null;


                            // 先找出 limit_conn one 与  limit_conn perserver
                            for (NgxEntry limitEntry : limitConns) {
                                NgxParam limit = (NgxParam) limitEntry;
                                String[] params = limit.getValue().split(" ");
                                if (params[0].equals("one")) {
                                    limit_conn_one = limit;
                                } else if (params[0].equals("perserver")) {
                                    limit_conn_perserver = limit;
                                }
                            }

                            // 添加新的值
                            NgxParam addParam2 = new NgxParam();
                            if (StringUtils.isNotEmpty(nginxProfileVo.getLimit_conn_one())) {
                                if (limit_conn_one != null) {
                                    serverBlock.remove(limit_conn_one);
                                }
                                addParam2.addValue("limit_conn one " + nginxProfileVo.getLimit_conn_one() + ";\n");
                            }
                            if (StringUtils.isNotEmpty(nginxProfileVo.getLimit_conn_perserver())) {
                                if (limit_conn_perserver != null) {
                                    serverBlock.remove(limit_conn_perserver);
                                }
                                addParam2.addValue("limit_conn perserver " + nginxProfileVo.getLimit_conn_perserver());
                            }
                            // 添加进server 中
                            if (StringUtils.isNotEmpty(addParam2.getValue())) {
                                serverBlock.addEntry(addParam2);
                            }
                            // 添加 location
                            for (NgxEntry locationEntry : ngxLocationBlock) {
                                serverBlock.addEntry(locationEntry);
                            }
                            // 把server 添加到 http中
                            if (httpBlock != null && serverBlock != null) {
                                httpBlock.addEntry(serverBlock);
                            }
                        }
                    }
                }
            }
            NgxDumper ngxDumper = new NgxDumper(ngxConfig);
            String content = ngxDumper.dump();
            logger.info("{}", content);
            return content.getBytes();
        } catch (Exception e) {
            logger.warn("write nginx.conf to file catch IOException!", e);
            return null;
        }
    }

    /**
     * 读取资源文件下的文件内容
     *
     * @param filePath 文件路径
     * @return
     */
    public static byte[] readProfileInfo(String filePath) {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try (
                InputStream inputStream = classPathResource.getInputStream();
                BufferedInputStream bf = new BufferedInputStream(inputStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = bf.read(buffer, 0, 1024))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("文件下载失败!", e);
            throw new RuntimeException("文件下载失败!");
        }
    }

    /**
     * 读取资源文件下的文件内容
     *
     * @param filePath 文件路径
     * @return String 类型
     */
    public static byte[] readProfileInfoToJstask(String filePath) {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try (
                InputStream inputStream = classPathResource.getInputStream();
                BufferedInputStream bf = new BufferedInputStream(inputStream);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = bf.read(buffer, 0, 1024))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("jstask快照失败!", e);
            throw new RuntimeException("jstask快照失败!");
        }
    }

    /**
     * 下载文件 获取文件字节数组
     *
     * @param filePath 文件全路径
     * @return
     */
    public static byte[] downloadfile(String filePath) {
        File file = new File(filePath);

        //判断文件是否存在
        if (file.exists()) {
            try (
                    FileInputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bf = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = bf.read(buffer, 0, 1024))) {
                    bos.write(buffer, 0, len);
                }
                return bos.toByteArray();
            } catch (Exception e) {
                logger.error("文件下载失败!", e);
                throw new RuntimeException("文件下载失败!");
            }
        } else {
            logger.error("文件不存在，文件下载失败!");
            throw new RuntimeException("文件不存在，文件下载失败!");
        }
    }

    public static List<String> hadleCheckbox(String str) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(str)) {
            if (str.indexOf(",") != -1) {
                String[] split = str.split(",");
                list = Arrays.asList(split);
            } else {
                list.add(str);
            }
        }
        return list;
    }

    /**
     * 根据属性名获取对应的数据库字段
     * @param tClass
     * @param columnKey
     * @return
     */
    public static String getDataFile(Class tClass, String columnKey){
        if (StringUtils.isEmpty(columnKey)) {
            return null;
        }
        Field[] declaredFields = tClass.getDeclaredFields();
        if (declaredFields != null && declaredFields.length > 0) {
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                if (name.equals(columnKey)) {
                    TableField annotation = declaredField.getAnnotation(TableField.class);
                    String value = annotation.value();
                    return value;
                }
            }
        }
        return null;
    }

}
