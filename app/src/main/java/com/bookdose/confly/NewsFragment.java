package com.bookdose.confly;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookdose.confly.adapter.NewDetailAdapter;
import com.bookdose.confly.adapter.NewsAdapter;
import com.bookdose.confly.helper.Helper;
import com.bookdose.confly.helper.ServiceRequest;
import com.bookdose.confly.object.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements NewsAdapter.NewsListener, NewDetailAdapter.NewsDetailListener {

    ListView newsList;
    LinearLayout menuLayout;
    ArrayList<News> news;
    Button backBtn;
    WebView webView;
    private ProgressDialog progressBar;

    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_SUB = "NEWS_SUB";
    public static final String NEWS_DETAIL = "NEWS_DETAIL";
    public static final String NEWS_CONTENT = "NEWS_CONTENT";

    private String newState=NEWS_TITLE;
    private String newId;

    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment() {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        newsList = (ListView)rootView.findViewById(R.id.news_list);
        menuLayout = (LinearLayout)rootView.findViewById(R.id.menuLayout);
        backBtn = (Button)rootView.findViewById(R.id.backBtn);
        backBtn.setVisibility(View.INVISIBLE);

        webView = (WebView)rootView.findViewById(R.id.webContent);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newState.equals(NEWS_TITLE))
                    showMenuNews();
                else if (newState.equals(NEWS_SUB)) {
                    showListNews();
                    getNewsMenu();
                    //showNewsSub(newId);
                }else if (newState.equals(NEWS_DETAIL))
                    showNewsSub(newId);
                else if (newState.equals(NEWS_CONTENT)) {
                    showListNews();
                    newState = NEWS_SUB;
                    showNewsSub(newId);
                }
            }
        });

        news = new ArrayList<>();

        TextView allNews = (TextView)rootView.findViewById(R.id.allNews);
        allNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRss("");
                showListNews();
            }
        });

        TextView thNews = (TextView)rootView.findViewById(R.id.thaiNews);
        thNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRss("th");
                showListNews();
            }
        });

        TextView enNews = (TextView)rootView.findViewById(R.id.engNews);
        enNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRss("en");
                showListNews();
            }
        });

        return rootView;
    }

    void showListNews(){
        webView.setVisibility(View.GONE);
        menuLayout.setVisibility(View.GONE);
        newsList.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);

    }

    void showMenuNews(){
        menuLayout.setVisibility(View.VISIBLE);
        newsList.setVisibility(View.GONE);
        backBtn.setVisibility(View.INVISIBLE);
    }

    void showWebContent(){
        webView.setVisibility(View.VISIBLE);
        newsList.setVisibility(View.INVISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    void showNewsSub(String newsId){
        showLoading();
        ArrayList<News> list = new ArrayList<>();
        for (News item:news) {
            if (item.newsId.equals(newsId))
                list.add(item);
        }

        NewDetailAdapter newsAdapter = new NewDetailAdapter(getActivity(),list);
        newsAdapter.setNewsDetailListener(this);
        newsList.setAdapter(newsAdapter);
        this.newId = newsId;
        hideLoading();
    }

    void loadContent(String url){
        showLoading();
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("load url", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("load url", "Finished loading URL: " +url);
                hideLoading();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("load url", "Error: " + description);
                //Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.loadUrl(url);
    }

    void loadRss(String lang){
        showLoading();
        JSONArray rss = ServiceRequest.requestNewsListAPI(lang, Helper.findDeviceID(getActivity()));
        if (rss!=null){
            news.clear();
            for (int i = 0;i < rss.length(); i++){
                try {
                    JSONObject object = rss.getJSONObject(i);
                    News news = new News(object);
                    this.news.add(news);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
       getNewsMenu();
        hideLoading();
    }

    void getNewsMenu(){
        ArrayList<News> list = new ArrayList<>();
        for (News item:news) {
            if (list.size() == 0)
                list.add(item);
            else {
                boolean check = false;
                for (News oldItem:list){
                    if(oldItem.newsId.equals(item.newsId))
                        check = true;
                }
                if (!check)
                    list.add(item);
            }
        }
        newState = NEWS_TITLE;
        NewsAdapter newsAdapter = new NewsAdapter(getActivity(),list,newState);
        newsAdapter.setNewsListenerr(this);
        newsList.setAdapter(newsAdapter);

    }

    @Override
    public void onSelectNews(News news) {
        if (newState.equals(NEWS_TITLE)) {
            newState = NEWS_SUB;
            showNewsSub(news.newsId);
        }else if (newState.equals(NEWS_SUB)) {
            newState = NEWS_DETAIL;
            showNewsSub(news.newsId);
        }
    }

    @Override
    public void onSelectNewsDetail(News news) {
        newState = NEWS_CONTENT;
        showWebContent();
        loadContent(news.link);

    }

    void showLoading(){
        progressBar = ProgressDialog.show(getActivity(), "", "Loading...");
    }

    void hideLoading(){
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }
}
