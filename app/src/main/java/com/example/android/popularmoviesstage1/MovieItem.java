package com.example.android.popularmoviesstage1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by domeniclavitola on 3/3/18.
 */

public class MovieItem implements Parcelable {
    public String Title;
    public int id;
    public String posterPath;

    protected MovieItem(Parcel in) {
        Title = in.readString();
        id = in.readInt();
        posterPath = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Title);
        parcel.writeInt(id);
        parcel.writeString(posterPath);
    }
}
