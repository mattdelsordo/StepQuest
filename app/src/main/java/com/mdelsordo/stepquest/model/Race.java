package com.mdelsordo.stepquest.model;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 4/13/17.
 * Models a race
 */

public class Race {

    public static final int BIRDPERSON = 0, ELF = 1, MATHHEAD = 2, PUMPKIN = 3;
    private final String mName;
    private final int[] mBonuses;
    private final int mRace;

    private Race(int race, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma){
        mRace = race;

        //TODO: these should really be static variables
        if(race == BIRDPERSON) mName = "Birdperson";
        else if(race == ELF) mName = "Elf";
        else if(race == MATHHEAD) mName = "Math'head";
        else if(race == PUMPKIN) mName = "Pump-kin";
        else mName = "????";

        mBonuses = new int[Stats.STAT_VOLUME];
        mBonuses[Stats.STR] = strength;
        mBonuses[Stats.DEX] = dexterity;
        mBonuses[Stats.CON] = constitution;
        mBonuses[Stats.INT] = intelligence;
        mBonuses[Stats.WIS] = wisdom;
        mBonuses[Stats.CHR] = charisma;
    }

    //returns the stat bonus for a given stat
    public int getStatBonus(int stat){return mBonuses[stat];}

    //returns the racial code
    public int getId(){return mRace;}

    //returns drawable for the race
    public int getDrawable(){
        if(mRace == BIRDPERSON) return R.drawable.birdperson1;
        else if(mRace == ELF) return R.drawable.elf1white;
        else if (mRace == MATHHEAD) return R.drawable.mathhead1;
        else if (mRace == PUMPKIN) return R.drawable.pumpkin1;
        else return R.mipmap.ic_launcher;
    }

    //racial bonuses for Pump-kins
    public static Race Pumpkin(){
        return new Race(PUMPKIN,0,0,1,0,2,-1);
    }

    //racial bonuses for Birdpeople
    public static Race Birdperson(){
        return new Race(BIRDPERSON,0,2,0,0,0,0);
    }

    //racial bonuses for Mathheads
    public static Race Mathhead(){
        return new Race(MATHHEAD,0,0,0,2,0,0);
    }

    //racial bonuses for Elves
    public static  Race Elf(){
        return new Race(ELF,2,0,0,-1,-1,2);
    }

    public static Race newRace(int race){
        if(race == PUMPKIN) return Pumpkin();
        else if(race == ELF) return Elf();
        else if (race == MATHHEAD) return Mathhead();
        else if(race == BIRDPERSON) return Birdperson();
        else return null;
    }

    @Override
    public String toString() {
        return mName;
    }
}
