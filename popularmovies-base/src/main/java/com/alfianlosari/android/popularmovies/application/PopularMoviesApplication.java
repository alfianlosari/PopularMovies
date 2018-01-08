package com.alfianlosari.android.popularmovies.application;

import android.app.Application;

import io.branch.referral.Branch;

/**
 * Created by alfianlosari on 08/01/18.
 */

public class PopularMoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }
}
