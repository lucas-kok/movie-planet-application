package com.pekict.movieplanet.domain.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "movie_lists")
public class MovieList implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String title;
    private List<Movie> movies;

    public MovieList(String title, List<Movie> movies) {
        this.title = title;
        this.movies = movies;
    }

    protected MovieList(Parcel in) {
        id = in.readInt();
        title = in.readString();
        movies = in.createTypedArrayList(Movie.CREATOR);
    }

    public static final Creator<MovieList> CREATOR = new Creator<MovieList>() {
        @Override
        public MovieList createFromParcel(Parcel in) {
            return new MovieList(in);
        }

        @Override
        public MovieList[] newArray(int size) {
            return new MovieList[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Movie[] getMoviesAsArray() {
        return movies.toArray(new Movie[movies.size()]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private boolean containsMovieId(int id) {
        for (Movie movie : movies) {
            if (movie.getId() == id)  {
                return true;
            }
        }

        return false;
    }

    public boolean addMovie(Movie newMovie) {
        if (containsMovieId(newMovie.getId())) { return false; }

        movies.add(newMovie);
        return true;
    }

    public void deleteMovie(Movie movie) {
        if (!movies.contains(movie)) { return; }

        movies.remove(movie);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeTypedList(movies);
    }
}
