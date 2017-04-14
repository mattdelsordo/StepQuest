package project3.csc214.stepquest.model;

/**
 * Created by mdelsord on 4/13/17.
 * Models a character with a bunch of stats
 */

public class Character {

    private final String mName;
    private final Vocation mVocation;
    private final Race mRace;
    private int mLevel;
    private int[] mBaseStats;

    public Character(String name, Vocation vocation, Race race, int[] stats){
        mName = name;
        mVocation = vocation;
        mRace = race;
        mBaseStats = stats;
    }
}
