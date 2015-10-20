package com.bookdose.confly.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

    public static String removeExtention(String filePath) {
        // These first few lines the same as Justin's
        File f = new File(filePath);

        // if it's a directory, don't remove the extention
        if (f.isDirectory()) return filePath;

        String name = f.getName();

        // Now we know it's a file - don't need to do any special hidden
        // checking or contains() checking because of:
        final int lastPeriodPos = name.lastIndexOf('.');
        if (lastPeriodPos <= 0)
        {
            // No period after first character - return name as it was passed in
            return filePath;
        }
        else
        {
            // Remove the last period and everything after it
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }

    public static String getThumbnailDirectory(String path){
        String filePath = getBookDirectory()+"/"+path+"/"+"thumb";
        final File m_fileBook = new File(filePath);
        if (!m_fileBook.exists())
            m_fileBook.mkdirs();
        return filePath;
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

    public static void createDirectory(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static boolean fileExits(String path){
        File file = new File(path);
        if (file.length()==0){
            return false;
        }
        Log.d("size ",""+file.length());
        return file.exists();
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

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getConfigPath(Issue issue){
        String savePath = Helper.getBookDirectory()+"/"+issue.path; //issue.path = folder name
        if (!Helper.fileExits(savePath)){
            Helper.createDirectory(savePath);
        }
        return  savePath+"/"+issue.path+".json";
    }

    public static boolean isEPub(Issue issue){
        String jsonData = readFile(getConfigPath(issue));
        try {
            JSONObject jobj = new JSONObject(jsonData);
            JSONObject detail = jobj.getJSONObject("detail");
            if(detail.getString("content_type").equals("epub")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFileEPubPath(Issue issue){
        String jsonData = readFile(getConfigPath(issue));
        try {
            JSONObject jobj = new JSONObject(jsonData);
            JSONObject detail = jobj.getJSONObject("detail");
            if(detail.getString("content_type").equals("epub")){
                JSONArray pages = jobj.getJSONArray("pages");
                JSONObject page = pages.getJSONObject(0);
                String path = page.getString("link");
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                String filePath = Helper.getBookDirectory()+"/"+issue.path+"/"+lastPath;
                return filePath;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void createThumbnail(String filePath, String folderName){
        FileInputStream epubInputStream = null;
        BufferedOutputStream bos = null;
        try {
            epubInputStream = new FileInputStream(new File(filePath));
            String lastPath = filePath.substring(filePath.lastIndexOf('/') + 1);
            String savePath = getThumbnailDirectory(folderName) + "/" + lastPath;

            if (!Helper.fileExits(savePath)) {
                byte[] cis = FileEncrypt.decrypt_data(epubInputStream);
                if (cis == null)
                    return;

                FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                bos = new BufferedOutputStream(outputStream);
                bos.write(cis, 0, cis.length);
                bos.flush();
                bos.close();

                byte[] imageData = null;

                //PDFView pdfView = new PDFView(getApplicationContext(),null);

                Bitmap imageBitmap = decodeFile(new File(savePath));

                int targetWidth = 150;

                double aspectRatio = (double) imageBitmap.getHeight() / (double) imageBitmap.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);

                //imageBitmap = Bitmap.createScaledBitmap(imageBitmap,(int)(imageBitmap.getWidth()*0.4), (int)(imageBitmap.getHeight()*0.4), true);
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, targetWidth, targetHeight, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                imageData = baos.toByteArray();

                outputStream = new FileOutputStream(new File(savePath));
                bos = new BufferedOutputStream(outputStream);
                bos.write(imageData, 0, imageData.length);
                bos.flush();
                bos.close();

                Thread.sleep(10);

            }
            //InputStream is = new ByteArrayInputStream(cis);
            //imageView.setImageBitmap(decodeFile(new File(savePath)));
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //imageView.setImageResource(R.drawable.no_image_detail);
        }
    }

    public static Bitmap bitmapWithPath(String path){
        if (fileExits(path))
            return decodeThumbFile(new File(path));
        else{
            Bitmap b = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(10);
            canvas.drawPaint(paint);
            //canvas.drawText("Loading", x, y, paint);
            canvas.drawARGB(1,0,0,0);
            Log.d("thumb", "width = " + b.getWidth());
            return b;
        }
    }

    public static Bitmap decodeThumbFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
            final int halfHeight = o.outHeight ;
            final int halfWidth = o.outWidth ;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / scale) > REQUIRED_SIZE
                    && (halfWidth / scale) > REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            //o2.inSampleSize = calculateInSampleSize(o, 70, 70);
            o2.inSampleSize = 1;
            //o2.inSampleSize = calculateInSampleSize(o2, reqWidth, reqHeight);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            //Log.d("thumb","width = "+bm.getWidth());
            //Canvas canvas = new Canvas(bm);
            return bm;
        }catch (OutOfMemoryError e){
//            Bitmap b = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(b);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(10);
//            canvas.drawPaint(paint);
//            //canvas.drawText("Loading", x, y, paint);
//            canvas.drawARGB(1,0,0,0);
//            Log.d("thumb", "width = " + b.getWidth());
            return null;
        }catch (FileNotFoundException e) {
//            Bitmap b = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(b);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(10);
//            canvas.drawPaint(paint);
//            //canvas.drawText("Loading", x, y, paint);
//            canvas.drawARGB(1,0,0,0);
//            Log.d("thumb", "width = " + b.getWidth());
            return null;
        }
    }

    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
            final int halfHeight = o.outHeight ;
            final int halfWidth = o.outWidth ;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / scale) > REQUIRED_SIZE
                    && (halfWidth / scale) > REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            //o2.inSampleSize = calculateInSampleSize(o, 70, 70);
            o2.inSampleSize = scale;
            //o2.inSampleSize = calculateInSampleSize(o2, reqWidth, reqHeight);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            //Log.d("thumb","width = "+bm.getWidth());
            //Canvas canvas = new Canvas(bm);
            return bm;
        }catch (OutOfMemoryError e){
//            Bitmap b = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(b);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(10);
//            canvas.drawPaint(paint);
//            //canvas.drawText("Loading", x, y, paint);
//            canvas.drawARGB(1,0,0,0);
//            Log.d("thumb", "width = " + b.getWidth());
            return null;
        }catch (FileNotFoundException e) {
//            Bitmap b = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(b);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(10);
//            canvas.drawPaint(paint);
//            //canvas.drawText("Loading", x, y, paint);
//            canvas.drawARGB(1,0,0,0);
//            Log.d("thumb", "width = " + b.getWidth());
            return null;
        }
    }
}
