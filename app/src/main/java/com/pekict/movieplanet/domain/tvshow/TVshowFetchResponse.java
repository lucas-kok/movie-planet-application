package com.pekict.movieplanet.domain.tvshow;

public class TVshowFetchResponse  {
    private TVshow[] results;

    public TVshowFetchResponse(TVshow[] results) {
        this.results = results;
    }

    public TVshow[] getResults() {
        return results;
    }
}
