package com.sjha.moviedb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sjha.moviedb.Adapter.GetMovies;
import com.sjha.moviedb.Model.MovieList;

public class SplashActivity extends Activity {

    private MovieList movieListPop;
    private MovieList favMovieList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(isNetworkConnected()) {
            getMoviesData();

        }
        prepUI();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.putExtra(Constants.MOVIE_POP, movieListPop);
                i.putExtra(Constants.MOVIE_FAV,favMovieList);
                startActivity(i);
                finish();
            }
        }, 4000);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private void getMoviesData() {
        GetMovies getMoviesPop = new GetMovies();
        getMoviesPop.execute(Constants.SORT_OPTION_POP);
        movieListPop = new MovieList();
        movieListPop.setMovies();
        favMovieList = new MovieList();
        favMovieList.setMoviesFavorite();
    }



    private void prepUI() {
        ImageView logo = (ImageView) findViewById(R.id.logo);
        Animation heart_pulse = AnimationUtils.loadAnimation(this,
                R.anim.pulse);

        logo.startAnimation(heart_pulse);
    }


}
