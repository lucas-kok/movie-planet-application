package com.pekict.movieplanet.storage.review;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pekict.movieplanet.domain.review.Review;
import com.pekict.movieplanet.domain.review.ReviewFetchResponse;
import com.pekict.movieplanet.storage.APIService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewRepository {
    private static final String TAG_NAME = ReviewRepository.class.getSimpleName();
    private static volatile ReviewRepository instance;

    private static MutableLiveData<Review[]> mReviews;

    public static ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }

    public ReviewRepository() {
        mReviews = new MutableLiveData<>();
    }

    public void fetchReviews(boolean hasInternet, int movieId) {
        if (hasInternet) {
            new ReviewRepository.FetchReviewsAPIAsyncTask(movieId).execute();
            Log.d(TAG_NAME, "Retrieving Reviews from API");
        } else {
            // Todo: Get reviews from SQLite
            Log.d(TAG_NAME, "Retrieving Reviews from Database");
        }
    }

    public LiveData<Review[]> getReviews() {
        return mReviews;
    }

    // AsyncTask Class that will fetch Reviews from the API
    private static class FetchReviewsAPIAsyncTask extends AsyncTask<String, Void, ReviewFetchResponse> {
        private final int mMovieId;

        public FetchReviewsAPIAsyncTask(int movieId) {
            mMovieId = movieId;
        }

        @Override
        protected ReviewFetchResponse doInBackground(String... strings) {
            try {
                String baseUrl = "https://api.themoviedb.org/3/movie/" + mMovieId + "/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Call<ReviewFetchResponse> call = service.getReviews();
                Response<ReviewFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while loading Reviews");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(ReviewFetchResponse result) {
            if (result != null && result.getResults() != null) {
                mReviews.setValue(result.getResults());
                Log.d(TAG_NAME, "onPostExecute found : " + result.getResults().length + " Reviews");
            } else {
                Log.e(TAG_NAME, "No meals found!");
            }
        }
    }

}
