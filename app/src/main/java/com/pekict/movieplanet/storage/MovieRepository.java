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

    public MovieRepository() {
        mMovies = new MutableLiveData<>();
    }

    // Get instance of Singleton MovieRepository
    public static MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    // Function that will start fetching the Meals based on if the user has internet
    public void fetchMeals(boolean hasInternet) {
        if (hasInternet) {
            new FetchMoviesAPIAsyncTask().execute();
            Log.d(TAG_NAME, "Retrieving Movies from API");
        } else {
            // Todo: Get meals from Room Database
//            new GetMealsAsyncTask(mMealDAO).execute();
            Log.d(TAG_NAME, "Retrieving Movies from Database");
        }
    }

    public LiveData<Movie[]> getMovies() {
        return mMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovies.setValue(movies);
    }

    // AsyncTask Class that will fetch Meals from the API
    private static class FetchMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {

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
