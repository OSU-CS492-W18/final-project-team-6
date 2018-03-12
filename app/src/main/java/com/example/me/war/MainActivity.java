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

    public void StartPlayActivity(){
        Intent playIntent = new Intent(this, PlayActivity.class );
        startActivity(playIntent);
    }
}
