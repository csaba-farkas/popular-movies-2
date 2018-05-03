package com.csabafarkas.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMoviesDbContract {

    public static final String AUTHORITY = "com.csabafarkas.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // movies paths
    public static final String MOVIES_PATH = "movies";
    public static final String MOVIE_SINGLE_PATH = "movies/#";

    // trailers paths
    public static final String TRAILERS_PATH = "trailers";
    public static final String TRAILERS_BY_MOVIE_ID_PATH = "trailers/movie_id/#";

    // reviews paths
    public static final String REVIEWS_PATH = "reviews";
    public static final String REVIEWS_BY_MOVIE_ID_PATH = "reviews/movie_id/#";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri MOVIES_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final Uri MOVIES_SINGLE_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_SINGLE_PATH).build();

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String POSTER_URL = "poster_url";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String MOVIE_PLOT = "movie_plot";
        public static final String TIME_ADDED = "time_added";
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri TRAILERS_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TRAILERS_PATH).build();
        public static final Uri TRAILERS_BY_MOVIE_ID_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TRAILERS_BY_MOVIE_ID_PATH).build();

        public static final String TABLE_NAME = "trailers";
        public static final String KEY = "key";
        public static final String NAME = "name";
        public static final String SITE = "site";
        public static final String SIZE = "size";
        public static final String TYPE = "type";
        public static final String MOVIE_ID = "movie_id";
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri REVIEWS_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(REVIEWS_PATH).build();
        public static final Uri REVIEWS_BY_MOVIE_ID_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(REVIEWS_BY_MOVIE_ID_PATH).build();

        public static final String TABLE_NAME = "reviews";
        public static final String AUTHOR = "author";
        public static final String URL = "url";
        public static final String CONTENT  = "content";
        public static final String MOVIE_ID = "movie_id";
    }
}
