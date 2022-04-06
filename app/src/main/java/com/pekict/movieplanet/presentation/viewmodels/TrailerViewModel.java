package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.trailer.Trailer;
import com.pekict.movieplanet.storage.movie.MovieRepository;
import com.pekict.movieplanet.storage.trailer.TrailerRepository;

public class TrailerViewModel extends AndroidViewModel {
    private static final String TAG_NAME = TrailerViewModel.class.getSimpleName();

    private final TrailerRepository mTrailerRepository;

    private final LiveData<Trailer[]> mTrailers;

    public TrailerViewModel(@NonNull Application application) {
        super(application);

        mTrailerRepository = TrailerRepository.getInstance();
        mTrailers = mTrailerRepository.getTrailers();
    }

    public LiveData<Trailer[]> getTrailers() {
        return mTrailers;
    }

    // Function to fetch the Trailers
    public void fetchTrailers(int movieId) {
        mTrailerRepository.fetchTrailers(movieId);
    }
}
