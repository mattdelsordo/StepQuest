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

    //constructor for creating a new character
    public Character(String name, Vocation vocation, Race race, int[] stats){
        mName = name;
        mVocation = vocation;
        mRace = race;
        mBaseStats = stats;
        mLevel = 1;
    }

    //constructor for loading a character from saved
    public Character(String name, int level, int vocation, int race, int[] stats){
        mName = name;
        mLevel = level;
        mVocation = Vocation.newVocation(vocation);
        mRace = Race.newRace(race);
        mBaseStats = stats;
    }

    //gets a specific stat
    public int getStat(int stat){
        return mBaseStats[stat] + mRace.getStatBonus(stat);
    }

    //returns the list of stats all summed out
    public int[] getStatList(){
        int[] stats = new int[Stats.STAT_VOLUME];
        for(int i = 0; i < stats.length; i++){
            stats[i] = getStat(i);
        }
        return stats;
    }

    public String getName() {
        return mName;
    }

    public Vocation getVocation() {
        return mVocation;
    }

    public Race getRace() {
        return mRace;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int[] getBaseStats() {
        return mBaseStats;
    }

    public void setBaseStats(int[] mBaseStats) {
        this.mBaseStats = mBaseStats;
    }
}
