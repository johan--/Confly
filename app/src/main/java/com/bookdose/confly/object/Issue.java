package com.bookdose.confly.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.bookdose.confly.helper.Helper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Teebio on 8/25/15 AD.
 */
public class Issue implements Parcelable {
    public String content_aid;
    public String content_name;
    public String content_type;
    public String category_aid;
    public String category_name;
    public String author;
    public String publish_month;
    public String issue_aid;
    public String vol;
    public String issue;
    public String issue_else;
    public String description;
    public String publish_date;
    public String publish_day;
    public String is_license;
    public String publish_year;
    public String is_generate_content;
    public String is_resize_cover;
    public String have_cover;
    public String have_file;
    public String filename;
    public String path;
    public String full_path;
    public String cover_image;
    public String status;

    public Issue(){

    }

    public Issue(JSONObject object){
        try {
            this.content_aid = object.getString("content_aid");
            this.content_name = object.getString("content_name");
            this.content_type = object.getString("content_type_name");
            this.category_aid = object.getString("category_aid");
            this.category_name = object.getString("category_name");
            this.author = object.getString("author");
            this.is_license = object.getString("is_license");
            this.issue_aid = object.getString("issue_id");
            this.vol = object.getString("vol");
            this.issue = object.getString("issue");
            this.issue_else = object.getString("issue_else");
            this.description = object.getString("description");
            this.publish_date = object.getString("publish_date");
            //this.publish_day = object.getString("publish_day");
            //this.publish_month = object.getString("publish_month");
            //this.publish_year = object.getString("publish_year");
            this.is_generate_content = object.getString("is_generate_content");
            //this.is_resize_cover = object.getString("is_resize_cover");
            //this.have_cover = object.getString("have_cover");
            this.have_file = object.getString("have_file");
            //this.filename = object.getString("filename");
            this.path = object.getString("path");
            this.full_path = object.getString("full_path");
            this.cover_image = object.getString("cover_image");
            this.status = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Issue(Parcel in) {
        content_aid = in.readString();
        content_name = in.readString();
        content_type = in.readString();
        category_aid = in.readString();
        category_name = in.readString();
        author = in.readString();
        publish_month = in.readString();
        issue_aid = in.readString();
        vol = in.readString();
        issue = in.readString();
        issue_else = in.readString();
        description = in.readString();
        publish_date = in.readString();
        publish_day = in.readString();
        is_license = in.readString();
        publish_year = in.readString();
        is_generate_content = in.readString();
        is_resize_cover = in.readString();
        have_cover = in.readString();
        have_file = in.readString();
        filename = in.readString();
        path = in.readString();
        full_path = in.readString();
        cover_image = in.readString();
        status = in.readString();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getCoverUrl(){
        String coverUrl = Constant.MAIN_URL+full_path+"cover/"+path+"-cover.jpg";
        return coverUrl;
    }

    public String getSmallCoverUrl(){
        String coverUrl = Constant.MAIN_URL+full_path+"cover/"+path+"-small.jpg";
        return coverUrl;
    }

    public String getThumbCoverUrl(){
        String coverUrl = Constant.MAIN_URL+full_path+"cover/"+path+"-thumb.jpg";
        return coverUrl;
    }

    public String getLargeCoverUrl(){
        String coverUrl = Constant.MAIN_URL+full_path+"cover/"+path+"-large.jpg";
        return coverUrl;
    }

    public String getLocalCover(){
        String coverUrl = Helper.getCoversDirectory()+"/"+cover_image;
        return coverUrl;
    }

    public String getFileUrl(){
        String coverUrl = Constant.MAIN_URL+full_path+"cover/"+path+"-large.jpg";
        return coverUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content_aid);
        dest.writeString(content_name);
        dest.writeString(content_type);
        dest.writeString(category_aid);
        dest.writeString(category_name);
        dest.writeString(author);
        dest.writeString(publish_month);
        dest.writeString(issue_aid);
        dest.writeString(vol);
        dest.writeString(issue);
        dest.writeString(issue_else);
        dest.writeString(description);
        dest.writeString(publish_date);
        dest.writeString(publish_day);
        dest.writeString(is_license);
        dest.writeString(publish_year);
        dest.writeString(is_generate_content);
        dest.writeString(is_resize_cover);
        dest.writeString(have_cover);
        dest.writeString(have_file);
        dest.writeString(filename);
        dest.writeString(path);
        dest.writeString(full_path);
        dest.writeString(cover_image);
        dest.writeString(status);
    }
}
