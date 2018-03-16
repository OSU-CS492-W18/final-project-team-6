package com.example.me.war;

import android.content.ContentValues;
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
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by me on 3/12/2018.
 */

public class PlayActivity extends AppCompatActivity{
    String DECK_ID = "";
    //DO NOT USE FOR GAME PLAY USED JUST FOR CARD SETUP
    ArrayList<Card> mComputerSetUpCardsList = new ArrayList<>();
    ArrayList<Card> mPlayerSetUpCardsList = new ArrayList<>();

    ArrayList<Card> warComputerList = new ArrayList<>();
    ArrayList<Card> warPlayerList = new ArrayList<>();

    String COMPUTER_PILE_ID = "computer";
    String PLAYER_PILE_ID = "player";
    //USE FOR NORMAL GAME PLAY
    Card mCurrentComputerCard = null;
    Card mCurrentPlayerCard = null;
    //USE FOR NORMAL GAME PLAY
    int numOfComputerCards = 0;
    int numOfPlayerCards = 0;

    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ((Button) findViewById(R.id.flipButton)).setEnabled(false);
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

        ArrayList<Integer> savedGames = getDB();
        addWinToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
//        Log.e("onCreate ", "Wins: " + savedGames.get(0) + "\nLosses: " + savedGames.get(1) + "\nGames Played: " + savedGames.get(2));
    }

    public void doFlip(View view) {
        ((Button) findViewById(R.id.flipButton)).setEnabled(false);

        //get computer flip
        String getNewComputerCard = PlayWarUtils.getComputerCardURL(DECK_ID, COMPUTER_PILE_ID);
        new getNewComputerCardTask().execute(getNewComputerCard);
        //get player flip
        String getNewPlayerCard = PlayWarUtils.getPlayerCardURL(DECK_ID, PLAYER_PILE_ID);
        new getNewPlayerCardTask().execute(getNewPlayerCard);
        if (mCurrentComputerCard != null && mCurrentPlayerCard != null) {
            //remove one card from players card count
            TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
            numOfPlayerCards = numOfPlayerCards - 1;
            numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
            //remove on card from computer card count
            TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
            numOfComputerCards = numOfComputerCards - 1;
            numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
            Log.d("yup", mCurrentPlayerCard.getSuit());
            //show computer flip
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardComputer))
                    .execute(mCurrentComputerCard.getImage());
            //show player flip
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardPlayer))
                    .execute(mCurrentPlayerCard.getImage());
            //find the winner of this round
            findWinner(mCurrentComputerCard, mCurrentPlayerCard);
        } else {
            if (mCurrentComputerCard == null && mCurrentPlayerCard != null) {
                Log.d("YOU", "LOST");
                //computer won
                // TODO: 3/12/2018 display the you lost screen probably in a frame view like how loading symbol is done in weather
                // TODO: 3/12/2018 increment the lost count in sqlite by one
                ArrayList<Integer> savedGames = getDB();
                addLossToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
            }
            else if(mCurrentPlayerCard == null && mCurrentComputerCard != null) {
                Log.d("YOU", "WON");
                //you won
                // TODO: 3/12/2018 display the you won screen probably in a frame view like how loading symbol is done in weather
                // TODO: 3/12/2018 increment the win count in sqlite by one
                ArrayList<Integer> savedGames = getDB();
                addWinToDB(savedGames.get(0), savedGames.get(1), savedGames.get(2));
            }
            else{
                //Log.d("YOU", "TIED");
                //you tied
                // TODO: 3/12/2018 this cant happen someone must have cards
            }
        }
        //let the user beable to click the button again
        ((Button) findViewById(R.id.flipButton)).setEnabled(true);
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

    public class getNewComputerCardTask extends AsyncTask<String, Void, String> {
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
                mCurrentComputerCard = PlayWarUtils.parseCardJSON(s);
            }
        }
    }

    public class getNewPlayerCardTask extends AsyncTask<String, Void, String> {
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
            mCurrentPlayerCard = PlayWarUtils.parseCardJSON(s);
        }
    }

    public class addTwoCardsToDeckTask extends AsyncTask<String, Void, String> {
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
            //mCurrentPlayerCard = PlayWarUtils.parseCardJSON(s);
        }
    }

    public void doWar(){
//        String getNewComputerCard = PlayWarUtils.getComputerCardURL(DECK_ID, COMPUTER_PILE_ID);
//        String getNewPlayerCard = PlayWarUtils.getComputerCardURL(DECK_ID, PLAYER_PILE_ID);
//        String results = null;
//        Card computerCard1 = null;
//        Card computerCard2 = null;
//        Card computerCard3 = null;
//        Card computerCard4 = null;
//        Card playerCard1 = null;
//        Card playerCard2 = null;
//        Card playerCard3 = null;
//        Card playerCard4 = null;
//        try {
//            results = NetworkUtils.doHTTPGet(getNewComputerCard);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(results != null){
//            computerCard1 = PlayWarUtils.parseCardJSON(results);
//        }
//        results = null;
//        try {
//            results = NetworkUtils.doHTTPGet(getNewComputerCard);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(results != null){
//            computerCard2 = PlayWarUtils.parseCardJSON(results);
//        }
//        results = null;
//        try {
//            results = NetworkUtils.doHTTPGet(getNewComputerCard);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(results != null){
//            computerCard3 = PlayWarUtils.parseCardJSON(results);
//        }
//        results = null;
//        try {
//            results = NetworkUtils.doHTTPGet(getNewComputerCard);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(results != null) {
//            computerCard4 = PlayWarUtils.parseCardJSON(results);
//        }
//        results = null;
//        try {
//            results = NetworkUtils.doHTTPGet(getNewPlayerCard);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        if(results != null) {
//            playerCard1 = PlayWarUtils.parseCardJSON(results);
//        }
//        try {
//            results = NetworkUtils.doHTTPGet(getNewPlayerCard);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        if(results != null) {
//            playerCard2 = PlayWarUtils.parseCardJSON(results);
//        }
//        try {
//            results = NetworkUtils.doHTTPGet(getNewPlayerCard);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        if(results != null) {
//            playerCard3 = PlayWarUtils.parseCardJSON(results);
//        }
//        try {
//            results = NetworkUtils.doHTTPGet(getNewPlayerCard);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        if(results != null) {
//            playerCard4 = PlayWarUtils.parseCardJSON(results);
//        }
//
//        warComputerList.add(computerCard1);
//        warComputerList.add(computerCard2);
//        warComputerList.add(computerCard3);
//        warComputerList.add(computerCard4);
//        warPlayerList.add(playerCard1);
//        warPlayerList.add(playerCard2);
//        warPlayerList.add(playerCard3);
//        warPlayerList.add(playerCard4);
//        numOfPlayerCards = numOfPlayerCards - warPlayerList.size();
//        numOfPlayerCards = numOfPlayerCards - warComputerList.size();
//        if(warComputerList.size() > warPlayerList.size()){
//            // TODO: 3/15/2018 computer wins END
//        }
//        else if(warComputerList.size() < warPlayerList.size()){
//            // TODO: 3/15/2018 player wins END
//        }
//        else{
//            findWarWinner();
//        }

    }

    public void findWarWinner(){
//        Card lastComputerCard = warComputerList.get(warComputerList.size()-1);
//        Card lastPlayerCard = warPlayerList.get(warPlayerList.size()-1);
//        int computerIntValue = getIntValue(lastComputerCard);
//        int playerIntValue = getIntValue(lastPlayerCard);
//        if(computerIntValue > playerIntValue){
//            Log.d("computer", "won this war");
//            TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
//            numOfComputerCards = numOfComputerCards + warComputerList.size() + warComputerList.size();
//            numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
//            String addTwoCardsToComputerURL = PlayWarUtils.addTwoCardsToDeck(DECK_ID, COMPUTER_PILE_ID, compCard.getCode(), playerCard.getCode());
//            new addTwoCardsToDeckTask().execute(addTwoCardsToComputerURL);
//        }
//        else if(computerIntValue < playerIntValue){
//            Log.d("computer", "lost the war");
//
//        }
//        else{
//            doWar();
//            Log.d("computer", "tied!!");
//        }
    }

    public void findWinner(Card compCard, Card playerCard) {
        //"ACE" "KING" "QUEEN" "JACK" 10 9 8 7 6 5 4 3 2
        int computerIntValue = getIntValue(compCard);
        int playerIntValue = getIntValue(playerCard);

        if(computerIntValue > playerIntValue){
            Log.d("computer", "won this round");
            TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
            numOfComputerCards = numOfComputerCards + 2;
            numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
            String addTwoCardsToComputerURL = PlayWarUtils.addTwoCardsToDeck(DECK_ID, COMPUTER_PILE_ID, compCard.getCode(), playerCard.getCode());
            new addTwoCardsToDeckTask().execute(addTwoCardsToComputerURL);
        }
        else if(computerIntValue < playerIntValue){
            Log.d("computer", "lost this round");
            TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
            numOfPlayerCards = numOfPlayerCards + 2;
            numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
            String addTwoCardsToPlayerURL = PlayWarUtils.addTwoCardsToDeck(DECK_ID, PLAYER_PILE_ID, compCard.getCode(), playerCard.getCode());
            new addTwoCardsToDeckTask().execute(addTwoCardsToPlayerURL);
        }
        else{
            Log.d("computer", "tied with player");
            doWar();
            //finish(); //currently just brings you back to the home screen
            // TODO: 3/12/2018 display war on screen
            // TODO: 3/12/2018 run logic in the background
            // TODO: 3/12/2018 call do flip
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
