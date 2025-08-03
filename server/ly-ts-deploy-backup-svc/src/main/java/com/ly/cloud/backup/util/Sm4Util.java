package com.ly.cloud.backup.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;

/**
 * @author SYC
 * @Date: 2022/11/1 18:02
 * @Description
 */
public class Sm4Util {
    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";

    //私有密钥，打死不要告诉别人！！！注意:这个私钥是后端生成的，然后返回给前端，不能随便修改的。
    private static final  String privateKey = "Ly37621040abcdef";

    /**
     * SM4算法目前只支持128位（即密鑰16位元組）
     */
    private static final int DEFAULT_KEY_SIZE = 128;

    /**
     * SM4算加密字符的长度32位
     */
    private static final int DEFAULT_VALUE_SIZE = 32;

    static {
        // 防止內存中出現多次BouncyCastleProvider的實例
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private Sm4Util() {
    }

    /**
     * 生成密鑰
     * <p>建議使用org.bouncycastle.util.encoders.Hex將二進位轉成HEX字符串</p>
     *
     * @return 密鑰16位
     * @throws Exception 生成密鑰異常
     */
    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * 加密，SM4-ECB-PKCS5Padding
     *
     * @param data 要加密的明文
     * @param key  密鑰16位元組，使用Sm4Util.generateKey()生成
     * @return 加密後的密文
     * @throws Exception 加密異常
     */
    public static byte[] encryptEcbPkcs5Padding(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密，SM4-ECB-PKCS5Padding
     *
     * @param data 要解密的密文
     * @param key  密鑰16位元組，使用Sm4Util.generateKey()生成
     * @return 解密後的明文
     * @throws Exception 解密異常
     */
    public static byte[] decryptEcbPkcs5Padding(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.DECRYPT_MODE);
    }

    /**
     * SM4對稱加解密
     *
     * @param input   明文（加密模式）或密文（解密模式）
     * @param key     密鑰
     * @param sm4mode sm4加密模式
     * @param iv      初始向量(ECB模式下傳NULL)
     * @param mode    Cipher.ENCRYPT_MODE - 加密；Cipher.DECRYPT_MODE - 解密
     * @return 密文（加密模式）或明文（解密模式）
     * @throws Exception 加解密異常
     */
    private static byte[] sm4(byte[] input, byte[] key, String sm4mode, byte[] iv, int mode)
            throws Exception {
        IvParameterSpec ivParameterSpec = null;
        if (null != iv) {
            ivParameterSpec = new IvParameterSpec(iv);
        }
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(sm4mode, BouncyCastleProvider.PROVIDER_NAME);
        if (null == ivParameterSpec) {
            cipher.init(mode, sm4Key);
        } else {
            cipher.init(mode, sm4Key, ivParameterSpec);
        }
        return cipher.doFinal(input);
    }

    /**
     * SM4解密方法
     * @param encryptValue 已经使用sm4加密的值，即需要解密的值。
     * @return 样例:9016005654fd00370fc44947e3622002 -> Ly37621040
     * @throws Exception
     */
    public static String sm4EcbDecrypt(String encryptValue) throws Exception {
        //判断需要解密的字段的长度是否等于32位，防止重复解密
        if (encryptValue.length() != DEFAULT_VALUE_SIZE){
            return encryptValue;
        }

        // 解密
        byte[] input = Hex.decode(encryptValue);
        byte[]  output = Sm4Util.decryptEcbPkcs5Padding(input, privateKey.getBytes());
        String decryptValue = new String(output, StandardCharsets.UTF_8);
        System.out.println("SM4-解密输出:"+encryptValue + "=>" + decryptValue);
        return decryptValue;
    }

    /**
     * SM4 加密方法
     * @param decryptValue 即需要加密的值。样例:Ly37621040 -> 9016005654fd00370fc44947e3622002
     * @return
     * @throws Exception
     */
    public static String sm4EcbEncrypt(String decryptValue) throws Exception {
        //判断需要加密的字段的长度是否小于32位，防止重复加密
        if (decryptValue.length()>=DEFAULT_VALUE_SIZE){
            return decryptValue;
        }
        // 解密
        byte[] output = Sm4Util.encryptEcbPkcs5Padding(decryptValue.getBytes(StandardCharsets.UTF_8), privateKey.getBytes(StandardCharsets.UTF_8));
        String encryptValue = Hex.toHexString(output);
        System.out.println("SM4-加密输出:"+decryptValue + "=>" + encryptValue);
        return encryptValue;
    }


    public static void main(String[] args) throws Exception {
//        加密
        String txt = "Ly37621040";
        String key = privateKey;
        byte[] output = Sm4Util.encryptEcbPkcs5Padding(txt.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        String hex = Hex.toHexString(output);
        System.out.println("加密："+ hex);
//         解密
        byte[] input = Hex.decode(hex);
        output = Sm4Util.decryptEcbPkcs5Padding(input, key.getBytes());
        String s = new String(output, StandardCharsets.UTF_8);
        System.out.println("解密："+ s);
        System.out.println("加密key ="+ Hex.toHexString(key.getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(txt, s);
    }


}