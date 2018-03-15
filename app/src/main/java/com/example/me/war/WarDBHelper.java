package com.example.me.war;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by travi on 3/14/2018.
 */

public class WarDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "warGames.db";
    private static final int DATABASE_VERSION = 1;

    public WarDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GAMES_TABLE =
                "CREATE TABLE " + WarContract.savedGames.TABLE_NAME + "(" +
                        "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WarContract.savedGames.COLUMN_GAMES_PLAYED + " INTEGER, " +
                        WarContract.savedGames.COLUMN_GAMES_WON + " INTEGER, " +
                        WarContract.savedGames.COLUMN_GAMES_LOST + " INTEGER" +
                        ");";
        db.execSQL(SQL_CREATE_GAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + WarContract.savedGames.TABLE_NAME + ";");
        onCreate(db);
    }
}
