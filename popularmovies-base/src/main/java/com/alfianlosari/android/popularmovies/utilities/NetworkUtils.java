package com.alfianlosari.android.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public final class NetworkUtils {

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String POPULAR_MOVIE_BASE_URL = MOVIE_BASE_URL + "/popular";
    private static final String TOP_RATED_MOVIE_BASE_URL = MOVIE_BASE_URL + "/top_rated";

    private static final String API_KEY = "9a4cbc5790eb0c51e521651e19490c35";
    private final static String API_KEY_PARAM = "api_key";

    private static String movieDetailURL(String movieId) {
        return MOVIE_BASE_URL + "/" + movieId;
    }

    private static String movieReviewsURL(String movieId) {
        return MOVIE_BASE_URL + "/" + movieId + "/reviews";
    }

    private static String movieVideosURL(String movieId) {
        return MOVIE_BASE_URL + "/" + movieId + "/videos";
    }

    public static URL buildURLForMovieDetail(String movieId) {
        String urlString = movieDetailURL(movieId);
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForMovieReview(String movieId) {
        String urlString = movieReviewsURL(movieId);
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForMovieVideos(String movieId) {
        String urlString = movieVideosURL(movieId);
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildUrl(SortType sortType) {
        String urlString;

        switch (sortType) {
            case POPULAR:
                urlString = POPULAR_MOVIE_BASE_URL;
                break;

            case TOP_RATED:
                urlString = TOP_RATED_MOVIE_BASE_URL;
                break;

            default:
                return null;
        }

        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
