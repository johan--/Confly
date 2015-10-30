package com.bookdose.confly.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookdose.confly.NewsFragment;
import com.bookdose.confly.R;
import com.bookdose.confly.object.News;

import java.util.ArrayList;

/**
 * Created by Teebio on 10/28/15 AD.
 */
public class NewsAdapter extends BaseAdapter{

    public interface NewsListener{
        void onSelectNews(News news);
    }

    private NewsListener newsListener;

    public void setNewsListenerr(NewsListener newsListener){
        this.newsListener = newsListener;
    }

    Context context;
    ArrayList<News> newses;
    String state;

    public NewsAdapter(Context context, ArrayList<News>newses,String state){
        this.context = context;
        this.newses = newses;
        this.state = state;
    }
    @Override
    public int getCount() {
        return newses.size();
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
            int layoutId = R.layout.news_list;
            convertView = ((Activity) context).getLayoutInflater().inflate(
                    layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.newsText = (TextView) convertView
                    .findViewById(R.id.news_text);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null)
        {
            News news = newses.get(position);
            if (state.equals(NewsFragment.NEWS_TITLE))
                viewHolder.newsText.setText(news.newsName);
            else
                viewHolder.newsText.setText(news.title);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsListener != null)
                    newsListener.onSelectNews(newses.get(position));
                Log.e("", "Position item click ===> " + v.getTag());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView newsText;
    }
}
