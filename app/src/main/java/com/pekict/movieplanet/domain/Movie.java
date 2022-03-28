package com.pekict.movieplanet.domain;

public class Movie {
    private final int id;
    private final String original_title;
    private final String original_language;
    private final String title;
    private final String backdrop_path;
    private final double popularity;
    private final int vote_count;
    private final double vote_average;

    public Movie(int id, String original_title, String original_language, String title, String backdrop_path, double popularity, int vote_count, int vote_average) {
        this.id = id;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    public String getImageURL() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }
}
