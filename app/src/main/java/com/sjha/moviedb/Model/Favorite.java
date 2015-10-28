package com.sjha.moviedb.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjha on 15-10-27.
 */
@Table(name = "FavoriteMovie")
public class Favorite extends Model implements Serializable {

    @Column(name = "Movieid")
    private String id;
    @Column(name = "backdropPath")
    private String backdropPath;
    @Column(name = "title")
    private String title;
    @Column(name = "overview")
    private String overview;
    @Column(name = "popularity")
    private String popularity;
    @Column(name = "releaseDate")
    private String releaseDate;
    @Column(name = "voteAverage")
    private Double voteAverage;
    @Column(name = "voteCount")
    private String voteCount;
    @Column(name = "posterPath")
    private String posterPath;
    @Column(name = "isFavorite")
    private Boolean isFavorite;

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMovieId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public static ArrayList<Movie> getFavoriteMovies() {
        List<Movie> getAll =  new Select()
                .all()
                .from(Movie.class)
                .execute();
        ArrayList<Movie> returnedMovies = new ArrayList<>(getAll);
        return returnedMovies;
    }

    public static void deleteFromFavoriteMovies(String id){
        new Delete().from(Movie.class).where("Movieid = ?", id).execute();
    }

    public Boolean getState(String id){
        Favorite movie = new Select()
                .from(Favorite.class)
                .where("Movieid = ?", id)
                .executeSingle();

        Boolean state = movie.getIsFavorite();
        return state;
    }

}
