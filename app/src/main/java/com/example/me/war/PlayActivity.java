package com.example.me.war;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by me on 3/12/2018.
 */

public class PlayActivity extends AppCompatActivity{
    String DECK_ID = "";
    ArrayList<Card> mComputerSetUpCardsList = new ArrayList<>();
    ArrayList<Card> mPlayerSetUpCardsList = new ArrayList<>();
    String COMPUTER_PILE_ID = "computer";
    String PLAYER_PILE_ID = "player";
    int numOfComputerCards = 0;
    int numOfPlayerCards = 0;

    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ((Button) findViewById(R.id.flipButton)).setEnabled(false);

        LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
        warLayout.setVisibility(LinearLayout.VISIBLE);
        LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
        WinLossLayout.setVisibility(LinearLayout.INVISIBLE);

        ImageView temp = (ImageView)(findViewById(R.id.PlayingCardComputerDeck));
        temp.setImageResource(R.mipmap.ic_cardback);
        ImageView temp2 = (ImageView)(findViewById(R.id.PlayingCardComputer));
        temp2.setImageResource(R.mipmap.ic_cardback);
        ImageView temp3 = (ImageView)(findViewById(R.id.PlayingCardPlayer));
        temp3.setImageResource(R.mipmap.ic_cardback);
        ImageView temp4 = (ImageView)(findViewById(R.id.PlayingCardPlayerDeck));
        temp4.setImageResource(R.mipmap.ic_cardback);
        //gets the new deck url
        String newDeckURL = PlayWarUtils.getNewDeck();
        //calls to set the new deck id
        Log.e("here","here_________________1");
        new SetNewDeckTask().execute(newDeckURL);
        Log.e("here","here_________________2");

        WarDBHelper dbHelper = new WarDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
    }
    public void onPlayClick(View view) {
        StartPlayActivity();
    }

    public void StartPlayActivity(){
        Intent playIntent = new Intent(this, PlayActivity.class );
        startActivity(playIntent);
    }



    public Card pullCard(String playerOrComputer){
        if(playerOrComputer == "player") {
            if(numOfPlayerCards > 0) {
                Card tempCard = mPlayerSetUpCardsList.remove(0);
                TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
                numOfPlayerCards = mPlayerSetUpCardsList.size();
                Log.d("player size", "after drawn " + Integer.toString(numOfPlayerCards));
                numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
                return  tempCard;
            }
            else {
                ((Button) findViewById(R.id.flipButton)).setEnabled(false);
                LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
                warLayout.setVisibility(LinearLayout.INVISIBLE);
                LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
                WinLossLayout.setVisibility(LinearLayout.VISIBLE);
                TextView winloss = (TextView) findViewById(R.id.WinLossText);
                winloss.setText("Lost");
                ArrayList<Integer> savedGames = getDB();
                addLossToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
                // TODO: 3/18/2018 you ran out of cards
                return null;
            }
        }
        else {
            if(numOfComputerCards > 0){
                Card tempCard = mComputerSetUpCardsList.remove(0);
                TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
                numOfComputerCards = mComputerSetUpCardsList.size();
                numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
                Log.d("computer size", "after drawn " + Integer.toString(numOfComputerCards));
                return  tempCard;
            }
            else {
                ((Button) findViewById(R.id.flipButton)).setEnabled(false);
                LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
                warLayout.setVisibility(LinearLayout.INVISIBLE);
                LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
                WinLossLayout.setVisibility(LinearLayout.VISIBLE);
                // TODO: 3/18/2018 you ran out of cards
                TextView winloss = (TextView) findViewById(R.id.WinLossText);
                winloss.setText("Won");
                ArrayList<Integer> savedGames = getDB();
                addWinToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
                return null;
            }
        }
    }

    public void findWarWinner(ArrayList<Card> playerWarList, ArrayList<Card> computerWarList){
        //"ACE" "KING" "QUEEN" "JACK" 10 9 8 7 6 5 4 3 2
        if(computerWarList.get(computerWarList.size()-1) == null){
            // TODO: 3/18/2018 computer lost
            ((Button) findViewById(R.id.flipButton)).setEnabled(false);
            LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
            warLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
            WinLossLayout.setVisibility(LinearLayout.VISIBLE);
            TextView winloss = (TextView) findViewById(R.id.WinLossText);
            winloss.setText("Won");
            ArrayList<Integer> savedGames = getDB();
            addWinToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
        }
        else if(playerWarList.get(playerWarList.size()-1) == null){
            // TODO: 3/18/2018 player lost
            ((Button) findViewById(R.id.flipButton)).setEnabled(false);
            LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
            warLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
            WinLossLayout.setVisibility(LinearLayout.VISIBLE);
            TextView winloss = (TextView) findViewById(R.id.WinLossText);
            winloss.setText("Lost");
            ArrayList<Integer> savedGames = getDB();
            addLossToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
        }
        else {
            int computerIntValue = getIntValue(computerWarList.get(computerWarList.size() - 1));
            Log.e("computer value", Integer.toString(computerIntValue));
            int playerIntValue = getIntValue(playerWarList.get(playerWarList.size() - 1));
            Log.e("player value", Integer.toString(playerIntValue));
            Log.e("computer size", Integer.toString(mComputerSetUpCardsList.size()));
            Log.e("player size", Integer.toString(mPlayerSetUpCardsList.size()));
            if (computerIntValue > playerIntValue) {
                Log.d("computer", "won this war");
                mComputerSetUpCardsList.addAll(computerWarList);
                mComputerSetUpCardsList.addAll(playerWarList);
                TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
                numOfComputerCards = mComputerSetUpCardsList.size();
                numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
                Log.e("computer new size", Integer.toString(mComputerSetUpCardsList.size()));
                Log.e("player new size", Integer.toString(mPlayerSetUpCardsList.size()));
            } else if (computerIntValue < playerIntValue) {
                Log.d("computer", "lost the war");
                mPlayerSetUpCardsList.addAll(playerWarList);
                mPlayerSetUpCardsList.addAll(computerWarList);
                TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
                numOfPlayerCards = mPlayerSetUpCardsList.size();
                numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
                Log.e("computer new size", Integer.toString(mComputerSetUpCardsList.size()));
                Log.e("player new size", Integer.toString(mPlayerSetUpCardsList.size()));

            } else {
                Log.d("WARRRRRRRRR again", "!!!!!!!!!!!!!!!!!!");
                LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
                warLayout.setVisibility(LinearLayout.INVISIBLE);
                LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
                WinLossLayout.setVisibility(LinearLayout.VISIBLE);
                TextView winloss = (TextView) findViewById(R.id.WinLossText);
                winloss.setText("Two wars in a row. No one wins");
                TextView temp = (TextView) findViewById(R.id.you);
                temp.setText("");
            }
        }
    }

    public void doWar(Card computerCard, Card playerCard){
        ArrayList<Card> playerWarDeck = new ArrayList<>();
        ArrayList<Card> computerWarDeck = new ArrayList<>();

        Log.d("player size", "before drawn " + Integer.toString(numOfPlayerCards));
        playerWarDeck.add(playerCard);
        Log.d("player size", "before drawn " + Integer.toString(numOfPlayerCards));
        playerWarDeck.add(pullCard("player"));
        Log.d("player size", "before drawn " + Integer.toString(numOfPlayerCards));
        playerWarDeck.add(pullCard("player"));
        Log.d("player size", "before drawn " + Integer.toString(numOfPlayerCards));
        playerWarDeck.add(pullCard("player"));
        Log.d("computer size", "before drawn " + Integer.toString(numOfComputerCards));
        computerWarDeck.add(computerCard);
        Log.d("computer size", "before drawn " + Integer.toString(numOfComputerCards));
        computerWarDeck.add(pullCard("computer"));
        Log.d("computer size", "before drawn " + Integer.toString(numOfComputerCards));
        computerWarDeck.add(pullCard("computer"));
        Log.d("computer size", "before drawn " + Integer.toString(numOfComputerCards));
        computerWarDeck.add(pullCard("computer"));

        if(playerWarDeck.size() == 4 && computerWarDeck.size() == 4){
            findWarWinner(playerWarDeck, computerWarDeck);
        }
        else if(playerWarDeck.size() > computerWarDeck.size()){
            // TODO: 3/18/2018 player wins
            Log.e("PlayerWin", "------------------------");
            LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
            warLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
            WinLossLayout.setVisibility(LinearLayout.VISIBLE);
            TextView winloss = (TextView) findViewById(R.id.WinLossText);
            winloss.setText("Won");
            ArrayList<Integer> savedGames = getDB();
            addWinToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
        }
        else if(playerWarDeck.size() < computerWarDeck.size()){
            // TODO: 3/18/2018 computer wins
            Log.e("computer win", "----------------------------");
            LinearLayout warLayout = (LinearLayout) findViewById(R.id.FLWarGame);
            warLayout.setVisibility(LinearLayout.INVISIBLE);
            LinearLayout WinLossLayout = (LinearLayout) findViewById(R.id.FLWinLostScreen);
            WinLossLayout.setVisibility(LinearLayout.VISIBLE);
            TextView winloss = (TextView) findViewById(R.id.WinLossText);
            winloss.setText("Lost");
            ArrayList<Integer> savedGames = getDB();
            addLossToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
        }
        else{
            Log.d("this", "SHOULD NOT HAPPEN");
        }
    }

    public void findWinner(Card compCard, Card playerCard) {
        //"ACE" "KING" "QUEEN" "JACK" 10 9 8 7 6 5 4 3 2
        int computerIntValue = getIntValue(compCard);
        int playerIntValue = getIntValue(playerCard);
        if(computerIntValue > playerIntValue){
            Log.d("computer", "won this round");
            mComputerSetUpCardsList.add(compCard);
            mComputerSetUpCardsList.add(playerCard);
            TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
            numOfComputerCards = mComputerSetUpCardsList.size();
            numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
        }
        else if(computerIntValue < playerIntValue){
            Log.d("computer", "lost this round");
            mPlayerSetUpCardsList.add(compCard);
            mPlayerSetUpCardsList.add(playerCard);
            TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
            numOfPlayerCards = mPlayerSetUpCardsList.size();
            numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
        }
        else{
            Log.d("WARRRRRRRRR", "!!!!!!!!!!!!!!!!!!");
            doWar(compCard, playerCard);
        }
    }


    public void doFlip(View view) {
        //stops use of button
        ((Button) findViewById(R.id.flipButton)).setEnabled(false);
        Log.d("computer size", "before drawn " + Integer.toString(numOfComputerCards));
        Card currentComputerCard = pullCard("computer");
        Log.d("player size", "before drawn " + Integer.toString(numOfPlayerCards));
        Card currentPlayerCard = pullCard("player");
        //starts use of button
        if(currentComputerCard == null || currentPlayerCard == null){

        }
        else{
            ((Button) findViewById(R.id.flipButton)).setEnabled(true);
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardComputer)).execute(currentComputerCard.getImage());
            //show player flip
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardPlayer)).execute(currentPlayerCard.getImage());
            findWinner(currentComputerCard, currentPlayerCard);
        }
    }

    //thread to show the back of the card images
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    //thread to create a new deck
    public class SetNewDeckTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String getURL = urls[0];

            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(getURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Deck newDeck = PlayWarUtils.parseNewDeckJSON(s);
                DECK_ID = newDeck.getDeckID();
                Log.d("Deck ID", DECK_ID);

                //sets the computers pile
                String get26CardsURL = PlayWarUtils.get26Cards(DECK_ID);
                Log.e("here","here_________________3");
                new getComputerPileTask().execute(get26CardsURL);
            }
        }
    }

    //thread pull 26 cards from the deck for computer
    public class getComputerPileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String getURL = urls[0];

            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(getURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                mComputerSetUpCardsList = PlayWarUtils.parseNewPileJSON(s);
                numOfComputerCards = 26;
                TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
                numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
                //sets the players pile
                String get26CardsURL = PlayWarUtils.get26Cards(DECK_ID);
                Log.e("here","here_________________4");
                new GetPlayerPileTask().execute(get26CardsURL);
            }
        }
    }

    //thread pull 26 cards from the deck for player
    public class GetPlayerPileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String getURL = urls[0];

            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(getURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                mPlayerSetUpCardsList = PlayWarUtils.parseNewPileJSON(s);
                TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
                numOfPlayerCards = 26;
                numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));

                String setUpComputerPile = PlayWarUtils.setUpComputerPile(DECK_ID, COMPUTER_PILE_ID, mComputerSetUpCardsList);
                String setUpPlayerPile = PlayWarUtils.setUpPlayerPile(DECK_ID, PLAYER_PILE_ID, mPlayerSetUpCardsList);
                new SetComputerPileTask().execute(setUpComputerPile);
                new SetPlayerPileTask().execute(setUpPlayerPile);
                Log.e("here","here_________________5");
                ((Button) findViewById(R.id.flipButton)).setEnabled(true);
            }
        }
    }

    public class SetComputerPileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String getURL = urls[0];

            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(getURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    public class SetPlayerPileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String getURL = urls[0];

            String results = null;
            try {
                results = NetworkUtils.doHTTPGet(getURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    public int getIntValue(Card curCard){
        int i = 0;
        switch (curCard.getValue()){
            case "2": i     = 2;  break;
            case "3": i     = 3;  break;
            case "4": i     = 4;  break;
            case "5": i     = 5;  break;
            case "6": i     = 6;  break;
            case "7": i     = 7;  break;
            case "8": i     = 8;  break;
            case "9": i     = 9;  break;
            case "10": i    = 10; break;
            case "JACK": i  = 11; break;
            case "QUEEN": i = 12; break;
            case "KING": i  = 13; break;
            case "ACE": i   = 14; break;
            default: Log.e("Cur Value = ", curCard.getValue());  break;
        }
        return i;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDB.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDB.close();
    }

    public ArrayList<Integer> getDB(){
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
        Log.e("PlayActivity","getDB");
        while(cursor.moveToNext()){
            int wins = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_WON));
            savedGames.add(wins); // games[0]
            int loss = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_LOST));
            savedGames.add(loss); //games[1]
            int played = cursor.getInt(cursor.getColumnIndex(WarContract.savedGames.COLUMN_GAMES_PLAYED));
            savedGames.add(played); //games[2]
            Log.e("getDB: ", "Wins: " + savedGames.get(0) + "\nLosses: " + savedGames.get(1) + "\nGames Played: " + savedGames.get(2));
        }
        cursor.close();

        return savedGames;
    }

    private void addWinToDB(int wins, int losses, int gamesPlayed) {
        ContentValues values = new ContentValues();
        Log.e("addWinToDB ", "Wins: " + wins + "\nLosses: " + losses + "\nGames Played: " + gamesPlayed);
        values.put(WarContract.savedGames.COLUMN_GAMES_WON, wins + 1);
        values.put(WarContract.savedGames.COLUMN_GAMES_LOST, losses);
        values.put(WarContract.savedGames.COLUMN_GAMES_PLAYED, gamesPlayed + 1);
        mDB.update(WarContract.savedGames.TABLE_NAME, values, null, null);
    }

    private void addLossToDB(int wins, int loss, int gamesPlayed) {
        ContentValues values = new ContentValues();
        values.put(WarContract.savedGames.COLUMN_GAMES_WON, wins);
        values.put(WarContract.savedGames.COLUMN_GAMES_LOST, loss + 1);
        values.put(WarContract.savedGames.COLUMN_GAMES_PLAYED, gamesPlayed + 1);
        mDB.update(WarContract.savedGames.TABLE_NAME, values, null, null);
    }
}
