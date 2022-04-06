package com.pekict.movieplanet.domain.trailer;

public class TrailerFetchResponse {
    private final Trailer[] results;

    public TrailerFetchResponse(Trailer[] results) {
        this.results = results;
    }

    public Trailer[] getResults() {
        return results;
    }
}
