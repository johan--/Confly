package com.bookdose.confly.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookdose.confly.R;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.object.Issue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Teebio on 9/4/15 AD.
 */
public class GridIssueAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private int mImageOffset = 0; // the index offset into the list of images
    private int mImageCount = -1; // -1 means that we display all images
    private int mNumTopics = 0;
    private ArrayList<Issue> mArrayList;
    public boolean isEdit;

    public interface GridIssueListener{
        public void didSelectGridIssue(Issue issue);
    }

    private GridIssueListener gridIssueListener;

    public void setGridIssueListener(GridIssueListener listener){
        this.gridIssueListener = listener;
    }

    private ImageLoader imageLoader;

    private int rowHeight = 0;

    public GridIssueAdapter(Activity activity, ArrayList<Issue> list, int imageOffset, int imageCount, int rowHeight)
    {
        DisplayImageOptions displayimageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showStubImage(R.drawable.no_image_detail)
                .showImageForEmptyUri(R.drawable.no_image_detail)
                .showImageOnFail(R.drawable.no_image_detail).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                activity).defaultDisplayImageOptions(displayimageOptions)
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        mArrayList = list;
        mContext = activity;
        mImageOffset = imageOffset;
        mImageCount = imageCount;
        mNumTopics = (list == null) ? 0 : list.size();
        this.rowHeight = rowHeight;
    }

    public int getCount()
    {
        int count = mImageCount;
        if (mImageOffset + mImageCount >= mNumTopics)
            count = mNumTopics - mImageOffset;
        return count;
    }

    public Issue getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return mImageOffset + position;
    }

    class ViewHolder
    {
        ImageView imgCover;
        TextView bookName;
        ImageButton editBtn;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        int numTopics = mArrayList.size();

        if (convertView == null)
        {
            int layoutId = R.layout.griditem;
            convertView = ((Activity) mContext).getLayoutInflater().inflate(
                    layoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.imgCover = (ImageView) convertView
                    .findViewById(R.id.coverImage);
            viewHolder.bookName = (TextView) convertView
                    .findViewById(R.id.bookName);
            viewHolder.editBtn = (ImageButton)convertView.findViewById(R.id.editBtn);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.editBtn.setVisibility(View.INVISIBLE);
        if (isEdit)
            viewHolder.editBtn.setVisibility(View.VISIBLE);

        if (viewHolder != null)
        {
            //Calculation for data position in array.
            int j = position + mImageOffset;
            if (j < 0)
                j = 0;
            if (j >= numTopics)
                j = numTopics - 1;

            Issue issue = mArrayList.get(j);

            if (Helper.fileExits(issue.getLocalCover())){
                File imgFile = new File(issue.getLocalCover());
                viewHolder.imgCover.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }else {
                imageLoader.displayImage(issue.getLargeCoverUrl(), viewHolder.imgCover);

            }

            viewHolder.bookName.setText(issue.content_name);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, rowHeight / mContext.getResources().getInteger(R.integer. num_rows));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            int numCol = mContext.getResources().getInteger(R.integer.num_cols);
            //int curCol = position > numCol ? (position / mContext.getResources().getInteger(R.integer.num_rows) != 0 ? position / mContext.getResources().getInteger(R.integer.num_rows) + position % mContext.getResources().getInteger(R.integer.num_rows) : position / mContext.getResources().getInteger(R.integer.num_rows)) % getCount() : position % getCount();
            int numRows = (getCount() - 1) / mContext.getResources().getInteger(R.integer.num_cols);
            int dividerShow = mContext.getResources().getInteger(R.integer.num_cols);

            if(numRows > 1)
                dividerShow = numRows * mContext.getResources().getInteger(R.integer.num_cols);
            else if(numRows == 0 && getCount() <= mContext.getResources().getInteger(R.integer.num_cols))
                dividerShow = 0;
            else if(numRows <= 1 && getCount() > mContext.getResources().getInteger(R.integer.num_cols))
                dividerShow = 1 * mContext.getResources().getInteger(R.integer.num_cols);

            viewHolder.imgCover.setOnClickListener(this);
            viewHolder.imgCover.setTag(j);
        }
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public void onClick(View v)
    {
        if(v != null)
        {
            if(v.getTag() != null)
            {
                if (gridIssueListener != null)
                    gridIssueListener.didSelectGridIssue(mArrayList.get(Integer.parseInt(v.getTag().toString())));
                Log.e("", "Position item click ===> " + v.getTag());
            }
        }
    }
}
