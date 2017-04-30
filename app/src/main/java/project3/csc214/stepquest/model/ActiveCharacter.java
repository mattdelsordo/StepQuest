package project3.csc214.stepquest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * Created by mdelsord on 4/17/17.
 * Wrapper for a character that loads any previous character from the SQL database/stores it as a singleton
 */

public class ActiveCharacter{

    private static ActiveCharacter sActiveCharacter;

    private Character mCharacter;
    private TreeMap<Weapon, Integer> mWeaponSet;
    private Weapon mEquippedWeapon;

    private ActiveCharacter(){
        mWeaponSet = new TreeMap<>(new Weapon.WeaponComparator());
    }

    public void setActiveCharacter(Character c){
        mCharacter = c;
    }

    public Character getActiveCharacter(){
        return mCharacter;
    }

    public static ActiveCharacter getInstance(){
        if(sActiveCharacter == null) sActiveCharacter = new ActiveCharacter();
        return sActiveCharacter;
    }

    //adds a weapon to the inventory
    public void addWeaponToInventory(Weapon w){
        Integer i = mWeaponSet.get(w);
        if(i != null) mWeaponSet.put(w, i + 1);
        else mWeaponSet.put(w, 1);

        //mWeaponSet.put(w, mWeaponSet.get(w) + 1);
        //equip weapon if there's none equipped
        if(mEquippedWeapon == null) mEquippedWeapon = w;

    }

    public void setEquippedWeapon(Weapon w){
        mEquippedWeapon = w;
    }

    public Weapon getEquippedWeapon(){
        return mEquippedWeapon;
    }

    public Collection<Weapon> getWeaponInventory(){
        return mWeaponSet.keySet();
    }

    public double getExpModifier(){
        return mEquippedWeapon.getModifier(mCharacter.getVocation());
    }
}

