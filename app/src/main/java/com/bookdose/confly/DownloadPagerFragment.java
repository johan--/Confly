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
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadPagerFragment extends Fragment implements View.OnClickListener, FragmentPageAdapter.PagerAdapterListener {

    FragmentPageAdapter mAdapter;
    ViewPager mPager;

    ArrayList<Issue> list = new ArrayList<Issue>();

    LinearLayout llDots;


    public interface DownloadPagerListener{
        public void didSelectedBook(Issue issue);
    }

    private DownloadPagerListener downloadPagerListener;

    public void setDownloadPagerListener(DownloadPagerListener downloadPagerListener) {
        this.downloadPagerListener = downloadPagerListener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadPagerFragment newInstance(String param1, String param2) {
        DownloadPagerFragment fragment = new DownloadPagerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DownloadPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download_pager, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.viewPagger);
        mPager.setOffscreenPageLimit(4);

        llDots = (LinearLayout) rootView.findViewById(R.id.llDots);

        loadProductList("","");

//        mAdapter = new FragmentPageAdapter(getActivity().getSupportFragmentManager(), list, getResources());
//        mAdapter.setPagerAdapterListener(this);

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

        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        if(v != null)
        {
            if(v.getTag() != null)
            {
                int pos = (Integer) v.getTag();
                for (int i = 0; i < mAdapter.getCount(); i++)
                {
                    if(i != pos)
                    {
                        ((ImageView)llDots.findViewWithTag(i)).setSelected(false);
                    }
                }
                mPager.setCurrentItem(pos, true);
                ((ImageView)llDots.findViewWithTag(pos)).setSelected(true);
            }
        }
    }

    public void loadProductList(String catId, String productMainId){
        list = new ArrayList<>();
        JSONArray datas = ServiceRequest.requestProductListAPI(catId,productMainId);
        for (int i=0; i<datas.length(); i++){
            try {
                JSONObject obj = datas.getJSONObject(i);
                Issue issue = new Issue(obj);
                list.add(issue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mAdapter = new FragmentPageAdapter(getActivity().getSupportFragmentManager(), list, getResources());
        mAdapter.setPagerAdapterListener(this);
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
    }

    @Override
    public void didSelectedBook(Issue issue) {
        if (downloadPagerListener != null)
            downloadPagerListener.didSelectedBook(issue);
    }
}
