package com.leadinsource.popularmovies2;


import android.arch.lifecycle.ViewModelProviders;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class TrailersFragment extends Fragment {

    private DetailActivityViewModel viewModel;
    private ImageButton shareButton;
    private RecyclerView rv;


    public TrailersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_trailers, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);

        rv = view.findViewById(R.id.rvTrailers);
        shareButton = view.findViewById(R.id.shareButton);

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        viewModel.getTrailers().observe(this, videos -> {
            rv.setAdapter(new TrailersAdapter(videos, video -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getKey()));
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }));

            shareButton.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, videos.get(0).getKey());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            });

        });

        return view;
    }



}
