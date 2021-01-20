package org.chen.util;


import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;


public class AES {
    public static void main(String[] args)throws Exception {

        String sessionKey="IkouLX4o92s39Nk2XsWJTg";
        String encryptedData="bFKdJ12G0BXRYCMumCfeqlHCY6mPMSKDPkrSymSUrHlkGi/NE1SK6WK7DMrKO4yWUHB1NbnYhugxtlLQF72UkWF3XTTUvMW19B4UyjAfrTXp54PCHrXnR5/mYzKgvxQhWYXbNUsw75NrTEyUTvRhtqTZtSwZAr4hrsNUKFIg362BDL0eNIhUWckzadTIjmnirfcVQhqmKty6NpDjxVS0skRRyBTZgGXZG+C3GbbMg0e3Su3wfpUgzN+UJSyhvI2dQXtvyqWAoSs9J8GrP8XPOtRu1FQB7ZvMLY3Q2wLionLGIfQqoJETGfZTjsLWIG4dRYs2Weq7Q6jBM1OXRhf2loTu8EM3tS23OuILGCAQ6h73Y5RACxEhTagfWtm22cGKtux67Oo3cfyjn5tYDY/QtuqG2bI+ZHrSQxYCCaho0MevulgvQof3lWXvRnaClubWX+JJTLXrdGQP/nLKfRBuglbTmW5gj4qAeGy+Tu3tpM+IZ4106OxMPyNYl+EbQG5LcSdPk4b9kRkICR28GhdU6Ocqa7IrPj5e1ZEa37KhHqs=";
        String iv="JJpoSXCYKlT3Z2KJU6begw==1";

        byte[] resultByte =decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
        if(null != resultByte && resultByte.length > 0){
            String userInfo = new String(resultByte, "UTF-8");
            System.out.println(userInfo);
        }
    }
	public static boolean initialized = false;  
	
	/**
	 * AES解密
	 * @param content 密文
	 * @return
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 */
	public static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
		initialize();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			Key sKeySpec = new SecretKeySpec(keyByte, "AES");
			
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化 
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();  
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();  
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}  
	
	private static void initialize(){
        if (initialized) return;  
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;  
    }
	//生成iv  
    private static AlgorithmParameters generateIV(byte[] iv) throws Exception{
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");  
        params.init(new IvParameterSpec(iv));
        return params;  
    }  
}  