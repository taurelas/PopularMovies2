package com.leadinsource.popularmovies1;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leadinsource.popularmovies1.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * MainActivity's Recycler View Adapter
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Movie> data;

    RecyclerViewAdapter(List<Movie> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(holder.thumbnail.getContext()).load(data.get(position).poster_path).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void updateData(List<Movie> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView thumbnail;

        ViewHolder(View itemView) {
              super(itemView);
              thumbnail = itemView.findViewById(R.id.ivPoster);

              itemView.setOnClickListener(v -> {
                  Intent intent = new Intent(v.getContext(), DetailActivity.class);
                  Movie currentMovie = data.get(getAdapterPosition());
                  intent.putExtra(DetailActivity.EXTRA_URL,currentMovie.poster_path);
                  intent.putExtra(DetailActivity.EXTRA_RELEASE_DATE, currentMovie.release_date);
                  intent.putExtra(DetailActivity.EXTRA_TITLE, currentMovie.title);
                  intent.putExtra(DetailActivity.EXTRA_VOTE_AVG, currentMovie.vote_average);
                  intent.putExtra(DetailActivity.EXTRA_SYNOPSIS, currentMovie.overview);
                  v.getContext().startActivity(intent);
              });
        }
    }

}
