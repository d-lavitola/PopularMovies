package Utilities;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by Domenic LaVitola on 7/23/17.
 *
 * Provides utility methods for reading, interpreting, and
 * reformatting a JSONResponse
 */

public class NetworkUtils {

    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static String api_key = "101415efb001431da44d27585bfea0bc";
    private static final String LOG_TAG = NetworkUtils.class.getName();

    // Create a method for making a network request for a url (as String)
    // and returning a String JSON Response
    public static String makeHttpRequest(String sUrl) throws IOException {
        URL url = buildUrl(sUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
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

    // Builds a URL object from a String url address
    private static URL buildUrl(String address) {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /** Builds string url for fetching reviews for a movie given a movie id */
    public static String constructReviewUrl(int movieId) {
        String reviewsRequestUrl = MOVIE_DB_BASE_URL + movieId + "/reviews?api_key=" + api_key;
        Log.i(TAG, "constructReviewUrl:url = " + reviewsRequestUrl);
        return reviewsRequestUrl;
    }

    /** Builds string url for fetching videos for a movie given a movie id */
    public static String constructVideosUrl(int movieId) {
        String videoRequestUrl = MOVIE_DB_BASE_URL + movieId + "/videos?api_key=" + api_key;
        Log.i(LOG_TAG, "constructVideosUrl: " + videoRequestUrl);
        return videoRequestUrl;
    }
}
