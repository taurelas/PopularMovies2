package com.leadinsource.popularmovies2;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.leadinsource.popularmovies2.databinding.FragmentDetailsBinding;
import com.leadinsource.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private DetailActivityViewModel viewModel;
    private ImageButton shareButton;
    private RecyclerView rv;
    private FragmentDetailsBinding binding;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_details, container,false);


        //View view = inflater.inflate(R.layout.fragment_details, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);

        viewModel.getMovie().observe(this, movie -> {
            Picasso.with(getContext()).load(movie.posterPath).into(binding.ivPoster);
            binding.tvTitle.setText(movie.title);
            binding.tvVoteAverage.setText(String.valueOf(movie.voteAverage));
            binding.tvReleaseDate.setText(movie.releaseDate);
            binding.tvSynopsis.setText(movie.overview);

        });

        return binding.getRoot();
    }


}
