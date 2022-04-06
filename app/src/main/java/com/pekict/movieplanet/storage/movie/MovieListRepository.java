package com.pekict.movieplanet.storage.movie;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pekict.movieplanet.domain.movie.MovieList;

public class MovieListRepository {
    private static final String TAG_NAME = MovieListRepository.class.getSimpleName();
    private static volatile MovieListRepository instance;

    private static MutableLiveData<MovieList[]> mMovieLists;
    private static MutableLiveData<MovieList> mMovieList;

    private final MovieListDAO mMovieListDAO;

    public MovieListRepository(Application application) {
        mMovieLists = new MutableLiveData<>();
        mMovieList = new MutableLiveData<>();

        mMovieListDAO = MovieDatabase.getInstance(application).getMovieListDAO();
    }

    // Get instance of Singleton MovieRepository
    public static MovieListRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieListRepository(application);
        }
        return instance;
    }

    // Function that will start fetching the Movies based on if the user has internet
    public void fetchMovieLists() {
        new GetAllMovieListsAsyncTask(mMovieListDAO).execute();
    }

    public void fetchMovieListById(int id) {
        new GetMovieListAsyncTask(mMovieListDAO, id).execute();
    }

    public void addNewList(MovieList newMovieList) {
        new InsertMovieListAsyncTask(mMovieListDAO, newMovieList).execute();
        fetchMovieLists();
    }

    public void updateList(MovieList movieList) {
        new UpdateMoviesAsyncTask(mMovieListDAO, movieList).execute();
        fetchMovieLists();
    }

    public void deleteList(MovieList movieList) {
        new DeleteMovieListAsyncTask(mMovieListDAO, movieList).execute();
        fetchMovieLists();
    }

    public LiveData<MovieList[]> getMovieLists() {
        return mMovieLists;
    }

    public LiveData<MovieList> getMovieList() {
        return mMovieList;
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class GetAllMovieListsAsyncTask extends AsyncTask<Void, Void, MovieList[]> {
        private final MovieListDAO mMovieListDAO;

        private GetAllMovieListsAsyncTask(MovieListDAO movieListDAO) {
            mMovieListDAO = movieListDAO;
        }

        @Override
        protected MovieList[] doInBackground(Void... voids) {
            return mMovieListDAO.getAllMovieLists();
        }

        protected void onPostExecute(MovieList[] result) {
            mMovieLists.setValue(result);
        }
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class GetMovieListAsyncTask extends AsyncTask<Void, Void, MovieList> {
        private final MovieListDAO mMovieListDAO;
        private final int mId;

        private GetMovieListAsyncTask(MovieListDAO movieListDAO, int id) {
            mMovieListDAO = movieListDAO;
            mId = id;
        }

        @Override
        protected MovieList doInBackground(Void... voids) {
            return mMovieListDAO.getMovieList(mId);
        }

        protected void onPostExecute(MovieList result) {
            mMovieList.setValue(result);
        }
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class InsertMovieListAsyncTask extends AsyncTask<Void, Void, Void> {
        private final MovieListDAO mMovieListDAO;
        private final MovieList mNewMovieList;

        private InsertMovieListAsyncTask(MovieListDAO movieListDAO, MovieList newMovieList) {
            mMovieListDAO = movieListDAO;
            mNewMovieList = newMovieList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMovieListDAO.insertMovieLists(mNewMovieList);
            return null;
        }
    }

    // AsyncTask Class that updates all Movies from the SQLite DataBase
    private static class UpdateMoviesAsyncTask extends AsyncTask<Void, Void, Void> {
        private final MovieListDAO mMovieListDAO;
        private final MovieList mMovieList;

        private UpdateMoviesAsyncTask(MovieListDAO movieListDAO, MovieList movieList) {
            mMovieListDAO = movieListDAO;
            mMovieList = movieList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMovieListDAO.deleteMovieList(mMovieList.getId());
            mMovieListDAO.insertMovieLists(mMovieList);
            return null;
        }
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class DeleteMovieListAsyncTask extends AsyncTask<Void, Void, Void> {
        private final MovieListDAO mMovieListDAO;
        private final MovieList mMovieList;

        private DeleteMovieListAsyncTask(MovieListDAO movieListDAO, MovieList movieList) {
            mMovieListDAO = movieListDAO;
            mMovieList = movieList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMovieListDAO.deleteMovieList(mMovieList.getId());
            return null;
        }
    }
}
