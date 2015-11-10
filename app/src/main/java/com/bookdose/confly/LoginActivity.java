package com.bookdose.confly;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends Activity {

    EditText userText;
    EditText passText;

    TextView expireText;

    LinearLayout loginLayout;
    LinearLayout logontLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = (LinearLayout)findViewById(R.id.loginLayout);
        logontLayout = (LinearLayout)findViewById(R.id.logoutLayout);

        userText = (EditText)findViewById(R.id.userText);
        passText = (EditText)findViewById(R.id.passText);

        expireText = (TextView)findViewById(R.id.expireText);

        SharedPreferences prefs = LoginActivity.this.getSharedPreferences(
                "com.bookdose.confly", Context.MODE_PRIVATE);
        String expire = prefs.getString(Constant.EXPIRE_KEY, "");
        if (!expire.equals("")) {
            loginLayout.setVisibility(View.GONE);
            logontLayout.setVisibility(View.VISIBLE);
            expireText.setText(expire);
        }else {
            loginLayout.setVisibility(View.VISIBLE);
            logontLayout.setVisibility(View.GONE);
        }

        ImageButton backbtn = (ImageButton)findViewById(R.id.backBtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button logoutBtn = (Button)findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
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

    void login(){
        String username = userText.getText().toString();
        String password = passText.getText().toString();

        if (username.length()<1){
            return;
        }
        if (password.length()<1){
            return;
        }
        JSONObject object = ServiceRequest.requestLoginAPI(username, password, Helper.findDeviceID(this));
        if (object != null){
            Log.d("login",object.toString());
            try {
                String token = object.getString("login_token");
                String salt = object.getString("salt");
                String hash_pwd = null;
                String key = null;
                try {
                    hash_pwd = Helper.md5ToHexString(password+"C0mp@n3w5");
                    key = Helper.md5ToHexString((hash_pwd+salt));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject obj = ServiceRequest.loginAPI(token, key, Helper.findDeviceID(this));
                if (obj != null){
                    SharedPreferences prefs = LoginActivity.this.getSharedPreferences(
                            "com.bookdose.confly", Context.MODE_PRIVATE);
                    prefs.edit().putString(Constant.TOKEN_KEY, obj.getString("token")).apply();
                    prefs.edit().putString(Constant.USERID_KEY, obj.getString("user_id")).apply();
                    prefs.edit().putString(Constant.ACTIVATE_TIME_KEY, obj.getString("activation_time")).apply();
                    prefs.edit().putString(Constant.REMAIN_KEY, obj.getString("remain_day")).apply();
                    prefs.edit().putString(Constant.USER_KEY, username).apply();
                    prefs.edit().putString(Constant.PASS_KEY, password).apply();
                    if (!obj.getString("remain_day").equals("0")){
                        String dtStart = obj.getString("activation_time");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(dtStart);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            cal.add(Calendar.DAY_OF_YEAR, 1); // <--
                            Date tomorrow = cal.getTime();
                            prefs.edit().putString(Constant.EXPIRE_KEY, tomorrow.toString()).apply();
                            System.out.println(date);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    LoginActivity.this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    void logout(){
        SharedPreferences prefs = LoginActivity.this.getSharedPreferences(
                "com.bookdose.confly", Context.MODE_PRIVATE);

        String ret = ServiceRequest.requestLogoutAPI(prefs.getString(Constant.TOKEN_KEY,""),Helper.findDeviceID(this));
        if(ret.equals("1")){
            prefs.edit().putString(Constant.TOKEN_KEY, "").apply();
            prefs.edit().putString(Constant.USERID_KEY, "").apply();
            prefs.edit().putString(Constant.ACTIVATE_TIME_KEY, "").apply();
            prefs.edit().putString(Constant.REMAIN_KEY, "").apply();
            prefs.edit().putString(Constant.USER_KEY, "").apply();
            prefs.edit().putString(Constant.PASS_KEY, "").apply();
            prefs.edit().putString(Constant.EXPIRE_KEY, "").apply();
        }
        LoginActivity.this.finish();
    }
}
