package com.csabafarkas.popularmovies.utilites;

import android.support.annotation.NonNull;

import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.MovieCollection;
import com.csabafarkas.popularmovies.models.RetrofitError;
import com.csabafarkas.popularmovies.models.ReviewCollection;
import com.csabafarkas.popularmovies.models.TrailerCollection;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lastminute84 on 10/03/18.
 */

public final class NetworkUtils {

    private enum SortType {
        MOST_POPULAR,
        TOP_RATED
    }

    private static final String BASE_URL = "https://api.themoviedb.org/";

    private static MovieDbService getMovieDbService() {
        return RetrofitClient.getClient(BASE_URL).create(MovieDbService.class);
    }

    public static void getMovie(final String apiKey, final String movieId, final PopularMoviesNetworkCallback callback) {
        final MovieDbService movieDbService = getMovieDbService();

        movieDbService.getMovie(movieId, apiKey)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                        if (response.isSuccessful()) {
                            // callback.onSuccess(response.body());
                            final Movie movie = response.body();
                            movieDbService.getTrailers(movieId, apiKey)
                                    .enqueue(new Callback<TrailerCollection>() {
                                        @Override
                                        public void onResponse(Call<TrailerCollection> call, final Response<TrailerCollection> response) {
                                            if (response.isSuccessful()) {
                                                movie.setTrailers(response.body().getResults());

                                                movieDbService.getReviews(movieId, apiKey)
                                                        .enqueue(new Callback<ReviewCollection>() {

                                                            @Override
                                                            public void onResponse(Call<ReviewCollection> call, final Response<ReviewCollection> response) {
                                                                if (response.isSuccessful()) {
                                                                    movie.setReviews(response.body().getResults());
                                                                    callback.onSuccess(movie);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<ReviewCollection> call, Throwable t) {
                                                                Gson gson = new Gson();
                                                                if (response.errorBody() == null) {
                                                                    callback.onFailure(new RetrofitError());
                                                                }
                                                                RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                                                                callback.onFailure(error);

                                                            }
                                                        });
                                            } else {
                                                Gson gson = new Gson();
                                                if (response.errorBody() == null) {
                                                    callback.onFailure(new RetrofitError());
                                                }
                                                RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                                                callback.onFailure(error);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<TrailerCollection> call, Throwable t) {
                                            callback.onError(t);
                                        }
                                    });
                        } else {
                            Gson gson = new Gson();
                            if (response.errorBody() == null) {
                                callback.onFailure(new RetrofitError());
                            }
                            RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                            callback.onFailure(error);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public static void getTrailers(String apiKey, String movieId, final PopularMoviesNetworkCallback callback) {
        MovieDbService movieDbService = getMovieDbService();

        movieDbService.getTrailers(movieId, apiKey)
                .enqueue(new Callback<TrailerCollection>() {
                    @Override
                    public void onResponse(Call<TrailerCollection> call, Response<TrailerCollection> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(response.body());
                        } else {
                            Gson gson = new Gson();
                            if (response.errorBody() == null) {
                                callback.onFailure(new RetrofitError());
                            }
                            RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                            callback.onFailure(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<TrailerCollection> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public static void getMostPopularMovies(String apiKey, int pageNumber, final PopularMoviesNetworkCallback callback) {
        fetchMovies(apiKey, pageNumber, callback, SortType.MOST_POPULAR);
    }

    public static void getTopRatedMovies(String apiKey, int pageNumber, final PopularMoviesNetworkCallback callback) {
        fetchMovies(apiKey, pageNumber, callback, SortType.TOP_RATED);
    }

    private static void fetchMovies(String apiKey, int pageNumber, final PopularMoviesNetworkCallback callback, SortType sortType) {
        MovieDbService movieDbService = getMovieDbService();

        switch (sortType) {
            case TOP_RATED:
                movieDbService.getTopRatedMovies(apiKey, pageNumber)
                        .enqueue(new Callback<MovieCollection>() {
                            @Override
                            public void onResponse(@NonNull Call<MovieCollection> call, @NonNull retrofit2.Response<MovieCollection> response) {

                                if (response.isSuccessful()) {
                                    callback.onSuccess(response.body());
                                } else {
                                    Gson gson = new Gson();
                                    if (response.errorBody() == null) {
                                        callback.onFailure(new RetrofitError());
                                    }
                                    RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                                    callback.onFailure(error);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MovieCollection> call, @NonNull Throwable t) {
                                callback.onError(t);
                            }
                        });
                break;
            case MOST_POPULAR:
                movieDbService.getMostPopularMovies(apiKey, pageNumber)
                        .enqueue(new Callback<MovieCollection>() {
                            @Override
                            public void onResponse(@NonNull Call<MovieCollection> call, @NonNull retrofit2.Response<MovieCollection> response) {

                                if (response.isSuccessful()) {
                                    callback.onSuccess(response.body());
                                } else {
                                    Gson gson = new Gson();
                                    if (response.errorBody() == null) {
                                        callback.onFailure(new RetrofitError());
                                    }
                                    RetrofitError error = gson.fromJson(response.errorBody().charStream(), RetrofitError.class);
                                    callback.onFailure(error);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MovieCollection> call, @NonNull Throwable t) {
                                callback.onError(t);
                            }
                        });
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

}
