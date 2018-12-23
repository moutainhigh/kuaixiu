package com.common.wechat.aes;
import org.apache.commons.codec.binary.Base64; 
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;

import javax.crypto.BadPaddingException; 
import javax.crypto.Cipher; 
import javax.crypto.IllegalBlockSizeException; 
import javax.crypto.NoSuchPaddingException; 
import javax.crypto.spec.IvParameterSpec; 
import javax.crypto.spec.SecretKeySpec; 
import java.io.UnsupportedEncodingException; 
import java.security.*; 
import java.security.spec.InvalidParameterSpecException; 

/** 
 * AES-128-CBC 加密方式 
 * 注： 
 * AES-128-CBC可以自己定义“密钥”和“偏移量“。 
 * AES-128是jdk自动生成的“密钥”。 
 * 微信小程序
 */ 
public class AesCbcUtil { 


  static { 
    //BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/ 
    Security.addProvider(new BouncyCastleProvider()); 
  } 

  /** 
   * AES解密 
   * 
   * @param data      //密文，被加密的数据 
   * @param key      //秘钥 
   * @param iv       //偏移量 
   * @return 
   * @throws Exception 
   */ 
  public static JSONObject decrypt(String key,String data, String iv) throws Exception { 

    //被加密的数据 
    byte[] dataByte = Base64.decodeBase64(data); 
    //加密秘钥 
    byte[] keyByte = Base64.decodeBase64(key); 
    //偏移量 
    byte[] ivByte = Base64.decodeBase64(iv); 
    JSONObject json=new JSONObject();

    try { 
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); 

      SecretKeySpec spec = new SecretKeySpec(keyByte, "AES"); 

      AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES"); 
      parameters.init(new IvParameterSpec(ivByte)); 

      cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化 

      byte[] resultByte = cipher.doFinal(dataByte); 
      if (null != resultByte && resultByte.length > 0) { 
        String result = new String(resultByte, "utf-8"); 
        json=(JSONObject) JSONObject.parse(result);
        System.out.println("解密返回："+json);
        return json; 
      } 
      return json; 
    } catch (Exception e) { 
      e.printStackTrace(); 
      throw new SystemException("解密错误");
    } 

  } 
  
  public static void main(String[] args) throws Exception {
	  /**手机号
	  String encrypData = "BYwsvopMtnGtXU77/WtGPWyHpCBdTKUn/8VWz4F1K8K6e1f/EujBKbJqwDHlxgnJ2mgzcuv1axgErdI1mkmDdmuwoJy+nLzs8wJu6zmhC3S605llqooRYajs+A25eDhv9uTupljfjAZ58I3M4kxG9DvCILSqTC+Q4Dq35iqWSf894TBlxq/Kw0Sg4IIcg4HSC1i4tR2jvA5k4J8jmK55CQ==";  
	  String iv = "YVatCITNeaD0xn9ORdTzjw==";  
	  String sessionKey = "yDns0oGpIGacnREa6IpfLQ==";  
	  **/
	  
 	 String  encrypData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
     String iv="r7BXXKkLb8qrSNn05n0qiA==";
     String sessionKey ="tiihtNczf5v6AKRyjwEUhQ==";
	  
	   JSONObject json = decrypt(sessionKey,encrypData, iv);
	  
	  
	  
}

} 
