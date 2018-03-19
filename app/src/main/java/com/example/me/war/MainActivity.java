package com.example.me.war;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WarDBHelper dbHelper = new WarDBHelper(this);
        mDB = dbHelper.getReadableDatabase();
        addToDB();
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
    private long addToDB(){
        ContentValues row = new ContentValues();
        row.put(WarContract.savedGames.COLUMN_GAMES_WON, 0);
        row.put(WarContract.savedGames.COLUMN_GAMES_LOST, 0);
        row.put(WarContract.savedGames.COLUMN_GAMES_PLAYED, 0);
        return mDB.insert(WarContract.savedGames.TABLE_NAME, null, row);
    }

}

/*  PROJECT REQUIREMENTS AND TODOLISTS

    It should have multiple activities the user can navigate between.
        PAGES NEEDED:
            MainActiviy
            PlayActivity
            StatsActivity

    It should use at least one implicit intent to launch another app.
        Share Victory or Loss within PlayActivity
        Share Stats within StatsActivity

    It should communicate via HTTP(s) with a third-party API to provide data for the app and optionally to send data back to the API.
        DeckOfCards API

    It must implement activity lifecycle methods to ensure that activity-related data is handled elegantly through lifecycle events.


    It should either store user preferences (via SharedPreferences) or store data in device storage (using SQLite). You may do both of these things if you want.


    It should have a polished, well-styled user interface.
        MEH

*/
