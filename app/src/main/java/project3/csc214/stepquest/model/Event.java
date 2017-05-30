package project3.csc214.stepquest.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import java.util.Comparator;

import project3.csc214.stepquest.R;

/**
 * Created by mdelsord on 4/15/17.
 * Models an event, a thing that's happening.
 * Each event has a duration and description and at least rewards exp if not a weapon
 */

public class Event {
    private static final String TAG = "Event";

    //array of challenge ratings
    private static final int[] CHALLENGE_RATING = new int[]{
            10, //-3
            25, //-2
            50, //-1
            100, //0
            200, //1
            450, //2
            700, //3
            1100, //4
            1800,
            2300,
            2900,
            3900,
            5000,
            6900,
            8400,
            10000,
            13000,
            16000,
            19000,
            22000,
            25000,
            28000,
            36000,
            43000,
            53000,
            65000,
            155000
    };
    public static final int RATING_FLOOR = -3;

    //accepts a CR number and returns the value
    public static int getChallengeRating(int rating){
        int paddedRating = rating +3;
        Log.i(TAG, "Got rating " + rating + ", selecting from " + paddedRating);
        return CHALLENGE_RATING[paddedRating];
    }

    public static int amountOfCRs(){
        return CHALLENGE_RATING.length;
    }

    private String mDescription;
    private int mDuration;
    private int mGoldReward;
    //private int mExpReward; might just have exp scale with duration??
    private Weapon mItemReward;
    private boolean mDoNotify;
    private String mNotificationText;

    //designates what kind of event this is
    public static final int DUNGEON_CLEAR = -1, MONSTER = 1;
    private int mEventClassTag;

    public Event(String desc, int duration){
        mDescription = desc;
        mDuration = duration;
//        mExpReward = exp;
        mGoldReward = 0;
        mItemReward = null;
    }

    //cloner constructor
    public Event(Event e){
        mDescription = e.getDescription();
        mDuration = e.getDuration();
        mGoldReward = e.getGoldReward();
        if(e.getItemReward() != null) mItemReward = new Weapon(e.getItemReward());
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Integer getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getGoldReward() {
        return mGoldReward;
    }

    public void setGoldReward(int mGoldReward) {
        this.mGoldReward = mGoldReward;
    }

//    public int getExpReward() {
//        return mExpReward;
//    }
//
//    public void setExpReward(int mExpReward) {
//        this.mExpReward = mExpReward;
//    }

    public Weapon getItemReward() {
        return mItemReward;
    }

    public void setItemReward(Weapon mItemReward) {
        this.mItemReward = mItemReward;
    }

    public int getEventClassTag() {
        return mEventClassTag;
    }

    public void setEventClassTag(int mEventClassTag) {
        this.mEventClassTag = mEventClassTag;
    }

    public void setDoNotify(String notificationText){
        mDoNotify = true;
        mNotificationText = notificationText;
    }

    public boolean doNotify(){return mDoNotify;}

    public String getNotificationText(){
        return mNotificationText;
    }

    @Override
    public String toString() {
        return mDescription;
    }

    //returns the amount of exp this event gives, as a function of duration, which I might change
    public int getExp(){
        return getDuration(); //TODO: I might change this up
    }

    //hackily parses out the last couple words of a monster
    public String parseMonsterName(){
        String[] descSplit = getDescription().split(" ");
        String monsterName = "";
        for(int i = 2; i < descSplit.length; i++){
            monsterName += descSplit[i] + " ";
        }
        return monsterName.substring(0, monsterName.length() - 4);
    }

    //comparator that sorts events by their duration
    public static class EventComparator implements Comparator<Event> {

        @Override
        public int compare(Event o1, Event o2) {
            return o1.getDuration().compareTo(o2.getDuration());
        }
    }
}
