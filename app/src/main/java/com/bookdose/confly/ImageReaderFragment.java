package com.bookdose.confly;


import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bookdose.confly.helper.FileEncrypt;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.object.TouchImageView;
import com.joanzapata.pdfview.PDFView;

import org.vudroid.core.codec.CodecDocument;
import org.vudroid.core.codec.CodecPage;
import org.vudroid.pdfdroid.codec.PdfContext;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageReaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageReaderFragment extends Fragment implements TouchImageView.TouchImageListener, PDFView.OnTapPDFView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FILE_PATH = "file_path";
    private static final String ARG_FOLDER = "folder";

    // TODO: Rename and change types of parameters
    private String contentName;
    private String filePath;
    private String path;

    ProgressBar progressBar;
    TouchImageView imageView;
    PDFView pdfView;

    public static ImageReaderFragment newInstance(String param1, String folder) {
        ImageReaderFragment fragment = new ImageReaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, param1);
        args.putString(ARG_FOLDER, folder);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void tapPdfView() {
        if (imageReaderListener != null)
            imageReaderListener.didDoubleTapImageReader();
    }

    public interface ImageReaderListener{
        void didDoubleTapImageReader();
    }

    public ImageReaderListener imageReaderListener;

    public void setImageReaderListener(ImageReaderListener imageReaderListener) {
        this.imageReaderListener = imageReaderListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filePath = getArguments().getString(ARG_FILE_PATH);
            contentName = getArguments().getString(ARG_FOLDER);
            String lastPath = filePath.substring(filePath.lastIndexOf('/') + 1);
            path = Helper.getBookDirectory()+"/"+contentName+"/"+lastPath;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_reader, container, false);
        progressBar = (ProgressBar)rootView.findViewById(R.id.waitingBar);

//        imageView = (TouchImageView) rootView.findViewById(R.id.readerImage);
//        imageView.setTouchImageListener(this);
//        imageView.setMaxZoom(3f);

//        if (Helper.fileExits(path)){
//            setImage();
//        }else {
//            //imageView.setImageResource(R.drawable.no_image_detail);
//            String status = new DatabaseHandler(getActivity()).getIssueStatus(contentName);
//            if (status != null)
//                if (!status.equals(Constant.DOWNLOADING_STATUS))
//                    downloade(filePath);
//        }

        pdfView = (PDFView)rootView.findViewById(R.id.pdfView);
        pdfView.setOnTapPDFView(this);

        if (Helper.fileExits(path)){
            //renderPDF();
            String lastPath = path.substring(path.lastIndexOf('/') + 1);
            String savePath = Helper.getBookDirectory() + "/" + contentName + "/" + contentName + "/" + lastPath;
            pdfView.fromFile(new File(savePath))
                    .defaultPage(1)
                    .load();
            progressBar.setVisibility(View.INVISIBLE);
            //new RenderTask().execute();
        }else {
            //imageView.setImageResource(R.drawable.no_image_detail);
//            String status = new DatabaseHandler(getActivity()).getIssueStatus(contentName);
//            if (status != null)
//                if (!status.equals(Constant.DOWNLOADING_STATUS))
                    downloade(filePath);
        }
        return rootView;
    }

    // Decodes image and scales it to reduce memory consumption
   /* private Bitmap decodeFile(File f) throws InterruptedException {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 360;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
            final int halfHeight = o.outHeight / 2;
            final int halfWidth = o.outWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / scale) > REQUIRED_SIZE
                    && (halfWidth / scale) > REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            //o2.inSampleSize = calculateInSampleSize(o, 70, 70);
            o2.inSampleSize = 2;
            Thread.sleep(5);
            //o2.inSampleSize = calculateInSampleSize(o2, reqWidth, reqHeight);
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    void renderPDF(){
//        InputStream epubInputStream = null;
//        try {
//            epubInputStream = new FileInputStream(new File(path));
//            String lastPath = path.substring(path.lastIndexOf('/') + 1);
//            String savePath = Helper.getBookDirectory() + "/" + contentName + "/" + contentName + "/" + lastPath;
//            if (!Helper.fileExits(savePath)) {
//                byte[] cis = FileEncrypt.decrypt_data(epubInputStream);
//
//                FileOutputStream outputStream = new FileOutputStream(new File(savePath));
//                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
//                bos.write(cis, 0, cis.length);
//                bos.flush();
//                bos.close();
//            }
//            Thread.sleep(5);
//            //InputStream is = new ByteArrayInputStream(cis);
//            pdfView.fromFile(new File(savePath))
//                    .defaultPage(1)
//                    .load();
//            progressBar.setVisibility(View.INVISIBLE);
//        }catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }catch (RuntimeException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private class RenderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            renderPDF();
            InputStream epubInputStream = null;
            try {
                epubInputStream = new FileInputStream(new File(path));
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                String savePath = Helper.getBookDirectory() + "/" + contentName + "/" + contentName + "/" + lastPath;
                if (!Helper.fileExits(savePath)) {
                    byte[] cis = FileEncrypt.decrypt_data(epubInputStream);

                    FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                    bos.write(cis, 0, cis.length);
                    bos.flush();
                    bos.close();
                }
                Thread.sleep(5);
                //InputStream is = new ByteArrayInputStream(cis);

            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String lastPath = path.substring(path.lastIndexOf('/') + 1);
            String savePath = Helper.getBookDirectory() + "/" + contentName + "/" + contentName + "/" + lastPath;
            pdfView.fromFile(new File(savePath))
                    .defaultPage(1)
                    .load();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void renderThumb(String filePath){
        try {

            PdfContext pdf_conext = new PdfContext();
            CodecDocument d = pdf_conext.openDocument(filePath);

            CodecPage vuPage = d.getPage(0); // choose your page number
            RectF rf = new RectF();
            rf.bottom = rf.right = (float)1.0;
            Bitmap bitmap = vuPage.renderBitmap(150, 150, rf);
            String lastPath = filePath.substring(filePath.lastIndexOf('/') + 1);
            String savePath = Helper.getThumbnailDirectory(contentName) + "/" + lastPath;

            BufferedOutputStream bos = null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] imageData = baos.toByteArray();

            savePath = Helper.removeExtention(savePath);
            String thumbPath = savePath+".jpg";

            FileOutputStream outputStream = null;

            outputStream = new FileOutputStream(new File(thumbPath));
            bos = new BufferedOutputStream(outputStream);
            bos.write(imageData, 0, imageData.length);
            bos.flush();
            bos.close();

            Thread.sleep(10);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

//    void setImage(){
//        InputStream epubInputStream = null;
//        try {
//            epubInputStream = new FileInputStream(new File(path));
//            String lastPath = path.substring(path.lastIndexOf('/') + 1);
//            String savePath = Helper.getBookDirectory() + "/" + contentName + "/" + contentName + "/" + lastPath;
//            if (!Helper.fileExits(savePath)) {
//                byte[] cis = FileEncrypt.decrypt_data(epubInputStream);
//
//                FileOutputStream outputStream = new FileOutputStream(new File(savePath));
//                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
//                bos.write(cis, 0, cis.length);
//                bos.flush();
//                bos.close();
//            }
//            //InputStream is = new ByteArrayInputStream(cis);
//            imageView.setImageBitmap(decodeFile(new File(savePath)));
//            progressBar.setVisibility(View.INVISIBLE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //imageView.setImageResource(R.drawable.no_image_detail);
//        }
//    }

    @Override
    public void doubleTapOnImage() {
        if (imageReaderListener != null)
            imageReaderListener.didDoubleTapImageReader();
    }

    public void downloade(String fileURL){
        // instantiate it within the onCreate method

        //final DownloadTask downloadTask = new DownloadTask(fileURL);
        new DownloadTask().execute(fileURL);

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            //setImage();
            renderPDF();

            //Helper.createThumbnail(path,contentName);
            Log.d("Load image","complete");
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                String path = sUrl[0];
                String lastPath = path.substring(path.lastIndexOf('/') + 1);

                // download the file
                input = connection.getInputStream();
                String savePath = Helper.getBookDirectory()+"/"+contentName+"/"+lastPath;
                output = new FileOutputStream(savePath);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                renderThumb(savePath);
                //Helper.createThumbnail(savePath,contentName);
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
