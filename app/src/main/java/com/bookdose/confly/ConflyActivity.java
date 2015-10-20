package com.bookdose.confly;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
    ImageButton editBtn;
    ImageButton bookBt;
    ImageButton docBt;
    ImageButton menuBt;
    ImageButton downloadMenu;
    ImageButton mylibraryMenu;
    ImageButton magBt;

    String result = "WAIT";

    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confly);

        if(Helper.isTablet(this))
            pushLibraryFragment();
        else
            pushLibraryFragment();

        downloadMenu = (ImageButton)findViewById(R.id.download_menu);
        mylibraryMenu = (ImageButton)findViewById(R.id.myshelf_menu);
        magBt = (ImageButton)findViewById(R.id.activity_btn_mag);
        magBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("3", v);
            }
        });

        bookBt = (ImageButton)findViewById(R.id.activity_btn_book);
        bookBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("2", v);
            }
        });

        docBt = (ImageButton)findViewById(R.id.activity_btn_doc);
        docBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("4", v);
            }
        });

        menuBt = (ImageButton)findViewById(R.id.activity_btn_menu);
        menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuiew();
            }
        });

        editBtn = (ImageButton)findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEdit = !isEdit;
                if (isEdit){
                    editBtn.setImageResource(R.drawable.bin_active);
                }else {
                    editBtn.setImageResource(R.drawable.bin_inactive);
                }

                shelfFragment.isEdit = isEdit;
                shelfFragment.reloadData();

            }
        });

        downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_inactive));

        downloadMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentMenu();
                pushLibraryFragment();
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_inactive));
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_active));
            }
        });
        mylibraryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShelfMenu();
                pushMyshelfFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_inactive));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_active));
            }
        });

        connectDatatBase();
        showContentMenu();

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

                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_inactive));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_active));
                showShelfMenu();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    void showPopoverCategory(String catID, View view){
        //get root layout
        RelativeLayout rootView = (RelativeLayout)findViewById(R.id.mainview);

        PopoverView popoverView = new PopoverView(ConflyActivity.this, R.layout.popover_list_view);
        popoverView.setContentSizeForViewInPopover(new Point(400, 480));
        popoverView.setDelegate(ConflyActivity.this);
        popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(view), PopoverView.PopoverArrowDirectionAny, true);
        ListView listView = (ListView)popoverView.findViewById(R.id.poplist);

        ArrayList<Category> categories = new ArrayList<Category>();
        JSONArray response = ServiceRequest.requestCategoryListAPI(catID);
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
        docBt.setVisibility(View.VISIBLE);
        bookBt.setVisibility(View.VISIBLE);
        magBt.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
    }

    void showShelfMenu(){
        docBt.setVisibility(View.INVISIBLE);
        bookBt.setVisibility(View.INVISIBLE);
        magBt.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.VISIBLE);
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
        downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
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
        shelfFragment.reloadData();
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
}
