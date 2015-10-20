package com.bookdose.confly;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bookdose.confly.adapter.BookmarkAdapter;
import com.bookdose.confly.adapter.ImageReaderAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.JsonHelper;
import com.bookdose.confly.object.Bookmark;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;
import com.joanzapata.pdfview.PDFView;

import java.util.ArrayList;

public class ImageReaderActivity extends FragmentActivity implements ImageReaderAdapter.ImageReaderAdapterListener, ViewPager.OnPageChangeListener, BookmarkAdapter.BookmarkListener {

    ImageReaderAdapter adapter;
    ViewPager mPager;
    Issue issue;
    ImageButton backImage;
    LinearLayout bookmarkBar;
    HorizontalScrollView thumbScroll;
    LinearLayout topToolbar;
    ListView markList;
    ImageButton bookmarkBtn;
    Button markBtn;

    boolean isShowToolbar;
    boolean isShowBookmark;
    ArrayList<String> thumbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_reader);

        Bundle b = this.getIntent().getExtras();
        if(b!=null)
            issue = b.getParcelable(Constant.ISSUE);

        Helper.createDirectory(Helper.getBookDirectory() + "/" + issue.path + "/" + issue.path);

        adapter = new ImageReaderAdapter(this.getSupportFragmentManager(), JsonHelper.getLinkDownload(issue),getResources(), issue.path);
        adapter.setPagerAdapterListener(this);

        mPager = (ViewPager)findViewById(R.id.viewPagger);
        mPager.setOffscreenPageLimit(3);
        mPager.addOnPageChangeListener(this);
        mPager.setOnPageChangeListener(this);
        mPager.setAdapter(adapter);

        backImage = (ImageButton)findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageReaderActivity.this.finish();
            }
        });


        initToolbarHeader();
        //generateThumbnail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_reader, menu);
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
    public void didSelectedBook(Issue issue) {

    }

    @Override
    public void didDoubleTap() {
        if (!isShowToolbar){
            showToolBar();
            isShowToolbar = true;
        }else {
            hiddenToolbar();
            if (isShowBookmark) {
                hideBookmarkBar();
                isShowBookmark = false;
            }
            isShowToolbar = false;
        }
    }

    void hiddenToolbar(){
        Animation anim = new TranslateAnimation(0, 0, 0,
                -topToolbar.getHeight());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                topToolbar.setVisibility(View.INVISIBLE);
            }
        });
        topToolbar.startAnimation(anim);
    }

    void showToolBar(){
//        toolbarHeader.setVisibility(View.VISIBLE);
//        thumbScroll.setVisibility(View.VISIBLE);

        Animation anim = new TranslateAnimation(0, 0,
                -topToolbar.getHeight(), 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                topToolbar.setVisibility(View.VISIBLE);
                generateThumbnail();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

            }
        });
        topToolbar.startAnimation(anim);
    }

    void showBookmarkBar(){
        Animation anim = new TranslateAnimation(bookmarkBar.getWidth(), 0,
                0, 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                bookmarkBar.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

            }
        });
        bookmarkBar.startAnimation(anim);
    }

    void hideBookmarkBar(){
        Animation anim = new TranslateAnimation(0, bookmarkBar.getWidth(), 0,
                0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                bookmarkBar.setVisibility(View.INVISIBLE);
            }
        });
        bookmarkBar.startAnimation(anim);
    }

    void initToolbarHeader(){

        isShowToolbar = true;
        //toolbarHeader = (LinearLayout) findViewById(R.id.toolbar_header);
        thumbScroll = (HorizontalScrollView)findViewById(R.id.thumbScroll);
        topToolbar = (LinearLayout)findViewById(R.id.top_toolbar);
        bookmarkBar = (LinearLayout)findViewById(R.id.bookmarkBar);
        markList = (ListView)findViewById(R.id.markList);
        bookmarkBtn = (ImageButton) findViewById(R.id.bookmarkBtn);
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowBookmark){
                    hideBookmarkBar();
                    isShowBookmark = false;
                }else {
                    loadBookmark();
                    showBookmarkBar();
                    isShowBookmark = true;
                }
            }
        });

        markBtn = (Button)findViewById(R.id.markBtn);
        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                Bookmark bookmark = databaseHandler.getBookmark(issue.content_aid, mPager.getCurrentItem());
                int page = mPager.getCurrentItem();
                if (bookmark.contentId == null || bookmark.contentId.equals("")) {
                    databaseHandler.insertBookmark(issue.content_aid, page, issue.path, thumbList.get(page));
                    Log.d("add mark", "page = " + mPager.getCurrentItem());
                    markBtn.setText("Remove Bookmark");
                } else {
                    Log.d("remove mark", "page = " + mPager.getCurrentItem());
                    databaseHandler.deleteBookmark(bookmark);
                    markBtn.setText("Add Bookmark");
                }
                loadBookmark();
            }
        });

        hideBookmarkBar();
    }

    void loadBookmark(){
        ArrayList<Bookmark> bookmarks = new DatabaseHandler(getApplicationContext()).getBookmark(issue.content_aid);
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(getApplicationContext(),bookmarks);
        bookmarkAdapter.setBookmarkListener(this);
        markList.setAdapter(bookmarkAdapter);
    }

    void generateThumbnail(){

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear);
        thumbList = JsonHelper.getThumbnails(issue);
        for (int i = 0; i < thumbList.size(); i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(10, 5, 10, 5);
            Bitmap bm = null;
            bm = Helper.bitmapWithPath(thumbList.get(i));
            imageView.setImageBitmap(bm);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPager.setCurrentItem(imageView.getId());
                }
            });
            layout.addView(imageView);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PDFView pdfView = new PDFView(getApplicationContext(),null);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("page change","position "+position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("page selected","position "+position);
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
        Bookmark bookmark = databaseHandler.getBookmark(issue.content_aid, position);
        if (bookmark.contentId == null || bookmark.contentId.equals("")) {
            markBtn.setText("Add Bookmark");
        }else {
            markBtn.setText("Remove Bookmark");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSelectBookmark(Bookmark bookmark) {
        mPager.setCurrentItem(bookmark.page);
    }
}
