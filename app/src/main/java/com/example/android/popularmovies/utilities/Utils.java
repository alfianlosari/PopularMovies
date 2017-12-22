package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by alfianlosari on 04/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Utils {

    private static final int COLUMN_WIDTH = 120;

    public static int calculateNumberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / COLUMN_WIDTH);
    }

    public static String posterPathURL(String posterPath) {
        return "http://image.tmdb.org/t/p/w185//" + posterPath;
    }
}
