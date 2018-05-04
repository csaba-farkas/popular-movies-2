package com.csabafarkas.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csabafarkas.popularmovies.R;
import com.csabafarkas.popularmovies.models.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private final Context context;
    private final List<Review> reviews;
    private final ReviewItemOnClickListener clickHandler;

    public interface ReviewItemOnClickListener {
        void onReviewItemClick(Uri uri);
    }

    public ReviewAdapter(@NonNull Context context, @NonNull List<Review> reviews,
                         @NonNull ReviewItemOnClickListener clickHandler) {
        this.context = context;
        this.reviews = reviews;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        view.setFocusable(true);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        String review = "\"" + reviews.get(position).getContent();
        if (review.length() > 100) {
            review = review.substring(0, 99);
            review += "...";
        }
        review += "\"";
        holder.reviewTextView.setText(review);
        String author = String.format("by %s", reviews.get(position).getAuthor());
        holder.authorTextView.setText(author);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.review_review_text_view)
        TextView reviewTextView;
        @BindView(R.id.review_author_text_view)
        TextView authorTextView;

        ReviewAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickHandler.onReviewItemClick(Uri.parse(reviews.get(getAdapterPosition()).getUrl()));
        }
    }
}
