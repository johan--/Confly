package com.bookdose.confly.helper;

import com.bookdose.confly.object.Constant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Teebio on 9/24/15 AD.
 */
public class FileEncrypt {

    public static final String USE_CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String AES_ENCRYPT_ALGO = "AES";


    public static byte[] encrypt(byte[] plainBytes){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey(), AES_ENCRYPT_ALGO);

            IvParameterSpec ivSpec = null;
            if(USE_CIPHER_TRANSFORMATION.contains("CBC"))
                ivSpec = new IvParameterSpec(getIVKey());

            Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
            if(ivSpec!=null)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            else
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encryptedBytes = cipher.doFinal(plainBytes);
            //return encodeBytes(encryptedBytes);
            return encryptedBytes;
        }
        catch(Exception e)
        {
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            return null;
        }
    }

    public static byte[] decrypt(byte[] plainBytes){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey(), AES_ENCRYPT_ALGO);

            IvParameterSpec ivSpec = null;
            if(USE_CIPHER_TRANSFORMATION.contains("CBC"))
                ivSpec = new IvParameterSpec(getIVKey());

            Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
            if(ivSpec!=null)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            else
                cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decryptedBytes = cipher.doFinal(plainBytes);
            //return encodeBytes(encryptedBytes);
            return decryptedBytes;
        }
        catch(Exception e)
        {
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            return null;
        }
    }

    public static boolean decrypt(FileInputStream fis, FileOutputStream fos){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey(), AES_ENCRYPT_ALGO);

            IvParameterSpec ivSpec = null;
            if(USE_CIPHER_TRANSFORMATION.contains("CBC"))
                ivSpec = new IvParameterSpec(getIVKey());

            Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
            if(ivSpec!=null)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            else
                cipher.init(Cipher.DECRYPT_MODE, keySpec);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            int b;
            byte[] d = new byte[8];
            while ((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            return false;
        }
    }

    public static boolean encrypt(FileInputStream fis, FileOutputStream fos){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(getAESKey(), AES_ENCRYPT_ALGO);

            IvParameterSpec ivSpec = null;
            if(USE_CIPHER_TRANSFORMATION.contains("CBC"))
                ivSpec = new IvParameterSpec(getIVKey());

            Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
            if(ivSpec!=null)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            else
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            CipherInputStream cis = new CipherInputStream(fis, cipher);

            int b;
            byte[] d = new byte[8];
            while ((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            return false;
        }
    }
//    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES_ENCRYPT_ALGO);
//        Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(clear);
//        return encrypted;
//    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES_ENCRYPT_ALGO);
        Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] getAESKey(){
        byte[] keyStart = Constant.AES_KEY.getBytes();
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sr.setSeed(keyStart);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] key = skey.getEncoded();
        return key;
    }

    public static byte[] getIVKey(){
        byte[] keyStart = Constant.AES_IV.getBytes();
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sr.setSeed(keyStart);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] key = skey.getEncoded();
        return key;
    }

    public static byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }

}
