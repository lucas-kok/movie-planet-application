package com.pekict.movieplanet.storage.trailer;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pekict.movieplanet.domain.trailer.Trailer;
import com.pekict.movieplanet.domain.trailer.TrailerFetchResponse;
import com.pekict.movieplanet.storage.APIService;
import com.pekict.movieplanet.storage.movie.MovieDAO;
import com.pekict.movieplanet.storage.movie.MovieDatabase;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrailerRepository {
    private static final String TAG_NAME = TrailerRepository.class.getSimpleName();
    private static volatile TrailerRepository instance;

    private static MutableLiveData<Trailer[]> mTrailers;

    private static MovieDAO mMovieDAO;

    public TrailerRepository(Application application) {
        mTrailers = new MutableLiveData<>();
        mMovieDAO = MovieDatabase.getInstance(application).getMovieDAO();
    }

    // Get instance of Singleton TrailerRepository
    public static TrailerRepository getInstance(Application application) {
        if (instance == null) {
            instance = new TrailerRepository(application);
        }
        return instance;
    }

    // Function that will start fetching the Trailers
    public void fetchTrailers(int movieId) {
        mTrailers.setValue(null);
        new FetchTrailersAPIAsyncTask(movieId).execute();
        Log.d(TAG_NAME, "Retrieving Movies from API");
    }

    public LiveData<Trailer[]> getTrailers() {
        return mTrailers;
    }

    // AsyncTask Class that will fetch Movies from the API
    private static class FetchTrailersAPIAsyncTask extends AsyncTask<String, Void, TrailerFetchResponse> {
        private int mMovieId;

        public FetchTrailersAPIAsyncTask(int movieId) {
            mMovieId = movieId;
        }


        @Override
        protected TrailerFetchResponse doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/movie/" + mMovieId + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Call<TrailerFetchResponse> call = service.getTrailers();
                Response<TrailerFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while loading Trailers");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(TrailerFetchResponse result) {
            if (result != null && result.getResults() != null) {
                mTrailers.setValue(result.getResults());
            } else {
                Log.e(TAG_NAME, "No meals found!");
            }
        }
    }
}
