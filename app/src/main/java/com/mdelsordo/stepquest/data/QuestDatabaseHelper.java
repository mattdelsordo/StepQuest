package com.mdelsordo.stepquest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mdelsordo.stepquest.util.Logger;

/**
 * Created by mdelsord on 5/1/17.
 * Interfaces with the SQL database where everything is stored
 */

public class QuestDatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG = "DBHelper";

    public QuestDatabaseHelper(Context context){
        super(context, QuestDbSchema.DATABASE_NAME, null, QuestDbSchema.VERSION);
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create character table
        db.execSQL("create table " + QuestDbSchema.CharacterTable.NAME
                        + "( " + QuestDbSchema.CharacterTable.Params.NAME + " PRIMARY KEY, "
                        + QuestDbSchema.CharacterTable.Params.RACE + ", "
                        + QuestDbSchema.CharacterTable.Params.VOCATION + ", "
                        + QuestDbSchema.CharacterTable.Params.LEVEL + ", "
                        + QuestDbSchema.CharacterTable.Params.STR + ", "
                        + QuestDbSchema.CharacterTable.Params.DEX + ", "
                        + QuestDbSchema.CharacterTable.Params.CON + ", "
                        + QuestDbSchema.CharacterTable.Params.WIS + ", "
                        + QuestDbSchema.CharacterTable.Params.INT + ", "
                        + QuestDbSchema.CharacterTable.Params.CHR + ", "
                        + QuestDbSchema.CharacterTable.Params.GOLD + ", "
                        + QuestDbSchema.CharacterTable.Params.EXP + ", "
                        + QuestDbSchema.CharacterTable.Params.LVL_TOKENS + ", "
                        + QuestDbSchema.CharacterTable.Params.WEAPON_ID + ", "
                        + QuestDbSchema.CharacterTable.Params.BOOST + ")");

        //create inventory table
        db.execSQL("create table " + QuestDbSchema.InventoryTable.NAME
                + "( " + QuestDbSchema.InventoryTable.Params.WEAPON_ID + " PRIMARY KEY, "
                + QuestDbSchema.InventoryTable.Params.QUANTITY + ")");

        //create event queue table
        db.execSQL("create table " + QuestDbSchema.EventQueueTable.NAME
                + "( " + QuestDbSchema.EventQueueTable.Params.ORDER + " PRIMARY KEY, "
                + QuestDbSchema.EventQueueTable.Params.DESCRIPTION + ", "
                + QuestDbSchema.EventQueueTable.Params.DURATION + ", "
                + QuestDbSchema.EventQueueTable.Params.GOLD + ", "
                + QuestDbSchema.EventQueueTable.Params.WEAPON_ID + ", "
                + QuestDbSchema.EventQueueTable.Params.NOTIFY + ", "
                + QuestDbSchema.EventQueueTable.Params.NOTIFICATION_TEXT + ", "
                + QuestDbSchema.EventQueueTable.Params.ADVANCE_PLOT + ", "
                + QuestDbSchema.EventQueueTable.Params.CLASS_TAG+ ", "
                + QuestDbSchema.EventQueueTable.Params.PROGRESS + ")");

        //create journal table
        db.execSQL("create table " + QuestDbSchema.JournalQueueTable.NAME
                + "( " + QuestDbSchema.JournalQueueTable.Params.ORDER + ", "
                + QuestDbSchema.JournalQueueTable.Params.TEXT + ", "
                + QuestDbSchema.JournalQueueTable.Params.DATE + ")");

        //create statistics table
        db.execSQL("create table " + QuestDbSchema.StatisticsTable.NAME
        + "( " + QuestDbSchema.StatisticsTable.Params.STEPS + ", "
                + QuestDbSchema.StatisticsTable.Params.MONSTERS + ", "
                + QuestDbSchema.StatisticsTable.Params.GOLD + ", "
                + QuestDbSchema.StatisticsTable.Params.WEAPONS + ", "
                + QuestDbSchema.StatisticsTable.Params.DUNGEONS + ")");

        db.execSQL("create table " + QuestDbSchema.PlotQueueTable.NAME
            + "( " + QuestDbSchema.PlotQueueTable.Params.ORDER + " PRIMARY KEY, "
            + QuestDbSchema.PlotQueueTable.Params.TEXT + ")");
        //Log.i(TAG, "Tables created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
