package project3.csc214.stepquest.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

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

    //randomly generates an event from the xml file I guess
//    public static Event randomMonster(Context context){
//        //get resources from xml
////        Resources res = context.getResources();
////        TypedArray ta = res.obtainTypedArray(R.array.monster_array);
////        int length = ta.length();
////        String[][] array = new String[length][];
////        for(int i = 0; i < length; i++){
////            int id = ta.getResourceId(i, 0);
////            if(id > 0){
////                array[i] = res.getStringArray(id);
////            }
////            else{
////                //TODO: Something wrong, throw error?
////            }
////        }
////        ta.recycle();
//    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getDuration() {
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
}
