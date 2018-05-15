package com.csabafarkas.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.csabafarkas.popularmovies.R;
import com.csabafarkas.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieItemViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final MovieItemOnClickListener clickHandler;

    public interface MovieItemOnClickListener {
        void onMovieItemClick(int position);
    }
    public MovieAdapter(@NonNull Context context, @NonNull List<Movie> movies,
                        @NonNull MovieItemOnClickListener clickListener) {
        this.context = context;
        this.movies = movies;
        this.clickHandler = clickListener;
    }
    @NonNull
    @Override
    public MovieItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                        .inflate(R.layout.grid_item_movie, parent, false);
        view.setFocusable(true);
        return new MovieItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieItemViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(context)
                .load(String.format(context.getResources().getString(R.string.poster_base_url_185), movie.getPosterPath()))
                .placeholder(R.drawable.ic_icon_img_placeholder)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poster_iv)
        ImageView posterImageView;

        MovieItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            posterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickHandler.onMovieItemClick(getAdapterPosition());
        }
    }
}
