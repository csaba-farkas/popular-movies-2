package com.csabafarkas.popularmovies.data;

import android.provider.BaseColumns;

public class PopularMoviesDbContract {

    public static final class FavouriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourite_movies";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String POSTER_URL = "poster_url";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String MOVIE_PLOT = "movie_plot";
    }
}
