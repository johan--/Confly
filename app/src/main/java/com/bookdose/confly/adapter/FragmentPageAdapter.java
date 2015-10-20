package com.bookdose.confly.adapter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bookdose.confly.GridFragment;
import com.bookdose.confly.R;
import com.bookdose.confly.object.Issue;

import java.util.ArrayList;

public class FragmentPageAdapter extends FragmentStatePagerAdapter implements GridFragment.GridFragmentListener {
	private int mNumItems = 0;
	private int mNumFragments = 0;
	private ArrayList<Issue> mTopicList;

	public boolean isEdit;

	private Resources resources;

	static final int DEFAULT_NUM_FRAGMENTS = 1;
	static final int DEFAULT_NUM_ITEMS = 1;

	public interface PagerAdapterListener{
		void didSelectedBook(Issue issue);
	}

	private PagerAdapterListener pagerAdapterListener;

	public void setPagerAdapterListener(PagerAdapterListener pagerAdapterListener) {
		this.pagerAdapterListener = pagerAdapterListener;
	}

	public FragmentPageAdapter(FragmentManager fm, ArrayList<Issue> list,
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
		f.isEdit = isEdit;
		f.setGridFragmentListener(this);
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

	@Override
	public void didSelectGridItem(Issue issue) {
		if (pagerAdapterListener != null)
			pagerAdapterListener.didSelectedBook(issue);
	}
}
