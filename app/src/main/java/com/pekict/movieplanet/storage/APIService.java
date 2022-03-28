package com.pekict.movieplanet.storage;

import com.pekict.movieplanet.domain.MovieFetchResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {

    @GET("/3/movie/popular?api_key=c7cc756ca295eabae15bda786602c697")
    Call<MovieFetchResponse> getMovies();
}
