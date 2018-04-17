package com.csabafarkas.popularmovies.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(@NonNull SQLiteDatabase db) {
        List<ContentValues> list = new ArrayList<>();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMoviesDbContract.MovieEntry._ID, 1386);
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_PLOT,
                "Believing they have left behind shadowy figures from their past, newlyweds Christian and Ana fully embrace an inextricable connection and shared life of luxury. But just as she steps into her role as Mrs. Grey and he relaxes into an unfamiliar stability, new threats could jeopardize their happy ending before it even begins.");
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_RATING, 6.1);
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_TITLE, "Fifty Shades Freed");
        contentValues.put(PopularMoviesDbContract.MovieEntry.POSTER_URL, "/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg");
        contentValues.put(PopularMoviesDbContract.MovieEntry.RELEASE_DATE, "2018-02-07");

        list.add(contentValues);

        contentValues = new ContentValues();
        contentValues.put(PopularMoviesDbContract.MovieEntry._ID, 269149);
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_PLOT, "Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, jumps at the chance to crack her first case - even if it means partnering with scam-artist fox Nick Wilde to solve the mystery.");
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_RATING, 7.7);
        contentValues.put(PopularMoviesDbContract.MovieEntry.MOVIE_TITLE, "Zootopia");
        contentValues.put(PopularMoviesDbContract.MovieEntry.POSTER_URL, "/sM33SANp9z6rXW8Itn7NnG1GOEs.jpg");
        contentValues.put(PopularMoviesDbContract.MovieEntry.RELEASE_DATE, "2016-02-11");

        list.add(contentValues);

        try {
            db.beginTransaction();
            db.delete(PopularMoviesDbContract.MovieEntry.TABLE_NAME, null, null);
            for (ContentValues cv : list) {
                db.insert(PopularMoviesDbContract.MovieEntry.TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } catch (SQLException sqlEx) {
            // OMFG
        } finally {
            db.endTransaction();
        }


    }
}
