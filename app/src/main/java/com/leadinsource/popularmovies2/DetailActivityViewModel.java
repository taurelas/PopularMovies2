package com.leadinsource.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.leadinsource.popularmovies2.model.Review;
import com.leadinsource.popularmovies2.model.Video;
import com.leadinsource.popularmovies2.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel for Detail Activity
 */

class DetailActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> isFavorite;
    private LiveData<List<Video>> trailers;
    private MutableLiveData<Integer> movieId;
    private LiveData<List<Review>> reviews;

    DetailActivityViewModel() {
        if(isFavorite == null) {
            isFavorite = new MutableLiveData<>();
            isFavorite.setValue(false);
        }

        if(movieId ==null) {
            movieId = new MutableLiveData<>();
        }

        //TODO change to observe data from the db



    }

    LiveData<Boolean> isFavorite() {
        return isFavorite;
    }

    void switchFavorite() {
        isFavorite.setValue(!isFavorite.getValue());
    }

    LiveData<List<Video>> getTrailers() {
        return Transformations.map(getTrailersWhenIdChanged(),
                this::fixYouTubeUrls);
    }

    private LiveData<List<Video>> getTrailersWhenIdChanged() {
        if(trailers==null) {

            trailers = Transformations.switchMap(movieId,
                    input -> MovieRepository.getInstance().fetchTrailers(input));
        }

        return trailers;
    }

    void setMovieId(int movieId) {
        if(this.movieId==null) {
            this.movieId = new MutableLiveData<>();
        }

        this.movieId.postValue(movieId);
    }

    private List<Video> fixYouTubeUrls(List<Video> videos) {
        String YT_PATH = "https://www.youtube.com/watch?v=";

        for (Video video : videos) {
            video.key = YT_PATH.concat(video.key);
        }

        return videos;
    }

    public LiveData<List<Review>> getReviews() {
        if(reviews== null) {
            reviews = Transformations.switchMap(movieId,
                    input -> MovieRepository.getInstance().fetchReviews(input));
        }

        return reviews;
    }
}
