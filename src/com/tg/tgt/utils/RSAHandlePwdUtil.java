package com.tg.tgt.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * 密码加密解密处理 帮助类
 * @author DELL
 *
 */
public class RSAHandlePwdUtil {

    private static KeyPair initKey(){
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(1024, random);
            return generator.generateKeyPair();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
 
    /**
     * 生成public key
     * @return
     */
    public static String getPublicKey(KeyPair keyPair){
        RSAPublicKey key = (RSAPublicKey)keyPair.getPublic();
        return new String(Base64.encodeBase64(key.getEncoded()));
    }
    
    public static String getPrivateKey(KeyPair keyPair){
    	RSAPrivateKey skey = (RSAPrivateKey)keyPair.getPrivate();
        return new String(Base64.encodeBase64(skey.getEncoded()));
    }
         
    /**
     * 解密
     * @param string
     * @return
     */
    public static String decryptBase64ByPrivateKey(String string,String privatekey) throws Exception {
        return new String(decryptByPrivate(decryptBASE64(string),privatekey));
    }
    private static byte[] decryptByPrivate(byte[] string,String privatekey) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            
         // 对密钥解密
            byte[] keyBytes = decryptBASE64(privatekey);

            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            
            RSAPrivateKey pbk = (RSAPrivateKey)privateKey;
            cipher.init(Cipher.DECRYPT_MODE, pbk);
            byte[] plainText = cipher.doFinal(string);
            return plainText;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(publicKey);

        // 取得私钥
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        Key puKey = keyFactory.generatePublic(keySpec);

        // 对数据加密
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, puKey);
        return encryptBASE64(cipher.doFinal(data.getBytes()));
    }

    /**
        * BASE64Encoder 加密
        * @param data 要加密的数据
        * @return 加密后的字符串
        */
    public static String encryptBASE64(byte[] data) {
        return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
    }
    /**
        * BASE64Decoder 解密
        * @param data 要解密的字符串
        * @return 解密后的byte[]
        * @throws Exception
        */
    private static byte[] decryptBASE64(String data) throws Exception {
        return android.util.Base64.decode(data, android.util.Base64.DEFAULT);
    }
    
    //公钥
    private static String ppk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQNVqLqr9rwM+flL+U3GWfkirttHWstY7iB2HStfLirVOG/Yg09ABMFtcvWK8+3yx2Z7UZFu16Z56YK+nI3aEzv0rni/3CIJ/ljO9o+j8KAc4Y+9Ql1WQAmDxGzE7GY60rALvzJgT6cdQFwwwI9AdiGdAyswD1R5y7Cu1M+aWUSwIDAQAB";
    //私钥
    private static String pvk = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJA1Wouqv2vAz5+Uv5TcZZ+SKu20day1juIHYdK18uKtU4b9iDT0AEwW1y9Yrz7fLHZntRkW7Xpnnpgr6cjdoTO/SueL/cIgn+WM72j6PwoBzhj71CXVZACYPEbMTsZjrSsAu/MmBPpx1AXDDAj0B2IZ0DKzAPVHnLsK7Uz5pZRLAgMBAAECgYEAiYD2N1q2b25ICufwzWZh2Aetuz5pPidYeJmFYz9uu0l97adt6uAGMIdO46xyXUa7xKMagTPleOO4y2yD3K3HeHfpVEs2r17x6rz5rfeBW4L7zT8nqc40dLXoSGAZ8IiQuVRbi/f0ts93Ru78Gx15nzXhULm4Za0rPWHGtejZDyECQQDdzLGvex0uV6ZUsiOHcTlXdTmU7H5LjsqeEUz31nKKfX03ugqz6yM86taf9vSYYDJ0XchmRH1QQ63p+snj0zCpAkEApnHUV6TCRZQNgdipaQHtt640fmhkWpKg1Thc1AKJs3FVEEcIm0LnAsaQ1T/0Z+QPQpnF/rs1lfVaUmjymRdR0wJAR8SyQgveN16ZLZKuuGbEnS4LQcr8WsqLeXYzp4Y2beWJHP0P9YPCVTXP2Nb58kw+RzUJYT4MJmqf3bQOm698cQJALZxNywKcNgLfPLDJo7vij44OVoF21pcNucArN/HGGEU7QS2l/x3zPgB52eYfrISDZgXLiwV0JrbIXILjz+3i/wJALTv2V2QvjC9moUMi1Ibww1scw7E7sf673nsTXgoxgcrTOu/cwNnNGcLa+blwqB2XUGtoUF4XJmyKEkAa+q9SWA==";
     
    public static void ma() throws Exception {
//    	KeyPair keyPair = initKey();
//    	System.out.println("公钥："+getPublicKey(keyPair));
//    	System.out.println("私钥："+getPrivateKey(keyPair));
    	String str = "哈哈哈哈";
        String jmh = null;
        try {
            jmh = encryptByPublicKey(str,ppk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("加密后："+jmh);
        System.out.println("解密后："+decryptBase64ByPrivateKey(jmh,pvk));
    }
}
