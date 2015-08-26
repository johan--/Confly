package com.bookdose.confly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.object.Issue;

import java.util.ArrayList;

/**
 * Created by Teebio on 8/25/15 AD.
 */
public class DownloadAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Issue> issues;

    public DownloadAdapter(Context context,ArrayList<Issue> issues){
        this.context = context;
        this.issues = issues;
    }

    @Override
    public int getCount() {
        return issues.size();
    }

    @Override
    public Object getItem(int position) {
        return issues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceHolder placeHolder = null;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_issue_item, parent, false);
            placeHolder = new PlaceHolder();
            placeHolder.bookName = (TextView) convertView.findViewById(R.id.booknameText);
            placeHolder.bookDetail = (TextView) convertView.findViewById(R.id.detailText);
            placeHolder.coverImage = (ImageView) convertView.findViewById(R.id.coverImage);

            Issue issue = issues.get(position);
            placeHolder.bookName.setText(issue.issueName);
            convertView.setTag(placeHolder);
        }else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public class PlaceHolder{
        ImageView coverImage;
        TextView bookName;
        TextView bookDetail;
    }
}
