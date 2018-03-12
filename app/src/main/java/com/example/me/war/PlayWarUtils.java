package com.example.me.war;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by me on 3/12/2018.
 */

public class PlayWarUtils {

    final static String DOC_BASE_URL = "https://deckofcardsapi.com/api/deck/";
    final static String CREATE_NEW_DECK_URL = "new/shuffle/?deck_count=1";
    final static String GET_26_CARDS_URL = "/draw/?count=26";
    final static String PILE_URL = "/pile/";
    final static String ADD_URL = "/add/?cards=";
    final static String GET_ONE_CARD = "/draw/bottom";
    //returns a url to create a new deck
    public static String getNewDeck(){
        return DOC_BASE_URL + CREATE_NEW_DECK_URL;
    }
    public static String get26Cards(String DeckId) {
        return DOC_BASE_URL + DeckId + GET_26_CARDS_URL;
    }
    public static String setUpComputerPile(String deckID, String computerID, ArrayList<Card> cards){
        String cardCodeList = cards.get(0).getCode();
        for(int i = 1; i < cards.size(); i++){
            cardCodeList = cardCodeList + "," + cards.get(i).getCode();
        }
        return DOC_BASE_URL + deckID + PILE_URL + computerID + ADD_URL + cardCodeList;
    }
    public static String setUpPlayerPile(String deckID, String playerID, ArrayList<Card> cards) {
        String cardCodeList = cards.get(0).getCode();
        for(int i = 1; i < cards.size(); i++){
            cardCodeList = cardCodeList + "," + cards.get(i).getCode();
        }
        return DOC_BASE_URL + deckID + PILE_URL + playerID + ADD_URL + cardCodeList;
    }

    public static String getComputerCardURL(String deckID, String computerID){
        return DOC_BASE_URL + deckID + PILE_URL + computerID + GET_ONE_CARD;
    }

    public static String getPlayerCardURL(String deckID, String playerID){
        return DOC_BASE_URL + deckID + PILE_URL + playerID + GET_ONE_CARD;
    }

    //parse a newly created deck
    public static Deck parseNewDeckJSON(String deckResultJSON) {
        try {
            Deck deck = new Deck();
            JSONObject deckResultObj = new JSONObject(deckResultJSON);
            deck.setDeckID(deckResultObj.getString("deck_id"));
            deck.setRemaining(deckResultObj.getInt("remaining"));
            deck.setIsShuffled(deckResultObj.getBoolean("shuffled"));
            deck.setWasSuccess(deckResultObj.getBoolean("success"));
            Log.d("returns: ", deckResultJSON);
            return deck;
        }catch (JSONException e){
            return null;
        }
    }
    public static Card parseCardJSON(String cardResultJSON){
        try{
            Card card = new Card();
            JSONObject cardResultObj = new JSONObject(cardResultJSON);
            JSONArray cardsList = cardResultObj.getJSONArray("cards");
            for (int i = 0; i <=0; i++){
                JSONObject jsonCard = cardsList.getJSONObject(i);
                card.setImage(jsonCard.getString("image"));
                card.setValue(jsonCard.getString("value"));
                card.setSuit(jsonCard.getString("suit"));
                card.setCode(jsonCard.getString("code"));
            }
            if(cardsList.length() == 0){
                return null;
            }
            else{
                return card;
            }
        }catch(JSONException e){
            return null;
        }
    }


    public static ArrayList<Card> parseNewPileJSON(String cardsResultJSON) {
        try{
            ArrayList<Card> cardList = new ArrayList<>();
            JSONObject cardsResultObj = new JSONObject(cardsResultJSON);
            JSONArray jsonCardList = cardsResultObj.getJSONArray("cards");
            for(int i = 0; i < jsonCardList.length(); i++){
                JSONObject jsonCard = jsonCardList.getJSONObject(i);
                Card newCard = new Card();
                newCard.setImage(jsonCard.getString("image"));
                newCard.setValue(jsonCard.getString("value"));
                newCard.setSuit(jsonCard.getString("suit"));
                newCard.setCode(jsonCard.getString("code"));
                cardList.add(newCard);
            }
            Log.d("returns: ", cardsResultJSON);
            return cardList;
        }catch(JSONException e){
            return null;
        }
    }
}
