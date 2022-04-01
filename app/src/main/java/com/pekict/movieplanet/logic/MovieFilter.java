package com.pekict.movieplanet.logic;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.pekict.movieplanet.domain.movie.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MovieFilter {
    private static final String TAG_NAME = MovieFilter.class.getSimpleName();

    public MovieFilter() {
    }

    public static Movie[] getFilteredMovies(Map<String, String> filterOptions, Movie[] movies) {
        List<Movie> filteredMeals = new ArrayList<>();

        for (Movie movie : movies) {
            if (movie.isMatchForFilters(filterOptions)) {
                filteredMeals.add(movie);
            }
        }

        Movie[] result = new Movie[filteredMeals.size()];
        for (int i = 0; i < filteredMeals.size(); i++) {
            result[i] = filteredMeals.get(i);
        }

        Log.d(TAG_NAME, "Meals Filtered: " + filteredMeals.size());
        return result;
    }
}
