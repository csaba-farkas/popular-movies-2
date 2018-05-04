package com.csabafarkas.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PopularMoviesContentProvider extends ContentProvider {

    // Integer values for UriMatcher
    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int TRAILERS = 200;
    public static final int TRAILERS_BY_MOVIE_ID = 210;
    public static final int REVIEWS = 300;
    public static final int REVIEWS_BY_MOVIE_ID = 310;

    // Helper method which constructs the UriMatcher
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // directory of movies
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.MOVIES_PATH, MOVIES);
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.MOVIE_SINGLE_PATH, MOVIE_WITH_ID);
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.TRAILERS_PATH, TRAILERS);
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.TRAILERS_BY_MOVIE_ID_PATH, TRAILERS_BY_MOVIE_ID);
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.REVIEWS_PATH, REVIEWS);
        uriMatcher.addURI(PopularMoviesDbContract.AUTHORITY, PopularMoviesDbContract.REVIEWS_BY_MOVIE_ID_PATH, REVIEWS_BY_MOVIE_ID);

        return uriMatcher;
    }

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private PopularMoviesDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Cursor ret;
        switch (match) {
            case MOVIES:
                ret = db.query(
                        PopularMoviesDbContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILERS:
                ret = db.query(
                        PopularMoviesDbContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEWS:
                ret = db.query(
                        PopularMoviesDbContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        long id;
        Uri ret;
        switch (match) {
            case MOVIES:
                id = db.insert(PopularMoviesDbContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    ret = ContentUris.withAppendedId(PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case REVIEWS:
                id = db.insert(PopularMoviesDbContract.ReviewEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    ret = ContentUris.withAppendedId(PopularMoviesDbContract.ReviewEntry.REVIEWS_CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRAILERS:
                id = db.insert(PopularMoviesDbContract.TrailerEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    ret = ContentUris.withAppendedId(PopularMoviesDbContract.TrailerEntry.TRAILERS_CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
