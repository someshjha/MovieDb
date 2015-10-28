package com.sjha.moviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_overview, new OverviewFragment())
                    .commit();
        }*/

    }
}
