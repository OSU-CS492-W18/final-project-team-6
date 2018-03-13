package com.example.me.war;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // TODO: 3/12/2018 add activity for stats and add in sqlite to hold stats   
    // TODO: 3/12/2018 add the share stuff to the app 
    
    public void onPlayClick(View view) {
        StartPlayActivity();
    }

    public void onStatsClick(View view) {
        StartStatsActivity();
    }

    public void StartPlayActivity(){
        Intent playIntent = new Intent(this, PlayActivity.class );
        startActivity(playIntent);
    }

    public void StartStatsActivity() {
        Intent statsIntent = new Intent(this, StatsActivity.class );
        startActivity(statsIntent);
    }
}

//    It should have multiple activities the user can navigate between.
//    It should use at least one implicit intent to launch another app.
//    It should communicate via HTTP(s) with a third-party API to provide data for the app and optionally to send data back to the API.
//    It must implement activity lifecycle methods to ensure that activity-related data is handled elegantly through lifecycle events.
//    It should either store user preferences (via SharedPreferences) or store data in device storage (using SQLite). You may do both of these things if you want.
//    It should have a polished, well-styled user interface.

