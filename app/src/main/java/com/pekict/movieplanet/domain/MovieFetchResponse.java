package com.pekict.movieplanet.domain;

public class MovieFetchResponse {
    private Movie[] results;

    public MovieFetchResponse(Movie[] results) {
        results = results;
    }

    public Movie[] getResult() {
        return results;
    }
}
