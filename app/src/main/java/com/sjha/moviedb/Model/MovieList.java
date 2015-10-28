package com.sjha.moviedb.Model;

import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjha on 15-10-27.
 */
public class MovieList implements Serializable {

    private List<Movie> movies;
    private List<Favorite> favMovies;

    public List<Movie> getMovies() {
        return movies;
    }
    public List<Favorite> getFavMovies(){
        return favMovies;
    }

    public void setMovies() {
        movies = new Select()
                .from(Movie.class)
                .orderBy("releaseDate DESC")
                .execute();
    }



    public void setMoviesFavorite() {
        favMovies = new Select()
                .from(Favorite.class)
                .orderBy("releaseDate")
                .execute();
    }


}
