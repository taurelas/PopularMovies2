package com.leadinsource.popularmovies2;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;

import com.leadinsource.popularmovies2.model.Review;
import com.leadinsource.popularmovies2.model.Video;
import com.leadinsource.popularmovies2.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel for Detail Activity
 */

class DetailActivityViewModel extends AndroidViewModel {

    private LiveData<Boolean> isFavorite;
    private LiveData<List<Video>> trailers;
    private MutableLiveData<Integer> movieId;
    private LiveData<List<Review>> reviews;
    private MovieRepository movieRepository;

    DetailActivityViewModel(Application application) {
        super(application);

        if(movieId ==null) {
            movieId = new MutableLiveData<>();
        }

        movieRepository = MovieRepository.getInstance(application.getContentResolver());

    }

    LiveData<Boolean> isFavorite() {

        if(isFavorite==null) {
            isFavorite = Transformations.switchMap(movieId, input ->
                    movieRepository.isFavorite(input));
        }

        return isFavorite;
    }

    void switchFavorite() {

        if(isFavorite.getValue()) {
            movieRepository.removeFromFavorites(movieId.getValue());
        } else {
            movieRepository.addToFavorites(movieId.getValue());
        }

    }

    LiveData<List<Video>> getTrailers() {
        return Transformations.map(getTrailersWhenIdChanged(),
                this::fixYouTubeUrls);
    }

    private LiveData<List<Video>> getTrailersWhenIdChanged() {
        if(trailers==null) {

            trailers = Transformations.switchMap(movieId,
                    input -> movieRepository.fetchTrailers(input));
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
                    input -> movieRepository.fetchReviews(input));
        }

        return reviews;
    }
}
