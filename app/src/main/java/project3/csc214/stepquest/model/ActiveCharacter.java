package project3.csc214.stepquest.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mdelsord on 4/17/17.
 * Wrapper for a character that loads any previous character from the SQL database/stores it as a singleton
 */

public class ActiveCharacter{

    private static ActiveCharacter sActiveCharacter;

    private Character mCharacter;
    private ArrayList<Weapon> mWeaponSet;
    private Weapon mEquippedWeapon;

    private ActiveCharacter(){
        mWeaponSet = new ArrayList<>();
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
        mWeaponSet.add(w);
        //equip weapon if there's none equipped
        if(mEquippedWeapon == null) mEquippedWeapon = w;
    }
}

