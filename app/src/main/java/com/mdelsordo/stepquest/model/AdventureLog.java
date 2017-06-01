package com.mdelsordo.stepquest.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Calendar;

import com.mdelsordo.stepquest.data.QuestCursorWrapper;
import com.mdelsordo.stepquest.data.QuestDatabaseHelper;
import com.mdelsordo.stepquest.data.QuestDbSchema;

/**
 * Created by mdelsord on 5/19/17.
 * This singleton records user statistics for display to the user
 */

public class AdventureLog {
    private static final String TAG = "AdventureLog";

    private static AdventureLog sLog;
    private Context mAppContext;
    private final SQLiteDatabase mDatabase;

    public static final int LOG_SIZE = 50;
    private ArrayDeque<JournalEntry> mEventLog; //stores strings corresponding to completed events

    //values representing various statistics
    private static final int AVG_STEP_LENGTH_IN = 31;
    private static final double INCHES_PER_MILE = 63360.0;
    private int mTotalSteps;
    private int mTotalMonstersSlain;
    private int mTotalGoldAcquired;
    private int mTotalWeaponsAcquired;
    private int mTotalDungeonsCleared;

    private AdventureLog(Context context) {
        mAppContext = context.getApplicationContext();
        mEventLog = new ArrayDeque<>(LOG_SIZE);
        mDatabase = new QuestDatabaseHelper(mAppContext).getWritableDatabase();

        load();
    }

    public static synchronized AdventureLog getInstance(Context context) {
        if (sLog == null) sLog = new AdventureLog(context);
        return sLog;
    }

    //adds a description to the list and then removes the last one if the cap is hit
    public void addEventToLog(String eventDescription) {
        JournalEntry newEntry = new JournalEntry(eventDescription, Calendar.getInstance());
        mEventLog.addFirst(newEntry);
        if (mEventLog.size() > LOG_SIZE) mEventLog.removeLast();
        if (mListener != null) mListener.updateJournal(newEntry);
        //Log.i(TAG, "Added " + newEntry + " to adventure log");
    }

    //returns the list of events
    public ArrayDeque<JournalEntry> getEventLog() {
        return mEventLog;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }

    public void addStep() {
        mTotalSteps++;
        //TODO: this isnt executing correctly?
        if (mListener != null){
            //Log.i(TAG, "AdventureLog listener is NOT null.");
            mListener.updateStats(getTotalSteps(), getApproxDistanceWalkedMiles(), getTotalMonstersSlain(), getTotalGoldAcquired(), getTotalWeaponsAcquired(), getTotalDungeonsCleared());
        }
        //else Log.i(TAG, "AdventureLog listener is null.");
    }

    //returns the approximate distance the user has walked, in MILES
    public double getApproxDistanceWalkedMiles() {
        return mTotalSteps * AVG_STEP_LENGTH_IN * (1 / INCHES_PER_MILE);
    }

    public int getTotalMonstersSlain() {
        return mTotalMonstersSlain;
    }

    public void addMonsterSlain() {
        mTotalMonstersSlain++;
    }

    public int getTotalGoldAcquired() {
        return mTotalGoldAcquired;
    }

    public void addTotalGoldAcquired(int gold) {
        mTotalGoldAcquired += gold;
    }

    public int getTotalWeaponsAcquired() {
        return mTotalWeaponsAcquired;
    }

    public void addTotalWeaponsAcquired() {
        mTotalWeaponsAcquired++;
    }

    public int getTotalDungeonsCleared() {
        return mTotalDungeonsCleared;
    }

    public void addTotalDungeonsCleared() {
        mTotalDungeonsCleared++;
    }

    public interface LogUpdateListener {
        void updateStats(int steps, double distance, int monsters, int gold, int weapons, int dungeons);

        void updateJournal(JournalEntry entry);
    }

    private LogUpdateListener mListener;

    public void bindLogUpdateListener(LogUpdateListener lul) {
        mListener = lul;
    }

    public void unbindLogUpdateListener() {
        mListener = null;
    }


    /**
     * Database loading/saving methods
     **/
    private QuestCursorWrapper queryJournal(String where, String args[]) {
        Cursor cursor = mDatabase.query(
                QuestDbSchema.JournalQueueTable.NAME,
                null,
                where,
                args,
                null,
                null,
                QuestDbSchema.JournalQueueTable.Params.ORDER
        );
        return new QuestCursorWrapper(cursor);
    }


    private QuestCursorWrapper queryStatistics(String where, String args[]){
        Cursor cursor = mDatabase.query(
                QuestDbSchema.StatisticsTable.NAME,
                null,
                where,
                args,
                null,
                null,
                null
        );
        return new QuestCursorWrapper(cursor);
    }

    //loads stats from database
    public void load(){
        mEventLog.clear();

        //get individual stats
        QuestCursorWrapper statWrapper = queryStatistics(null, null);
        try{
            if(statWrapper.getCount() == 1){
                statWrapper.moveToFirst();
                QuestCursorWrapper.StatisticsBundle bundle = statWrapper.getStatistics();
                mTotalSteps = bundle.steps;
                mTotalGoldAcquired = bundle.gold;
                mTotalDungeonsCleared = bundle.dungeons;
                mTotalWeaponsAcquired = bundle.weapons;
                mTotalMonstersSlain = bundle.monsters;
            }else{
                //Log.e(TAG, "Too may sets of statistics in database!");
            }
        }finally {
            statWrapper.close();
        }

        //get journal log
        QuestCursorWrapper logWrapper = queryJournal(null, null);
        try{
            if(logWrapper.getCount() > 0){
                logWrapper.moveToFirst();
                while(logWrapper.isAfterLast() == false){
                    JournalEntry entry = logWrapper.getJournalEntry();
                    mEventLog.add(entry);
                    logWrapper.moveToNext();
                }
            }
        }finally {
            logWrapper.close();
        }
    }

    private ContentValues getJournalEntryValues(JournalEntry entry, int order){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.JournalQueueTable.Params.ORDER, order);
        values.put(QuestDbSchema.JournalQueueTable.Params.TEXT, entry.getEntryText());
        values.put(QuestDbSchema.JournalQueueTable.Params.DATE, entry.getEntryDate());
        return values;
    }

    private ContentValues getStatContentValues(int steps, int gold, int dungeons, int weapons, int monsters){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.StatisticsTable.Params.DUNGEONS, dungeons);
        values.put(QuestDbSchema.StatisticsTable.Params.STEPS, steps);
        values.put(QuestDbSchema.StatisticsTable.Params.GOLD, gold);
        values.put(QuestDbSchema.StatisticsTable.Params.WEAPONS, weapons);
        values.put(QuestDbSchema.StatisticsTable.Params.MONSTERS, monsters);
        return values;
    }

    public void save(){
        //Log.i(TAG, "Saving statistics.");
        mDatabase.delete(QuestDbSchema.StatisticsTable.NAME, null, null);
        mDatabase.insert(QuestDbSchema.StatisticsTable.NAME, null, getStatContentValues(mTotalSteps, mTotalGoldAcquired, mTotalDungeonsCleared, mTotalWeaponsAcquired, mTotalMonstersSlain));


        //Log.i(TAG, "Saving journal.");
        mDatabase.delete(QuestDbSchema.JournalQueueTable.NAME, null, null);
        ArrayDeque<JournalEntry> logCopy = mEventLog.clone();
        for(int i = 0; !logCopy.isEmpty(); i++){
            //TODO: fairly confident but not certain that this will work
            mDatabase.insert(QuestDbSchema.JournalQueueTable.NAME, null, getJournalEntryValues(logCopy.removeFirst(), i));
        }
    }
}
