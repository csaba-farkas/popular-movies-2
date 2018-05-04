package com.csabafarkas.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
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
import com.csabafarkas.popularmovies.utilites.PopularMoviesNetworkCallback;
import com.squareup.picasso.Picasso;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity
            implements PopularMoviesNetworkCallback, TrailerAdapter.TrailerAdapterOnClickListener,
                    ReviewAdapter.ReviewItemOnClickListener{

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
    @BindDrawable(R.drawable.ic_heart_red)
    Drawable redHeartDrawable;
    @BindDrawable(R.drawable.ic_heart_white)
    Drawable whiteHeartDrawable;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        String movieId;

        if (getIntent() != null) {
            // get the movie id from the intent
            if (getIntent().hasExtra(getResources().getString(R.string.movie_id_key))) {
                movieId = getIntent().getStringExtra(getResources().getString(R.string.movie_id_key));
                if (movieId != null)
                    NetworkUtils.getMovie(BuildConfig.MovieDbApiKey, movieId, this);
            }
        }
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
        // TODO: create a callback method that changes the image resource of the ImageButton after inserting or deleting the movie from the db
        favButton.setImageResource(R.drawable.ic_heart_red);    // this is just for demonstration purposes

        // insert movie details
        ContentValues cv = new ContentValues();

        cv.put(PopularMoviesDbContract.MovieEntry._ID, movie.getId());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_TITLE, movie.getTitle());
        cv.put(PopularMoviesDbContract.MovieEntry.POSTER_URL, movie.getPosterPath());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_RATING, movie.getVoteAverage());
        cv.put(PopularMoviesDbContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate());
        cv.put(PopularMoviesDbContract.MovieEntry.MOVIE_PLOT, movie.getOverview());

        Uri uri = getContentResolver().insert(PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI, cv);

        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
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

                uri = getContentResolver().insert(PopularMoviesDbContract.ReviewEntry.REVIEWS_CONTENT_URI, cv);
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

                uri = getContentResolver().insert(PopularMoviesDbContract.TrailerEntry.TRAILERS_CONTENT_URI, cv);
            }
        }

    }
}
