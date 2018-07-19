package com.hyphenate.easeui.utils;

//import java.util.Base64;
//import java.util.Base64.Decoder;
//import java.util.Base64.Encoder;

//import org.apache.commons.codec.binary.Base64;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES对称加密和解密
 * @author DELL
 *
 */
public class AESCodeer {


	private static final String IV_STRING = "16-Bytes--String";

	public static String AESEncode(String key,String content){
		try{
			byte[] byteContent = content.getBytes("UTF-8");
			// 注意，为了能与 iOS 统一
			// 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
			byte[] enCodeFormat = key.getBytes();
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			byte[] initParam = IV_STRING.getBytes();
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			// 指定加密的算法、工作模式和填充方式
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] encryptedBytes = cipher.doFinal(byteContent);
			// 同样对加密后数据进行 base64 编码
//	    		Encoder encoder = Base64.e.getEncoder();
//	    		return encoder.encodeToString(encryptedBytes);
			//return Base64.encodeBase64String(encryptedBytes);
			return Base64.encodeToString(encryptedBytes,Base64.DEFAULT);
		}catch(Exception e){
			//logger.error("AES 对称加密异常！",e);
		}
		return null;
	}


	public static String AESDncode(String key,String content){
		try{
			// base64 解码
//	    		Decoder decoder = Base64.getDecoder();
//	    		byte[] encryptedBytes = decoder.decode(content);

			//byte[] encryptedBytes = Base64.decodeBase64(content);
			byte[] encryptedBytes = Base64.decode(content,Base64.DEFAULT);
			byte[] enCodeFormat = key.getBytes();
			SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
			byte[] initParam = IV_STRING.getBytes();
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			byte[] result = cipher.doFinal(encryptedBytes);
			return new String(result, "UTF-8");
		}catch(Exception e){
			//	logger.error("AES解密异常！",e);
		}
		return null;
	}

	public static void maina(){
		String tk = "lfjoiqwerooasfiowjeriojoqweroijwioerjiojqweior";
		String s = getLoginSecretKey(tk);
		//lfjoiqwerooasfio
		System.out.println(s);
		String code = "6mds";
		String timestamp = "1521625529816";
		String p = "username=123@xdp123.cn&password=e10adc3949ba59abbe56e057f20f883e&code="+code+"&timestamp="+timestamp;
		System.out.println(p);
		String sign = AESCodeer.AESEncode(s,p);
		System.out.println("sign:"+sign.replaceAll("\r|\n", ""));

		String d = AESCodeer.AESDncode(s, sign);
		System.out.println("解密："+d);


	}
	private static String getLoginSecretKey(String tk){
		int[] indexs = {4,0,6,10,31,5,22,7,30,1,10,10,25,17,8,19};
		String result = "";
		for(int i=0;i<indexs.length;i++){
			result=result+ tk.substring(indexs[i],indexs[i]+1);//tkArray[];
		}
		return result;
	}
	public static String getKey(String random){
		int[] indexs = {4,0,6,10,31,5,22,7,30,1,10,10,25,17,8,19};
		String result = "";
		for(int i=0;i<indexs.length;i++){
			result=result+ random.substring(indexs[i],indexs[i]+1);
		}
		return result;
	}
}
