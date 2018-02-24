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
        String[] string = {
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/coss7RgL0NH6g4fC2s5atvf3dFO.jpg",
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg",
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/6uOMVZ6oG00xjq0KQiExRBw2s3P.jpg",
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/bLBUCtMQGJclH36clliPLmljMys.jpg",
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg",
                "https://image.tmdb.org/t/p/w185_and_h278_bestv2/oSLd5GYGsiGgzDPKTwQh7wamO8t.jpg"};

        data.setValue(string);

        new AsyncTask<Void, Void, String[]>() {

            @Override
            protected String[] doInBackground(Void... voids) {
                try {
                    Thread.sleep(5000);
                    return new String[]{
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/sM33SANp9z6rXW8Itn7NnG1GOEs.jpg",
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/q0R4crx2SehcEEQEkYObktdeFy.jpg",
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/34xBL6BXNYFqtHO9zhcgoakS4aP.jpg",
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/tWqifoYuwLETmmasnGHO7xBjEtt.jpg",
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/ebSnODDg9lbsMIaWg2uAbjn7TO5.jpg",
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg"};
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
