package project3.csc214.stepquest.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.Comparator;

import project3.csc214.stepquest.R;

/**
 * Created by mdelsord on 4/15/17.
 * Models an event, a thing that's happening.
 * Each event has a duration and description and at least rewards exp if not a weapon
 */

public class Event {

    private String mDescription;
    private int mDuration;
    private int mGoldReward;
    //private int mExpReward; might just have exp scale with duration??
    private Weapon mItemReward;

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
