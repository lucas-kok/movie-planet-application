package com.pekict.movieplanet.storage;

import com.pekict.movieplanet.domain.movie.MovieFetchResponse;
import com.pekict.movieplanet.domain.review.ReviewFetchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface APIService {

    @GET("/3/movie/popular?api_key=c7cc756ca295eabae15bda786602c697")
    Call<MovieFetchResponse> getMovies();

    @GET("reviews?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&page=1")
    Call<ReviewFetchResponse> getReviews();
}
