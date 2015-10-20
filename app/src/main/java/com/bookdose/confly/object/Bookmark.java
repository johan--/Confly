package com.bookdose.confly.object;

/**
 * Created by Teebio on 10/13/15 AD.
 */
public class Bookmark {
    public int page;
    public String imageName;
    public String contentName;
    public String contentId;

    public String getPageText(){
        return "page "+page;
    }
}

