package com.demo.gridviewdemo;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.GridView;

public class GridFragment extends Fragment
{
	int mNum;
	int mFirstImage = 0;
	int mImageCount = -1;
	ArrayList<GridItem> mTopicList;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mNum = ((args != null) ? args.getInt("num") : 0);

		if (args != null)
		{
			mTopicList = (ArrayList<GridItem>) args
					.getSerializable("topicList");
			mFirstImage = args.getInt("firstImage");
		}
		int numRows = getResources().getInteger(R.integer.num_rows);
		int numCols = getResources().getInteger(R.integer.num_cols);
		int numTopicsPerPage = numRows * numCols;
		mImageCount = numTopicsPerPage;

		mFirstImage = (mFirstImage / numTopicsPerPage) * numTopicsPerPage;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		View rootView = getView();
		final GridView gridview = (GridView) rootView.findViewById(R.id.gridView);
		
		ViewTreeObserver treeObserver = gridview.getViewTreeObserver();
		treeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout()
			{
				gridview.setColumnWidth(gridview.getWidth() / gridview.getNumColumns());
				gridview.setAdapter(new GridImageAdapter(getActivity(), mTopicList, mFirstImage, mImageCount, gridview.getHeight()));
	    		gridview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	/**
	 * onCreateView Build the view that shows the grid.
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// Build the view that shows the grid.
		View view = inflater.inflate(R.layout.gridview, container, false);
		return view;
	}
}
