package com.csabafarkas.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csabafarkas.popularmovies.R;
import com.csabafarkas.popularmovies.models.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private final Context context;
    private final TrailerAdapterOnClickListener onClickListener;
    private List<Trailer> trailers;

    public interface TrailerAdapterOnClickListener {
        // TODO: parameter is up for debate
        void onClickTrailerItem(String videoId);
    }

    public TrailerAdapter(@NonNull Context context,
                          @NonNull List<Trailer> trailers, TrailerAdapterOnClickListener clickHandler) {
        this.context = context;
        this.onClickListener = clickHandler;
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        view.setFocusable(true);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        holder.trailerTitleTextView.setText(trailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (trailers == null) return 0;
        return trailers.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trailer_title_text_view)
        TextView trailerTitleTextView;

        TrailerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            onClickListener.onClickTrailerItem(trailers.get(adapterPosition).getKey());
        }
    }
}
