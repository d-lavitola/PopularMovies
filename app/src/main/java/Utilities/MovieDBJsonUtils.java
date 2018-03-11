package Utilities;

import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.Review;
import com.example.android.popularmovies.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Domenic LaVitola on 7/31/17.
 *
 * Provides utility methods for reading, interpreting, and
 * reformatting a JSONResponse
 */

public class MovieDBJsonUtils {
    public final static String IMAGE_BASE_URL_342 = "http://image.tmdb.org/t/p/w342/";
    public final static String IMAGE_BASE_URL_ORIGINAL = "http://image.tmdb.org/t/p/original/";


    // Extracts and returns ArrayList of MovieProvider objects from a given jsonResponse
    public static ArrayList<Movie> extractMovies(String jsonResponse) {
        ArrayList<Movie> movies = new ArrayList<>();

        try {
            // Get JSON Object from given jsonResponse
            JSONObject root = new JSONObject(jsonResponse);

            // Get the Array named results from JSON Object created above
            JSONArray movieArray = root.getJSONArray("results");

            // Iterate through array of movies to create MovieProvider objects for
            // each movie
            for (int i = 0; i < movieArray.length(); i++) {

                int mVoteCount;
                int mId;
                boolean mVideo;
                String mTitle;
                double mPopularity;
                double mVoteAverage;
                String mOriginalTitle;
                String mOriginalLanguage;
                int[] mGenreIds;
                String mBackdropPath;
                boolean mAdult;
                String mPosterPath;
                String mOverview;
                String mReleaseDate;

                JSONObject currentMovie = movieArray.getJSONObject(i);

                mVoteCount = currentMovie.getInt("vote_count");
                mId = currentMovie.getInt("id");
                mVideo = currentMovie.getBoolean("video");
                mTitle = currentMovie.getString("title");
                mPopularity = currentMovie.getDouble("popularity");
                //Select currentMovie's user rating (out of 10)
                mVoteAverage = currentMovie.getDouble("vote_average");
                mOriginalTitle = currentMovie.getString("original_title");
                mOriginalLanguage = currentMovie.getString("original_language");

                JSONArray genreIdsJsonArray = currentMovie.getJSONArray("genre_ids");
                mGenreIds = new int[genreIdsJsonArray.length()];
                for (int j = 0; j < genreIdsJsonArray.length(); ++j) {
                    mGenreIds[j] = genreIdsJsonArray.optInt(j);
                }

                mBackdropPath = currentMovie.getString("backdrop_path");
                mAdult = currentMovie.getBoolean("adult");
                mPosterPath = currentMovie.getString("poster_path");
                mOverview = currentMovie.getString("overview");
                mReleaseDate = currentMovie.getString("release_date");

                // Save selected attributes to a new MovieProvider Object
                Movie movie = new Movie(mVoteCount, mId, mVideo, mTitle, mPopularity, mVoteAverage,
                        mOriginalTitle, mOriginalLanguage, mGenreIds, mBackdropPath, mAdult,
                        mPosterPath, mOverview, mReleaseDate);

                // Add newly created MovieProvider Object to array "movies"
                movies.add(movie);

            }
        } catch (JSONException e) {
            // Throws and logs an error if there is a problem parsing JSON results
            Log.e("MovieDBJSONUtils", "Problem parsing the movie JSON results", e);
        }
        return movies;
    }

    // Extracts and returns ArrayList of Video objects from a given jsonResponse
    public static ArrayList<Video> extractVideos(String jsonResponse) {
        ArrayList<Video> videos = new ArrayList<>();

        try {
            // Get JSON Object from given jsonResponse
            JSONObject root = new JSONObject(jsonResponse);

            // Get the Array named results from JSON Object created above
            JSONArray videosArray = root.getJSONArray("results");

            // Iterate through array of movies to create MovieProvider objects for
            // each movie
            for (int i = 0; i < videosArray.length(); i++) {
                String id;
                String iso_639_1;
                String iso_3166_1;
                String key;
                String name;
                String site;
                int size;
                String type;

                JSONObject video = videosArray.getJSONObject(i);

                id = video.getString("id");
                iso_639_1 = video.getString("iso_639_1");
                iso_3166_1 = video.getString("iso_3166_1");
                key = video.getString("key");
                name = video.getString("name");
                site = video.getString("site");
                size = video.getInt("size");
                type = video.getString("type");

                videos.add(new Video(id, iso_639_1, iso_3166_1, key, name, site, size, type));
            }
        } catch (JSONException e) {
            // Throws and logs an error if there is a problem parsing JSON results
            Log.e("MovieDBJSONUtils", "Problem parsing the trailers JSON results", e);
        }

        return videos;
    }

    // Extracts and returns ArrayList of Review objects from a given jsonResponse
    public static ArrayList<Review> extractReviews(String jsonResponse) {
        ArrayList<Review> reviews = new ArrayList<>();

        try {
            // Get JSON Object from given jsonResponse
            JSONObject root = new JSONObject(jsonResponse);

            // Get the Array named results from JSON Object created above
            JSONArray reviewsArray = root.getJSONArray("results");

            // Iterate through array of movies to create MovieProvider objects for
            // each movie
            for (int i = 0; i < reviewsArray.length(); i++) {
                String id;
                String author;
                String content;
                String url;

                JSONObject review = reviewsArray.getJSONObject(i);

                id = review.getString("id");
                author = review.getString("author");
                content = review.getString("content");
                url = review.getString("url");

                reviews.add(new Review(id, author, content, url));
            }
        } catch (JSONException e) {
            // Throws and logs an error if there is a problem parsing JSON results
            Log.e("MovieDBJSONUtils", "Problem parsing the trailers JSON results", e);
        }

        return reviews;
    }

    // Formats dates to be displayed properly in MovieDescriptionActivity
    public static String formatDate(String dateJson) {
        // Create format of incoming date string from JsonResponse
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // Create format for outgoing date strings
        DateFormat dfReturn = new SimpleDateFormat("MMMM d, yyyy", Locale.US);

        Date releaseDate;
        try {
            //
            releaseDate = df.parse(dateJson);
            return dfReturn.format(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static float formatStars(Double ratingJson) {
        long rating = Math.round(ratingJson/2);
        return (float) rating;
    }
}
