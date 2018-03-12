package com.leadinsource.popularmovies2;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.leadinsource.popularmovies2.databinding.ActivityDetailBinding;
import com.leadinsource.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Displays details of a movie that was selected in MainActivity
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
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
            int id = intent.getIntExtra(EXTRA_MOVIE_ID,0);

            viewModel.setMovieId(id);
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
            boolean favorite = viewModel.isFavorite().getValue();
            viewModel.switchFavorite();
            if(favorite) {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }

        }

        if(item.getItemId()==android.R.id.home) {
            finish();
        }

        return true;
    }

    /**
     * Starts observing whether the movie is a favorite or not and sets the icon accordingly
     */
    private void observeIsFavorite() {
        viewModel.isFavorite().observe(this, isFavorite -> {
            if(isFavorite) {
                addToFavorites.setIcon(android.R.drawable.star_big_on);
            } else {
                addToFavorites.setIcon(android.R.drawable.star_big_off);
            }
            onPrepareOptionsMenu(menu);
        });
    }
}
