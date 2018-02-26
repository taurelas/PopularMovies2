package com.leadinsource.popularmovies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.leadinsource.popularmovies1.databinding.ActivityMainBinding;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private MenuItem sortSwitch;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this, new MainActivityVMF(getResources())).get(MainActivityViewModel.class);

        viewModel.getData().observe(this, data -> {
            binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            binding.recyclerView.setAdapter(new RecyclerViewAdapter(data));
        });

    }

    /**
     * Starts observing sortOrder data, must be called after menu has been created
     */
    private void observeSortOrder() {
        viewModel.getSortOrder().observe(this, s -> {
           sortSwitch.setTitle(s);
           onPrepareOptionsMenu(menu);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        this.menu = menu;
        sortSwitch = menu.findItem(R.id.sort_switch);

        observeSortOrder();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item == sortSwitch) {
            viewModel.switchSorting();
        }

        return super.onOptionsItemSelected(item);
    }
}
