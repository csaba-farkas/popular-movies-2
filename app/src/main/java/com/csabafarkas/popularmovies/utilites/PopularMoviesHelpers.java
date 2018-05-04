package com.csabafarkas.popularmovies.utilites;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.csabafarkas.popularmovies.data.PopularMoviesDbContract;
import com.csabafarkas.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class PopularMoviesHelpers {

    public static List<Movie> convertMovieCursorToList(@NonNull Cursor data) {
        List<Movie> movies = new ArrayList<>();
        if (!data.moveToFirst()) return movies;

        int _idColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry._ID);
        int titleColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry.MOVIE_TITLE);
        int posterUrlColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry.POSTER_URL);
        int ratingColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry.MOVIE_RATING);
        int releaseDateColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry.RELEASE_DATE);
        int plotColumn = data.getColumnIndex(PopularMoviesDbContract.MovieEntry.MOVIE_PLOT);

        do {
            Movie movie = new Movie();

            movie.setId(data.getLong(_idColumn));
            movie.setTitle(data.getString(titleColumn));
            movie.setPosterPath(data.getString(posterUrlColumn));
            movie.setVoteAverage(data.getDouble(ratingColumn));
            movie.setReleaseDate(data.getString(releaseDateColumn));
            movie.setOverview(data.getString(plotColumn));

            movies.add(movie);
        } while (data.moveToNext());
        data.close();

        return movies;
    }
}
