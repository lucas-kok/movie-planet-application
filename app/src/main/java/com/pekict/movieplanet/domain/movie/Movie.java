package com.pekict.movieplanet.domain.movie;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pekict.movieplanet.logic.FilterOptionsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Filter;

@Entity(tableName = "popular_movie_table")
public class Movie implements Parcelable {
    private static final String TAG_NAME = Movie.class.getSimpleName();

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
    @PrimaryKey
    private final int id;
    private final String original_language;
    private final String original_title;
    private final String overview;
    private final String title;
    private final String backdrop_path;
    private final double popularity;
    private final int vote_count;
    private final double vote_average;
    private List<Integer> genre_ids = new ArrayList<>();

    public Movie(int id, String original_language, String original_title, String overview, String title, String backdrop_path, List<Integer> genre_ids, double popularity, int vote_count, double vote_average) {
        this.id = id;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.genre_ids = genre_ids;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        original_language = in.readString();
        original_title = in.readString();
        overview = in.readString();
        title = in.readString();
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

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
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

    public double getPopularity() {
        return popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    private boolean hasId(int id) {
        for (Integer genreId : genre_ids) {
            if (genreId == id) {
                return true;
            }
        }

        return false;
    }

    public boolean isMatchForFilters(Map<String, String> filterOptions) {
        // Looping through the Movies Ids and checking for overlap
        if (filterOptions.get(FilterOptionsManager.ALLGENRES).equals("false")) {
            for (String key : filterOptions.keySet()) {
                if (!filterOptions.get(key).equals("true")) {
                    continue;
                }

                if (!hasId(Integer.parseInt(key))) {
                    return false;
                }
            }
        }

        String languageString = Objects.requireNonNull(filterOptions.get(FilterOptionsManager.LANGUAGE));
        if (!languageString.equals("All")) {
            if (!original_language.equals(languageString)) {
                return false;
            }
        }

        String ratingString = Objects.requireNonNull(filterOptions.get(FilterOptionsManager.RATING));
        if (ratingString.equals("All")) {
            return true;
        }

        ratingString = ratingString.replace("+", "").replace(" Stars", "");
        int rating = Integer.parseInt(ratingString);
        return vote_average / 2 > rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(original_language);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeList(genre_ids);
        parcel.writeDouble(popularity);
        parcel.writeInt(vote_count);
        parcel.writeDouble(vote_average);
    }

    // Function that returns a String containing the Movie Genres based on the Ids
    public String getFirstGenre(List<Integer> genre_ids) {
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

        StringBuilder genreString = new StringBuilder();
        for (Integer id : genre_ids) genreString.append(genres.get(id));

        return genreString.substring(0, genreString.length() - 2);
    }
}
