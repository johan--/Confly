package com.bookdose.confly;

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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class ConflyActivity extends FragmentActivity implements DownloadFragment.DownloadFragmentListener, DownloadPagerFragment.DownloadPagerListener, PopoverView.PopoverViewDelegate, CategorylistAdapter.CategoryListListener, ShelfFragment.ShelfListenner {

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

        ImageButton menuBt = (ImageButton)findViewById(R.id.activity_btn_menu);
        menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuiew();
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
        ShelfFragment shelfFragment = new ShelfFragment();
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
        downloadFragment.loadProductList(category.category_aid, category.product_main_aid);
    }

    @Override
    public void openIssue(Issue issue) {
        openBook(issue);
    }

    void openBook(Issue issue){
        if (Helper.isEPub(issue)){
            try {
                final String EBC_NOPAD_CIPHER_TRANSFORMATION = "AES/ECB/NoPadding";
                final String EBC_PKCS7_CIPHER_TRANSFORMATION = "AES/ECB/PKCS7Padding";
                InputStream epubInputStream = new FileInputStream(Helper.getFileEPubPath(issue));

                String savePath = Helper.getBookDirectory()+"/"+issue.path+"/"+issue.path+".epub";
                OutputStream ops = new FileOutputStream(savePath);

                CipherInputStream cis = null;
                // find InputStream for book
                SecretKey key = new SecretKeySpec(Constant.AES_KEY.getBytes("UTF-8"), "AES");

                try {
                    Cipher cipher = null;
//                    try {
//                        //cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
//
//                    } catch (NoSuchProviderException e) {
//                        e.printStackTrace();
//                    }
                    //cipher = Cipher.getInstance("AES/ECB/NoPadding");
                    try {
                        cipher = Cipher.getInstance(EBC_NOPAD_CIPHER_TRANSFORMATION, "BC");
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    }
                    try {
                        cipher.init(Cipher.DECRYPT_MODE, key);
                        cis = new CipherInputStream(epubInputStream, cipher);
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }

                // Load Book from inputStream

                Book book = (new EpubReader()).readEpub(cis);


                // Log the book's authors

                Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());


                // Log the book's title

                //Log.i("epublib", "title: " + book.getTitle());

                // Log the book's coverimage property

                //Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());

                //Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");


                // Log the tale of contents

                //logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

            } catch (IOException e) {

                Log.e("epublib", e.getMessage());

            }
        }else {

        }

    }
}
