package com.pekict.movieplanet.domain.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pekict.movieplanet.logic.FilterOptionsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity(tableName = "movies_table")
public class Movie implements Parcelable {

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
    @PrimaryKey(autoGenerate = true)
    private int databaseId;

    private final int id;
    private final String original_language;
    private final String overview;
    private final String release_date;
    private final String title;
    private final String backdrop_path;
    private final double popularity;
    private final int vote_count;
    private final double vote_average;
    private List<Integer> genre_ids = new ArrayList<>();

    public Movie(int id, String original_language, String overview, String release_date,
                 String title, String backdrop_path, List<Integer> genre_ids, double popularity, int vote_count, double vote_average) {
        this.id = id;
        this.original_language = original_language;
        this.overview = overview;
        this.release_date = release_date;
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
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        in.readList(genre_ids, Integer.class.getClassLoader());
        popularity = in.readDouble();
        vote_count = in.readInt();
        vote_average = in.readDouble();
    }

    public int getDatabaseId() {
        return databaseId;
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

    public String getRelease_date() {
        return release_date;
    }

    public String getReleaseYear() {
        if (release_date == null) {
            return null;
        }
        return !release_date.isEmpty() ? release_date.substring(0, 4) : null;
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

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
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
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeList(genre_ids);
        parcel.writeDouble(popularity);
        parcel.writeInt(vote_count);
        parcel.writeDouble(vote_average);
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
