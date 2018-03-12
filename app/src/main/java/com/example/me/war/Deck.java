package com.example.me.war;

/**
 * Created by me on 3/12/2018.
 */

public class Deck {
    private String DeckID;
    private Integer Remaining;
    private Boolean IsShuffled;
    private Boolean WasSuccess;

    public String getDeckID() {
        return DeckID;
    }

    public void setDeckID(String deckID) {
        DeckID = deckID;
    }

    public Integer getRemaining() {
        return Remaining;
    }

    public void setRemaining(Integer remaining) {
        Remaining = remaining;
    }

    public Boolean getIsShuffled() {
        return IsShuffled;
    }

    public void setIsShuffled(Boolean shuffled) {
        IsShuffled = shuffled;
    }

    public Boolean getWasSuccess() {
        return WasSuccess;
    }

    public void setWasSuccess(Boolean wasSuccess) {
        WasSuccess = wasSuccess;
    }
}