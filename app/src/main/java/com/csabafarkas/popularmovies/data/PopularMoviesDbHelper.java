package com.csabafarkas.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 1;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE = "CREATE TABLE " +
                PopularMoviesDbContract.MovieEntry.TABLE_NAME + "( " +
                PopularMoviesDbContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                PopularMoviesDbContract.MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                PopularMoviesDbContract.MovieEntry.POSTER_URL + " TEXT NOT NULL, " +
                PopularMoviesDbContract.MovieEntry.MOVIE_RATING + " REAL NOT NULL, " +
                PopularMoviesDbContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMoviesDbContract.MovieEntry.MOVIE_PLOT + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesDbContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
