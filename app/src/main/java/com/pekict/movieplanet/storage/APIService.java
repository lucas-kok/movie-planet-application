package com.pekict.movieplanet.storage;

import com.pekict.movieplanet.domain.movie.MovieFetchResponse;
import com.pekict.movieplanet.domain.review.ReviewFetchResponse;
import com.pekict.movieplanet.domain.trailer.TrailerFetchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface APIService {

    @GET
    Call<MovieFetchResponse> getMovies(@Url String url);

    @GET("reviews?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&page=1")
    Call<ReviewFetchResponse> getReviews();

    @GET("videos?api_key=c7cc756ca295eabae15bda786602c697")
    Call<TrailerFetchResponse> getTrailers();

    @GET()
    Call<MovieFetchResponse> searchMovies(@Url String url);
}
