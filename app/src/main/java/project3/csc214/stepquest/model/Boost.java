package project3.csc214.stepquest.model;

/**
 * Created by mdelsord on 5/16/17.
 * Models a boost that places a multiplier on the user's step weight
 */

public class Boost {

    private int mDuration;
    private double mStepMultiplier;

    public Boost(double multiplier, int duration){
        mDuration = duration;
        mStepMultiplier = multiplier;
    }

    public int getDuration() {
        return mDuration;
    }

    public double getStepMultiplier() {
        return mStepMultiplier;
    }
}
