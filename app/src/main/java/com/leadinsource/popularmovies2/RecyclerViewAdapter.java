package com.leadinsource.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leadinsource.popularmovies2.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * MainActivity's Recycler View Adapter
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private List<Movie> data;
    private final RecyclerViewClickListener clickListener;

    public interface RecyclerViewClickListener {
        void onItemClick(Movie movie);
    }


    RecyclerViewAdapter(List<Movie> data, RecyclerViewClickListener clickListener) {
        this.data = data;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindImageUsingPicasso(holder, position);
    }

    /**
     * Attempts to get image from cache first, if fails it tries the network
     *
     * Code as per Sanket Berde https://stackoverflow.com/a/30686992/3886459
     * @param holder
     * @param position
     */
    private void bindImageUsingPicasso(ViewHolder holder, int position) {
        Picasso.with(holder.thumbnail.getContext())
                .load(data.get(position).posterPath)
                .networkPolicy(NetworkPolicy.OFFLINE)

                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        //ok
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(holder.thumbnail.getContext())
                                .load(data.get(position).posterPath)
                                .error(android.R.drawable.stat_notify_error)
                                .into(holder.thumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //ok
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e(TAG, "Could not fetch the image");
                                    }
                                });
                    }
                });
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

              itemView.setOnClickListener(v -> clickListener.onItemClick(data.get(getAdapterPosition())));
        }
    }

}
