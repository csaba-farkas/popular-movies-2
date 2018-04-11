package com.csabafarkas.popularmovies;

import android.content.DialogInterface;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.PopularMoviesModel;
import com.csabafarkas.popularmovies.models.RetrofitError;
import com.csabafarkas.popularmovies.utilites.NetworkUtils;
import com.csabafarkas.popularmovies.utilites.PopularMoviesNetworkCallback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements PopularMoviesNetworkCallback {

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
        Movie movieParam = (Movie) movie;
        updateUI(movieParam);
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

    private void updateUI(Movie movie) {
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

        progressBar.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
    }
}
