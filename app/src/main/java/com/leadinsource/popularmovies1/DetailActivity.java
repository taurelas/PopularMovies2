package com.leadinsource.popularmovies1;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leadinsource.popularmovies1.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_RELEASE_DATE = "EXTRA_RELEASE_DATE";
    public static final String EXTRA_VOTE_AVG = "VOTE_AVG";
    public static final String EXTRA_SYNOPSIS = "EXTRA_SYNOPISIS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();

        if(intent!=null) {
            String url = intent.getStringExtra(EXTRA_URL);
            String title = intent.getStringExtra(EXTRA_TITLE);
            String releaseDate = intent.getStringExtra(EXTRA_RELEASE_DATE);
            String synopsis = intent.getStringExtra(EXTRA_SYNOPSIS);
            float voteAverage = intent.getFloatExtra(EXTRA_VOTE_AVG, 0.0f);
            Picasso.with(this).load(url).into(binding.ivPoster);
            binding.tvTitle.setText(title);
            binding.tvVoteAverage.setText(String.valueOf(voteAverage));
            binding.tvReleaseDate.setText(releaseDate);
            binding.tvSynopsis.setText(synopsis);
        }
    }
}
