package com.mdelsordo.stepquest.model;

import android.util.Log;
import android.util.NoSuchPropertyException;

import java.io.Serializable;
import java.util.Comparator;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 4/15/17.
 * Each weapon has a type and a modifier that changes based on the class.
 */

public class Weapon implements Serializable{
    public static final String TAG = "Weapon";

    public static final int DEFAULT = -1, BLADE = 0, BOW = 1, STAFF = 2, BLUNT = 3; //weapon classes
    public static final Double WOOD = 1.0, BRONZE = 1.5, IRON = 2.0, STEEL = 2.5, OBSIDIAN = 3.0, MITHRIL = 4.0;//material bonuses
    public static final double STAT_BOOST_COEFFICIENT = 0.125;
    public static final double CHR_DISCOUNT_COEFFICIENT = 0.0125;

    public static final double GOOD = 1.5, BAD = 0.6666; //multipliers for experience gain for weapons

    private final String mId;
    private final String mName;
    private final int mType;
    private final double mMaterial;
    //private final double mModifier;

    //default constructor until I sort out how to load this
    public Weapon() {
        mId = "stick";
        mName = "Stick";
        mType = DEFAULT;
        //mModifier = 1;
        mMaterial = WOOD;
    }

    public Weapon(String id, String name, int type, double material) {
        mId = id;
        mName = name;
        mType = type;
        //mModifier = modifier;
        mMaterial = material;
    }

    //cloner constructor
    public Weapon(Weapon w){
        mId = w.getId();
        mName = w.getName();
        mType = w.getType();
        mMaterial = w.getMaterial();
    }

    //modifier factors in the material, the type, and the relevant stat
    //!! The main modifier calculating method!!
    public double getModifier(Character character) {
        double base = mMaterial;
        //Log.i(TAG, "Base weapon modifier = " + base);
        if (mType == character.getVocation().getGoodWeapon()) {
            base *= GOOD;
        } else if (mType == character.getVocation().getBadWeapon()) {
            //Log.i(TAG, "Bad=" + BAD);
            base *= BAD;
        }
        //Log.i(TAG, "Weapon modifier: " + base);
        return base * getStatModifier(character);
    }

    public double getProficiencyModifier(Vocation vocation){
        if (mType == vocation.getGoodWeapon()) {
            return GOOD;
        } else if (mType == vocation.getBadWeapon()) {
            //Log.i(TAG, "Bad=" + BAD);
            return BAD;
        } else {
            return 1.0;
        }
    }

    //calculates the part of the modifier from the stat bonus
    public double getStatModifier(Character character){
        double modifier = 0;
        if(mType == BLADE){
            modifier = character.getStat(Stats.STR) * STAT_BOOST_COEFFICIENT;
        }else if(mType == BOW){
            modifier = character.getStat(Stats.DEX)* STAT_BOOST_COEFFICIENT;
        }else if(mType == STAFF){
            modifier = character.getStat(Stats.INT)* STAT_BOOST_COEFFICIENT;
        }else if(mType == BLUNT){
            modifier = character.getStat(Stats.CON)* STAT_BOOST_COEFFICIENT;
        }

        if(modifier < 1) modifier = 1;
        //Log.i(TAG, "Stat modifier: " + modifier);
        return modifier;
    }

    //converts string classifications to the double format
    public static double parseMaterial(String material) {
        switch (material) {
            case "WOOD":
                return WOOD;
            case "BRONZE":
                return BRONZE;
            case "IRON":
                return IRON;
            case "STEEL":
                return STEEL;
            case "OBSIDIAN":
                return OBSIDIAN;
            case "MITHRIL":
                return MITHRIL;
            default:
                throw new NoSuchPropertyException(material);
        }
    }

    //converts a string type to the int classification
    public static int parseType(String type) {
        switch (type) {
            case "BLADE":
                return BLADE;
            case "BOW":
                return BOW;
            case "STAFF":
                return STAFF;
            case "BLUNT":
                return BLUNT;
            default:
                throw new NoSuchPropertyException(type);
        }
    }

    //accepts an int type and returns the corresponding resource id
    //This should get string resources from
    public static int getTypeString(int type){
        switch (type){
            case DEFAULT: return R.string.junk;
            case BLADE: return R.string.blade;
            case BOW: return R.string.bow;
            case STAFF: return R.string.staff;
            case BLUNT: return R.string.blunt;
            default: return R.string.unknown_parameter;
        }
    }

    public int getTypeString(){
        return getTypeString(mType);
    }

    public static int getMaterialString(double material){
        if(material == WOOD) return R.string.wood;
        else if(material == IRON) return R.string.iron;
        else if(material == BRONZE) return R.string.bronze;
        else if(material == STEEL) return R.string.steel;
        else if(material == OBSIDIAN) return R.string.obsidian;
        else if(material == MITHRIL) return R.string.mithril;
        else return R.string.unknown_parameter;
    }

    public int getMaterialString(){
        return getMaterialString(mMaterial);
    }

    @Override
    public String toString() {
        return mName + "(" + mMaterial + " " + mType + ")";
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public Double getMaterial() {
        return mMaterial;
    }

    public String getId() {
        return mId;
    }

    //nested class compares two weapons
    public static class WeaponComparator implements Comparator<Weapon> {
        @Override
        public int compare(Weapon o1, Weapon o2) {
            //if(o1.getType() == DEFAULT) return -1;
            int materialComp = o1.getMaterial().compareTo(o2.getMaterial());
            if(materialComp == 0){
                return o1.getName().compareTo(o2.getName());
            }
            else return materialComp;
        }
    }

    //returns the percent buff for a given character
    public double calcBuff(Character c){
        double modifier = getModifier(c) * 100;
        //Log.i(TAG, "Modifier: " + modifier);
        return Math.round(modifier - 100.0);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Weapon){
            Weapon w = (Weapon)obj;
            return mId.equals(w.getId());
        }
        return false;
    }

    //returns the proper kind of drawable for this weapon
    public int getDrawable(){
        switch(getType()){
            case DEFAULT: return R.drawable.ic_misc_weapon;
            case BLADE: return R.drawable.ic_sword;
            case BOW: return R.drawable.ic_bow;
            case BLUNT: return R.drawable.ic_blunt;
            case STAFF: return R.drawable.ic_staff;
            default: return R.drawable.ic_misc;
        }
    }

    //returns the weapon's price
    //TODO: put actual thought into this
    public int getPrice(Character c){
        int base = (int)(mMaterial * 1337 * 0.1);
        if(mType == DEFAULT) base = 10;
        int price = base - (int)(base * calcDiscount(c));
        //Log.i(TAG, mName + " base price: " + base);
        return price;
    }

    //price when the user sells a weapon back to the store
    public int getSalePrice(Character c){
        return (int)(0.85 * getPrice(c));
    }

    //calculates % off a price given a character's charisma
    public double calcDiscount(Character c){
        int charisma = c.getStat(Stats.CHR);
        //Log.i(TAG, "CHR: " + (charisma*1.25));
        return (double)charisma * CHR_DISCOUNT_COEFFICIENT;
    }

}