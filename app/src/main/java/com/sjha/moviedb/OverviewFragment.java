package com.sjha.moviedb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sjha.moviedb.Adapter.TrailerListAdapter;
import com.sjha.moviedb.Model.Favorite;
import com.sjha.moviedb.Model.Movie;
import com.sjha.moviedb.Model.Trailer;
import com.squareup.picasso.Picasso;

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
import java.util.List;

/**
 * Created by sjha on 15-10-24.
 */
public class OverviewFragment extends Fragment {


    private TextView titleTxt;
    private TextView releaseDateTxt;
    private TextView durationTxt;
    private ImageView poster;
    private TextView  overviewTxt;
    private TextView voteTxt;
    private Button reviewBtn;
    private ListView trailerList;
    private ImageButton markButton;
    private TextView markFavTxt;
    private boolean isFavorite;
    private Movie mMovie;

    public OverviewFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview =  inflater.inflate(R.layout.fragment_overview, container, false);
        Intent intent = getActivity().getIntent();
        initialize(rootview);
        final Movie mMovie = (Movie) intent.getSerializableExtra(Constants.DETAIL_INTENT);
        if(mMovie == null){
            final Movie mMovieNull = AAUtility.get(Movie.class, "movieid = ?", "135397");
            populateDate(mMovieNull);
            GetTrailers getTrailers = new GetTrailers();
            getTrailers.execute(mMovieNull.getMovieId());
        }else{
            populateDate(mMovie);
            GetTrailers getTrailers = new GetTrailers();
            getTrailers.execute(mMovie.getMovieId());
        }


        rootview.setFocusableInTouchMode(true);
        rootview.requestFocus();
        return rootview;


    }



    private void initialize(View rootView){
        isFavorite = false;
        titleTxt = (TextView)rootView.findViewById(R.id.titleText);
        releaseDateTxt = (TextView)rootView.findViewById(R.id.txtReleaseDate);
        durationTxt = (TextView)rootView.findViewById(R.id.txtDuration);
        poster = (ImageView) rootView.findViewById(R.id.posterImage);
        overviewTxt = (TextView)rootView.findViewById(R.id.txtOverview);
        voteTxt = (TextView)rootView.findViewById(R.id.txtVoteAverage);
        reviewBtn = (Button)rootView.findViewById(R.id.reviewBtn);
        markButton = (ImageButton) rootView.findViewById(R.id.markMovie);
        markFavTxt = (TextView)rootView.findViewById(R.id.markFavTxt);

    }




    private void populateDate(final Movie movieObject){
        Favorite movie = AAUtility.get(Favorite.class, "Movieid = ?", movieObject.getMovieId());
        if(movie != null) {

            isFavorite = movie.getState(movieObject.getMovieId());
        }
        if(isFavorite){
            markButton.setBackgroundColor(Color.parseColor("#fbd80c"));
            markFavTxt.setText(getString(R.string.markedText));

        }else {
            markButton.setBackgroundColor(Color.parseColor("#00BFA5"));
            markFavTxt.setText(getString(R.string.markText));
        }
        titleTxt.setText(movieObject.getTitle());
        releaseDateTxt.setText(movieObject.getReleaseDate());
        durationTxt.setText("Popularity: " + movieObject.getPopularity());
        Picasso.with(getContext()).load(Constants.IMAGE_URL+movieObject.getPosterPath()).fit().into(poster);
        overviewTxt.setText(movieObject.getOverview());
        voteTxt.setText("Rating: " + movieObject.getVoteAverage().toString() + "/10");
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviewActivity = new Intent(getContext(), ReviewActivity.class);
                reviewActivity.putExtra(Constants.REVIEW_INTENT, movieObject);
                startActivity(reviewActivity);
            }
        });
        markButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markFavTxt.getText().equals(getString(R.string.markedText))) {
                    markButton.setBackgroundColor(Color.parseColor("#00BFA5"));
                    markFavTxt.setText(getString(R.string.markText));
                    Favorite fav = AAUtility.get(Favorite.class, "movieid = ?", movieObject.getMovieId());
                    if (fav != null)
                        fav.delete();
                    getAll();

                } else {

                    markButton.setBackgroundColor(Color.parseColor("#fbd80c"));
                    markFavTxt.setText(getString(R.string.markedText));
                    addFavorite(movieObject);

                }
            }
        });
    }

    private void addFavorite(Movie movie){
        Favorite favMovie = new Favorite();
        favMovie.setId(movie.getMovieId());
        favMovie.setIsFavorite(true);
        favMovie.setBackdropPath(movie.getBackdropPath());
        favMovie.setOverview(movie.getOverview());
        favMovie.setPopularity(movie.getPopularity());
        favMovie.setPosterPath(movie.getPosterPath());
        favMovie.setReleaseDate(movie.getReleaseDate());
        favMovie.setTitle(movie.getTitle());
        favMovie.setVoteAverage(movie.getVoteAverage());
        favMovie.setVoteCount(movie.getVoteCount());
        favMovie.save();
        getAll();
    }






    public class GetTrailers extends AsyncTask<String, Void, ArrayList<Trailer>> {
        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailerData = null;
            try {
                Uri buildUri = Uri.parse(Constants.URL_REVIEW_PATH + params[0] + Constants.VIDEO_PARAM + Constants.API_REVIEW_PARAM + Constants.API_KEY).buildUpon().build();
                URL url = new URL(buildUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                trailerData = buffer.toString();
                Log.v(Constants.LOG_TAG, "Trailer Data " + trailerData);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(Constants.LOG_TAG, "Error Closing stream", e);
                    }
                }
            }
            try {
                return getTrailerData(trailerData);
            } catch (JSONException e) {
                Log.e(Constants.LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        private ArrayList<Trailer> getTrailerData(String trailerData) throws JSONException {
            JSONObject trailerObject = new JSONObject(trailerData);
            JSONArray trailerArray = trailerObject.getJSONArray(Constants.RESULTS);
            ArrayList<Trailer> trailerCollection = new ArrayList<>();
            for (int i = 0; i < trailerArray.length(); i++) {
                Trailer trailer = new Trailer();
                JSONObject trailerResults = trailerArray.getJSONObject(i);
                trailer.setKey(trailerResults.getString(Constants.KEY));
                trailer.setType(trailerResults.getString(Constants.TYPE));
                trailer.setName(trailerResults.getString(Constants.NAME));
                trailerCollection.add(trailer);
            }
            return trailerCollection;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(getString(R.string.txtloading));
            pd.show();

        }
        @Override
        protected void onPostExecute(final ArrayList<Trailer> trailer) {
            Log.d(Constants.LOG_TAG, trailer.toString());
            trailerList = (ListView)getActivity().findViewById(R.id.listViewTrailer);
            trailerList.setAdapter(new TrailerListAdapter(getContext() ,trailer));
            trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer trailerPosObject = trailer.get(position);
                    String key = trailerPosObject.getKey();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.API_TRAILERS + key));
                    startActivity(i);
                }
            });
            pd.dismiss();

        }
    }
    public void getAll(){
        List<Favorite> getMovies = AAUtility.getList(Favorite.class);
        for(int x=0; x < getMovies.size(); x++){
            Log.d("check", Integer.toString(x) +" : "+ getMovies.get(x).getTitle());
        }
    }


}
