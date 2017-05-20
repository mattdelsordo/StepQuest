package project3.csc214.stepquest.model;

import android.content.Context;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by mdelsord on 5/19/17.
 * This singleton records user statistics for display to the user
 */

public class AdventureLog {
    private static final String TAG = "AdventureLog";

    private static AdventureLog sLog;
    private Context mAppContext;

    public static final int LOG_SIZE = 50;
    private ArrayDeque<String> mEventLog; //stores strings corresponding to completed events

    //values representing various statistics
    private static final int AVG_STEP_LENGTH_IN = 31;
    private static final double INCHES_PER_MILE = 63360.0;
    private int mTotalSteps;
    private int mTotalMonstersSlain;
    private int mTotalGoldAcquired;
    private int mTotalWeaponsAcquired;
    private int mTotalDungeonsCleared;

    private AdventureLog(Context context){
        mAppContext = context.getApplicationContext();
        mEventLog = new ArrayDeque<>(LOG_SIZE);
    }

    public static synchronized AdventureLog getInstance(Context context){
        if(sLog == null) sLog = new AdventureLog(context);
        return sLog;
    }

    //adds a description to the list and then removes the last one if the cap is hit
    public void addEventToLog(String eventDescription){
        mEventLog.addFirst(eventDescription);
        if(mEventLog.size() > LOG_SIZE) mEventLog.removeLast();
        if(mListener!=null)mListener.updateJournal(mEventLog);
    }

    //returns the list of events
    public ArrayDeque<String> getEventLog(){
        return mEventLog;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }

    public void addStep() {
        mTotalSteps++;
        //TODO: this isnt executing correctly?
        if(mListener != null)mListener.updateStats(getTotalSteps(), getApproxDistanceWalkedMiles(), getTotalMonstersSlain(), getTotalGoldAcquired(), getTotalWeaponsAcquired(), getTotalDungeonsCleared());
    }

    //returns the approximate distance the user has walked, in MILES
    public double getApproxDistanceWalkedMiles() {
        return mTotalSteps * AVG_STEP_LENGTH_IN * (1/INCHES_PER_MILE);
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

    public interface LogUpdateListener{
        void updateStats(int steps, double distance, int monsters, int gold, int weapons, int dungeons);
        void updateJournal(ArrayDeque<String> list);
    }
    private LogUpdateListener mListener;
    public void bindLogUpdateListener(LogUpdateListener lul){mListener = lul;}
    public void unbindLogUpdateListener(){
        mListener = null;
    }
}
