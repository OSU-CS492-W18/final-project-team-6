package com.example.me.war;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class StatsActivity extends AppCompatActivity {

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    private RetainedFragment mRetainedFragment;

    private TextView mWinsTV;
    private TextView mLossesTV;
    private TextView mGamesPlayedTV;
    private TextView mWinLossRatioTV;
    Button btn;
    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        WarDBHelper dbHelper = new WarDBHelper(this);
        mDB = dbHelper.getReadableDatabase();

        mWinsTV = findViewById(R.id.tv_stats_wins);
        mLossesTV = findViewById(R.id.tv_stats_losses);
        mGamesPlayedTV = findViewById(R.id.tv_stats_games_played);
        mWinLossRatioTV= findViewById(R.id.tv_stats_win_loss_ratio);

        // find the retained fragment on activity restarts
        android.app.FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        // create the fragment and data the first time
        if (mRetainedFragment == null) {
            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            mRetainedFragment.setData(loadMyData());
        }
        Log.e("wins", Integer.toString(mRetainedFragment.getData().win));

        btn = (Button)findViewById(R.id.share_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                ArrayList<Integer> data = getDB();
                intent.putExtra(Intent.EXTRA_SUBJECT, "Wins: " + data.get(0).toString() + "\nLosses: " + data.get(1).toString() + "\nGames played: " + data.get(2).toString());
                intent.putExtra(Intent.EXTRA_TEXT, "Here are my stats for War!");
                startActivity(Intent.createChooser(intent, "Share using: "));

            }
        });
        //ArrayList<Integer> savedGamse = getDB();
        //add1(savedGamse.get(0)); //--------------------------------------------------------ADD1
        setTVDB();
    }

    private retaineData loadMyData() {
        ArrayList<Integer> savedGames = getDB();
        retaineData newRetaine = new retaineData();
        newRetaine.win = savedGames.get(0);
        newRetaine.loss = savedGames.get(1);
        newRetaine.GamesPlayed = savedGames.get(2);
        return newRetaine;
    }

    private ArrayList<Integer> getDB(){
        Cursor cursor = mDB.query(
                WarContract.savedGames.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<Integer> savedGames = new ArrayList<>();
        Log.e("StatsActivity","getDB");
        while(cursor.moveToNext()){
            int wins = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_WON));
            savedGames.add(wins); // games[0]
            //Log.e("WINS # SIZE: ", Integer.toString(savedGames.size()));
            int loss = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_LOST));
            savedGames.add(loss); //games[1]
            int played = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_PLAYED));
            savedGames.add(played); //games[2]
        }
        cursor.close();

        return savedGames;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void add1(int vals){
        ContentValues row = new ContentValues();
        row.put(WarContract.savedGames.COLUMN_GAMES_WON, vals+1);
        row.put(WarContract.savedGames.COLUMN_GAMES_LOST, vals+1);
        row.put(WarContract.savedGames.COLUMN_GAMES_PLAYED, vals+1);
        mDB.update(WarContract.savedGames.TABLE_NAME, row, null, null);

    }


    private void setTVDB(){
        int mwin = mRetainedFragment.getData().win;
        int mloss = mRetainedFragment.getData().loss;
        int mgamesplayed = mRetainedFragment.getData().GamesPlayed;
        if(mwin > 0 ){
            mWinsTV.setText("Victories: " + mwin);
        }
        else{
            mWinsTV.setText("Victories: " + 0);
        }
        if(mloss > 0 ){
            mLossesTV.setText("Losses: " + mloss);
        }
        else{
            mLossesTV.setText("Losses: " + 0);
        }
        if(mgamesplayed > 0 ){
            mGamesPlayedTV.setText("Played: " + mgamesplayed);
        }
        else{
            mGamesPlayedTV.setText("Played: " + 0);
        }
    }
}
