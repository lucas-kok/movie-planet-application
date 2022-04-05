package com.pekict.movieplanet.storage.movie;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pekict.movieplanet.domain.movie.Movie;

@Dao
public interface MovieDAO {

    @Insert
    void savePopularMovies(Movie[] movies);

    @Query("DELETE FROM movies_table")
    void deleteAllPopularMovies();

    @Query("SELECT * FROM movies_table ORDER BY " +
            "CASE WHEN :order = 'title' AND :sorting = 'asc' THEN title END ASC," +
            "CASE WHEN :order = 'title' AND :sorting = 'desc'  THEN title END DESC," +
            "CASE WHEN :order = 'popularity' AND :sorting = 'asc'  THEN popularity END ASC," +
            "CASE WHEN :order = 'popularity' AND :sorting = 'desc'  THEN popularity END DESC," +
            "CASE WHEN :order = 'vote_average' AND :sorting = 'asc'  THEN vote_average END ASC," +
            "CASE WHEN :order = 'vote_average' AND :sorting = 'desc'  THEN vote_average END DESC," +
            "CASE WHEN :order = 'release_date' AND :sorting = 'asc'  THEN release_date END ASC," +
            "CASE WHEN :order = 'release_date' AND :sorting = 'desc'  THEN release_date END DESC"
    )
    Movie[] getAllPopularMovies(String order, String sorting);
}
