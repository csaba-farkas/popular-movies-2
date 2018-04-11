package com.csabafarkas.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.csabafarkas.popularmovies.adapters.MovieAdapter;
import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.MovieCollection;
import com.csabafarkas.popularmovies.models.PopularMoviesModel;
import com.csabafarkas.popularmovies.models.RetrofitError;
import com.csabafarkas.popularmovies.utilites.NetworkUtils;
import com.csabafarkas.popularmovies.utilites.PopularMoviesNetworkCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PopularMoviesNetworkCallback {

    @BindView(R.id.activity_main_root_gv)
    GridView rootView;
    private int currentSelection;
    private int pageNumber;
    private int currentPosition;
    private List<Movie> movies;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSelection == 0) {
            currentSelection = R.id.sort_most_popular;
        }
        if (movies == null) {
            loadMovies(currentSelection);
        }

        // load more on scroll to bottom
        rootView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && !loading) {
                    loadMovies(currentSelection);
                }
            }
        });

        // add onItemClickListener to GridView
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie_id_key), movies.get(position).getId().toString());
                startActivity(intent);
            }
        });

        // scroll to current position
        if (currentPosition >= 0)
            rootView.smoothScrollToPosition(currentPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_position", rootView.getFirstVisiblePosition());
        outState.putInt("current_selection", currentSelection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("current_position")) {
            currentPosition = savedInstanceState.getInt("current_position");
        }
        if (savedInstanceState.containsKey("current_selection")) {
            currentSelection = savedInstanceState.getInt("current_selection");
        }
    }

    @Override
    public void onSuccess(PopularMoviesModel popularMoviesModel) {
        if (!(popularMoviesModel instanceof MovieCollection)) return;
        MovieCollection movieCollection = (MovieCollection) popularMoviesModel;
        pageNumber = movieCollection.getPage();
        if (movies == null) {
            movies = movieCollection.getMovies();
        } else {
            this.movies.addAll(movieCollection.getMovies());
        }

        if (rootView.getAdapter() == null) {
            MovieAdapter movieAdapter = new MovieAdapter(this, 0, movies);
            rootView.setAdapter(movieAdapter);
        } else {
            ((MovieAdapter) rootView.getAdapter()).notifyDataSetChanged();
        }
        loading = false;
    }

    @Override
    public void onFailure(RetrofitError retrofitError) {
        Toast.makeText(this, retrofitError.getErrorCode() + " " + retrofitError.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.popular_movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_top_rated:
                if (currentSelection == R.id.sort_top_rated) break;
                movies.clear();
                pageNumber = 0;
                currentSelection = R.id.sort_top_rated;
                loadMovies(currentSelection);
                break;
            case R.id.sort_most_popular:
                if (currentSelection == R.id.sort_most_popular) break;
                movies.clear();
                pageNumber = 0;
                currentSelection = R.id.sort_most_popular;
                loadMovies(currentSelection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown selection");
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(int currentSelection) {
        loading = true;
        pageNumber++;
        switch (currentSelection) {
            case R.id.sort_most_popular:
                NetworkUtils.getMostPopularMovies(BuildConfig.MovieDbApiKey, pageNumber, this);
                break;
            case R.id.sort_top_rated:
                NetworkUtils.getTopRatedMovies(BuildConfig.MovieDbApiKey, pageNumber, this);
                break;
            default:
                throw new UnsupportedOperationException("Unknown selection!");
        }
    }
}
