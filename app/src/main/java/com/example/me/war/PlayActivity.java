package com.example.me.war;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    Card mCurrentComputerCard = null;
    Card mCurrentPlayerCard = null;

    int numOfComputerCards = 0;
    int numOfPlayerCards = 0;

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
    }

    public void doFlip(View view) {
        ((Button) findViewById(R.id.flipButton)).setEnabled(false);

        //get computer flip
        String getNewComputerCard = PlayWarUtils.getComputerCardURL(DECK_ID, COMPUTER_PILE_ID);
        new getNewComputerCardTask().execute(getNewComputerCard);
        TextView numberOfPlayerCardsView = (TextView) findViewById(R.id.numbOfPlayerCards);
        numOfPlayerCards = numOfPlayerCards - 1;
        numberOfPlayerCardsView.setText(String.valueOf(numOfPlayerCards));
        TextView numberOfComputerCardsView = (TextView) findViewById(R.id.numbOfComputerCards);
        numOfComputerCards = numOfComputerCards - 1;
        numberOfComputerCardsView.setText(String.valueOf(numOfComputerCards));
        //get player flip
        String getNewPlayerCard = PlayWarUtils.getPlayerCardURL(DECK_ID, PLAYER_PILE_ID);
        new getNewPlayerCardTask().execute(getNewPlayerCard);
        if (mCurrentComputerCard != null && mCurrentPlayerCard != null) {
            Log.d("yup", mComputerSetUpCardsList.get(0).getSuit());
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardComputer))
                    .execute(mCurrentComputerCard.getImage());
            new DownloadImageTask((ImageView) findViewById(R.id.PlayingCardPlayer))
                    .execute(mCurrentPlayerCard.getImage());
            //show computer flip
            //show player flip
            findWinner(mCurrentComputerCard, mCurrentPlayerCard);

            //add cards to winning pile

        } else {
            if (mCurrentComputerCard != null) {
                //computer won
                Log.d("YOU", "LOST");
            } else {
                Log.d("YOU", "WON");
                //you won
            }
            // show screen
        }
        ((Button) findViewById(R.id.flipButton)).setEnabled(true);
        // TODO: 2/15/2018 play a round
        // TODO: 2/15/2018 at the end of a round check to see if one pile has 52 card if so diplay win screen with new buttons to start or share.
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
                Log.e("here","here_________________");
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

    public void findWinner(Card compCard, Card playerCard) {
        //"ACE" "KING" "QUEEN" "JACK" 10 9 8 7 6 5 4 3 2
        int computerIntValue = getIntValue(compCard);
        int playerIntValue = getIntValue(playerCard);

        if(computerIntValue > playerIntValue){
            Log.d("computer", "won this round");
            // add both cards to computer deck and increase size by two
        }
        else if(computerIntValue < playerIntValue){
            Log.d("computer", "lost this round");
        }
        else{
            Log.d("computer", "tied with player");
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
}
