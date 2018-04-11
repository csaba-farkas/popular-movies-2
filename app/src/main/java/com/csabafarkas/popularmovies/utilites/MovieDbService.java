package com.csabafarkas.popularmovies.utilites;

import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.MovieCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lastminute84 on 10/03/18.
 */

public interface MovieDbService {

    @GET("/3/movie/popular")
    Call<MovieCollection> getMostPopularMovies(@Query("api_key") String apiKey, @Query("page") int pageNumber);

    @GET("/3/movie/top_rated")
    Call<MovieCollection> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") int pageNumber);

    @GET("/3/movie/{id}")
    Call<Movie> getMovie(@Path(value = "id", encoded = true) String movieId, @Query("api_key") String apiKey);
}
