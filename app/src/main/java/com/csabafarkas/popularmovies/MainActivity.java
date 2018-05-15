package com.csabafarkas.popularmovies;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.csabafarkas.popularmovies.adapters.MovieAdapter;
import com.csabafarkas.popularmovies.data.PopularMoviesDbContract;
import com.csabafarkas.popularmovies.models.Movie;
import com.csabafarkas.popularmovies.models.MovieCollection;
import com.csabafarkas.popularmovies.models.PopularMoviesModel;
import com.csabafarkas.popularmovies.models.RetrofitError;
import com.csabafarkas.popularmovies.utilites.NetworkUtils;
import com.csabafarkas.popularmovies.utilites.PopularMoviesHelpers;
import com.csabafarkas.popularmovies.utilites.PopularMoviesNetworkCallback;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesNetworkCallback, LoaderManager.LoaderCallbacks<Cursor>, MovieAdapter.MovieItemOnClickListener {

    private static final int MOVIE_LOADER_ID = 1;

    @BindView(R.id.activity_main_movie_grid)
    RecyclerView movieList;
    @BindString(R.string.current_position_key)
    String currentPositionKey;
    @BindString(R.string.current_selection_key)
    String currentSelectionKey;
    @BindString(R.string.current_movies_key)
    String currentMoviesKey;
    @BindString(R.string.page_number_key)
    String pageNumberKey;
    @BindInt(R.integer.most_popular_selection)
    int mostPopularSelected;
    @BindInt(R.integer.top_rated_selection)
    int topRatedSelected;
    @BindInt(R.integer.favourites_selection)
    int favouritesSelected;
    private int currentSelection;
    private int pageNumber;
    private int currentPosition;
    private ArrayList<Movie> movies;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        movieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int numberOfColumns = layoutManager.getSpanCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalMovieCount = 0;
                    try {
                        totalMovieCount = movies.size();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                    if (lastVisibleItemPosition >= (totalMovieCount - numberOfColumns)) {
                        loadMovies(currentSelection);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentSelection = PopularMoviesHelpers.getSharedPrefInt(this, currentSelectionKey);
        if (currentSelection == -1) {
            currentSelection = mostPopularSelected;
            PopularMoviesHelpers.setSharedPrefInt(this, currentSelectionKey, currentSelection);
        }
        if (movies == null) {
            loadMovies(currentSelection);
        } else {
            MovieAdapter movieAdapter = new MovieAdapter(this, movies, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                    PopularMoviesHelpers.calculateNoOfColumns(this));
            movieList.setLayoutManager(gridLayoutManager);
            movieList.setAdapter(movieAdapter);
        }

        // scroll to current position
        if (currentPosition >= 0) {
            movieList.smoothScrollToPosition(currentPosition);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(pageNumberKey, pageNumber);
        int currPos = ((GridLayoutManager) movieList.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        outState.putInt(currentPositionKey, currPos);
        if (movies != null) {
            outState.putParcelableArrayList(currentMoviesKey, movies);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(currentPositionKey)) {
            currentPosition = savedInstanceState.getInt(currentPositionKey);
        }
        if (savedInstanceState.containsKey(currentMoviesKey)) {
            movies = savedInstanceState.getParcelableArrayList(currentMoviesKey);
        }
        if (savedInstanceState.containsKey(pageNumberKey)) {
            pageNumber = savedInstanceState.getInt(pageNumberKey);
        }
    }

    @Override
    public void onSuccess(PopularMoviesModel popularMoviesModel) {
        if (!(popularMoviesModel instanceof MovieCollection)) return;
        MovieCollection movieCollection = (MovieCollection) popularMoviesModel;
        pageNumber = movieCollection.getPage();
        if (movies == null) {
            movies = new ArrayList<>();
            movies.addAll(movieCollection.getMovies());
        } else {
            this.movies.addAll(movieCollection.getMovies());
        }

        if (movieList.getAdapter() == null) {
            MovieAdapter movieAdapter = new MovieAdapter(this, movies, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                    PopularMoviesHelpers.calculateNoOfColumns(this));
            movieList.setLayoutManager(gridLayoutManager);
            movieList.setAdapter(movieAdapter);
        } else {
            movieList.getAdapter().notifyDataSetChanged();
        }
        loading = false;
    }

    @Override
    public void onFailure(RetrofitError retrofitError) {
        Toast.makeText(this, retrofitError.getErrorCode() + " " + retrofitError.getErrorMessage(), Toast.LENGTH_LONG).show();
        loading = false;
    }

    @Override
    public void onError(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
        loading = false;

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
                if (currentSelection == topRatedSelected) break;
                movies.clear();
                pageNumber = 0;
                currentSelection = topRatedSelected;
                PopularMoviesHelpers.setSharedPrefInt(this, currentSelectionKey, currentSelection);
                loadMovies(currentSelection);
                break;
            case R.id.sort_most_popular:
                if (currentSelection == mostPopularSelected) break;
                movies.clear();
                pageNumber = 0;
                currentSelection = mostPopularSelected;
                PopularMoviesHelpers.setSharedPrefInt(this, currentSelectionKey, currentSelection);
                loadMovies(currentSelection);
                break;
            case R.id.sort_favourite_movies:
                if (currentSelection == favouritesSelected) break;
                currentSelection = favouritesSelected;
                PopularMoviesHelpers.setSharedPrefInt(this, currentSelectionKey, currentSelection);
                loadMovies(currentSelection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown selection");
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(int currentSelection) {
        pageNumber++;

        if (currentSelection == mostPopularSelected) {
            if (loading) return;
            loading = true;
            getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
            NetworkUtils.getMostPopularMovies(BuildConfig.MovieDbApiKey, pageNumber, this);
        } else if (currentSelection == topRatedSelected) {
            if (loading) return;
            loading = true;
            getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
            NetworkUtils.getTopRatedMovies(BuildConfig.MovieDbApiKey, pageNumber, this);
        } else if (currentSelection == favouritesSelected){
            // load from SQLite db
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            throw new UnsupportedOperationException("Unknown selection");
        }
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
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

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(
                            PopularMoviesDbContract.MovieEntry.MOVIES_CONTENT_URI,
                            null,
                            null,
                            null,
                            PopularMoviesDbContract.MovieEntry.TIME_ADDED);
                } catch (Exception ex) {
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            movies = PopularMoviesHelpers.convertMovieCursorToList(data);
        } else {
            movies = new ArrayList<>();
        }

        MovieAdapter movieAdapter = new MovieAdapter(this, movies, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                PopularMoviesHelpers.calculateNoOfColumns(this));
        movieList.setLayoutManager(gridLayoutManager);
        movieList.setAdapter(movieAdapter);
        if (movies.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.no_favourite_movies_found_message), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        movies.clear();
    }

    @Override
    public void onMovieItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        if (currentSelection != favouritesSelected) {
            intent.putExtra(getResources().getString(R.string.movie_id_key), movies.get(position).getId().toString());
        } else {
            intent.putExtra(getResources().getString(R.string.movie_key), movies.get(position));
        }
        startActivity(intent);
    }
}
