package com.bookdose.confly;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookdose.confly.adapter.BookmarkAdapter;
import com.bookdose.confly.adapter.ImageReaderAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.helper.FileEncrypt;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.JsonHelper;
import com.bookdose.confly.object.Bookmark;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;
import com.joanzapata.pdfview.PDFView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;

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

    HListView listView;

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
        mPager.setOffscreenPageLimit(2);
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

//        listView = (HListView) findViewById( R.id.hListView1 );
//        listView.setHeaderDividersEnabled(true);
//        listView.setFooterDividersEnabled(true);


        initToolbarHeader();
        generateThumbnail();
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
        //thumbScroll = (HorizontalScrollView)findViewById(R.id.thumbScroll);
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
            //PDFView pdfView = new PDFView(getApplicationContext(),null);
        }

//        thumbList = JsonHelper.getThumbnails(issue);
//        //ThumbAdapter thumbAdapter = new ThumbAdapter(this,thumbList, issue.path);
//        ThumbAdapter thumbAdapter = new ThumbAdapter( this, R.layout.thumbnail_view, R.id.page, thumbList,R.id.thumbPdf,issue.path, R.id.image );
//        listView.setAdapter(thumbAdapter);
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

    class ThumbAdapter extends ArrayAdapter<String> {

        List<String> mItems;
        LayoutInflater mInflater;
        String issueName;
        int mResource;
        int mTextResId;
        int mPDFId;
        int mImageId;

//        public ThumbAdapter( Context context, List<String> objects, String issueName ) {
//            mInflater = LayoutInflater.from( context );
//            mItems = objects;
//            this.issueName = issueName;
//        }

        public ThumbAdapter( Context context, int resourceId, int textViewResourceId, List<String> objects ,int pdfId, String issueName, int imageId) {
            super( context, resourceId, textViewResourceId, objects );
            mInflater = LayoutInflater.from( context );
            mResource = resourceId;
            mTextResId = textViewResourceId;
            mItems = objects;
            this.issueName = issueName;
            mPDFId = pdfId;
            mImageId = imageId;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public long getItemId( int position ) {
            return getItem( position ).hashCode();
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType( int position ) {
            return position%3;
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {

//            PDFView pdfView=null;
//            TextView textView = null;
//
//            if( null == convertView ) {
//                convertView = mInflater.inflate( R.layout.thumbnail_view, parent, false );
//                pdfView = (PDFView)convertView.findViewById(R.id.pdfView);
//                textView = (TextView) convertView.findViewById( R.id.page );
//            }
//
//            textView.setText( position);
//            renderPDF(mItems.get(position), pdfView);

//            ViewHolder viewHolder = null;
//            if (convertView == null)
//            {
//                int layoutId = R.layout.thumbnail_view;
//                convertView = mInflater.inflate(
//                        layoutId, null);
//                viewHolder = new ViewHolder();
//                viewHolder.page = (TextView) convertView
//                        .findViewById(R.id.page);
//                //viewHolder.pdfView = (PDFView)convertView.findViewById(R.id.pdfView);
//            }
//            else
//            {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            if (viewHolder != null)
//            {
//                viewHolder.page.setText(position);
//                //renderPDF(mItems.get(position), viewHolder.pdfView);
//            }
//            ViewGroup.LayoutParams params = convertView.getLayoutParams();
//            params.width = getResources().getDimensionPixelSize( R.dimen.thumb_item_size );
//
//
//
//            int type = getItemViewType( position );
//
//            ViewGroup.LayoutParams params = convertView.getLayoutParams();
//            params.width = getResources().getDimensionPixelSize( R.dimen.thumb_item_size );

            if( null == convertView ) {
                convertView = mInflater.inflate( mResource, parent, false );
            }

            TextView textView = (TextView) convertView.findViewById( mTextResId );
            PDFView pdfView = (PDFView) convertView.findViewById( mPDFId );
            //ImageView imageView = (ImageView)convertView.findViewById( mImageId );
            textView.setText("" + position);
            renderPDF(mItems.get(position), pdfView);

            if (pdfView.thumbPagePart != null) {
                Bitmap bm = pdfView.thumbPagePart.getRenderedBitmap();
                //imageView.setImageBitmap(bm);
            }

            int type = getItemViewType( position );

            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            if( type == 0 ) {
                params.width = getResources().getDimensionPixelSize(R.dimen.thumb_item_size);
            } else if( type == 1 ) {
                params.width = getResources().getDimensionPixelSize( R.dimen.thumb_item_size );
            } else {
                params.width = getResources().getDimensionPixelSize( R.dimen.thumb_item_size );
            }

            return convertView;
        }
        void renderPDF(String path, PDFView pdfView){
            InputStream epubInputStream = null;
            try {
                epubInputStream = new FileInputStream(new File(path));
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                String savePath = Helper.getBookDirectory() + "/" + issueName + "/" + issueName + "/" + lastPath;
                if (!Helper.fileExits(savePath)) {
                    byte[] cis = FileEncrypt.decrypt_data(epubInputStream);

                    FileOutputStream outputStream = new FileOutputStream(new File(savePath));
                    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                    bos.write(cis, 0, cis.length);
                    bos.flush();
                    bos.close();
                }
                //InputStream is = new ByteArrayInputStream(cis);
                pdfView.fromFile(new File(savePath))
                        .defaultPage(1)
                        .load();

            } catch (Exception e) {
                e.printStackTrace();
                //imageView.setImageResource(R.drawable.no_image_detail);
            }
        }

        public class ViewHolder{
            TextView page;
            PDFView pdfView;
        }
    }
}
