package com.bookdose.confly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
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

import com.bookdose.confly.EpubReader.EPubReaderActivity;
import com.bookdose.confly.adapter.CategorylistAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.FileEncrypt;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Category;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ConflyActivity extends FragmentActivity implements DownloadFragment.DownloadFragmentListener, DownloadPagerFragment.DownloadPagerListener, PopoverView.PopoverViewDelegate, CategorylistAdapter.CategoryListListener, ShelfFragment.ShelfListenner {

    DownloadPagerFragment downloadFragment;
    ShelfFragment shelfFragment;
    NewsFragment newsFragment;
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

        downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_active));

        downloadMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentMenu();
                if(Helper.isTablet(ConflyActivity.this))
                    pushLibraryFragment();
                else
                    pushDownloadFragment();
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_active));
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_inactive_mobile));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_inactive_mobile));
            }
        });
        mylibraryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShelfMenu();
                pushMyshelfFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_active_mobile));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_inactive));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_inactive_mobile));
            }
        });
        newsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewsMenu();
                pushNewsFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.shelft_inactive_mobile));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.content_inactive));
                newsMenu.setImageDrawable(getResources().getDrawable(R.drawable.news_active_mobile));
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

                if (category != null)
                    downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
                else
                    downloadFragment.loadProductList(Constant.ALL_ID,Constant.MAGAZINE_ID);
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
        JSONArray response = ServiceRequest.requestCategoryListAPI(catID);
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
        DownloadFragment downloadFragment = DownloadFragment.newInstance("", "");
        downloadFragment.setDownloadFragmentListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel, downloadFragment)
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
        downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
        this.category = category;
    }

    @Override
    public void openIssue(Issue issue) {
        if (isEdit)
            editIssue(issue);
        else
            openBook(issue);
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
        if (Helper.isEPub(issue)){
            try {

                InputStream epubInputStream = new FileInputStream(Helper.getFileEPubPath(issue));

                String savePath = Helper.getBookDirectory()+"/"+issue.path+"/"+issue.path+".epub";

                byte[] cis = FileEncrypt.decrypt_data(epubInputStream);

                FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                bos.write(cis,0,cis.length);
                bos.flush();
                bos.close();


                Intent i = new Intent(this, EPubReaderActivity.class);
                i.putExtra("path", savePath);
                i.putExtra("name", issue.content_name);

                startActivity(i);

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
}
