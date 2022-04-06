package com.pekict.movieplanet.logic;

import android.util.Log;

import com.pekict.movieplanet.domain.movie.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MovieFilter {
    private static final String TAG_NAME = MovieFilter.class.getSimpleName();

    public MovieFilter() {
    }

    public static Movie[] getFilteredMovies(Map<String, String> filterOptions, Movie[] movies) {
        List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : movies) {
            if (movie.isMatchForFilters(filterOptions)) {
                filteredMovies.add(movie);
            }
        }

        Movie[] result = new Movie[filteredMovies.size()];
        for (int i = 0; i < filteredMovies.size(); i++) {
            result[i] = filteredMovies.get(i);
        }

        Log.d(TAG_NAME, "Movies Filtered: " + filteredMovies.size());
        return result;
    }
}
