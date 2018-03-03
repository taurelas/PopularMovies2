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
public class ReviewsFragment extends Fragment {

    private DetailActivityViewModel viewModel;
    private RecyclerView rv;


    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);

        rv = view.findViewById(R.id.rvReviews);

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        viewModel.getReviews().observe(this,
                reviews -> rv.setAdapter(new ReviewAdapter(reviews)));

        return view;
    }



}
