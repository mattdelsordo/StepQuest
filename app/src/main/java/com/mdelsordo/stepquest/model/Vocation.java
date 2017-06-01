package com.mdelsordo.stepquest.model;

import java.io.Serializable;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 4/13/17.
 * Models a race
 */

public class Vocation implements Serializable{

    public static final int FIGHTER = 0, RANGER = 1, WIZARD = 2, CLERIC = 3;
    private final String mName;
    private int mClass;
    private int mGoodWeapon, mBadWeapon;

    private Vocation(int vocation, int weapon_good, int weapon_bad){
        mClass = vocation;

        //TODO: these should really be static variables
        if(vocation == FIGHTER) mName = "Warrior";
        else if(vocation == RANGER) mName = "Ranger";
        else if(vocation == WIZARD) mName = "Sorcerer";
        else if(vocation == CLERIC) mName = "Cleric";
        else mName = "????";

        mGoodWeapon = weapon_good;
        mBadWeapon = weapon_bad;
    }

    //returns color id for the vocation
    public int getColor(){
        if(mClass == FIGHTER) return R.color.warrior;
        else if(mClass == RANGER) return R.color.ranger;
        else if(mClass == WIZARD) return R.color.sorcerer;
        else if (mClass == CLERIC) return R.color.cleric;
        else return R.color.transparent;
    }

    public static Vocation Fighter(){
        return new Vocation(FIGHTER, Weapon.BLADE, Weapon.STAFF);
    }

    public static Vocation Ranger(){
        return new Vocation(RANGER, Weapon.BOW, Weapon.BLUNT);
    }

    public static Vocation Wizard(){
        return new Vocation(WIZARD, Weapon.STAFF, Weapon.BOW);
    }

    public static Vocation Cleric(){
        return new Vocation(CLERIC, Weapon.BLUNT, Weapon.BLADE);
    }

    //makes new vocation based on string
    public static Vocation newVocation(int vocation) {
        if (vocation == FIGHTER) return Fighter();
        else if (vocation == RANGER) return Ranger();
        else if (vocation == WIZARD) return Wizard();
        else if (vocation == CLERIC) return Cleric();
        else return null;
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getGoodWeapon() {
        return mGoodWeapon;
    }

    public int getBadWeapon() {
        return mBadWeapon;
    }

    public int getId() {
        return mClass;
    }
}
