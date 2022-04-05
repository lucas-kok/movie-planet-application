package com.pekict.movieplanet.domain.tvshow;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TVshow implements Parcelable {
    private static final String TAG_NAME = TVshow.class.getSimpleName();

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
    private final String original_language;
    private final String overview;
    private final String first_air_date;
    private final String name;
    private final String backdrop_path;
    private final double popularity;
    private final int vote_count;
    private final double vote_average;
    private List<Integer> genre_ids = new ArrayList<>();

    public TVshow(int id, String original_language, String overview, String first_air_date, String name, String backdrop_path, double popularity, int vote_count, double vote_average) {
        this.id = id;
        this.original_language = original_language;
        this.overview = overview;
        this.first_air_date = first_air_date;
        this.name = name;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    public TVshow(Parcel in) {
        id = in.readInt();
        original_language = in.readString();
        overview = in.readString();
        first_air_date = in.readString();
        name = in.readString();
        backdrop_path = in.readString();
        in.readList(genre_ids, Integer.class.getClassLoader());
        popularity = in.readDouble();
        vote_count = in.readInt();
        vote_average = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOverview() {
        return overview;
    }

    public String getFirst_air_date() {
        if (first_air_date == null) { return null; }
        return !first_air_date.isEmpty() ? first_air_date.substring(0, 4) : null;
    }

    public String getName() {
        return name;
    }

    public String getBackdrop_path() {
        return backdrop_path;
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

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    // Will return the URL of the Movies image with a width of w500
    public String getSmallImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    // Will return the URL of the Movies image in its original size
    public String getOriginalImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    private boolean hasId(int id) {
        for (Integer genreId : genre_ids) {
            if (genreId == id) {
                return true;
            }
        }

        return false;
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

        if (genre_ids == null) {
            return "";
        }

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
