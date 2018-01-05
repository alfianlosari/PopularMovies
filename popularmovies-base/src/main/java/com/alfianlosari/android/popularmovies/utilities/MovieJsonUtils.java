package com.alfianlosari.android.popularmovies.utilities;

import com.alfianlosari.android.popularmovies.data.Movie;
import com.alfianlosari.android.popularmovies.data.Review;
import com.alfianlosari.android.popularmovies.data.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfianlosari on 06/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class MovieJsonUtils {

    public static Movie getMovieFromJson(String movieJsonStr)
        throws JSONException {

        JSONObject movieJSON = new JSONObject(movieJsonStr);
        Movie movie = new Movie(movieJSON);
        return movie;
    }

    public static Movie[] getMoviesFromJson(String movieJsonStr)
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

    public static Review[] getReviewsFromJSON(String reviewJsonStr) throws JSONException {
        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewsArray = reviewJson.getJSONArray("results");

        Review[] parsedReviewData = new Review[reviewsArray.length()];
        for(int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewObject = reviewsArray.getJSONObject(i);
            Review review = new Review(reviewObject);
            parsedReviewData[i] = review;
        }

        return parsedReviewData;

    }

    public static Video[] getVideosFromJSON(String videoJsonStr) throws JSONException {
        JSONObject videoJson = new JSONObject(videoJsonStr);
        JSONArray videosArray = videoJson.getJSONArray("results");

        Video[] parsedVideoData = new Video[videosArray.length()];
        for(int i = 0; i < videosArray.length(); i++) {
            JSONObject videoObject = videosArray.getJSONObject(i);
            Video video = new Video(videoObject);
            parsedVideoData[i] = video;
        }

        return parsedVideoData;

    }




}
