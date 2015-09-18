package com.demo.gridviewdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GridImageAdapter extends BaseAdapter implements OnClickListener
{
	private Context mContext;
	private int mImageOffset = 0; // the index offset into the list of images
	private int mImageCount = -1; // -1 means that we display all images
	private int mNumTopics = 0;
	private ArrayList<GridItem> mArrayList;

	private ImageLoader imageLoader;
	
	private int rowHeight = 0;

	public GridImageAdapter(Activity activity, ArrayList<GridItem> list, int imageOffset, int imageCount, int rowHeight)
	{
		DisplayImageOptions displayimageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true)
				.showStubImage(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).build();

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

	public GridItem getItem(int position)
	{
		return null;
	}

	public long getItemId(int position)
	{
		return mImageOffset + position;
	}

	class ViewHolder
	{
		ImageView imgGridItem;
		ImageView imgHorDivider;
		ImageView imgVerDivider;
		TextView txtGridItem;
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
			viewHolder.imgGridItem = (ImageView) convertView
					.findViewById(R.id.imgGridItem);
			viewHolder.imgHorDivider = (ImageView) convertView
					.findViewById(R.id.imgHorDivider);
			viewHolder.imgVerDivider = (ImageView) convertView
					.findViewById(R.id.imgVerDivider);
			viewHolder.txtGridItem = (TextView) convertView
					.findViewById(R.id.txtGridItem);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (viewHolder != null)
		{
			//Calculation for data position in array.
			int j = position + mImageOffset;
			if (j < 0)
				j = 0;
			if (j >= numTopics)
				j = numTopics - 1;
			
			imageLoader.displayImage(mArrayList.get(j).getImagePath(), viewHolder.imgGridItem);
			viewHolder.txtGridItem.setText(mArrayList.get(j).getTitle());
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, rowHeight / mContext.getResources().getInteger(R.integer. num_rows));
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			viewHolder.imgVerDivider.setLayoutParams(params);		
			
			int numCol = mContext.getResources().getInteger(R.integer.num_cols);
			int curCol = position > numCol ? (position / mContext.getResources().getInteger(R.integer.num_rows) != 0 ? position / mContext.getResources().getInteger(R.integer.num_rows) + position % mContext.getResources().getInteger(R.integer.num_rows) : position / mContext.getResources().getInteger(R.integer.num_rows)) % getCount() : position % getCount();

			if (position % numCol == 0)
			{
				viewHolder.imgHorDivider.setImageResource(R.drawable.img_hor_divider1);
				viewHolder.imgVerDivider.setVisibility(View.VISIBLE);
			}
			else if (position % numCol == 1)
			{
				viewHolder.imgHorDivider.setImageResource(R.drawable.img_hor_divider2);
				viewHolder.imgVerDivider.setVisibility(View.VISIBLE);
			}
			else if (position % numCol == 2 || (curCol > 1 && curCol < numCol - 2))
			{
				viewHolder.imgHorDivider.setImageResource(R.drawable.img_hor_divider3);
				viewHolder.imgVerDivider.setVisibility(View.VISIBLE);
			}
			else if (position % numCol == numCol - 2)
			{
				viewHolder.imgHorDivider.setImageResource(R.drawable.img_hor_divider4);
				viewHolder.imgVerDivider.setVisibility(View.VISIBLE);
			}
			else if (position % numCol == numCol - 1)
			{
				viewHolder.imgHorDivider.setImageResource(R.drawable.img_hor_divider5);
				viewHolder.imgVerDivider.setVisibility(View.INVISIBLE);
			}
			
			int numRows = (getCount() - 1) / mContext.getResources().getInteger(R.integer.num_cols);
			int dividerShow = mContext.getResources().getInteger(R.integer.num_cols);
			
			if(numRows > 1)
				dividerShow = numRows * mContext.getResources().getInteger(R.integer.num_cols);
			else if(numRows == 0 && getCount() <= mContext.getResources().getInteger(R.integer.num_cols))
				dividerShow = 0;
			else if(numRows <= 1 && getCount() > mContext.getResources().getInteger(R.integer.num_cols))
				dividerShow = 1 * mContext.getResources().getInteger(R.integer.num_cols);;
			
			if((dividerShow <= position))
				viewHolder.imgHorDivider.setVisibility(View.GONE);
			else
				viewHolder.imgHorDivider.setVisibility(View.VISIBLE);
			
			viewHolder.imgGridItem.setOnClickListener(this);
			viewHolder.imgGridItem.setTag(j);
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
				Log.e("", "Position item click ===> " + v.getTag());
			}
		}
	}
}

