package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.storage.movie.MovieRepository;

public class MovieViewModel extends AndroidViewModel {
    private static final String TAG_NAME = MovieViewModel.class.getSimpleName();

    private final MovieRepository mMovieRepository;

    private LiveData<Movie[]> mPopularMovies;
    private LiveData<Movie[]> mSearchedMovies;
    private LiveData<Movie> mSharedMovie;

    private int popularMoviePages;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = MovieRepository.getInstance(application);

        mPopularMovies = mMovieRepository.getMovies();
        mSearchedMovies = mMovieRepository.getSearchedMovies();
        mSharedMovie = mMovieRepository.getSharedMovie();

        popularMoviePages = 1;
    }

    public LiveData<Movie[]> getMovies() {
        return mPopularMovies;
    }

    public LiveData<Movie[]> getSearchedMovies() {
        return mSearchedMovies;
    }

    public LiveData<Movie> getSharedMovie() {
        return mSharedMovie;
    }

    public void setMovies(Movie[] movies) {
        mMovieRepository.setMovies(movies);
        mPopularMovies = mMovieRepository.getMovies();
    }

    // Function to fetch the meals
    public void fetchMovies(boolean hasInternet, String query, int popularMoviePages) {
        mMovieRepository.fetchMovies(hasInternet, query, popularMoviePages);
    }

    public void fetchMovieById(int movieId) {
        mMovieRepository.fetchMovieById(movieId);
    }

    public void loadMoreMovies(boolean hasInternet, String query) {
        popularMoviePages++;
        fetchMovies(hasInternet, query, popularMoviePages);
    }

    // Function that will search movies based on the users Query
    public void searchMovies(String query) {
        mMovieRepository.searchMovies(query);
    }

    public void clearMovies() {
        mMovieRepository.clearMovies();
    }
}
