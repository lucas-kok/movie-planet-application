package com.pekict.movieplanet.domain.review;

public class ReviewFetchResponse {
    private Review[] results;

    public ReviewFetchResponse(Review[] results) {
        this.results = results;
    }

    public Review[] getResults() {
        return results;
    }
}
