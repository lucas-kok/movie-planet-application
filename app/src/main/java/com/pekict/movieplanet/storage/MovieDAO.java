package com.pekict.movieplanet.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pekict.movieplanet.domain.Movie;

@Dao
public interface MovieDAO {

    @Insert
    void savePopularMovies(Movie[] movies);

    @Query("DELETE FROM popular_movie_table")
    void deleteAllPopularMovies();

    @Query("SELECT * FROM popular_movie_table ORDER BY id ASC")
    Movie[] getAllPopularMovies();
}
