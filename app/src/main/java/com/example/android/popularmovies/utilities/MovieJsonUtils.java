package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfianlosari on 06/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class MovieJsonUtils {

    public static Movie[] getSimpleWeatherStringsFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");

        Movie[] parsedMovieData = new Movie[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            Movie movie = new Movie(movieObject);
            parsedMovieData[i] = movie;
        }
        return parsedMovieData;
    }
}
