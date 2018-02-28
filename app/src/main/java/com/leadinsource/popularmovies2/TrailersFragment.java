package com.leadinsource.popularmovies2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.leadinsource.popularmovies2.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrailersFragment extends Fragment {


    public TrailersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_trailers, container, false);

        RecyclerView rv = view.findViewById(R.id.rvTrailers);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        List<Video> list = new ArrayList<>();

        Video video = new Video();

        video.setKey("https://youtu.be/64-iSYVmMVY");
        video.setName("Trailer test");
        list.add(video);

        rv.setAdapter(new TrailersAdapter(list, video1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video1.getKey()));
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
            }

        }));

        ImageButton shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, video.getKey());
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        });

        return view;
    }

}
