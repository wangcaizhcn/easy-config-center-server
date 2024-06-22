package com.github.wangcaizhcn.config.util;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES 加解密
 * @author wangcai
 */
public class DESUtil {

    private final static String DES = "DES";
    private final static String ENCODE = "UTF-8";
    private final static String DEFAULTKEY = "rtyuvbnm";

    /**
     * 加密
     * @param data 明文
     * @return 密文
     */
    public static String encrypt(String data) {
    	try {
    		byte[] bt = encrypt(data.getBytes(ENCODE), DEFAULTKEY.getBytes(ENCODE));
    		return Base64.getEncoder().encodeToString(bt);
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * 解密
     * @param data 密文
     * @return 明文
     */
    public static String decrypt(String data) {
        try {
        	byte[] buffer = Base64.getDecoder().decode(data);
            byte[] bt = decrypt(buffer, DEFAULTKEY.getBytes(ENCODE));
            return new String(bt, ENCODE);
        } catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * 指定秘钥加密
     * @param data 明文
     * @param key 秘钥， 需要8个字符的秘钥
     * @return
     */
    public static String encrypt(String data, String key) {
    	try {
    		byte[] bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
            return Base64.getEncoder().encodeToString(bt);
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * 指定秘钥解密
     * @param data 密文
     * @param key 秘钥， 需要8个字符的秘钥
     * @return
     */
    public static String decrypt(String data, String key) {
    	try {
            byte[] buffer = Base64.getDecoder().decode(data);
            byte[] bt = decrypt(buffer, key.getBytes(ENCODE));
            return new String(bt, ENCODE);
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }
}