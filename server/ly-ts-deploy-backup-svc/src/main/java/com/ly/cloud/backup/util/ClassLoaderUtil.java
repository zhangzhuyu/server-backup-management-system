package com.ly.cloud.backup.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class Name: ModuleClassLoader Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月08日 10:07
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Slf4j
public class ClassLoaderUtil extends URLClassLoader {

    private static ClassLoaderUtil instance;

    private static URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    private static final Method ADD_URL = initAddMethod();

    public static final String JAR_SUFFIX = ".jar";

    public static final String ZIP_SUFFIX = ".zip";

    static {
		ClassLoader.registerAsParallelCapable();// 是否并行加载，默认懒加载
    }

    private ClassLoaderUtil(URL[] urls) {
        super(urls);
    }

    public static ClassLoaderUtil getInstance() {
        if (instance == null) {
            instance = new ClassLoaderUtil(new URL[]{});
        }
        return instance;
    }

    private static Method initAddMethod() {
        try {
            Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrl.setAccessible(true);
            return addUrl;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载jar包
     *
     * @param filepath
     */
    public void loadClasspath(String filepath, File file) {
        if(null != filepath){
            file = new File(filepath);
        }
        if(null != file){
            loopFiles(file);
        }
    }

    /**
     * 加载源代码
     *
     * @param filepath
     */
    public void loadResourceDir(String filepath, File file) {
        if(null != filepath){
            file = new File(filepath);
        }
        if(null != file){
            loopDirs(file);
        }
    }

    /**
     * 循环遍历目录，找出所有的资源路径。
     *
     * @param file 当前遍历文件
     */
    private void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            loadJar(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }

    /**
     * 循环遍历目录，找出所有的jar包。
     *
     * @param file 当前遍历文件
     */
    private void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        } else {

            if (file.getAbsolutePath().endsWith(JAR_SUFFIX) || file.getAbsolutePath().endsWith(ZIP_SUFFIX)) {
                loadJar(file);
            }
        }
    }

    private void loadJar(File file) {
        try {
            ADD_URL.invoke(classLoader, file.toURI().toURL());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
