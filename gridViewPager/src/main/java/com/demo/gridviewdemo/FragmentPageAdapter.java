package com.demo.gridviewdemo;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPageAdapter extends FragmentStatePagerAdapter
{
	private int mNumItems = 0;
	private int mNumFragments = 0;
	private ArrayList<GridItem> mTopicList;

	private Resources resources;

	static final int DEFAULT_NUM_FRAGMENTS = 1;
	static final int DEFAULT_NUM_ITEMS = 1;

	public FragmentPageAdapter(FragmentManager fm, ArrayList<GridItem> list,
			Resources re)
	{
		super(fm);
		mTopicList = list;
		resources = re;
		setup();
	}

	@Override
	public Fragment getItem(int position)
	{
		Bundle args = new Bundle();
		args.putInt("firstImage", position * mNumItems);

		// The last page might not have the full number of items.
		int imageCount = mNumItems;
		if (position == (mNumFragments - 1))
		{
			int numTopics = mTopicList.size();
			int rem = numTopics % mNumItems;
			if (rem > 0)
				imageCount = rem;
		}
		args.putInt("imageCount", imageCount);
		args.putSerializable("topicList", mTopicList);

		GridFragment f = new GridFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public int getCount()
	{
		return mNumFragments;
	}

	void setup()
	{
		if (mTopicList == null)
		{
			mNumItems = DEFAULT_NUM_ITEMS;
			mNumFragments = DEFAULT_NUM_FRAGMENTS;
		}
		else
		{
			int numTopics = mTopicList.size();
			int numRows = resources.getInteger(R.integer.num_rows);
			int numCols = resources.getInteger(R.integer.num_cols);
			int numTopicsPerPage = numRows * numCols;
			int numFragments = numTopics / numTopicsPerPage;

			if (numTopics % numTopicsPerPage != 0)
				numFragments++;

			mNumFragments = numFragments;
			mNumItems = numTopicsPerPage;
		}
	}

}
