package com.ly.cloud.backup.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * 字符串加密解密工具，可逆加密，秘钥很重要，一定要自己改秘钥，打死也不要告诉其他人
 *
 * @author 夏增明
 * @version 1.0
 */
@Slf4j
public class DesUtil {


    /**
     * 密钥，是加密解密的凭据，长度为8的倍数
     */
    private static final String PASSWORD_CRYPT_KEY = "Ly37621040";
    private final static String DES = "DES";

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        return des(src, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        return des(src, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 加解密
     *
     * @param src        数据源
     * @param key        密钥，长度必须是8的倍数
     * @param cipherType Cipher对象类型
     * @return 返回解密后的原始数据
     */
    private static byte[] des(byte[] src, byte[] key, int cipherType) throws Exception {
        SecureRandom sr = new SecureRandom();

        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey secretKey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密匙初始化Cipher对象
        cipher.init(cipherType, secretKey, sr);

        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     *
     * @param data 解密的数据
     * @return String 解密后的数据
     */
    public static String decrypt(String data) {
        try {
            return Sm4Util.sm4EcbDecrypt(data);
        } catch (Exception e) {
            log.info(e.getMessage());
            return data;
        }
   /*     try {
            return new String(decrypt(hex2byte(data.getBytes()), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return data;
        }*/
    }

    /**
     * 密码解密
     *
     * @param data 解密的数据
     * @param salt 盐值
     * @return String 解密后的数据
     */
    public static String decrypt(String data, String salt) {
        try {
            return Sm4Util.sm4EcbDecrypt(data);
        } catch (Exception e) {
            log.info(e.getMessage());
            return data;
        }
     /*   try {
            return new String(decrypt(hex2byte(data.getBytes()), salt.getBytes()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return data;
        }*/
    }

    /**
     * 密码加密
     *
     * @param password 加密的数据
     * @return String 加密后的数据
     */
    public static String encrypt(String password) {
        try {
            return Sm4Util.sm4EcbEncrypt(password);
        } catch (Exception e) {
            log.info(e.getMessage());
            return password;
        }
      /*  try {
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }*/
    }

    /**
     * 密码加密
     *
     * @param password 加密的数据
     * @param salt     盐值
     * @return String 加密后的数据
     */
    public static String encrypt(String password, String salt) {
        try {
            return byte2hex(encrypt(password.getBytes(), salt.getBytes()));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    /**
     * 二行制转字符串
     *
     * @param b 字节
     * @return String 字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stamp;
        for (byte value : b) {
            stamp = (Integer.toHexString(value & 0XFF));
            if (stamp.length() == 1) {
                hs.append("0").append(stamp);
            } else {
                hs.append(stamp);
            }
        }
        return hs.toString().toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        int len = 2;
        if ((b.length % len) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * 测试用例，不需要传递任何参数，直接执行即可。
     *
     * @param args args
     */
    public static void main(String[] args) {
//        String s = "this is 我的 #$%^&()first encrypt program 知道吗?DES算法要求有一个可信任的随机数源 --//*。@@@1";
        String s = "pagan";
        String str1 = encrypt(s);

        System.out.println("原始值: " + s);
        System.out.println("加密后: " + str1);
        System.out.println("解密后: " + decrypt(str1));
        System.out.println("为空时 is : " + decrypt(encrypt("")));


        System.out.println("======================");
        System.out.println(decrypt("FA"));

        String plainClientCredentials = "b49d35b7704e41f_uuid:0683126daa811b7da48c67ccd3c498aa";

        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
        System.out.println(base64ClientCredentials);
    }

}