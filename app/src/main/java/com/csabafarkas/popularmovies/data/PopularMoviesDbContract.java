package com.csabafarkas.popularmovies.data;

import android.provider.BaseColumns;

public class PopularMoviesDbContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String POSTER_URL = "poster_url";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String MOVIE_PLOT = "movie_plot";
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailers";
        public static final String KEY = "key";
        public static final String NAME = "name";
        public static final String SITE = "site";
        public static final String SIZE = "size";
        public static final String TYPE = "type";
        public static final String MOVIE_ID = "movie_id";
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String AUTHOR = "author";
        public static final String URL = "url";
        public static final String MOVIE_ID = "movie_id";
    }
}
