package com.bookdose.confly.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.bookdose.confly.object.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Teebio on 8/25/15 AD.
 */
public class ServiceRequest {
    public static final String SERVICE = "webservice/";
    public static final String GET_PRODUCT_LIST = Constant.MAIN_URL+SERVICE+"get_product_list";
    public static final String MYLIB_API = "";
    public static final String ISSUE_DETAIL_API = "";

    public static List<NameValuePair> defaultParam(){
        List<NameValuePair> defaultValue = new ArrayList<NameValuePair>();
        defaultValue.add(new BasicNameValuePair("device", Constant.DEVICE));
        defaultValue.add(new BasicNameValuePair("appname", Constant.APP_NAME));
        defaultValue.add(new BasicNameValuePair("device_id", "123456"));
        defaultValue.add(new BasicNameValuePair("version", Constant.VERSION));
        return defaultValue;
    }

    public static JSONArray requestProductListAPI(){
        JSONObject request = null;
        try {
            request = new RequestTask().execute(GET_PRODUCT_LIST).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            return request.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class RequestTask extends AsyncTask<String, NameValuePair, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            return postData(params[0]);
        }

        protected void onPostExecute(Double result){
//            pb.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        public JSONObject postData(String url) {
            InputStream is = null;
            JSONObject jObj = null;
            String json = "";
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://somewebsite.com/receiver.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("myHttpData", ""));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            try{
                List<NameValuePair> nameValuePairs = defaultParam();
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return jObj;
        }

    }
}
