package com.example.me.war;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Created by David on 3/13/2018.
 */

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

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
