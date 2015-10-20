package com.bookdose.confly.helper;

import android.util.Base64;

import com.bookdose.confly.object.Constant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Teebio on 9/24/15 AD.
 */
public class FileEncrypt {

    public static final String USE_CIPHER_TRANSFORMATION = "AES/ECB/NoPadding";
    public static final String AES_ENCRYPT_ALGO = "AES";

    public static byte[] getBytesFromInputStream(InputStream in)
    {
        try {
            byte[] fileBytes = new byte[in.available()];
            in.read( fileBytes);
            in.close();
            return fileBytes;
        }
        catch (Exception e) {
            //Log.e(AppCommon.TAG_DOWNLOAD, "Cannot get bytes from file " + file_path + ": " + e.getMessage());
            return null;
        }
    }

    public static InputStream decrypt(InputStream inputStream){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(Constant.AES_KEY.getBytes(), USE_CIPHER_TRANSFORMATION);

            byte[] data = getBytesFromInputStream(inputStream);

            byte[] datas = decrypt(Base64.decode(data, Base64.DEFAULT));

            InputStream myInputStream = new ByteArrayInputStream(datas);
            return myInputStream;
        }
        catch(Exception e)
        {
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt_data(InputStream inputStream){
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(Constant.AES_KEY.getBytes(), USE_CIPHER_TRANSFORMATION);

            byte[] data = getBytesFromInputStream(inputStream);

            byte[] datas = decrypt(Base64.decode(data, Base64.DEFAULT));

            return datas;
        }catch (OutOfMemoryError ex){
            ex.printStackTrace();
            return null;
        }
        catch(Exception e)
        {
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] cipherbytes)
    {
        if(cipherbytes==null)
            return null;

        if(cipherbytes.length==0)
            return cipherbytes;

        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(Constant.AES_KEY.getBytes(), USE_CIPHER_TRANSFORMATION);

            IvParameterSpec ivSpec = null;
            if(USE_CIPHER_TRANSFORMATION.contains("CBC"))
                ivSpec = new IvParameterSpec(Constant.AES_IV.getBytes());

            Cipher cipher = Cipher.getInstance(USE_CIPHER_TRANSFORMATION);
            if(ivSpec!=null)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            else
                cipher.init(Cipher.DECRYPT_MODE, keySpec);

            //byte[] plainBytes = cipher.doFinal(decode(cipherbytes));
            byte[] plainBytes = cipher.doFinal(cipherbytes);
            return plainBytes;
        }catch (OutOfMemoryError ex){
            ex.printStackTrace();
            return null;
        }
        catch(Exception e)
        {
            //this.error_msg = "Error " + e.getClass().getName() + ":" + e.getMessage();
            e.printStackTrace();
            return null;
        }
    } //end decrypt


}
