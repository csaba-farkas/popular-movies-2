package com.csabafarkas.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.csabafarkas.popularmovies.adapters.ReviewAdapter;
import com.csabafarkas.popularmovies.adapters.TrailerAdapter;
import com.csabafarkas.popularmovies.data.PopularMoviesDbContract;
import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.PopularMoviesModel;
import com.csabafarkas.popularmovies.models.RetrofitError;
import com.csabafarkas.popularmovies.models.Review;
import com.csabafarkas.popularmovies.models.Trailer;
import com.csabafarkas.popularmovies.utilites.NetworkUtils;
import com.csabafarkas.popularmovies.utilites.PopularMoviesHelpers;
import com.csabafarkas.popularmovies.utilites.PopularMoviesNetworkCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity
            implements PopularMoviesNetworkCallback, TrailerAdapter.TrailerAdapterOnClickListener,
                    ReviewAdapter.ReviewItemOnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TRAILERS_LOADER_ID = 1;
    private static final int REVIEWS_LOADER_ID = 2;
    private static final int MOVIE_LOADER_ID = 3;

    @BindView(R.id.movie_details_poster_iv)
    ImageView posterImageView;
    @BindView(R.id.movie_details_rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.movie_details_release_date_tv)
    TextView releaseDateTv;
    @BindView(R.id.movie_details_plot_tv)
    TextView plotTv;
    @BindView(R.id.movie_details_rating_tv)
    TextView ratingTv;
    @BindView(R.id.main_toolbar)
    Toolbar titleToolbar;
    @BindView(R.id.movie_details_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.main_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.movie_details_trailers_list)
    RecyclerView trailersList;
    @BindView(R.id.movie_details_reviews_list)
    RecyclerView reviewsList;
    @BindView(R.id.movie_details_constraint_layout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.movie_details_favourite_button)
    ImageButton favButton;
    @BindString(R.string.youtube_trailer_url)
    String youtubeBaseUrl;
    @BindString(R.string.movie_id_key)
    String movieIdKey;
    @BindString(R.string.movie_key)
    String movieKey;
    @BindString(R.string.favourite_tag)
    String favTag;
    @BindDrawable(R.drawable.ic_heart_red)
    Drawable redHeartDrawable;
    @BindDrawable(R.drawable.ic_heart_white)
    Drawable whiteHeartDrawable;
    private String movieId;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        if (getIntent() == null) return;

        // get the movie id from the intent
        if (getIntent().hasExtra(movieIdKey)) {
            movieId = getIntent().getStringExtra(movieIdKey);
            if (movieId == null) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
                finish();
            }

            NetworkUtils.getMovie(BuildConfig.MovieDbApiKey, movieId, this);
            favButton.setTag("");
        } else if (getIntent().hasExtra(movieKey)) {
            movie = getIntent().getParcelableExtra(movieKey);
            Bundle args = new Bundle();
            args.putLong(movieIdKey, movie.getId());
            getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, args, this);
            favButton.setImageResource(R.drawable.ic_heart_red);
            favButton.setTag(favTag);
        }

        Bundle args = new Bundle();
        args.putString(movieIdKey, movieId);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, args, this);
    }

    @Override
    public void onSuccess(PopularMoviesModel movie) {
        if (!(movie instanceof Movie)) return;
        this.movie = (Movie) movie;
        updateUI();
    }

    @Override
    public void onFailure(RetrofitError error) {
        AlertDialog alertDialog = new AlertDialog.Builder(MovieDetailsActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.failure_alert_dialog_title));
        String message = getResources().getString(R.string.failure_alert_dialog_message) + error.getErrorMessage();
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onError(Throwable t) {
        AlertDialog alertDialog = new AlertDialog.Builder(MovieDetailsActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.error_alert_dialog_title));
        String message = getResources().getString(R.string.error_alert_dialog_message) + t.getMessage();
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void updateUI() {
        String baseUrl = getResources().getString(R.string.poster_base_url_185);
        Picasso.with(this)
                .load(String.format(baseUrl, movie.getPosterPath()))
                .placeholder(R.drawable.ic_icon_img_placeholder)
                .into(posterImageView);

        plotTv.setText(movie.getOverview());
        String averageVote = movie.getVoteAverage().toString();
        averageVote += "/10";
        ratingTv.setText(averageVote);
        releaseDateTv.setText(movie.getReleaseDate());

        // get year from release date
        String year = movie.getReleaseDate().substring(0, 4);
        String title = String.format(movie.getTitle() + " (%s)", year);
        titleToolbar.setTitle(title);

        // set trailers adapter
        LinearLayoutManager trailersLayoutManger =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        trailersList.setLayoutManager(trailersLayoutManger);
        TrailerAdapter trailerAdapter;

        // add trailers to list adapter - max number of trailers = 5
        if (movie.getTrailers() == null)
            movie.setTrailers(new ArrayList<Trailer>());
        if (movie.getTrailers().size() <= 5)
            trailerAdapter = new TrailerAdapter(this, movie.getTrailers(), this);
        else
            trailerAdapter = new TrailerAdapter(this, movie.getTrailers().subList(0, 5), this);
        trailersList.setAdapter(trailerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                LinearLayoutManager.HORIZONTAL
        );
        trailersList.addItemDecoration(dividerItemDecoration);

        // set reviews adapter
        LinearLayoutManager reviewsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        reviewsList.setLayoutManager(reviewsLayoutManager);
        ReviewAdapter reviewAdapter;

        // add reviews to lis adapter - max number of reviews = 5
        if (movie.getReviews() == null)
            movie.setReviews(new ArrayList<Review>());

        if (movie.getReviews().size() <= 20)
            reviewAdapter = new ReviewAdapter(this, movie.getReviews(), this);
        else
            reviewAdapter = new ReviewAdapter(this, movie.getReviews().subList(0, 20), this);
        reviewsList.setAdapter(reviewAdapter);

        reviewsList.addItemDecoration(dividerItemDecoration);

        adjustConstraintLayoutToView(reviewsList);
        progressBar.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
    }

    private void adjustConstraintLayoutToView(final View view) {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (constraintLayout.getMeasuredHeight() > 0) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    constraintLayout.getLayoutParams().height = location[1];
                    constraintLayout.requestLayout();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void onClickTrailerItem(String videoId) {
        String trailerUrl = String.format(this.youtubeBaseUrl, videoId);
        Uri uri = Uri.parse(trailerUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));

    }

    @Override
    public void onReviewItemClick(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void onFavouriteButtonClicked(View view) {

        if (favButton.getTag() == null) {
            insertMovieIntoDatabase();
            return;
        }

        if (!favButton.getTag().equals(favTag)) {
            insertMovieIntoDatabase();
        } else {
            deleteMovieFromDatabase();
        }
    }

    private void insertMovieIntoDatabase() {
        ContentValues cv = new ContentValues();

        cv.put(PopularMoviesDbContract.MovieEntry._ID, movie.getId());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_TITLE, movie.getTitle());
        cv.put(PopularMoviesDbContract.MovieEntry.POSTER_URL, movie.getPosterPath());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_RATING, movie.getVoteAverage());
        cv.put(PopularMoviesDbContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_PLOT, movie.getOverview());

        Uri uri = getContentResolver().insert(PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI, cv);

        if (uri != null) {
            Toast.makeText(getBaseContext(), String.format(getResources().getString(R.string.success_on_insert), movie.getTitle()), Toast.LENGTH_LONG).show();
        }

        //insert reviews
        if (movie.getReviews().size() > 0) {
            cv.clear();

            for (Review review : movie.getReviews()) {
                cv.put(PopularMoviesDbContract.ReviewEntry._ID, review.getId());
                cv.put(PopularMoviesDbContract.ReviewEntry.AUTHOR, review.getAuthor());
                cv.put(PopularMoviesDbContract.ReviewEntry.CONTENT, review.getContent());
                cv.put(PopularMoviesDbContract.ReviewEntry.URL, review.getUrl());
                cv.put(PopularMoviesDbContract.ReviewEntry.MOVIE_ID, movie.getId());

                getContentResolver().insert(PopularMoviesDbContract.ReviewEntry.REVIEWS_CONTENT_URI, cv);
            }
        }

        // insert trailers
        if (movie.getTrailers().size() > 0) {
            cv.clear();

            for (Trailer trailer : movie.getTrailers()) {
                cv.put(PopularMoviesDbContract.TrailerEntry._ID, trailer.getId());
                cv.put(PopularMoviesDbContract.TrailerEntry.KEY, trailer.getKey());
                cv.put(PopularMoviesDbContract.TrailerEntry.SITE, trailer.getSite());
                cv.put(PopularMoviesDbContract.TrailerEntry.NAME, trailer.getName());
                cv.put(PopularMoviesDbContract.TrailerEntry.SIZE, trailer.getSize());
                cv.put(PopularMoviesDbContract.TrailerEntry.TYPE, trailer.getType());
                cv.put(PopularMoviesDbContract.TrailerEntry.MOVIE_ID, movie.getId());

                getContentResolver().insert(PopularMoviesDbContract.TrailerEntry.TRAILERS_CONTENT_URI, cv);
            }
        }

        favButton.setImageResource(R.drawable.ic_heart_red);
        favButton.setTag(favTag);
    }

    private void deleteMovieFromDatabase() {
        String id = String.valueOf(movie.getId());
        String movieTitle = movie.getTitle();
        Uri uri = PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .build();

        int deletedRows = getContentResolver().delete(uri, "_id = ?", new String[] { id });

        if (deletedRows > 0) {
            Toast.makeText(this, String.format(getResources().getString(R.string.success_on_delete), movieTitle), Toast.LENGTH_SHORT).show();
        }

        favButton.setImageResource(R.drawable.ic_heart_white);
        favButton.setTag("");
    }
    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable final Bundle args) {
        final int loaderId = id;
        return new CursorLoader(this) {

            Cursor cursor = null;

            @Override
            protected void onStartLoading() {
                if (cursor == null) {
                    forceLoad();
                } else {
                    deliverResult(cursor);
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                switch (loaderId) {
                    case TRAILERS_LOADER_ID:
                        return getContentResolver().query(
                                PopularMoviesDbContract.TrailerEntry.TRAILERS_CONTENT_URI,
                                null,
                                PopularMoviesDbContract.TrailerEntry.MOVIE_ID + " = ?",
                                new String[] { args.getLong(movieIdKey) + ""},
                                PopularMoviesDbContract.TrailerEntry.NAME
                        );
                    case REVIEWS_LOADER_ID:
                        return getContentResolver().query(
                                PopularMoviesDbContract.ReviewEntry.REVIEWS_CONTENT_URI,
                                null,
                                PopularMoviesDbContract.ReviewEntry.MOVIE_ID + " = ?",
                                new String[] { args.getLong(movieIdKey) + "" },
                                null
                        );
                    case MOVIE_LOADER_ID:
                        String movieId;
                        if ( (movieId = args.getString(movieIdKey)) == null) return null;
                        Uri movieUri = PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI
                                .buildUpon()
                                .appendPath(movieId)
                                .build();
                        return getContentResolver().query(movieUri,
                                null,
                                null,
                                null,
                                null);
                    default:
                        throw new UnsupportedOperationException("Failed to find loader with loader id: " + loaderId);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case TRAILERS_LOADER_ID:
                movie.setTrailers(PopularMoviesHelpers.convertTrailersCursorToList(data));
                Bundle args = new Bundle();
                args.putLong(movieIdKey, movie.getId());
                getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, args, this);
                break;
            case REVIEWS_LOADER_ID:
                movie.setReviews(PopularMoviesHelpers.convertReviewsCursorToList(data));
                updateUI();
                break;
            case MOVIE_LOADER_ID:
                if (data == null) break;
                if (PopularMoviesHelpers.convertMovieCursorToList(data).size() > 0) {
                    favButton.setImageResource(R.drawable.ic_heart_red);
                    favButton.setTag(favTag);
                } else {
                    favButton.setImageResource(R.drawable.ic_heart_white);
                    favButton.setTag("");
                }
                break;
            default:
                throw new UnsupportedOperationException("Failed to find loader with id " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        finish();
    }
}
