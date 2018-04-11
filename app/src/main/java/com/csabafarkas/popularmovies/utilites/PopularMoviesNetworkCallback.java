package com.csabafarkas.popularmovies.utilites;

import com.csabafarkas.popularmovies.models.PopularMoviesModel;
import com.csabafarkas.popularmovies.models.RetrofitError;

public interface PopularMoviesNetworkCallback {
    void onSuccess(PopularMoviesModel movie);
    void onFailure(RetrofitError error);
    void onError(Throwable t);
}
