package com.jadyer.seed.comm.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 处理加盐Hash操作密码的工具类
 * @version 1.0
 * @history 1.0-->初始化，增加加盐Hash以及密码验证的方法
 * @update 2016/7/7 13:12.
 * Created by 玄玉<http://jadyer.cn/> on 2016/7/7 9:37.
 */
public final class PasswordUtil {
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTE_SIZE = 24;
    private static final int HASH_BYTE_SIZE = 24;
    private static final int PBKDF2_ITERATIONS = 1000;
    private static final int HASH_SECTIONS = 3;
    private static final int INDEX_ALGORITHM = 0;
    private static final int INDEX_SALT = 1;
    private static final int INDEX_PBKDF2 = 2;

    public static String createHash(String password){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);
        String hash = createHash(password, new String(salt));
        String[] parts = new String[HASH_SECTIONS];
        parts[INDEX_ALGORITHM] = "sha1";
        parts[INDEX_SALT]      = Hex.encodeHexString(salt);
        parts[INDEX_PBKDF2]    = hash;
        String result = "";
        for(String obj : parts){
            result = result + ":" + obj;
        }
        return result.substring(1);
    }


    public static String createHash(String password, String salt){
        byte[] hash = pbkdf2(password.toCharArray(), salt.getBytes(), PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        return Hex.encodeHexString(hash);
    }


    public static boolean verifyPassword(String password, String correctHash){
        String[] params = correctHash.split(":");
        if(HASH_SECTIONS != params.length){
            LogUtil.getLogger().warn("Fields are missing from the password hash.");
            return false;
        }
        if(!"sha1".equals(params[INDEX_ALGORITHM])){
            LogUtil.getLogger().warn("Unsupported hash type.");
            return false;
        }
        byte[] salt;
        try {
            salt = Hex.decodeHex(params[INDEX_SALT].toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException("还原Salt失败", e);
        }
        return verifyPassword(password, new String(salt), params[INDEX_PBKDF2]);
    }


    public static boolean verifyPassword(String password, String salt, String correctHash){
        byte[] hash;
        try {
            hash = Hex.decodeHex(correctHash.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException("还原Hash失败", e);
        }
        byte[] testHash = pbkdf2(password.toCharArray(), salt.getBytes(), PBKDF2_ITERATIONS, hash.length);
        return slowEquals(hash, testHash);
    }


    private static boolean slowEquals(byte[] a, byte[] b){
        int diff = a.length ^ b.length;
        for(int i=0; i<a.length && i<b.length; i++){
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }


    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes){
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("加盐Hash失败", e);
        }
    }
}