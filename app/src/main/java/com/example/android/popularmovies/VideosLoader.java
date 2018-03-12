package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import Utilities.MovieDBJsonUtils;
import Utilities.NetworkUtils;

/**
 * Created by domeniclavitola on 2/20/18.
 */

class VideosLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<Video>> {

    private String jsonResponse;

    /** Tag for log messages */
    private static final String LOG_TAG = VideosLoader.class.getName();

    /** Query URL */
    private final String mUrl;

    VideosLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading is called");
        forceLoad();
    }

    /**
     * Background networking and jsonParsing tasks. Returns an ArrayList<Video> videos
     */

    @Override
    public ArrayList<Video> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground: loadInBackground has been called");
        if (mUrl == null) {
            return null;
        }
        try {
            jsonResponse = NetworkUtils.makeHttpRequest(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "loadInBackground: jsonResponse = " + jsonResponse);

        return MovieDBJsonUtils.extractVideos(jsonResponse);
    }
}
