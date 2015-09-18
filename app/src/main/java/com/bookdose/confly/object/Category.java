package com.bookdose.confly.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Teebio on 9/17/15 AD.
 */
public class Category {
    public String product_main_aid;
    public String product_main_name;
    public String category_aid;
    public String category_name;
    public Category(JSONObject object){
        try {
            this.product_main_aid = object.getString("product_main_aid");
            this.product_main_name = object.getString("product_main_name");
            this.category_aid = object.getString("category_aid");
            this.category_name = object.getString("category_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
