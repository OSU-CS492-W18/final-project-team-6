package com.example.me.war;

import android.provider.BaseColumns;

/**
 * Created by travi on 3/14/2018.
 */

public class WarContract {
    private WarContract() {}

    public static class savedGames implements BaseColumns {
        public static final String TABLE_NAME = "savedGames";
        public static final String COLUMN_GAMES_PLAYED = "gamesPlayed";
        public static final String COLUMN_GAMES_WON = "gamesWon";
        public static final String COLUMN_GAMES_LOST = "gamesLost";

    }
}
