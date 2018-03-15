package com.example.me.war;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by David on 3/13/2018.
 */

public class StatsActivity extends AppCompatActivity {

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
        ArrayList<Integer> savedGamse = getDB();
        add1(savedGamse.get(0));
        setTVDB();
//        mWinsTV.setText("Victories: HELLO WORLD I PLAY WAR AND WIN");
//        mLossesTV.setText("Defeats: DANG IT! I'VE LOST MY GAME");
//        mGamesPlayedTV.setText("Games Played: I'VE PLAYED THIS MANY GAMES");
//        mWinLossRatioTV.setText("Win - Loss Ratio: WIN/LOSS");

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mForecastLocation = sharedPreferences.getString(
//                getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default_value)
//        );
//        String temperatureUnitsValue = sharedPreferences.getString(
//                getString(R.string.pref_units_key),
//                getString(R.string.pref_units_default_value)
//        );
    }
    
    private ArrayList<Integer> getDB(){
        Cursor cursor = mDB.query(WarContract.savedGames.TABLE_NAME, null, null, null, null, null, null, null);

        ArrayList<Integer> savedGames = new ArrayList<>();
        Log.e("!!!!!!","hererererer");
        while(cursor.moveToNext()){
            int wins = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_WON));
            savedGames.add(wins); // games[0]
            Log.e("WINS # SIZE: ", Integer.toString(savedGames.size()));
            int loss = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_LOST));
            savedGames.add(loss); //games[1]
            int played = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_PLAYED));
            savedGames.add(played); //games[2]
        }
        cursor.close();
        return savedGames;

    }



    public void add1(int vals){
        ContentValues row = new ContentValues();
        row.put(WarContract.savedGames.COLUMN_GAMES_WON, vals+1);
        row.put(WarContract.savedGames.COLUMN_GAMES_LOST, vals+1);
        row.put(WarContract.savedGames.COLUMN_GAMES_PLAYED, vals+1);
        mDB.update(WarContract.savedGames.TABLE_NAME, row, null, null);

    }
    private void setTVDB(){
        ArrayList<Integer> savedGames = getDB();
        if(savedGames.size() > 0) {
            int wins = savedGames.get(0);
            int loss = savedGames.get(1);
            int played = savedGames.get(2);
            mWinsTV.setText("Victories: " + wins);
            mLossesTV.setText("Losses: " + loss);
            mGamesPlayedTV.setText("Played: " + played);
        }else{
            mWinsTV.setText("Victories: " + 0);
            mLossesTV.setText("Losses: " + 0);
            mGamesPlayedTV.setText("Played: " + 0);
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.forecast_item_detail, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_share:
//                shareForecast();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    public void shareForecast() {
//        if (mForecastItem != null) {
//            String shareText = "Weather for " + mForecastLocation +
//                    ", " + DATE_FORMATTER.format(mForecastItem.dateTime) +
//                    ": " + mForecastItem.temperature + mTemperatureUnitsAbbr +
//                    " - " + mForecastItem.description +
//                    " " + FORECAST_HASHTAG;
//            ShareCompat.IntentBuilder.from(this)
//                    .setType("text/plain")
//                    .setText(shareText)
//                    .setChooserTitle(R.string.share_chooser_title)
//                    .startChooser();
//        }
//    }
}
