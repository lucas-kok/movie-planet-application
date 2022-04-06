package com.pekict.movieplanet.domain.tvshow;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TVshow implements Parcelable {

    public static final Creator<TVshow> CREATOR = new Creator<TVshow>() {
        @Override
        public TVshow createFromParcel(Parcel in) {
            return new TVshow(in);
        }

        @Override
        public TVshow[] newArray(int size) {
            return new TVshow[size];
        }
    };

    private final int id;
    private final String first_air_date;
    private final String name;
    private final String backdrop_path;
    private final List<Integer> genre_ids = new ArrayList<>();

    public TVshow(Parcel in) {
        id = in.readInt();
        first_air_date = in.readString();
        name = in.readString();
        backdrop_path = in.readString();
        in.readList(genre_ids, Integer.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public String getFirst_air_date() {
        if (first_air_date == null) { return null; }
        return !first_air_date.isEmpty() ? first_air_date.substring(0, 4) : null;
    }

    public String getName() {
        return name;
    }

    // Will return the URL of the Movies image with a width of w500
    public String getSmallImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    // Function that returns a String containing the Movie Genres based on the Ids
    public String getGenresAsString() {
        StringBuilder genreString = new StringBuilder();

        Map<Integer, String> genres = new HashMap<>();
        genres.put(12, "Adventure, ");
        genres.put(14, "Fantasy, ");
        genres.put(16, "Animation, ");
        genres.put(18, "Drama, ");
        genres.put(27, "Horror, ");
        genres.put(28, "Action, ");
        genres.put(35, "Comedy, ");
        genres.put(36, "History, ");
        genres.put(37, "Western, ");
        genres.put(53, "Thriller, ");
        genres.put(80, "Crime, ");
        genres.put(99, "Documentary, ");
        genres.put(878, "Science Fiction, ");
        genres.put(9648, "Mystery, ");
        genres.put(10402, "Music, ");
        genres.put(10749, "Romance, ");
        genres.put(10751, "Family, ");
        genres.put(10752, "War, ");
        genres.put(10770, "TV Movie, ");

        for (Integer id : genre_ids) genreString.append(genres.get(id));

        return genre_ids.size() == 0 ? "" : genreString.substring(0, genreString.length() - 2);
    }

}
