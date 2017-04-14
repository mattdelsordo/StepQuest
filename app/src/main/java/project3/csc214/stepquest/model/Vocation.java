package project3.csc214.stepquest.model;

/**
 * Created by mdelsord on 4/13/17.
 * Models a race
 */

public class Vocation {

    private final int[] mBonuses;

    private Vocation(int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma){
        mBonuses = new int[Stats.STAT_VOLUME];
        mBonuses[Stats.STR] = strength;
        mBonuses[Stats.DEX] = dexterity;
        mBonuses[Stats.CON] = constitution;
        mBonuses[Stats.INT] = intelligence;
        mBonuses[Stats.WIS] = wisdom;
        mBonuses[Stats.CHR] = charisma;
    }
}
