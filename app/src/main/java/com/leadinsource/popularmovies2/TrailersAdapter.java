package com.leadinsource.popularmovies2;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leadinsource.popularmovies2.model.Video;

import java.util.List;

/**
 * Adapter between TrailersFragment - RecyclerView
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    interface ClickListener {
        void onItemClick(Video video);
    }

    private List<Video> data;
    private ClickListener trailerClickListener;

    TrailersAdapter(List<Video> data, ClickListener trailerClickListener) {
        this.data = data;
        this.trailerClickListener = trailerClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.trailer_list_element, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video currentVideo = data.get(holder.getAdapterPosition());

        holder.textView.setText(currentVideo.getName());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton trailerButton;
        TextView textView;


        ViewHolder(View itemView) {
            super(itemView);

            trailerButton = itemView.findViewById(R.id.trailerButton);

            trailerButton.setOnClickListener(v -> trailerClickListener.onItemClick(data.get(getAdapterPosition())));

            textView = itemView.findViewById(R.id.trailerTitle);
        }
    }

}
