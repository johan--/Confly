package com.bookdose.confly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.GridView;

import com.bookdose.confly.adapter.GridIssueAdapter;
import com.bookdose.confly.object.Issue;

import java.util.ArrayList;

public class GridFragment extends Fragment implements GridIssueAdapter.GridIssueListener
{
	public boolean isEdit;
	int mNum;
	int mFirstImage = 0;
	int mImageCount = -1;
	ArrayList<Issue> mTopicList;

	public interface GridFragmentListener{
		public void didSelectGridItem(Issue issue);
	}

	private GridFragmentListener gridFragmentListener;

	public void setGridFragmentListener(GridFragmentListener listener){
		this.gridFragmentListener = listener;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mNum = ((args != null) ? args.getInt("num") : 0);

		if (args != null)
		{
			mTopicList = (ArrayList<Issue>) args
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

		final GridIssueAdapter gridIssueAdapter = new GridIssueAdapter(getActivity(), mTopicList, mFirstImage, mImageCount, gridview.getHeight());
		gridIssueAdapter.isEdit = isEdit;
		gridIssueAdapter.setGridIssueListener(this);
		
		ViewTreeObserver treeObserver = gridview.getViewTreeObserver();
		treeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout()
			{
				gridview.setColumnWidth(gridview.getWidth() / gridview.getNumColumns());

				gridview.setAdapter(gridIssueAdapter);
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

	@Override
	public void didSelectGridIssue(Issue issue) {
		if (gridFragmentListener != null)
			gridFragmentListener.didSelectGridItem(issue);
		Log.e("", "Position item click ===> " + issue.content_name);
	}
}
