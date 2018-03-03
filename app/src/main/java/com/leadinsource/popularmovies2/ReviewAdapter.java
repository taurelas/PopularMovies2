package com.leadinsource.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leadinsource.popularmovies2.model.Review;

import java.util.List;

/**
 * Adapter for RecyclerView with Reviews
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {


    private final List<Review> reviews;

    ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ViewHolder(inflater.inflate(R.layout.review_list_element, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(reviews.get(position).content);
        holder.author.setText(reviews.get(position).author);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        TextView author;

        ViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tvContent);
            author = itemView.findViewById(R.id.tvAuthor);
        }
    }

}
