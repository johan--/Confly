package com.bookdose.confly.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.object.Bookmark;

import java.util.ArrayList;

/**
 * Created by Teebio on 10/13/15 AD.
 */
public class BookmarkAdapter extends BaseAdapter {
    public interface BookmarkListener{
        void onSelectBookmark(Bookmark bookmark);
    }

    private BookmarkListener bookmarkListener;

    public void setBookmarkListener(BookmarkListener bookmarkListener){
        this.bookmarkListener = bookmarkListener;
    }

    Context context;
    ArrayList<Bookmark> bookmarks;
    public BookmarkAdapter(Context context, ArrayList<Bookmark>bookmarks){
        this.context = context;
        this.bookmarks = bookmarks;
    }
    @Override
    public int getCount() {
        return bookmarks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            int layoutId = R.layout.bookmark_layout;

            convertView =  LayoutInflater.from(context).inflate(
                    layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.page = (TextView) convertView
                    .findViewById(R.id.markPage);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.markImg);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null)
        {
            Bookmark bookmark = bookmarks.get(position);
            viewHolder.page.setText(bookmark.getPageText());
            viewHolder.imageView.setImageBitmap(Helper.bitmapWithPath(bookmark.imageName));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarkListener != null)
                    bookmarkListener.onSelectBookmark(bookmarks.get(position));
                Log.e("", "Position item click ===> " + v.getTag());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView page;
        ImageView imageView;
    }
}
