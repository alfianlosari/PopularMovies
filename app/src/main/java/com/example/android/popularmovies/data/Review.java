package com.example.android.popularmovies.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfianlosari on 21/12/17.
 */

public class Review {

    public String id;
    public String author;
    public String content;
    public String url;


    public Review(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.author = json.getString("author");
        this.content = json.getString("content");
        this.url = json.getString("url");
    }



}
