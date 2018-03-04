package com.leadinsource.popularmovies2;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.leadinsource.popularmovies2.databinding.ActivityMainBinding;

/**
 * Displays grid of movie posters
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private MenuItem sortSwitch;
    private MenuItem listSwitch;
    private Menu menu;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        viewModel.getMoviesData().observe(this, data -> {
            if(binding.recyclerView.getAdapter()== null) {
                binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

                adapter = new RecyclerViewAdapter(data, movie -> {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_MOVIE,movie);
                    startActivity(intent);
                });

                binding.recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(data);
            }
        });

    }

    /**
     * Starts observing sortOrder text date, must be called after menu has been created
     */
    private void observeSortOrder() {
        viewModel.getSortOrderText().observe(this, s -> {
           sortSwitch.setTitle(s);
           onPrepareOptionsMenu(menu);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        this.menu = menu;
        sortSwitch = menu.findItem(R.id.sort_switch);
        listSwitch = menu.findItem(R.id.list_switch);
        observeSortOrder();
        observeListType();

        return true;
    }

    /**
     * Starts observing type of movie list: either favorites or top-rated.
     */
    private void observeListType() {
        viewModel.getMovieListType().observe(this, text -> {
            listSwitch.setTitle(text);
            onPrepareOptionsMenu(menu);
        });
    }

    /**
     * Handles clicks on menu items, switches sorting and list type of movies accordingly.
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item == sortSwitch) {
            viewModel.switchSorting();
        }

        if(item == listSwitch) {
            viewModel.switchMovieListType();
        }

        return super.onOptionsItemSelected(item);
    }
}
