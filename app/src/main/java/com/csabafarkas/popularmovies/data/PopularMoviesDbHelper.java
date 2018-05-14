package com.csabafarkas.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 5;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
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
                PopularMoviesDbContract.MovieEntry.MOVIE_PLOT + " TEXT NOT NULL, " +
                PopularMoviesDbContract.MovieEntry.TIME_ADDED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                PopularMoviesDbContract.TrailerEntry.TABLE_NAME + "(" +
                PopularMoviesDbContract.TrailerEntry._ID + " TEXT PRIMARY KEY, " +
                PopularMoviesDbContract.TrailerEntry.KEY + " TEXT NON NULL, " +
                PopularMoviesDbContract.TrailerEntry.SITE + " TEXT NON NULL, " +
                PopularMoviesDbContract.TrailerEntry.NAME + " TEXT NON NULL, " +
                PopularMoviesDbContract.TrailerEntry.SIZE + " TEXT NON NULL, " +
                PopularMoviesDbContract.TrailerEntry.TYPE + " TEXT NON NULL, " +
                PopularMoviesDbContract.TrailerEntry.MOVIE_ID + " INTEGER, " +
                "FOREIGN KEY(" + PopularMoviesDbContract.TrailerEntry.MOVIE_ID + ") REFERENCES " +
                PopularMoviesDbContract.MovieEntry.TABLE_NAME + "(" + PopularMoviesDbContract.MovieEntry._ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(SQL_CREATE_TRAILERS_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                PopularMoviesDbContract.ReviewEntry.TABLE_NAME + "(" +
                PopularMoviesDbContract.ReviewEntry._ID + " TEXT PRIMARY KEY, " +
                PopularMoviesDbContract.ReviewEntry.AUTHOR + " TEXT NON NULL, " +
                PopularMoviesDbContract.ReviewEntry.URL + " TEXT NON NULL, " +
                PopularMoviesDbContract.ReviewEntry.CONTENT + " TEXT NON NULL, " +
                PopularMoviesDbContract.ReviewEntry.MOVIE_ID + " INTEGER, " +
                "FOREIGN KEY(" + PopularMoviesDbContract.ReviewEntry.MOVIE_ID + ") REFERENCES " +
                PopularMoviesDbContract.MovieEntry.TABLE_NAME + "(" + PopularMoviesDbContract.MovieEntry._ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesDbContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesDbContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesDbContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
