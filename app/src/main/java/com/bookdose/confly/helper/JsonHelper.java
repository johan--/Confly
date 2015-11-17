package com.bookdose.confly.helper;

import com.bookdose.confly.object.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Teebio on 9/30/15 AD.
 */
public class JsonHelper {

    public static JSONObject getJsonConfig(Issue issue){
        JSONObject obj = null;
        try {
            obj = new JSONObject(Helper.readFile(Helper.getConfigPath(issue)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;

    }

    public static ArrayList<String> getLinkDownload(Issue issue){
        ArrayList<String>links = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(Helper.readFile(Helper.getConfigPath(issue)));
            JSONObject detail = obj.getJSONObject("detail");
            JSONArray pages = obj.getJSONArray("pages");
            for (int i=0; i<pages.length(); i++){
                JSONObject page = pages.getJSONObject(i);
                links.add(page.getString("link"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return links;

    }

    public static ArrayList<String> getContents(Issue issue){
        ArrayList<String>links = new ArrayList<>();
        String bookPath = Helper.getBookDirectory()+"/"+issue.path; //issue.path = folder name
        try {
            JSONObject obj = new JSONObject(Helper.readFile(Helper.getConfigPath(issue)));
            JSONObject detail = obj.getJSONObject("detail");
            JSONArray pages = obj.getJSONArray("pages");
            for (int i=0; i<pages.length(); i++){
                JSONObject page = pages.getJSONObject(i);
                String path = page.getString("link");
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                links.add(bookPath+"/"+lastPath);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return links;

    }

    public static ArrayList<String> getThumbnails(Issue issue){
        ArrayList<String>links = new ArrayList<>();
        String thumbPath = Helper.getThumbnailDirectory(issue.path); //issue.path = folder name
        try {
            JSONObject obj = new JSONObject(Helper.readFile(Helper.getConfigPath(issue)));
            JSONArray pages = obj.getJSONArray("pages");
            for (int i=0; i<pages.length(); i++){
                JSONObject page = pages.getJSONObject(i);
                String path = page.getString("link");
                String lastPath = path.substring(path.lastIndexOf('/') + 1);
                lastPath = Helper.removeExtention(lastPath);
                links.add(thumbPath+"/"+lastPath+".jpg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return links;
    }
}
