package com.sjha.moviedb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sjha.moviedb.Model.Movie;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private boolean mTwoPane;
    private static final String OVERVIEWFRAGMENT_TAG = "OFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_overview)!= null){

            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_overview, new OverviewFragment(), OVERVIEWFRAGMENT_TAG)
                        .commit();
            }

        }else {
            mTwoPane = false;
        }

    }


    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putSerializable(OverviewFragment.OVERVIEW_MOVIE, movie);

            OverviewFragment fragment = new OverviewFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_overview, fragment, OVERVIEWFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, OverviewActivity.class)
                    .putExtra(Constants.DETAIL_INTENT, movie);
            startActivity(intent);
        }
    }
}
