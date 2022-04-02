package com.pekict.movieplanet.domain.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.pekict.movieplanet.logic.FilterOptionsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity(tableName = "popular_movie_table")
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

    public boolean isMatchForFilters(Map<String, String> filterOptions) {
        // Looping through the Movies Ids and checking for overlap
        String ratingString = Objects.requireNonNull(filterOptions.get(FilterOptionsManager.RATING));
        ratingString = ratingString.replace("+", "").replace(" Stars", "");
        int rating = Integer.parseInt(ratingString);

        for (Integer genreId : genre_ids) {
            if (!filterOptions.containsKey(String.valueOf(genreId))) {
                continue;
            }

            if (Objects.equals(filterOptions.get(String.valueOf(genreId)), "true")) {
                return vote_average / 2 > rating;
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

    public String getFirstGenre(List<Integer> genre_ids) {
        StringBuilder genreString = new StringBuilder();
        for (Integer id : genre_ids) {
            if (id == 28) genreString.append("Action, ");
            else if (id == 12) genreString.append("Adventure, ");
            else if (id == 16) genreString.append("Animation, ");
            else if (id == 35) genreString.append("Comedy, ");
            else if (id == 80) genreString.append("Crime, ");
            else if (id == 99) genreString.append("Documentary, ");
            else if (id == 18) genreString.append("Drama, ");
            else if (id == 10751) genreString.append("Family, ");
            else if (id == 14) genreString.append("Fantasy, ");
            else if (id == 36) genreString.append("History, ");
            else if (id == 27) genreString.append("Horror, ");
            else if (id == 10402) genreString.append("Music, ");
            else if (id == 9648) genreString.append("Mystery, ");
            else if (id == 10749) genreString.append("Romance, ");
            else if (id == 878) genreString.append("Science Fiction, ");
            else if (id == 10770) genreString.append("TV Movie, ");
            else if (id == 53) genreString.append("Thriller, ");
            else if (id == 10752) genreString.append("War, ");
            else if (id == 37) genreString.append("Western, ");
        }
        return genreString.substring(0, genreString.length() - 2);
    }
}
