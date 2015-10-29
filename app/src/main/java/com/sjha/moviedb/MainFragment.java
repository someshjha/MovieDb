package com.sjha.moviedb;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.sjha.moviedb.Adapter.DialogAdapter;
import com.sjha.moviedb.Adapter.GetMovies;
import com.sjha.moviedb.Adapter.ImageAdapter;
import com.sjha.moviedb.Model.Favorite;
import com.sjha.moviedb.Model.Movie;
import com.sjha.moviedb.Model.MovieList;

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
public class MainFragment extends Fragment {

    private SwipeRefreshLayout mSwipeContainer;
    private GridView mMoviesList;
    private String mSortBy;
    private MovieList movieListFav;
    public MainFragment() {

    }



    public void getAll(){
        List<Movie> getMovies = AAUtility.getList(Movie.class);
        for(int x=0; x < getMovies.size(); x++){
            Log.d("check", Integer.toString(x) +" : "+ getMovies.get(x).getTitle());
        }
    }

    private List<Movie> getMoviesFromStorage(){

        return new Select()
                .from(Movie.class)
                .orderBy("releaseDate DESC")
                .execute();

    }


    private List<Movie> castFavToMovie(List<Favorite> movie){

        ArrayList<Movie> resultList = new ArrayList<>();
        for (int x= 0; x<movie.size(); x++ ) {
            Movie favMovie = new Movie();

            favMovie.setId(movie.get(x).getMovieId());
            favMovie.setIsFavorite(true);
            favMovie.setBackdropPath(movie.get(x).getBackdropPath());
            favMovie.setOverview(movie.get(x).getOverview());
            favMovie.setPopularity(movie.get(x).getPopularity());
            favMovie.setPosterPath(movie.get(x).getPosterPath());
            favMovie.setReleaseDate(movie.get(x).getReleaseDate());
            favMovie.setTitle(movie.get(x).getTitle());
            favMovie.setVoteAverage(movie.get(x).getVoteAverage());
            favMovie.setVoteCount(movie.get(x).getVoteCount());
            resultList.add(favMovie);
        }

        return resultList;
    }

    private void setGridForImage(final List<Movie> movieList) {

        String[] urlArray = new String[movieList.size()];
        if(movieList != null) {
            for (int i = 0; i < movieList.size(); i++) {
                urlArray[i] = (Constants.IMAGE_URL + movieList.get(i).getPosterPath());
            }
            mMoviesList.setAdapter(new ImageAdapter(getActivity(), urlArray));
            mMoviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent detail = new Intent(getActivity(), OverviewActivity.class);
                    Movie movieObject =  movieList.get(position);
                    detail.putExtra(Constants.DETAIL_INTENT, movieObject);
                    getActivity().startActivity(detail);
                }
            });
        }

    }


    private void initialize(View rootView){


        mSortBy = Constants.SORT_OPTION_POP;
        ImageButton mSettingBtn = (ImageButton)rootView.findViewById(R.id.btnSetting);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
               View view = mInflater.inflate(R.layout.dialog_list_view, null);
               final Dialog dialog = new Dialog(getContext());
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
               dialog.setContentView(view);
               Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
               btnCancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View arg0) {
                       dialog.dismiss();
                   }

               });

               String[] sortLabel = {getString(R.string.mostPopularLbl),
                       getString(R.string.highestRatedLbl),
                       getString(R.string.revenueLbl),
                       getString(R.string.releaseDateLbl),
                       getString(R.string.originalTitleLbl),
                       getString(R.string.primaryReleaseDateLbl),
                       getString(R.string.favMovie)
               };
               final ListView listConference = (ListView) view.findViewById(R.id.sortLabelList);
               listConference.setAdapter(new DialogAdapter(getContext(), R.layout.list_item_sort, R.id.sortText, sortLabel));
               listConference.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                           mSortBy = listConference.getItemAtPosition(position).toString();
                           GetMoviesFromURL getMovies = new GetMoviesFromURL();
                           if (mSortBy.equals(getString(R.string.mostPopularLbl))) {
                               mSortBy = Constants.SORT_OPTION_POP;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           } else if (mSortBy.equals(getString(R.string.highestRatedLbl))) {
                               mSortBy = Constants.SORT_OPTION_RATING;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           } else if (mSortBy.equals(getString(R.string.revenueLbl))) {
                               mSortBy = Constants.SORT_OPTION_REVENUE;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           } else if (mSortBy.equals(getString(R.string.releaseDateLbl))) {
                               mSortBy = Constants.SORT_OPTION_RELEASE_DATE;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           }else if (mSortBy.equals(getString(R.string.originalTitleLbl))) {
                               mSortBy = Constants.SORT_OPTION_ORIGINAL_TITLE;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           }else if (mSortBy.equals(getString(R.string.primaryReleaseDateLbl))) {
                               mSortBy = Constants.SORT_OPTION_PRIMARY_RELEASE_DATE;
                               AAUtility.deleteList(Movie.class);
                               getMovies.execute(mSortBy);
                           }else if (mSortBy.equals(getString(R.string.favMovie))){
                               getFavoriteMovie();
                           }
                           dialog.dismiss();
                       }
               });

               dialog.show();

           }
        });
        mMoviesList=(GridView)rootView.findViewById(R.id.moviesList);
        mSwipeContainer=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(R.color.blue_dark);
        mSwipeContainer.setOnRefreshListener(mOnPullToRefreshListener);


    }


    private void getFavoriteMovie(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MovieList favMovieList = new MovieList();
                favMovieList.setMoviesFavorite();
                List<Favorite> getMovie = favMovieList.getFavMovies();
                if (getMovie.size() != 0) {
                    setGridForImage(castFavToMovie(getMovie));
                } else
                    Toast.makeText(getActivity(), "No Favorite Movie", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        MovieList movieListPop = (MovieList)intent.getSerializableExtra(Constants.MOVIE_POP);
        movieListFav = (MovieList)intent.getSerializableExtra(Constants.MOVIE_FAV);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(rootView);
        if(isNetworkConnected()){
            mSwipeContainer.setVisibility(View.VISIBLE);
            GetMovies getMoviesFromURL = new GetMovies();
            AAUtility.deleteList(Movie.class);
            getMoviesFromURL.execute(mSortBy);
            List<Movie> getMovie = movieListPop.getMovies();

            if(getMovie.size() != 0){

                setGridForImage(getMovie);

            }
        }else{
            List<Movie> getMovie = getMoviesFromStorage();
            if(getMovie.size() != 0){
                setGridForImage(getMovie);

            }
        }
        return rootView;

    }


        private SwipeRefreshLayout.OnRefreshListener mOnPullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh () {
            mSwipeContainer.setRefreshing(false);
            if(isNetworkConnected()){
                mSwipeContainer.setVisibility(View.VISIBLE);
                GetMoviesFromURL getMoviesFromURL = new GetMoviesFromURL();
                if(mSortBy.equals(getString(R.string.favMovie))){
                    getFavoriteMovie();
                }else
                    getMoviesFromURL.execute(mSortBy);
            }else{
                Toast.makeText(getContext(), getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            }
        }


    };


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }




    public class GetMoviesFromURL extends AsyncTask<String, Void, ArrayList<Movie>> {

        ProgressDialog pd = new ProgressDialog(getActivity());



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
            getAll();
            return pictureCollection;

        }

        public GetMoviesFromURL() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(getString(R.string.txtloading));
            pd.show();
        }

        @Override
        protected void onPostExecute(final ArrayList<Movie> movies) {
            String[] urlArray = new String[movies.size()];
            if(movies != null) {
                for (int i = 0; i < movies.size(); i++) {
                    urlArray[i] = (Constants.IMAGE_URL + movies.get(i).getPosterPath());
                }
                mMoviesList.setAdapter(new ImageAdapter(getActivity(), urlArray));
                mMoviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent detail = new Intent(getActivity(), OverviewActivity.class);
                        Movie movieObject =  movies.get(position);
                        detail.putExtra(Constants.DETAIL_INTENT, movieObject);
                        getActivity().startActivity(detail);
                    }
                });
            }
            pd.dismiss();
        }
    }





}
