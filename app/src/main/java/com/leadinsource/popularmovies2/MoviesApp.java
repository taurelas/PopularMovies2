package com.leadinsource.popularmovies2;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * For application wide settings
 */

public class MoviesApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
