package com.pekict.movieplanet.logic;

import com.pekict.movieplanet.domain.trailer.Trailer;

public class TrailerFilter {

    public TrailerFilter() {

    }

    // Function that will return the best matching Trailer for in the application
    public static Trailer getBestTrailer(Trailer[] trailers) {
        if (trailers == null || trailers.length == 0) return null;

        for (Trailer trailer : trailers) {
            if (trailer.isMatchForApplication()) return trailer;
        }

        // Returning the first Trailer when no official Trailers are available
        return trailers[0];
    }
}
