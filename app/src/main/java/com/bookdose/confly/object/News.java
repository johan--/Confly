package com.bookdose.confly.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Teebio on 10/27/15 AD.
 */
public class News {
    public String rssId;
    public String rssName;
    public String newsId;
    public String newsName;
    public String url;
    public String languageId;
    public String languageName;
    public String languageCode;
    public String title;
    public String description;
    public String link;
    public String pubDate;

    public News(JSONObject obj){
        try {
            rssId = obj.getString("rss_id");
            rssName = obj.getString("rss_name");
            newsId = obj.getString("news_id");
            newsName = obj.getString("news_name");
            url = obj.getString("url");
            languageId = obj.getString("language_id");
            languageName = obj.getString("language_name");
            languageCode = obj.getString("language_code");
            title = obj.getString("title");
            description = obj.getString("description");
            pubDate = obj.getString("pub_date");
            link = obj.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
