package project3.csc214.stepquest.model;

import android.util.Log;

/**
 * Created by mdelsord on 4/13/17.
 * Models a character with a bunch of stats
 */

public class Character {

    private static final int BASE_LEVELUP_EXP = 300; //How much it takes to go from lvl one to lvl 2, in steps (??)

    private static final String TAG = "Character";

    private final String mName;
    private final Vocation mVocation;
    private final Race mRace;
    private int mLevel;
    private int[] mBaseStats;
    private int mFunds;
    private int mExp;
    private int mLevelUpTokens; //these keep track of how many points the character has for levelling up

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
    public void addToBaseStat(int stat, int amount){mBaseStats[stat] += amount;}

    public int getFunds() {
        return mFunds;
    }
    public void setFunds(int mFunds) {
        this.mFunds = mFunds;
    }

    public void addLvlUpToken(){mLevelUpTokens++;}
    public void spendLvlUpToken(){mLevelUpTokens--;}
    public void clearLvlUpTokens(){mLevelUpTokens = 0;}
    public int getLvlUpTokenAmnt(){return mLevelUpTokens;}

    public void setLevelUpTokens(int mLevelUpTokens) {
        this.mLevelUpTokens = mLevelUpTokens;
    }

    public int getExp(){return mExp;}
    public void setExp(int exp){
        mExp = exp;
    }

    //returns true if a level up took place
    public boolean addExp(int exp){
        //dont add exp if level >= 20
        if(getLevel() < 20){
            mExp += exp;
            boolean levelledUp = false;

            //do check for level up
            while(canLevelUp(mExp, mLevel)){
                mLevel++;
                addLvlUpToken();
                Log.i(TAG, mName + " level'd up! (" + mLevel + ")");
                levelledUp = true;
            }

            return levelledUp;
        }
        return false;
    }


    //returns true if the criteria for leveling up has been met, false otherwise
    //implements levelling experience function
    public static boolean canLevelUp(int exp, int level){
        return exp >= levelUpFunction(level);
    }

    public static int levelUpFunction(int level){
        if(level == 0) return 0;
        else if(level == 1) return BASE_LEVELUP_EXP;
        else return levelUpFunction(level - 1) + (int)(levelUpFunction(level - 1) * 1.1);
    }
}
