package com.jadyer.seed.comm.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加解密工具类
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.7
 * @history v1.8-->修复buildAESPKCS7Decrypt()方法没有初始化BouncyCastleProvider的BUG
 * @history v1.7-->细化各方法参数注释，使之描述更清晰
 * @history v1.6-->RSA算法加解密方法增加分段加解密功能，理论上可加解密任意长度的明文或密文
 * @history v1.5-->增加RSA算法加解密及签名验签的方法
 * @history v1.4-->增加AES-PKCS7算法加解密数据的方法
 * @history v1.3-->增加buildHMacSign()的签名方法，目前支持<code>HMacSHA1,HMacSHA256,HMacSHA512,HMacMD5</code>算法
 * @history v1.2-->修改buildHexSign()方法，取消用于置顶返回字符串大小写的第四个参数，修改后默认返回大写字符串
 * @history v1.1-->增加AES,DES,DESede等算法的加解密方法
 * @history v1.0-->新增buildHexSign()的签名方法，目前支持<code>MD5,SHA,SHA1,SHA-1,SHA-256,SHA-384,SHA-512</code>算法
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2013/10/06 00:00.
 */
public final class CodecUtil {
    public static final String CHARSET = "UTF-8";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_RSA_SIGN = "SHA256WithRSA";
    public static final int ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_PKCS7 = "AES";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DESede = "DESede";
    private static final String ALGORITHM_CIPHER_AES = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_AES_PKCS7 = "AES/CBC/PKCS7Padding";
    private static final String ALGORITHM_CIPHER_DES = "DES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_DESede = "DESede/ECB/PKCS5Padding";

    private CodecUtil(){}

    private static AlgorithmParameters initIV(){
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte)0x00);
        AlgorithmParameters params;
        try {
            params = AlgorithmParameters.getInstance(ALGORITHM_AES_PKCS7);
            params.init(new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new IllegalArgumentException("生成"+ALGORITHM_CIPHER_AES_PKCS7+"专用的IV时失败", e);
        }
        return params;
    }


    public static String initKey(String algorithm, boolean isPKCS7Padding){
        if(isPKCS7Padding){
            Security.addProvider(new BouncyCastleProvider());
        }
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + algorithm + "]");
        }
        if(ALGORITHM_AES.equals(algorithm)){
            kg.init(128);
        }else if(ALGORITHM_AES_PKCS7.equals(algorithm)){
            kg.init(128);
        }else if(ALGORITHM_DES.equals(algorithm)){
            kg.init(56);
        }else if(ALGORITHM_DESede.equals(algorithm)){
            kg.init(168);
        }else{
            throw new IllegalArgumentException("Not supported algorithm-->[" + algorithm + "]");
        }
        SecretKey secretKey = kg.generateKey();
        if(isPKCS7Padding){
            return Hex.encodeHexString(secretKey.getEncoded());
        }
        return Base64.encodeBase64URLSafeString(secretKey.getEncoded());
    }


    public static Map<String, String> initRSAKey(int keysize){
        if(keysize != ALGORITHM_RSA_PRIVATE_KEY_LENGTH){
            throw new IllegalArgumentException("RSA1024已经不安全了，请使用"+ALGORITHM_RSA_PRIVATE_KEY_LENGTH+"初始化RSA密钥对");
        }
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + ALGORITHM_RSA + "]");
        }
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        KeyPair keyPair = kpg.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }


    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas){
        int maxBlock;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8;
        }else{
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }


    public static String buildRSAEncryptByPrivateKey(String data, String key){
        try{
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildRSAEncryptByPublicKey(String data, String key){
        try{
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildRSADecryptByPublicKey(String data, String key){
        try{
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildRSADecryptByPrivateKey(String data, String key){
        try{
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildRSASignByPrivateKey(String data, String key){
        try{
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(CHARSET));
            return Base64.encodeBase64URLSafeString(signature.sign());
        }catch(Exception e){
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }


    public static boolean buildRSAverifyByPublicKey(String data, String key, String sign){
        try{
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        }catch(Exception e){
            throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildAESEncrypt(String data, String key){
        try{
            //实例化Cipher对象,它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            //还原密钥,并初始化Cipher对象,设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            //执行加密操作,加密后的结果通常都会用Base64编码进行传输
            //将Base64中的URL非法字符如'+','/','='转为其他字符,详见RFC3548
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildAESDecrypt(String data, String key){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildAESPKCS7Encrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try{
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initIV());
            return Hex.encodeHexString(cipher.doFinal(data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildAESPKCS7Decrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try {
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initIV());
            return new String(cipher.doFinal(Hex.decodeHex(data.toCharArray())), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildDESEncrypt(String data, String key){
        try{
            DESKeySpec dks = new DESKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildDESDecrypt(String data, String key){
        try {
            DESKeySpec dks = new DESKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DES).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildDESedeEncrypt(String data, String key){
        try{
            DESedeKeySpec dks = new DESedeKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DESede).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DESede);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildDESedeDecrypt(String data, String key){
        try {
            DESedeKeySpec dks = new DESedeKeySpec(Base64.decodeBase64(key));
            SecretKey secretKey = SecretKeyFactory.getInstance(ALGORITHM_DESede).generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_DESede);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String buildHmacSign(Map<String, String> param, String key, String algorithm){
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(param.keySet());
        Collections.sort(keys);
        for (String obj : keys) {
            String value = param.get(obj);
            if (obj.equalsIgnoreCase("cert") || obj.equalsIgnoreCase("hmac") || obj.equalsIgnoreCase("sign") || obj.equalsIgnoreCase("signMsg") || StringUtils.isEmpty(value)) {
                continue;
            }
            sb.append(obj).append("=").append(value).append("&");
        }
        sb.append("key=").append(key);
        return buildHmacSign(sb.toString(), key, algorithm);
    }


    public static String buildHmacSign(String data, String key, String algorithm){
        if("HmacMD5".equals(algorithm) || "HmacSHA1".equals(algorithm)){
            LogUtil.getLogger().warn("HmacMD5和HmacSHA1已经是不安全的了，不推荐使用");
        }
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：Invalid key-->[" + key + "]");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：No Such Algorithm-->[" + algorithm + "]");
        }
        byte[] dataBytes;
        try {
            dataBytes = data.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("将字符串[" + data + "]转为byte[]时发生异常：Unsupported Encoding-->[" + CHARSET + "]");
        }
        return Hex.encodeHexString(mac.doFinal(dataBytes));
    }


    public static String buildHexSign(Map<String, String> param, String charset, String algorithm, String signKey){
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(param.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String value = param.get(key);
            if (key.equalsIgnoreCase("cert") || key.equalsIgnoreCase("hmac") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("signMsg") || StringUtils.isEmpty(value)) {
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append("key=").append(signKey);
        return buildHexSign(sb.toString(), charset, algorithm);
    }


    public static String buildHexSign(String data, String charset, String algorithm){
        LogUtil.getLogger().info("待签名字符串为-->[{}]", data);
        byte[] dataBytes;
        try {
            dataBytes = data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("将字符串[" + data + "]转为byte[]时发生异常：Unsupported Encoding-->[" + charset + "]");
        }
        byte[] algorithmData;
        try {
            algorithmData = MessageDigest.getInstance(algorithm).digest(dataBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("签名字符串[" + data + "]时发生异常：No Such Algorithm-->[" + algorithm + "]");
        }
        char[] respData = new char[algorithmData.length << 1];
        char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for(int i=0,j=0; i<algorithmData.length; i++){
            respData[j++] = DIGITS[(0xF0 & algorithmData[i]) >>> 4];
            respData[j++] = DIGITS[0x0F & algorithmData[i]];
        }
        String sign = new String(respData);
        LogUtil.getLogger().info("生成的签名值为-->[{}]", sign);
        return sign;
    }
}