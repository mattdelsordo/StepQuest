package project3.csc214.stepquest.model;

import android.nfc.Tag;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mdelsord on 5/16/17.
 * Models a boost that places a multiplier on the user's step weight
 */

public class Boost {
    private static final String TAG = "Boost";
    private static final int HOUR = 3600000;

    private long mDuration;
    private double mStepMultiplier;
    private String mDesc;
    private String mName;

    public Boost(double multiplier, int duration){
        mDuration = duration;
        mStepMultiplier = multiplier;
        mDesc = makeDesc(mDuration, mStepMultiplier);
        mName = makeName();
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDesc() {
        return mDesc;
    }

    public double getStepMultiplier() {
        return mStepMultiplier;
    }

    public String getName() {
        return mName;
    }

    //TODO: update this for actual prices
    public int getPrice(){
        return 0;
    }

    //makes a description for the boost based on the duration and multiplier
    private String makeDesc(long duration, double multiplier){
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(mins);
        long secs = TimeUnit.MILLISECONDS.toSeconds(duration);
        String timeFormatted = String.format("%02d:%02d:%02d",hours,mins,secs);

        String multFormatted = "+" + ((multiplier * 100) - 100) + "%";
        return multFormatted + " for " + timeFormatted;
    }

    //TODO: make this randomly generate a cool name or something
    private String makeName(){
        String length = "";
        if(mDuration >= (1000 * 60 * 60 * 12)) length = "Lemgthy";
        if(mDuration >= (1000 * 60 * 60 * 24)) length = "Extravagant";

        String qualifier = "";
        if(mStepMultiplier >= 2) qualifier = "Zippy";
        if(mStepMultiplier >= 5) qualifier = "Intense";
        if(mStepMultiplier >= 10) qualifier = "Extravagant";

        return length + " Aura of " + qualifier + " Speed";
    }


    //static method that generates all the allowed boosts
    //may change how these work later
    public static ArrayList<Boost> generatePossibleBoostList(){
        ArrayList<Boost> possibleBoosts = new ArrayList<>();
        //possibleBoosts.add(new Boost(100, 20000));
        for(int i  = HOUR / 2; i <= HOUR * 24; i += HOUR/2){
            for(double j = 2.0; j <= 10.0; j += 0.5){
//                Log.i(TAG, "Duration: " + i);
//                Log.i(TAG, "Multiplier: " + j);
                possibleBoosts.add(new Boost(j, i));
            }
        }
        return possibleBoosts;
    }
}
