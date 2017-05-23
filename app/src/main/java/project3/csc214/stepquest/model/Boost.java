package project3.csc214.stepquest.model;

import android.nfc.Tag;
import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
    private static final int MINUTE = 60000;
    private static final int LIST_GEN_TIME_INCREMENT = MINUTE * 10;
    private static final double LIST_GEN_MULT_INCREMENT = 0.1;

    //constants for calculating boost prices
    private static final int BASE_PRICE = 123, PRICE_PER_TIME_INCREMENT = 74, PRICE_PER_PERCENT_INCREMENT = 52;

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

    public int getPrice(){
        Log.i(TAG, "Base Price: " + BASE_PRICE);
        double timePrice = (PRICE_PER_TIME_INCREMENT * mDuration / LIST_GEN_TIME_INCREMENT);
        Log.i(TAG, "Time Price: " + timePrice);
        double multPrice = (PRICE_PER_PERCENT_INCREMENT * mStepMultiplier / LIST_GEN_MULT_INCREMENT) - 500;
        Log.i(TAG, "Mult Price: " + multPrice);
        return (int) (BASE_PRICE + timePrice + multPrice);
    }

    //makes a description for the boost based on the duration and multiplier
    private String makeDesc(long duration, double multiplier){
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(mins);
        long secs = TimeUnit.MILLISECONDS.toSeconds(duration);
        String timeFormatted = String.format("%02d:%02d:%02d",hours,mins,secs);

        double multiplierShifted = ((multiplier * 100) - 100);
        String multNoDecimals = new DecimalFormat("#.##").format(multiplierShifted);
        String multFormatted = "+" + multNoDecimals + "%";
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
    public static ArrayList<Boost> generatePossibleBoostList(Character character){
        ArrayList<Boost> possibleBoosts = new ArrayList<>();

        int maxBoostTime = maxBoostTimeMillis(character.getStat(Stats.CON));
        double maxBoostAmnt = maxBoostSpeed(character.getStat(Stats.DEX));
        for(int i  = LIST_GEN_TIME_INCREMENT; i <= maxBoostTime; i += LIST_GEN_TIME_INCREMENT){
            for(double j = 1.0 + LIST_GEN_MULT_INCREMENT; j <= maxBoostAmnt; j += LIST_GEN_MULT_INCREMENT){
//                Log.i(TAG, "Duration: " + i);
//                Log.i(TAG, "Multiplier: " + j);
                possibleBoosts.add(new Boost(j, i));
            }
        }
        return possibleBoosts;
    }

    //calculates the maximum boost time for a constitution level
    public static int maxBoostTimeMillis(int constitution){
        int maxBoostTime = (int)(Math.pow(constitution, 2) - (4 * constitution));
        Log.i(TAG, "Max boost time (min): " + maxBoostTime);
        int maxBoostTimeMillis = maxBoostTime * MINUTE;
        return maxBoostTimeMillis;
    }

    //calculates the maximum available boost speed based off dexterity
    public static double maxBoostSpeed(int dexterity){
        double maxBoostAmnt = (0.004167 * Math.pow(dexterity, 2)) + (0.058 * dexterity) + 1;
        Log.i(TAG, "Max boost speed multiplier: " + maxBoostAmnt);
        return maxBoostAmnt;
    }
}
