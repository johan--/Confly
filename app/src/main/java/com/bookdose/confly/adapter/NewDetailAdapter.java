package com.bookdose.confly.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.object.News;

import java.util.ArrayList;

/**
 * Created by Teebio on 10/28/15 AD.
 */
public class NewDetailAdapter extends BaseAdapter {
    public interface NewsDetailListener{
        void onSelectNewsDetail(News news);
    }

    private NewsDetailListener newsDetailListener;

    public void setNewsDetailListener(NewsDetailListener newsListener){
        this.newsDetailListener = newsListener;
    }

    Context context;
    ArrayList<News> newses;
    String state;

    public NewDetailAdapter(Context context, ArrayList<News>newses){
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
            int layoutId = R.layout.news_detail;
            convertView = ((Activity) context).getLayoutInflater().inflate(
                    layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.newsText = (TextView) convertView
                    .findViewById(R.id.titleText);
            viewHolder.dayText = (TextView) convertView
                    .findViewById(R.id.dateText);
            viewHolder.dateText = (TextView) convertView
                    .findViewById(R.id.dayText);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null)
        {
            News news = newses.get(position);
            viewHolder.newsText.setText(news.title);
            //viewHolder.dayText.setText(news.);
            viewHolder.dateText.setText(news.pubDate);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsDetailListener != null)
                    newsDetailListener.onSelectNewsDetail(newses.get(position));
                Log.e("", "Position news click ===> " + v.getTag());
            }
        });

        return convertView;
    }

    public class ViewHolder{
        TextView newsText;
        TextView dayText;
        TextView dateText;
    }
}
