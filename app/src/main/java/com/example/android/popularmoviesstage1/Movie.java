package com.example.android.popularmoviesstage1;

import java.io.Serializable;

/**
 * Created by Domenic LaVitola on 7/23/17.
 *
 * Creates class for MovieProvider objects, a MovieProvider Object Constructor, and methods to return attributes
 * of MovieProvider Objects
 */
public class Movie implements Serializable {
    private final int mVoteCount;
    public static int mId = 0;
    private final boolean mVideo;
    private final String mTitle;
    private final double mPopularity;
    private final double mVoteAverage;
    private final String mOriginalTitle;
    private final String mOriginalLanguage;
    private final int[] mGenreIds;
    private final String mBackdropPath;
    private final boolean mAdult;
    private final String mPosterPath;
    private final String mOverview;
    private final String mReleaseDate;

    public Movie(int voteCount, int id, boolean video, String title, double popularity, Double voteAverage,
                 String originalLanguage, String originalTitle, int[] genreIds, String backdropPath,
                 boolean adult, String posterPath, String overview, String releaseDate) {
        mVoteCount = voteCount;
        mOriginalLanguage = originalLanguage;
        mOriginalTitle = originalTitle;
        mGenreIds = genreIds;
        mAdult = adult;
        mId = id;
        mVideo = video;
        mTitle = title;
        mPopularity = popularity;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mBackdropPath = backdropPath;
    }

    public int getmVoteCount() {
        return mVoteCount;
    }

    public int getmId() {
        return mId;
    }

    public boolean ismVideo() {
        return mVideo;
    }

    public String getmTitle() {
        return mTitle;
    }

    public double getmPopularity() {
        return mPopularity;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public int[] getmGenreIds() {
        return mGenreIds;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public boolean ismAdult() {
        return mAdult;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public String getmOverview() {
        return mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }
}
