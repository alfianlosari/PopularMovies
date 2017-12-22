package com.example.android.popularmovies.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfianlosari on 21/12/17.
 */

public class Video {
    public String id;
    public String name;
    public String key;

    public Video(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.name = json.getString("name");
        this.key = json.getString("key");
    }

    public String youtubeURL() {
        return "https://www.youtube.com/watch?v=" + key;
    }

}
