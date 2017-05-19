package project3.csc214.stepquest.model;

import android.content.Context;

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
    private ArrayList<String> mEventLog; //stores strings corresponding to completed events

    //values representing various statistics
    private static final int AVG_STEP_LENGTH_IN = 31;
    private static final int INCHES_PER_MILE = 63360;
    private int mTotalSteps;
    private int mTotalMonstersSlain;
    private int mTotalGoldAcquired;
    private int mTotalWeaponsAcquired;
    private int mTotalDungeonsCleared;

    private AdventureLog(Context context){
        mAppContext = context.getApplicationContext();
        mEventLog = new ArrayList<>(50);
    }

    public static synchronized AdventureLog getInstance(Context context){
        if(sLog == null) sLog = new AdventureLog(context);
        return sLog;
    }

    //adds a description to the list and then removes the last one if the cap is hit
    public void addEventToLog(String eventDescription){
        mEventLog.add(0, eventDescription);
        if(mEventLog.size() > LOG_SIZE) mEventLog.remove(mEventLog.size() - 1);
    }

    //returns the list of events
    public ArrayList<String> getEventLog(){
        return mEventLog;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }

    public void setTotalSteps(int mTotalSteps) {
        this.mTotalSteps = mTotalSteps;
    }

    //returns the approximate distance the user has walked, in MILES
    public double getApproxDistanceWalkedMiles() {
        return mTotalSteps * AVG_STEP_LENGTH_IN * (1/INCHES_PER_MILE);
    }

    public int getTotalMonstersSlain() {
        return mTotalMonstersSlain;
    }

    public void setTotalMonstersSlain(int mTotalMonstersSlain) {
        this.mTotalMonstersSlain = mTotalMonstersSlain;
    }

    public int getTotalGoldAcquired() {
        return mTotalGoldAcquired;
    }

    public void setTotalGoldAcquired(int mTotalGoldAcquired) {
        this.mTotalGoldAcquired = mTotalGoldAcquired;
    }

    public int getTotalWeaponsAcquired() {
        return mTotalWeaponsAcquired;
    }

    public void setTotalWeaponsAcquired(int mTotalWeaponsAcquired) {
        this.mTotalWeaponsAcquired = mTotalWeaponsAcquired;
    }

    public int getTotalDungeonsCleared() {
        return mTotalDungeonsCleared;
    }

    public void setTotalDungeonsCleared(int mTotalDungeonsCleared) {
        this.mTotalDungeonsCleared = mTotalDungeonsCleared;
    }
}
