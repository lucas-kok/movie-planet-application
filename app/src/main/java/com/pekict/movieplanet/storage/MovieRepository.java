package com.pekict.movieplanet.storage;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pekict.movieplanet.domain.Movie;
import com.pekict.movieplanet.domain.MovieFetchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private static final String TAG_NAME = MovieRepository.class.getSimpleName();
    private static volatile MovieRepository instance;

    private static MutableLiveData<Movie[]> mMovies;

    private final MovieDAO mMovieDAO;

    public MovieRepository(Application application) {
        mMovies = new MutableLiveData<>();
        mMovieDAO = MovieDatabase.getInstance(application).getMovieDAO();
    }

    // Get instance of Singleton MovieRepository
    public static MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository(application);
        }
        return instance;
    }

    // Function that will start fetching the Movies based on if the user has internet
    public void fetchMovies(boolean hasInternet, int popularMoviePages) {
        if (hasInternet) {
            new FetchPopularMoviesAPIAsyncTask().execute();
            Log.d(TAG_NAME, "Retrieving Movies from API");
        } else {
            new GetPopularMealsAsyncTask(mMovieDAO).execute();
            Log.d(TAG_NAME, "Retrieving Movies from Database");
        }
    }

    // Function that saves the Movies to the database
    public void savePopularMoviesToDatabase() {
        new UpdatePopularMealsAsyncTask(mMovieDAO).execute(mMovies.getValue());
        Log.d(TAG_NAME, "Meals saved in Database");
    }

    public LiveData<Movie[]> getMovies() {
        return mMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovies.setValue(movies);
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class GetPopularMealsAsyncTask extends AsyncTask<Void, Void, Movie[]> {
        private final MovieDAO mMovieDAO;

        private GetPopularMealsAsyncTask(MovieDAO movieDAO) {
            mMovieDAO = movieDAO;
        }

        @Override
        protected Movie[] doInBackground(Void... voids) {
            return mMovieDAO.getAllPopularMovies();
        }

        protected void onPostExecute(Movie[] result) {
            mMovies.setValue(result);
        }
    }

    // AsyncTask Class that updates all Movies from the SQLite DataBase
    private static class UpdatePopularMealsAsyncTask extends AsyncTask<Movie[], Void, Void> {
        private final MovieDAO mMovieDAO;

        private UpdatePopularMealsAsyncTask(MovieDAO movieDAO) {
            mMovieDAO = movieDAO;
        }

        @Override
        protected Void doInBackground(Movie[]... movies) {
            mMovieDAO.deleteAllPopularMovies();
            mMovieDAO.savePopularMovies(movies[0]);
            return null;
        }
    }

    // AsyncTask Class that will fetch Movies from the API
    private static class FetchPopularMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {

        @Override
        protected MovieFetchResponse doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Call<MovieFetchResponse> call = service.getMovies();
                Response<MovieFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while loading meals");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(MovieFetchResponse result) {
            if (result != null && result.getResult() != null) {
                mMovies.setValue(result.getResult());
                Log.d(TAG_NAME, "onPostExecute found : " + result.getResult().length + " Movies");
            } else {
                Log.e(TAG_NAME, "No meals found!");
            }
        }
    }
}
