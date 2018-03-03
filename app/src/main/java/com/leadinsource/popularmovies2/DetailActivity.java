package com.leadinsource.popularmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.leadinsource.popularmovies2.databinding.ActivityDetailBinding;
import com.leadinsource.popularmovies2.model.Movie;
import com.leadinsource.popularmovies2.model.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    private MenuItem addToFavorites;
    private Menu menu;

    private DetailActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);

        Intent intent = getIntent();

        if(intent!=null) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);

            Picasso.with(this).load(movie.posterPath).into(binding.ivPoster);
            binding.tvTitle.setText(movie.title);
            binding.tvVoteAverage.setText(String.valueOf(movie.voteAverage));
            binding.tvReleaseDate.setText(movie.releaseDate);
            binding.tvSynopsis.setText(movie.overview);
            viewModel.setMovieId(movie.id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        this.menu = menu;
        addToFavorites = menu.findItem(R.id.favorite);

        observeIsFavorite();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item==addToFavorites) {
            viewModel.switchFavorite();
        }

        if(item.getItemId()==android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return true;
    }

    private void observeIsFavorite() {
        viewModel.isFavorite().observe(this, isFavorite -> {
            if(isFavorite) {
                addToFavorites.setIcon(android.R.drawable.star_big_on);

            } else {
                addToFavorites.setIcon(android.R.drawable.star_big_off);

            }
        });
    }


}
