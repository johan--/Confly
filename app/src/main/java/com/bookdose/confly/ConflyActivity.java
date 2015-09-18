package com.bookdose.confly;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bookdose.confly.adapter.CategorylistAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Category;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ConflyActivity extends FragmentActivity implements DownloadFragment.DownloadFragmentListener, DownloadPagerFragment.DownloadPagerListener, PopoverView.PopoverViewDelegate, CategorylistAdapter.CategoryListListener {

    DownloadPagerFragment downloadFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confly);

        if(Helper.isTablet(this))
            pushLibraryFragment();
        else
            pushLibraryFragment();
        final ImageButton downloadMenu = (ImageButton)findViewById(R.id.download_menu);
        final ImageButton mylibraryMenu = (ImageButton)findViewById(R.id.myshelf_menu);
        ImageButton magBt = (ImageButton)findViewById(R.id.activity_btn_mag);
        magBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("3", v);
            }
        });

        ImageButton bookBt = (ImageButton)findViewById(R.id.activity_btn_book);
        bookBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("2", v);
            }
        });

        ImageButton docBt = (ImageButton)findViewById(R.id.activity_btn_doc);
        docBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopoverCategory("4", v);
            }
        });

        downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_inactive));

        downloadMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushLibraryFragment();
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_inactive));
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_active));
            }
        });
        mylibraryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushMyshelfFragment();
                mylibraryMenu.setImageDrawable(getResources().getDrawable(R.drawable.myshelf_inactive));
                downloadMenu.setImageDrawable(getResources().getDrawable(R.drawable.download_active));
            }
        });

        connectDatatBase();

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

    void pushDownloadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DownloadFragment downloadFragment = DownloadFragment.newInstance("", "");
        downloadFragment.setDownloadFragmentListener(this);
        fragmentManager.beginTransaction()
                .replace(R.id.contentPanel,downloadFragment)
                .commit();
    }

    void pushMyshelfFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShelfFragment shelfFragment = new ShelfFragment();
        //downloadFragment.setDownloadFragmentListener(this);
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
        Intent intent = new Intent(this,BookDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(Constant.ISSUE, issue);
        intent.putExtras(b);
        startActivity(intent);
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
        downloadFragment.loadProductList(category.category_aid,category.product_main_aid);
    }
}
