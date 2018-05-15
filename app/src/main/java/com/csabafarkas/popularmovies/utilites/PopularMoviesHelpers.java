package com.csabafarkas.popularmovies.utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.csabafarkas.popularmovies.MainActivity;
import com.csabafarkas.popularmovies.data.PopularMoviesDbContract;
import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.Review;
import com.csabafarkas.popularmovies.models.Trailer;

import java.util.ArrayList;
import java.util.List;

public class PopularMoviesHelpers {

    public static ArrayList<Movie> convertMovieCursorToList(@NonNull Cursor data) {
        ArrayList<Movie> movies = new ArrayList<>();
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

        return movies;
    }

    public static List<Trailer> convertTrailersCursorToList(@NonNull Cursor data) {
        List<Trailer> trailers = new ArrayList<>();
        if (!data.moveToFirst()) return trailers;

        int _idColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry._ID);
        int keyColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry.KEY);
        int siteColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry.SITE);
        int nameColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry.NAME);
        int sizeColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry.SIZE);
        int typeColumn = data.getColumnIndex(PopularMoviesDbContract.TrailerEntry.TYPE);

        do {
            Trailer trailer = new Trailer();
            trailer.setId(data.getString(_idColumn));
            trailer.setKey(data.getString(keyColumn));
            trailer.setSite(data.getString(siteColumn));
            trailer.setName(data.getString(nameColumn));
            trailer.setSize(data.getInt(sizeColumn));
            trailer.setType(data.getString(typeColumn));

            trailers.add(trailer);
        } while (data.moveToNext());

        return trailers;
    }

    public static List<Review> convertReviewsCursorToList(@NonNull Cursor data) {
        List<Review> reviews = new ArrayList<>();
        if (!data.moveToFirst()) return reviews;

        int _idColumn = data.getColumnIndex(PopularMoviesDbContract.ReviewEntry._ID);
        int authorColumn = data.getColumnIndex(PopularMoviesDbContract.ReviewEntry.AUTHOR);
        int contentColumn = data.getColumnIndex(PopularMoviesDbContract.ReviewEntry.CONTENT);
        int urlColumn = data.getColumnIndex(PopularMoviesDbContract.ReviewEntry.URL);

        do {
            Review review = new Review();

            review.setId(data.getString(_idColumn));
            review.setAuthor(data.getString(authorColumn));
            review.setContent(data.getString(contentColumn));
            review.setUrl(data.getString(urlColumn));

            reviews.add(review);
        } while (data.moveToNext());

        return reviews;

    }

    public static int getSharedPrefInt(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, -1);
    }

    public static void setSharedPrefInt(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 185);
    }
}
