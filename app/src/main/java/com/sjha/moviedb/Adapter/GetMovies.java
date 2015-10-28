package com.sjha.moviedb.Adapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sjha.moviedb.Constants;
import com.sjha.moviedb.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sjha on 15-10-27.
 */
public class GetMovies extends AsyncTask<String, Void, ArrayList<Movie>> {





    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieData = null;

        try{
            Uri buildUri = Uri.parse(Constants.URL_PATH + params[0] + Constants.API_Param + Constants.API_KEY).buildUpon().build();
            URL url = new URL(buildUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieData = buffer.toString();

            Log.v(Constants.LOG_TAG, "Movie Data: " + movieData);

        }catch (IOException e){
            Log.e(Constants.LOG_TAG,"Error", e);
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e(Constants.LOG_TAG, "Error Closing stream", e);
                }
            }
        }



        try{
            return getMovieData(movieData);
        }catch (JSONException e){
            Log.e(Constants.LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }


        return null;


    }


    private ArrayList<Movie> getMovieData(String movieData) throws JSONException {



        JSONObject movieObject  = new JSONObject(movieData);
        JSONArray movieArray = movieObject.getJSONArray(Constants.RESULTS);

        ArrayList<Movie> pictureCollection = new ArrayList<>();
        for(int i = 0; i <movieArray.length(); i++){
            Movie movie = new Movie();

            JSONObject movieResults = movieArray.getJSONObject(i);
            movie.setId(movieResults.getString(Constants.ID));
            movie.setBackdropPath(movieResults.getString(Constants.BACKDROP_PATH));
            movie.setTitle(movieResults.getString(Constants.TITLE));
            movie.setOverview(movieResults.getString(Constants.OVERVIEW));
            movie.setPopularity(movieResults.getString(Constants.POPULARITY));
            movie.setReleaseDate(movieResults.getString(Constants.RELEASE_DATE));
            movie.setVoteAverage(movieResults.getDouble(Constants.VOTE_AVERAGE));
            movie.setVoteCount(movieResults.getString(Constants.VOTE_COUNT));
            movie.setPosterPath(movieResults.getString(Constants.POSTER_PATH));
            movie.setIsFavorite(false);
            movie.save();
            pictureCollection.add(movie);

        }
        return pictureCollection;

    }

    public GetMovies() {
        super();
    }


}
