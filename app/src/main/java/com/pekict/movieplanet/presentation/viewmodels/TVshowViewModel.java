package com.pekict.movieplanet.presentation.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.tvshow.TVshow;
import com.pekict.movieplanet.storage.tvshow.TVshowRepository;

public class TVshowViewModel extends AndroidViewModel {
    private static final String TAG_NAME = TVshowViewModel.class.getSimpleName();

    private final TVshowRepository mTVshowRepository;
    private final LiveData<TVshow[]> mTVshows;

    public TVshowViewModel(@NonNull Application application) {
        super(application);

        mTVshowRepository = TVshowRepository.getInstance();
        mTVshows = mTVshowRepository.getmTVshows();
    }

    public LiveData<TVshow[]> getTVshows() {
        return mTVshows;
    }

    public void fetchTVshows(String query) {
        mTVshowRepository.fetchTVshows(query);
    }
}

