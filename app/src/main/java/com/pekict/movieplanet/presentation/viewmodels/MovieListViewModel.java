package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.storage.movie.MovieListRepository;
import com.pekict.movieplanet.storage.movie.MovieRepository;

public class MovieListViewModel extends AndroidViewModel {
    private static final String TAG_NAME = MovieListViewModel.class.getSimpleName();

    private final MovieListRepository mMovieListRepository;
    private LiveData<MovieList[]> mMovieLists;


    public MovieListViewModel(@NonNull Application application) {
        super(application);

        mMovieListRepository = MovieListRepository.getInstance(application);
        mMovieLists = mMovieListRepository.getMovieLists();
    }

    public LiveData<MovieList[]> getMovieLists() {
        return mMovieLists;
    }

    // Function to fetch the meals
    public void fetchMovieLists() {
        mMovieListRepository.fetchMovieLists();
    }

    public void addNewList(MovieList newMovieList) {
        mMovieListRepository.addNewList(newMovieList);
    }

    public void updateList(MovieList movieList) {
        mMovieListRepository.updateList(movieList);
    }

    public void deleteList(MovieList movieList) {
        mMovieListRepository.deleteList(movieList);
    }
}
