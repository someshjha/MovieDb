package com.sjha.moviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* if(findViewById(R.id.fragment_main)!= null){

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_main, new OverviewFragment(), Constants.LOG_TAG)
                        .commit();
            }

        }else {
            mTwoPane = false;
        }*/

    }


}
