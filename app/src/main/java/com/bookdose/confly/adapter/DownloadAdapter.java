package com.bookdose.confly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by Teebio on 8/25/15 AD.
 */
//public class DownloadAdapter extends ArrayAdapter<Issue> {
//
//    private Context context;
//    private ArrayList<Issue> issues;
//
//    public DownloadAdapter(Context context,ArrayList<Issue> issues){
//        this.context = context;
//        this.issues = issues;
//    }
//
//    @Override
//    public int getCount() {
//        return 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return 0;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        PlaceHolder placeHolder = null;
//        if (convertView == null){
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.list_issue_item, parent, false);
//            placeHolder = new PlaceHolder();
//            TextView bookName = (TextView) convertView.findViewById(R.id.booknameText);
//            TextView bookDetail = (TextView) convertView.findViewById(R.id.detailText);
//            ImageView coverImage = (ImageView) convertView.findViewById(R.id.coverImage);
//
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return 0;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 0;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//

//}

public class DownloadAdapter extends ArrayAdapter<Issue> {

    public DownloadAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public DownloadAdapter(Context context, int resource, List<Issue> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        PlaceHolder placeHolder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_issue_item, null);
            placeHolder = new PlaceHolder();

            placeHolder.bookName = (TextView) v.findViewById(R.id.booknameText);
            placeHolder.bookDetail = (TextView) v.findViewById(R.id.detailText);
            placeHolder.coverImage = (ImageView) v.findViewById(R.id.coverImage);

            v.setTag(placeHolder);

        }else {
            placeHolder = (PlaceHolder)v.getTag();
        }

        Issue issue = getItem(position);

        if (issue != null) {
            String bookname = issue.content_name;
            if(issue.category_aid.equals(Constant.MAGAZINE_ID) || issue.category_aid.equals("1")){
                if (!issue.issue_else.equals(""))
                    bookname = issue.content_name+"\n"+issue.issue_else;
                else if (!issue.vol.equals("0") && !issue.issue.equals("0"))
                    bookname = issue.content_name+"\nvol : "+issue.vol+" issue : "+issue.issue;
                else if (!issue.vol.equals("0") && issue.issue.equals("0"))
                    bookname = issue.content_name+"\nvol : "+issue.vol;
                else if (issue.vol.equals("0") && !issue.issue.equals("0"))
                    bookname = issue.content_name+"\nissue : "+issue.issue;
                else if (issue.vol.equals("0") && issue.issue.equals("0"))
                    bookname = issue.content_name;
            }

            placeHolder.bookName.setText(bookname);
            //placeHolder.bookDetail.setText(issue.description);
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
            imageLoader.displayImage(issue.getLargeCoverUrl(), placeHolder.coverImage);
        }

        return v;
    }

    public class PlaceHolder{
        ImageView coverImage;
        TextView bookName;
        TextView bookDetail;
    }

}
