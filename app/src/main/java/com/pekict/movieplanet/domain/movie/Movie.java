package com.pekict.movieplanet.domain.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "popular_movie_table")
public class Movie implements Parcelable {
    @PrimaryKey
    private final int id;
    private final String original_title;
    private final String original_language;
    private final String title;
    private final String backdrop_path;
    private final double popularity;
    private final int vote_count;
    private final double vote_average;

    public Movie(int id, String original_title, String original_language, String title, String backdrop_path, double popularity, int vote_count, double vote_average) {
        this.id = id;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readDouble();
        vote_count = in.readInt();
        vote_average = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    // Will return the URL of the Movies image with a width of w500
    public String getSmallImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    // Will return the URL of the Movies image in its original size
    public String getOriginalImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(original_title);
        parcel.writeString(original_language);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeDouble(popularity);
        parcel.writeInt(vote_count);
        parcel.writeDouble(vote_average);
    }
}
