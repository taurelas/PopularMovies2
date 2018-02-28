package com.leadinsource.popularmovies2;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leadinsource.popularmovies2.databinding.ActivityDetailBinding;
import com.leadinsource.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();

        if(intent!=null) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);

            Picasso.with(this).load(movie.posterPath).into(binding.ivPoster);
            binding.tvTitle.setText(movie.title);
            binding.tvVoteAverage.setText(String.valueOf(movie.voteAverage));
            binding.tvReleaseDate.setText(movie.releaseDate);
            binding.tvSynopsis.setText(movie.overview);
        }
    }
}
