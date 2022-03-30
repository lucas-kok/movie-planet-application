package com.pekict.movieplanet.domain.movie;

public class MovieFetchResponse {
    private Movie[] results;

    public MovieFetchResponse(Movie[] results) {
        this.results = results;
    }

    public Movie[] getResults() {
        return results;
    }
}
