package com.bookdose.confly.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Teebio on 8/27/15 AD.
 */
public class Helper {

    public static String BOOKDOSE_MAIN_PATH;
    private static final String BOOKDOSE_MAIN_FOLDER_NAME = "confly";
    public static final String BOOKDOSE_PATH_ENDSWITH_BOOK = "/books";
    public static final String BOOKDOSE_PATH_ENDSWITH_COVER = "/covers";

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void initMainDirectory(){
        BOOKDOSE_MAIN_PATH = Environment.getExternalStorageDirectory()+"/"+BOOKDOSE_MAIN_FOLDER_NAME;
        final File m_fileMain = new File(BOOKDOSE_MAIN_PATH);
        boolean canMkDirs = false;
        if (!m_fileMain.exists()) {
            canMkDirs = m_fileMain.mkdirs();
            if (!canMkDirs) {
                // do something
            }
        }else {
            canMkDirs = true;
        }
        if (canMkDirs) {
            final File m_fileBookCovers = new File(BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_COVER);
            final File m_fileBook = new File(BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_BOOK);
            if (!m_fileBook.exists()) {
                m_fileBook.mkdirs();
            }
            if (!m_fileBookCovers.exists()) {
                m_fileBookCovers.mkdirs();
            }
        }
    }

    public static String getBookDirectory(){
        final File m_fileBook = new File(BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_BOOK);
        if (!m_fileBook.exists())
            m_fileBook.mkdirs();
        return BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_BOOK;
    }

    public static String getCoversDirectory(){
        initMainDirectory();
        final File m_fileCover = new File(BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_COVER);
        if (!m_fileCover.exists())
            m_fileCover.mkdirs();
        return BOOKDOSE_MAIN_PATH + BOOKDOSE_PATH_ENDSWITH_COVER;
    }

    public static void saveCoverImage(Bitmap bm,String fileName){
        OutputStream fOut = null;
        Uri outputFileUri;
        try {
            File root = new File(getCoversDirectory());
            //root.mkdirs();
            File sdImageMainDirectory = new File(root, fileName);
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            //Toast.makeText(this, "Error occured. Please try again later.",Toast.LENGTH_SHORT).show();
        }

        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public static boolean fileExits(String path){
        File file = new File(path);
        return file.exists()& !file.isDirectory();
    }

    public static String findDeviceID(Context context) {
        final String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (android_id != null) {
            return android_id;
        }else {
            String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                    Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                    Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                    Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                    Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                    Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                    Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                    Build.USER.length()%10 ; //13 digits
            return m_szDevIDShort;
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
}
