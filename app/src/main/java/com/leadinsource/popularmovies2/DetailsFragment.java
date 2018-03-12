package com.leadinsource.popularmovies2;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leadinsource.popularmovies2.databinding.FragmentDetailsBinding;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private static final String TAG = DetailsFragment.class.getSimpleName();
    private DetailActivityViewModel viewModel;
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

        viewModel.init(savedInstanceState);

        viewModel.getMovie().observe(this, movie -> {
            Picasso.with(getContext()).load(movie.posterPath).into(binding.ivPoster);
            binding.tvTitle.setText(movie.title);
            binding.tvVoteAverage.setText(String.valueOf(movie.voteAverage));
            binding.tvReleaseDate.setText(movie.releaseDate);
            binding.tvSynopsis.setText(movie.overview);

        });

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "saving state");
        viewModel.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
