package com.pekict.movieplanet.storage.movie;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieList;

@Dao
public interface MovieListDAO {

    @Insert
    void insertMovieLists(MovieList newMovieList);

    @Query("DELETE FROM movie_lists WHERE id = :id")
    void deleteMovieList(int id);

    @Query("SELECT * FROM movie_lists ORDER BY title ASC")
    MovieList[] getAllMovieLists();

    @Query("SELECT * FROM movie_lists WHERE id = :id")
    MovieList getMovieList(int id);
}
