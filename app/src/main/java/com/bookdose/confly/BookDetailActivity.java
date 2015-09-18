package com.bookdose.confly;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookDetailActivity extends Activity {

    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;
    Issue issue;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if(b!=null)
            issue = b.getParcelable(Constant.ISSUE);

        setContentView(R.layout.activity_book_detail);

        final ImageView coverView = (ImageView) findViewById(R.id.coverImage);
        TextView bookTitle = (TextView)findViewById(R.id.bookTitle);
        TextView bookTitleHeader = (TextView)findViewById(R.id.bookTitleHeader);
        TextView bookDetail = (TextView)findViewById(R.id.detailText);

        ImageButton downloadBtn = (ImageButton)findViewById(R.id.downloadBtn);
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        if (databaseHandler.getIssue(Integer.parseInt(issue.content_aid)) != null){
            downloadBtn.setBackgroundResource(R.drawable.already_tablet_icon);
            downloadBtn.setEnabled(false);
        }

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler databaseHandler = new DatabaseHandler(BookDetailActivity.this);
                if(databaseHandler.addIssue(issue)>0) {
                    coverView.buildDrawingCache();
                    Bitmap bm=coverView.getDrawingCache();
                    Helper.saveCoverImage(bm, issue.cover_image);
                    downloadIssue();
                }
            }
        });

        ImageButton backBtn = (ImageButton)findViewById(R.id.backImage);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDetailActivity.this.finish();
            }
        });

        ImageButton facebookBtn = (ImageButton)findViewById(R.id.btn_facebook);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWithFaceBook();
            }
        });

        ImageButton twitterBtn = (ImageButton)findViewById(R.id.btn_twitter);
        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (issue != null){
            Log.d("file url", issue.full_path);
            bookTitle.setText(issue.content_name);
            bookTitleHeader.setText(issue.content_name);
            bookDetail.setText(issue.description);
            new DownloadImageTask(coverView).execute(issue.getLargeCoverUrl());
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

            @Override
            public void onSuccess(Sharer.Result result) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //pd.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //pd.dismiss();
            bmImage.setImageBitmap(result);
        }
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
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

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/file_name.extension");

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

    public void downloadIssue(){
        // instantiate it within the onCreate method

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Download "+issue.content_name);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute("the url to the file you want to download");

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    void shareWithFaceBook(){
        try {

//            Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//            SharePhoto photo = new SharePhoto.Builder()
//                    .setBitmap(image)
//                    .setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
//                    .build();
//
//            SharePhotoContent content = new SharePhotoContent.Builder()
//                    .addPhoto(photo)
//                    .build();
//
//            ShareApi.share(content, null);


//            ShareLinkContent content = new ShareLinkContent.Builder()
//                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                    .build();


//            final File coverFile = new File(issue.getCoverUrl());
//            final FileOutputStream fOut = new FileOutputStream(coverFile);
//            //bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//            fOut.flush();
//            fOut.close();
//
//            String urlToShare = Constant.MAIN_URL+"/book-detail/";
//            String urlfacebook = urlToShare+issue.content_aid;
//            Intent intentfacebook = new Intent(android.content.Intent.ACTION_SEND);
////						intentfacebook.setType("text/plain");
////						intentfacebook.setType("*/*");
//
////						intentfacebook.putExtra(android.content.Intent.EXTRA_TEXT, "PTTEP E-Llibrary |" );
//
//
//            intentfacebook.setType("image/*");
////					    intentfacebook.putExtra(android.content.Intent.EXTRA_SUBJECT,"PTTEP E-Llibrary |");
//            intentfacebook.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://" + coverFile.getAbsolutePath()));
//
//
//
//
//            boolean facebookAppFound = false;
//            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intentfacebook, 0);
//            for (ResolveInfo info : matches) {
//                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook")) {
//                    intentfacebook.setPackage(info.activityInfo.packageName);
//                    facebookAppFound = true;
//                    break;
//                }
//            }
//            //If facebook app not found, load sharer.php in a browser
//            if (!facebookAppFound) {
//                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + issue.cover_image;
//                intentfacebook = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
//            }
//            startActivity(intentfacebook);

            if (shareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Hello Facebook")
                        .setContentDescription(
                                "The 'Hello Facebook' sample  showcases simple Facebook integration")
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                        .build();

                shareDialog.show(linkContent);
            }

        }catch (Exception e) {
            System.out.println("ShelfActiviy - createBookDownloadItemPanel "+e.toString());
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
