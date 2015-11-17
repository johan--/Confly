package com.bookdose.confly;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bookdose.confly.adapter.FragmentPageAdapter;
import com.bookdose.confly.helper.DatabaseHandler;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShelfFragment extends Fragment implements View.OnClickListener, FragmentPageAdapter.PagerAdapterListener{

    FragmentPageAdapter mAdapter;
    ViewPager mPager;
    public boolean isEdit;

    ArrayList<Issue> list = new ArrayList<Issue>();

    LinearLayout llDots;

    public ShelfFragment() {
        // Required empty public constructor
    }

    public interface ShelfListenner{
        void openIssue(Issue issue);
        void downloadIssue(Issue issue);
    }

    private ShelfListenner shelfListenner;

    public void setShelfListenner(ShelfListenner shelfListenner){
        this.shelfListenner = shelfListenner;
    }

    public void reloadData(String type){
        if (type.equals(Constant.ALL))
            list = new DatabaseHandler(getActivity()).getAllIssue();
        else if (type.equals(Constant.MAGAZINE))
            list = new DatabaseHandler(getActivity()).getIssueFromCategory(Constant.MAGAZINE_ID);
        else if (type.equals(Constant.BOOK))
            list = new DatabaseHandler(getActivity()).getIssueFromCategory(Constant.BOOK_ID);
        else if (type.equals(Constant.DOCCUMENT))
            list = new DatabaseHandler(getActivity()).getIssueFromCategory(Constant.DOCCUMENT_ID);

        mAdapter = new FragmentPageAdapter(getActivity().getSupportFragmentManager(), list, getResources());
        mAdapter.setPagerAdapterListener(this);
        mAdapter.isEdit = isEdit;
        mPager.setOffscreenPageLimit(4);
        mPager.removeAllViews();;
        mPager.setAdapter(mAdapter);

        llDots.removeAllViews();

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            ImageButton imgDot = new ImageButton(getActivity());
            imgDot.setTag(i);
            imgDot.setImageResource(R.drawable.dot_selector);
            imgDot.setBackgroundResource(0);
            imgDot.setPadding(5, 5, 5, 5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            imgDot.setLayoutParams(params);
            if(i == 0)
                imgDot.setSelected(true);
            imgDot.setOnClickListener(this);
            llDots.addView(imgDot);
        }

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                Log.e("", "Page Selected is ===> " + pos);
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (i != pos) {
                        ((ImageView) llDots.findViewWithTag(i)).setSelected(false);
                    }
                }
                ((ImageView) llDots.findViewWithTag(pos)).setSelected(true);
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shelf, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.viewPagger);
        llDots = (LinearLayout) rootView.findViewById(R.id.llDots);

        isEdit = false;

        reloadData(Constant.ALL);

        return rootView;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void didSelectedBook(Issue issue) {
        if (issue.status.equals(Constant.COMPLETE_STATUS)) {
            if (shelfListenner != null)
                shelfListenner.openIssue(issue);
        }else {
            if (shelfListenner != null)
                shelfListenner.downloadIssue(issue);
        }
    }
}
