package com.pekict.movieplanet.storage.tvshow;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pekict.movieplanet.domain.tvshow.TVshow;
import com.pekict.movieplanet.domain.tvshow.TVshowFetchResponse;
import com.pekict.movieplanet.storage.APIService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TVshowRepository {
    private static final String TAG_NAME = TVshowRepository.class.getSimpleName();
    private static volatile TVshowRepository instance;

    private static MutableLiveData<TVshow[]> mTVshows;

    public static TVshowRepository getInstance() {
        if (instance == null) {
            instance = new TVshowRepository();
        }
        return instance;
    }

    public TVshowRepository() {
        mTVshows = new MutableLiveData<>();
    }

    public void fetchTVshows(String query) {
        if (!query.equals("")) {
            //new ReviewRepository.FetchReviewsAPIAsyncTask(movieId).execute();
            new TVshowRepository.FetchTVshowsAPIAsyncTask(query).execute();
            Log.d(TAG_NAME, "Retrieving TV Shows from API");
        } else {
            // Todo: Get reviews from SQLite
            Log.d(TAG_NAME, "Retrieving TV Shows from Database");
        }
    }

    public MutableLiveData<TVshow[]> getmTVshows() {
        return mTVshows;
    }

    // AsyncTask Class that will fetch Reviews from the API
    private static class FetchTVshowsAPIAsyncTask extends AsyncTask<String, Void, TVshowFetchResponse> {
        private final String query;

        public FetchTVshowsAPIAsyncTask(String query) {
            this.query = query;
        }

        @Override
        protected TVshowFetchResponse doInBackground(String... strings) {
            try {
                String baseUrl = "https://api.themoviedb.org/3/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Uri queryUri = Uri.parse(query);

                Call<TVshowFetchResponse> call = service.getTVshows("search/tv?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&page=1&query=" + queryUri);
                Response<TVshowFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while loading TV Shows");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(TVshowFetchResponse result) {
            if (result != null && result.getResults() != null) {
                mTVshows.setValue(result.getResults());
                Log.d(TAG_NAME, "onPostExecute found : " + result.getResults().length + " TV Shows");
            } else {
                Log.e(TAG_NAME, "No TV Shows found!");
            }
        }
    }
}

