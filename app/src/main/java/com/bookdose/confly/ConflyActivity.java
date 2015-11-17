package com.bookdose.confly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bookdose.confly.adapter.CategorylistAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.FileEncrypt;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.JsonHelper;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Category;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import epubtest.BookViewActivity;
import epubtest.CustomFont;
import epubtest.SkyApplication;
import epubtest.SkyUtility;

public class ConflyActivity extends FragmentActivity implements DownloadFragment.DownloadFragmentListener, DownloadPagerFragment.DownloadPagerListener, PopoverView.PopoverViewDelegate, CategorylistAdapter.CategoryListListener, ShelfFragment.ShelfListenner {

    DownloadPagerFragment downloadFragment;
    ShelfFragment shelfFragment;
    NewsFragment newsFragment;
    DownloadFragment downloadListFragment;

    ImageButton editBtn;
    ImageButton bookBt;
    ImageButton docBt;
    ImageButton menuBt;
    ImageButton downloadMenu;
    ImageButton mylibraryMenu;
    ImageButton magBt;
    ImageButton newsMenu;
    ImageButton langBt;
    ImageButton chooseTypeBt;
    TextView activityText;
    TextView languageText;
    ProgressDialog progressBar;

    ProgressDialog mProgressDialog;

    boolean isShelf;
    String result = "WAIT";
    Category category;
    String chooseType = Constant.ALL;

    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confly);

        if(Helper.isTablet(this))
            pushLibraryFragment();
        else
            pushDownloadFragment();

        downloadMenu = (ImageButton)findViewById(R.id.download_menu);
        mylibraryMenu = (ImageButton)findViewById(R.id.myshelf_menu);
        mylibraryMenu = (ImageButton)findViewById(R.id.myshelf_menu);
        newsMenu = (ImageButton)findViewById(R.id.news_menu);
        magBt = (ImageButton)findViewById(R.id.activity_btn_mag);
        langBt = (ImageButton)findViewById(R.id.language_menu);
        langBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverLanguage(v);
            }
        });

        magBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory(Constant.MAGAZINE_ID, v);
            }
        });

        bookBt = (ImageButton)findViewById(R.id.activity_btn_book);
        bookBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory(Constant.BOOK_ID, v);
            }
        });

        docBt = (ImageButton)findViewById(R.id.activity_btn_doc);
        docBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory(Constant.DOCCUMENT_ID, v);
            }
        });

        menuBt = (ImageButton)findViewById(R.id.activity_btn_menu);
        menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuiew();
            }
        });

        activityText = (TextView)findViewById(R.id.activity_txt);
        languageText = (TextView)findViewById(R.id.languageText);

        editBtn = (ImageButton)findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEdit = !isEdit;
                if (isEdit) {
                    editBtn.setImageResource(R.drawable.bin_active);
                } else {
                    editBtn.setImageResource(R.drawable.bin_inactive);
                }

                shelfFragment.isEdit = isEdit;
                shelfFragment.reloadData(chooseType);

            }
        });

        chooseTypeBt = (ImageButton)findViewById(R.id.activity_btn_type);
        chooseTypeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverChooseType(v);
            }
        });

        downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_active_mobile));

        downloadMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentMenu();
                if(Helper.isTablet(ConflyActivity.this))
                    pushLibraryFragment();
                else
                    pushDownloadFragment();
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_active_mobile));
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_inactive_mobile));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_inactive_mobile));
                isShelf = false;
            }
        });
        mylibraryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShelfMenu();
                pushMyshelfFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_active_mobile));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_inactive_mobile));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_inactive_mobile));

            }
        });
        newsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewsMenu();
                pushNewsFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_inactive_mobile));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_inactive_mobile));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_active_mobile));
                isShelf = false;
            }
        });

        connectDatatBase();
        showContentMenu();

        activityText.setText(Constant.MAGAZINE + " : ALL");
        SharedPreferences prefs = this.getSharedPreferences(
                "com.bookdose.confly", Context.MODE_PRIVATE);
        String lang = prefs.getString(Constant.LANGUAGE_KEY, "All");
        languageText.setText(lang);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confly, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (result.equals("SUCCESS")){
            pushMyshelfFragment();
            result = "";
        }else {
            if (!isShelf){
                if (Helper.isTablet(ConflyActivity.this)) {
                    if (category != null)
                        downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
                    else
                        downloadFragment.loadProductList(Constant.ALL_ID, Constant.MAGAZINE_ID);
                }else {
                    if (category != null)
                        downloadListFragment.loadProductList(category.category_aid, category.product_main_aid);
                    else
                        downloadListFragment.loadProductList(Constant.ALL_ID, Constant.MAGAZINE_ID);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                result = data.getStringExtra("result");

                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_active_mobile));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_inactive));
                showShelfMenu();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    void showPopoverChooseType(View view){
        RelativeLayout rootView = (RelativeLayout)findViewById(R.id.mainview);

        final PopoverView popoverView = new PopoverView(ConflyActivity.this, R.layout.popover_list_view);
        popoverView.setContentSizeForViewInPopover(new Point(320, 320));
        popoverView.setDelegate(ConflyActivity.this);
        popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(view), PopoverView.PopoverArrowDirectionUp, true);
        ListView listView = (ListView)popoverView.findViewById(R.id.poplist);

        final ArrayList<String> types = new ArrayList<String>();
        types.add(Constant.ALL);
        types.add(Constant.MAGAZINE);
        types.add(Constant.BOOK);
        types.add(Constant.DOCCUMENT);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, types);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                activityText.setText("TYPE : "+types.get(position));
                chooseType = types.get(position);
                popoverView.dissmissPopover(true);
                shelfFragment.reloadData(chooseType);

//                SharedPreferences prefs = ConflyActivity.this.getSharedPreferences(
//                        "com.bookdose.confly", Context.MODE_PRIVATE);
//                prefs.edit().putString(Constant.LANGUAGE_KEY, types.get(position)).apply();

            }

        });
    }

    void showPopoverLanguage(View view){
        //get root layout
        RelativeLayout rootView = (RelativeLayout)findViewById(R.id.mainview);

        final PopoverView popoverView = new PopoverView(ConflyActivity.this, R.layout.popover_list_view);
        popoverView.setContentSizeForViewInPopover(new Point(320, 320));
        popoverView.setDelegate(ConflyActivity.this);
        popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(view), PopoverView.PopoverArrowDirectionUp, true);
        ListView listView = (ListView)popoverView.findViewById(R.id.poplist);

        final ArrayList<String> languages = new ArrayList<String>();
        languages.add("ALL");
        languages.add("TH");
        languages.add("EN");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, languages);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                languageText.setText(languages.get(position));
                popoverView.dissmissPopover(true);

                SharedPreferences prefs = ConflyActivity.this.getSharedPreferences(
                        "com.bookdose.confly", Context.MODE_PRIVATE);
                prefs.edit().putString(Constant.LANGUAGE_KEY, languages.get(position)).apply();

                if (Helper.isTablet(ConflyActivity.this)) {
                    if (category != null)
                        downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
                    else
                        downloadFragment.loadProductList(Constant.ALL_ID, Constant.MAGAZINE_ID);
                }else {
                    if (category != null)
                        downloadListFragment.loadProductList(category.category_aid, category.product_main_aid);
                    else
                        downloadListFragment.loadProductList(Constant.ALL_ID, Constant.MAGAZINE_ID);
                }
            }

        });

    }

    void showPopoverCategory(String catID, View view){
        //get root layout
        RelativeLayout rootView = (RelativeLayout)findViewById(R.id.mainview);

        PopoverView popoverView = new PopoverView(ConflyActivity.this, R.layout.popover_list_view);
        popoverView.setContentSizeForViewInPopover(new Point(400, 480));
        popoverView.setDelegate(ConflyActivity.this);
        popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(view), PopoverView.PopoverArrowDirectionUp, true);
        ListView listView = (ListView)popoverView.findViewById(R.id.poplist);

        ArrayList<Category> categories = new ArrayList<Category>();
        JSONArray response = ServiceRequest.requestCategoryListAPI(catID,Helper.findDeviceID(this));
        Category cat = new Category();
        cat.category_name = Constant.ALL;
        cat.category_aid = Constant.ALL_ID;
        cat.product_main_aid = catID;
        categories.add(cat);

        if (response != null){
            for (int loop=0; loop <response.length(); loop++){
                try {
                    JSONObject object = response.getJSONObject(loop);
                    categories.add(new Category(object));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CategorylistAdapter categorylistAdapter = new CategorylistAdapter(ConflyActivity.this,categories );
            categorylistAdapter.setCategoryListListener(this);
            listView.setAdapter(categorylistAdapter);
        }
    }

    void showMenuiew(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void pushDownloadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        downloadListFragment = DownloadFragment.newInstance("", "");
        downloadListFragment.setDownloadFragmentListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel, downloadListFragment)
                .commit();
    }

    void pushNewsFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        newsFragment = new NewsFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel,newsFragment)
                .commit();
    }

    void pushMyshelfFragment(){
        isShelf = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        shelfFragment = new ShelfFragment();
        shelfFragment.setShelfListenner(this);
        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel,shelfFragment)
                .commit();
    }

    void pushLibraryFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        downloadFragment = DownloadPagerFragment.newInstance("", "");
        downloadFragment.setDownloadPagerListener(this);
        //downloadFragment.setDownloadFragmentListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel,downloadFragment)
                .commit();
    }

    void showBookDetail(Issue issue){
//        DownloadListener downloadListener = new DownloadListener() {
//            @Override
//            public void onDownloadCompleted(Issue issue) {
//                showShelfMenu();
//                pushMyshelfFragment();
//                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_inactive));
//                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_active));
//            }
//        };
        Intent intent = new Intent(this,BookDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(Constant.ISSUE, issue);
        //b.putSerializable("Listener", downloadListener);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }

    public void connectDatatBase(){
        DatabaseHandler myDbHelper =null;
        myDbHelper = new DatabaseHandler(this.getApplicationContext());

        try {
            myDbHelper.createDatabase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");
        }

//        try {
//
//            myDbHelper.openDatabase();
//
//        }catch(SQLException sqle){
//
//            throw sqle;
//
//        }
    }

    void showContentMenu(){
        activityText.setText(Constant.MAGAZINE + " : ALL");
        docBt.setVisibility(View.VISIBLE);
        bookBt.setVisibility(View.VISIBLE);
        magBt.setVisibility(View.VISIBLE);
        chooseTypeBt.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        langBt.setVisibility(View.VISIBLE);
        languageText.setVisibility(View.VISIBLE);

        if (isEdit) {
            editBtn.setImageResource(R.drawable.bin_inactive);
            isEdit = false;
            shelfFragment.isEdit = isEdit;
            shelfFragment.reloadData(chooseType);
        }
    }

    void showShelfMenu(){
        activityText.setText("TYPE : ALL");
        docBt.setVisibility(View.GONE);
        bookBt.setVisibility(View.GONE);
        magBt.setVisibility(View.GONE);
        chooseTypeBt.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.VISIBLE);
        langBt.setVisibility(View.INVISIBLE);
        languageText.setVisibility(View.INVISIBLE);
    }

    void showNewsMenu(){
        activityText.setText("NEWS");
        chooseTypeBt.setVisibility(View.INVISIBLE);
        docBt.setVisibility(View.INVISIBLE);
        bookBt.setVisibility(View.INVISIBLE);
        magBt.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        langBt.setVisibility(View.INVISIBLE);
        languageText.setVisibility(View.INVISIBLE);

        if (isEdit) {
            editBtn.setImageResource(R.drawable.bin_inactive);
            isEdit = false;
            shelfFragment.isEdit = isEdit;
            shelfFragment.reloadData(chooseType);
        }
    }

    public void registerFonts() {
        this.registerCustomFont("Underwood", "uwch.ttf");
        this.registerCustomFont("Mayflower", "Mayflower Antique.ttf");
//        SkyUtility st = new SkyUtility(this);
//        st.copyFontToDevice(fontFileName);
//        st.makeSetup();
    }

    public void registerCustomFont(String fontFaceName,String fontFileName) {
        SkyUtility st = new SkyUtility(this);
        st.copyFontToDevice(fontFileName);
        st.makeSetup();

        SkyApplication app = new SkyApplication();
        app.customFonts.add(new CustomFont(fontFaceName, fontFileName));
    }

    @Override
    public void onSelectIssue(Issue issue) {
        showBookDetail(issue);
    }

    @Override
    public void didSelectedBook(Issue issue) {
        showBookDetail(issue);
    }

    @Override
    public void popoverViewWillShow(PopoverView view) {

    }

    @Override
    public void popoverViewDidShow(PopoverView view) {

    }

    @Override
    public void popoverViewWillDismiss(PopoverView view) {

    }

    @Override
    public void popoverViewDidDismiss(PopoverView view) {

    }

    @Override
    public void onSelectCategory(Category category) {
        if (category.product_main_aid.equals(Constant.MAGAZINE_ID))
            activityText.setText(Constant.MAGAZINE+" : "+category.category_name);
        else if (category.product_main_aid.equals(Constant.BOOK_ID))
            activityText.setText(Constant.BOOK+" : "+category.category_name);
        else if (category.product_main_aid.equals(Constant.DOCCUMENT_ID))
            activityText.setText(Constant.DOCCUMENT+" : "+category.category_name);
        if(Helper.isTablet(this))
            downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
        else
            downloadListFragment.loadProductList(category.category_aid, category.product_main_aid);
        this.category = category;
    }

    @Override
    public void openIssue(Issue issue) {
        if (isEdit)
            editIssue(issue);
        else
            openBook(issue);
    }

    @Override
    public void downloadIssue(Issue issue) {
        download(issue);
    }

    void editIssue(final Issue issue){
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteIssue(issue);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void deleteIssue(Issue issue){
        new DatabaseHandler(this).deleteIssue(issue);
        String issuePath = Helper.getBookDirectory()+"/"+issue.path;
        Helper.deleteDirectory(new File(issuePath));
        shelfFragment.reloadData(chooseType);
    }

    void openBook(Issue issue){
        isShelf = true;

        if (Helper.isEPub(issue)){
            try {

                registerFonts();

                InputStream epubInputStream = new FileInputStream(Helper.getFileEPubPath(issue));

                String savePath = Helper.getBookDirectory()+"/"+issue.path+"/"+issue.path+".epub";

                byte[] cis = FileEncrypt.decrypt_data(epubInputStream);

                FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                bos.write(cis, 0, cis.length);
                bos.flush();
                bos.close();


//                Intent i = new Intent(this, EPubReaderActivity.class);
//                i.putExtra("path", savePath);
//                i.putExtra("name", issue.content_name);
//
//                startActivity(i);

                Intent intent = new Intent(this, BookViewActivity.class);
//                if (!bi.isFixedLayout) {
//                    intent = new Intent(this,BookViewActivity.class);
//                }else {
//                    intent = new Intent(this,MagazineActivity.class);
//                }
//                intent.putExtra("BOOKCODE",issue.);
                intent.putExtra("TITLE",issue.content_name);
                intent.putExtra("AUTHOR", issue.author);
                intent.putExtra("BOOKNAME",issue.path);
                intent.putExtra("FILEPATH",savePath);
//                if (bi.isRTL && !bi.isRead) {
//                    intent.putExtra("POSITION",(double)1);
//                }else {
//                    intent.putExtra("POSITION",bi.position);
//                }
//                intent.putExtra("THEMEINDEX",app.setting.theme);
//                intent.putExtra("DOUBLEPAGED",app.setting.doublePaged);
//                intent.putExtra("transitionType",app.setting.transitionType);
//                intent.putExtra("GLOBALPAGINATION",app.setting.globalPagination);
//                intent.putExtra("RTL",bi.isRTL);
//                intent.putExtra("VERTICALWRITING",bi.isVerticalWriting);
//
//                intent.putExtra("SPREAD", bi.spread);
//                intent.putExtra("ORIENTATION", bi.orientation);

                //startActivity(intent);
                startActivityForResult(intent, 1);

            } catch (IOException e) {

                Log.e("epublib", e.getMessage());

            }
        }else {
            Bundle b = new Bundle();
            b.putParcelable(Constant.ISSUE, issue);
            Intent intent = new Intent(this,ImageReaderActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }

    }

    void download(Issue issue){
        JSONObject obj = JsonHelper.getJsonConfig(issue);
        try {
            JSONObject detail = obj.getJSONObject("detail");
            if(detail.getString("content_type").equals("epub")){
                JSONArray pages = obj.getJSONArray("pages");
                JSONObject page = pages.getJSONObject(0);
                downloadePub(page.getString("link"), issue);
            }else {
                ArrayList<String> links = JsonHelper.getLinkDownload(issue);
                downloadIssue(links, issue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface DownloadListener extends Serializable {
        void onDownloadCompleted(Issue issue);
    }

    void showLoading(){
        progressBar = ProgressDialog.show(this, "", "Loading...");
    }

    void hideLoading(){
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }

    public void downloadePub(String fileURL, Issue issue){
        // instantiate it within the onCreate method

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Download "+issue.content_name);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

// execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this,fileURL,issue);
        downloadTask.execute(fileURL);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    public void downloadIssue(ArrayList<String> links, Issue issue){
        // instantiate it within the onCreate method

        mProgressDialog = new ProgressDialog(this);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this,null,issue);
        String[] stockArr = new String[links.size()];
        stockArr = links.toArray(stockArr);
        downloadTask.execute(stockArr);

        //if(Helper.isEPub(issue)){

        mProgressDialog.setMessage("Download "+issue.content_name);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
        //}

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private Issue issue;

        public DownloadTask(Context context) {
            this.context = context;
        }
        public DownloadTask(Context context, String url){
            this.context = context;
        }
        public DownloadTask(Context context, String url, Issue issue){
            this.context = context;
            this.issue = issue;
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
//            if(Helper.isEPub(issue))
//                mProgressDialog.show();
//            else {
//                onComplete();
//            }
            mProgressDialog.show();
            new DatabaseHandler(getApplicationContext()).updateIssueStatus(Constant.DOWNLOADING_STATUS, issue.content_aid);
            Toast.makeText(context, "File start downloaded", Toast.LENGTH_SHORT).show();
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
            if(Helper.isEPub(issue)) {
                mProgressDialog.dismiss();
                //onComplete();
            }else {
                mProgressDialog.dismiss();
            }
            if (result != null) {
                new DatabaseHandler(getApplicationContext()).updateIssueStatus(Constant.FAIL_STATUS, issue.content_aid);
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }else {
                new DatabaseHandler(getApplicationContext()).updateIssueStatus(Constant.COMPLETE_STATUS, issue.content_aid);
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            }

            shelfFragment.reloadData(chooseType);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                for (int i = 0; i < sUrl.length; i++) {
                    URL url = new URL(sUrl[i]);
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

                    String path = sUrl[i];
                    String lastPath = path.substring(path.lastIndexOf('/') + 1);

                    // download the file
                    input = connection.getInputStream();
                    String savePath = Helper.getBookDirectory()+"/"+issue.path+"/"+lastPath;
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

                    renderPDF(savePath);

                    if (i ==  sUrl.length-1) {
                        ConflyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                //onComplete();
                            }
                        });
                        //onComplete();

                    }
                    //new ThumbTask().execute(savePath);

                    //Helper.createThumbnail(savePath,issue.path);
                }

            } catch (Exception e) {
                e.printStackTrace();
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

        void renderPDF(String path){
            render(path);
        }

        public void render(String path){
            InputStream epubInputStream = null;
            try {
                epubInputStream = new FileInputStream(new File(path));
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                String folderPath = Helper.getBookDirectory() + "/" + issue.path + "/" + issue.path;
                if (!Helper.fileExits(folderPath)){
                    Helper.createDirectory(folderPath);
                }
                String savePath = folderPath+"/" + lastPath;
                if (!Helper.fileExits(savePath)) {
                    byte[] cis = FileEncrypt.decrypt_data(epubInputStream);

                    FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                    bos.write(cis, 0, cis.length);
                    bos.flush();
                    bos.close();
                }
                //InputStream is = new ByteArrayInputStream(cis);
                renderThumb(savePath);
            } catch (Exception e) {
                e.printStackTrace();
                //imageView.setImageResource(R.drawable.no_image_detail);
            }
        }

        public void renderThumb(String filePath){
            try {


                PdfContext pdf_conext = new PdfContext();
                CodecDocument d = pdf_conext.openDocument(filePath);

                CodecPage vuPage = d.getPage(0); // choose your page number
                RectF rf = new RectF();
                rf.bottom = rf.right = (float)1.0;
                double aspectRatio = (double) vuPage.getWidth() / (double) vuPage.getHeight();
                int w = (int) (150 * aspectRatio);
                Bitmap bitmap = vuPage.renderBitmap(w, 150, rf);
                String lastPath = filePath.substring(filePath.lastIndexOf('/') + 1);
                String savePath = Helper.getThumbnailDirectory(issue.path) + "/" + lastPath;

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

                Thread.sleep(5);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
