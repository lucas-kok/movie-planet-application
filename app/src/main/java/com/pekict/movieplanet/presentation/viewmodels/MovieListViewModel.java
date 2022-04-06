package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.storage.movie.MovieListRepository;

public class MovieListViewModel extends AndroidViewModel {
    private static final String TAG_NAME = MovieListViewModel.class.getSimpleName();

    private final MovieListRepository mMovieListRepository;
    private final LiveData<MovieList[]> mMovieLists;
    private final LiveData<MovieList> mMovieList;


    public MovieListViewModel(@NonNull Application application) {
        super(application);

        mMovieListRepository = MovieListRepository.getInstance(application);
        mMovieLists = mMovieListRepository.getMovieLists();
        mMovieList = mMovieListRepository.getMovieList();
    }

    public LiveData<MovieList[]> getMovieLists() {
        return mMovieLists;
    }

    public LiveData<MovieList> getMovieList() {
        return mMovieList;
    }

    // Function to fetch the meals
    public void fetchMovieLists() {
        mMovieListRepository.fetchMovieLists();
    }

    public void fetchMovieListById(int id) {
        mMovieListRepository.fetchMovieListById(id);
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
