package com.leadinsource.popularmovies1;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

/**
 * ViewModel for MainActivity
 */

public class MainActivityViewModel extends ViewModel {

    MovieRepository movieRepository;

    private MutableLiveData<String[]> data;
    public LiveData<String[]> getData() {
        if(data== null) {
            data = new MutableLiveData<>();
            initializeData();
        }

        return data;
    }

    private void initializeData() {
        // TODO normally we will fetch the data here from internet/ sqlite db
        // I'm guessing using a repository as the single source of truth for all the data
        String[] string = {"1", "2", "3", "4", "5", "6"};

        data.setValue(string);

        new AsyncTask<Void, Void, String[]>() {

            @Override
            protected String[] doInBackground(Void... voids) {
                try {
                    Thread.sleep(5000);
                    String[] string2 = {"2", "4", "6", "8", "10", "12"};
                    return string2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String[] strings) {
                data.setValue(strings);
            }
        }.execute();

    }
}
