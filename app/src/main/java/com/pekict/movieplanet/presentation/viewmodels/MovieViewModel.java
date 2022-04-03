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

    private int popularMoviePages;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = MovieRepository.getInstance(application);

        mPopularMovies = mMovieRepository.getMovies();
        mSearchedMovies = mMovieRepository.getSearchedMovies();

        popularMoviePages = 1;
    }

    public LiveData<Movie[]> getMovies() {
        return mPopularMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovieRepository.setMovies(movies);
        mPopularMovies = mMovieRepository.getMovies();
    }

    public LiveData<Movie[]> getSearchedMovies() {
        return mSearchedMovies;
    }

    // Function to fetch the meals
    public void fetchMovies(boolean hasInternet, int popularMoviePages) {
        mMovieRepository.fetchMovies(hasInternet, popularMoviePages);
    }

    public void loadMoreMovies(boolean hasInternet) {
        popularMoviePages++;
        fetchMovies(hasInternet, popularMoviePages);
    }

    // Function that will search movies based on the users Query
    public void searchMovies(String query) {
        mMovieRepository.searchMovies(query);
    }

    // Function that will sort movies
    public void sortMovies(String query) { mMovieRepository.sortMovies(query);}
}
