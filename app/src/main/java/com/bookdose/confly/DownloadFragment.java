package com.bookdose.confly;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.bookdose.confly.adapter.DownloadAdapter;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.Constant;
import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadFragment extends Fragment implements AbsListView.OnItemClickListener {

   public interface DownloadFragmentListener{
       public void onSelectIssue(Issue issue);
   }
    private DownloadFragmentListener downloadFragmentListener;
    public void setDownloadFragmentListener(DownloadFragmentListener listener){
        this.downloadFragmentListener = listener;
    }

    private static final String ARG_PARAM2 = "Category";

    private String category;
    ArrayList<Issue> listData;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private DownloadAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static DownloadFragment newInstance(String param1, String param2) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DownloadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            category = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        loadProductList("","");
        //((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != downloadFragmentListener) {
            downloadFragmentListener.onSelectIssue(listData.get(position));
        }
    }

    public void loadProductList(String catId, String productMainId){
        listData = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences(
                "com.bookdose.confly", Context.MODE_PRIVATE);
        String lang = prefs.getString(Constant.LANGUAGE_KEY, "All");
        JSONArray datas = ServiceRequest.requestProductListAPI(catId, productMainId, lang);
        for (int i=0; i<datas.length(); i++){
            try {
                JSONObject obj = datas.getJSONObject(i);
                Issue issue = new Issue(obj);
                listData.add(issue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter = new DownloadAdapter(getActivity(),R.layout.list_issue_item,listData);
        mListView.setAdapter(mAdapter);
    }

}
