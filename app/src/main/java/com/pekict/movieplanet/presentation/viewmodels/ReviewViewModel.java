package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.review.Review;
import com.pekict.movieplanet.storage.review.ReviewRepository;

public class ReviewViewModel extends AndroidViewModel {
    private static final String TAG_NAME = ReviewViewModel.class.getSimpleName();

    private ReviewRepository mReviewRepository;
    private LiveData<Review[]> mReviews;

    public ReviewViewModel(@NonNull Application application) {
        super(application);

        mReviewRepository = ReviewRepository.getInstance(application);
        mReviews = mReviewRepository.getReviews();
    }

    public LiveData<Review[]> getReviews() {
        return mReviews;
    }

    public void fetchReviews(boolean hasInternet, int movieId) {
        mReviewRepository.fetchReviews(hasInternet, movieId);
    }
}
