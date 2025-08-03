package com.ly.cloud.license.server;

import de.schlichtherle.license.AbstractKeyStoreParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * 自定义KeyStoreParam，用于将公私钥存储文件存放到其他磁盘位置而不是项目中
 *
 * @author zifangsky
 * @date 2018/4/26
 * @since 1.0.0
 */
public class CustomKeyStoreParam extends AbstractKeyStoreParam {

    private static Logger logger = LogManager.getLogger(CustomKeyStoreParam.class);


    /**
     * 公钥/私钥在磁盘上的存储路径
     */
    private String storePath;
    private String alias;
    private String storePwd;
    private String keyPwd;

    public CustomKeyStoreParam(Class clazz, String resource,String alias,String storePwd,String keyPwd) {
        super(clazz, resource);
        this.storePath = resource;
        this.alias = alias;
        this.storePwd = storePwd;
        this.keyPwd = keyPwd;
    }


    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getStorePwd() {
        return storePwd;
    }

    @Override
    public String getKeyPwd() {
        return keyPwd;
    }

    /**
     * 读取资源文件下的文件内容
     *
     * @param filePath 文件路径
     * @return
     */
    public InputStream getConfigParam(String filePath) throws Exception {
        return this.getClass().getClassLoader().getResourceAsStream(filePath);
    }

    /**
     * 复写de.schlichtherle.license.AbstractKeyStoreParam的getStream()方法<br/>
     * 用于将公私钥存储文件存放到其他磁盘位置而不是项目中
     * @author zifangsky
     * @date 2018/4/26 18:28
     * @since 1.0.0
     * @param
     * @return java.io.InputStream
     */
    @Override
    public InputStream getStream() throws IOException {
        InputStream in = null;
        try {
            in = this.getConfigParam(storePath);
            if (in != null){
                return in;
            }
            in = new FileInputStream(new File(storePath));
            if (null == in){
                throw new FileNotFoundException(storePath);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return in;
    }
}
