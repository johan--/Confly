package com.bookdose.confly.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Teebio on 8/25/15 AD.
 */
public class Issue {
    public String issueId;
    public String issueName;
    public String issueDetail;
    public String coverName;
    public String fileUrl;
    public String coverUrl;

    public Issue(JSONObject object){
        try {
            this.issueId = object.getString("");
            this.issueName = object.getString("");
            this.issueDetail = object.getString("");
            this.coverName = object.getString("");
            this.fileUrl = object.getString("");
            this.coverUrl = object.getString("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
