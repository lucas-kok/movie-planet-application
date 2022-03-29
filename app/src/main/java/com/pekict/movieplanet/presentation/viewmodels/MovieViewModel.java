package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.Movie;
import com.pekict.movieplanet.storage.MovieRepository;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private static final String TAG_NAME = MovieViewModel.class.getSimpleName();

    private final MovieRepository mMovieRepository;

    private LiveData<Movie[]> mMovies;
    private int popularMoviePages;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mMovieRepository = MovieRepository.getInstance(application);

        mMovies = mMovieRepository.getMovies();
        popularMoviePages = 1;
    }

    public LiveData<Movie[]> getMovies() {
        return mMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovieRepository.setMovies(movies);
        mMovies = mMovieRepository.getMovies();
    }

    // Function to fetch the meals
    public void fetchMovies(boolean hasInternet) {
        mMovieRepository.fetchMovies(hasInternet, popularMoviePages);
    }

    // Function that will save the current Movies to the SQLite Database
    public void savePopularMoviesToDatabase() {
        mMovieRepository.savePopularMoviesToDatabase();
    }
}
