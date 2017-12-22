package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.utilities.SortType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfianlosari on 06/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Movie implements Parcelable{

    public String id;
    public double voteAverage;
    public String title;
    public double popularity;
    public String posterPath;
    public String overview;
    public String releaseDate;
    public String originalTitle;
    public boolean isFavorite = false;

    public Movie(String id , String title, String posterPath,
                 String overview, String releaseDate, String originalTitle,
                 double voteAverage, double popularity) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
    }

    public Movie(JSONObject json) throws JSONException {
        this.id = json.getString("id");
        this.title = json.getString("title");
        this.originalTitle = json.getString("original_title");
        this.voteAverage = json.getDouble("vote_average");
        this.popularity = json.getDouble("popularity");
        this.posterPath = json.getString("poster_path");
        this.overview = json.getString("overview");
        this.releaseDate = json.getString("release_date");
    }



    public String imagePath() {
        return "http://image.tmdb.org/t/p/w185//" + posterPath;
    }
    public ContentValues contentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        return cv;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeDouble(voteAverage);
        parcel.writeString(title);
        parcel.writeDouble(popularity);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(originalTitle);
        parcel.writeString(releaseDate);
    }

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public Movie(Parcel pc){
        id = pc.readString();
        voteAverage = pc.readDouble();
        title = pc.readString();
        popularity = pc.readDouble();
        posterPath = pc.readString();
        overview = pc.readString();
        originalTitle = pc.readString();
        releaseDate = pc.readString();
    }

    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



}
