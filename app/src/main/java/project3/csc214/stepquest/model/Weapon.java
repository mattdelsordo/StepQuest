package project3.csc214.stepquest.model;

import android.util.Log;
import android.util.NoSuchPropertyException;

import java.util.Comparator;
import java.util.NoSuchElementException;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.WeaponList;

/**
 * Created by mdelsord on 4/15/17.
 * Each weapon has a type and a modifier that changes based on the class.
 */

public class Weapon{
    public static final String TAG = "Weapon";

    public static final int BLADE = 0, BOW = 1, STAFF = 2, BLUNT = 3; //weapon classes
    public static final Double WOOD = 1.0, BRONZE = 1.5, IRON = 2.0, STEEL = 2.5, OBSIDIAN = 3.0, MITHRIL = 4.0;//material bonuses


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
        mType = BLUNT;
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

    public double getModifier(Vocation vocation) {
        if (mType == vocation.getGoodWeapon()) {
            return mMaterial * GOOD;
        } else if (mType == vocation.getBadWeapon()) {
            //Log.i(TAG, "Bad=" + BAD);
            return mMaterial * BAD;
        } else {
            return mMaterial;
        }
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
            case BLADE: return R.string.blade;
            case BOW: return R.string.bow;
            case STAFF: return R.string.staff;
            case BLUNT: return R.string.blunt;
            default: return R.string.unknown_parameter;
        }
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
            int materialComp = o1.getMaterial().compareTo(o2.getMaterial());
            if(materialComp == 0){
                return o1.getName().compareTo(o2.getName());
            }
            else return materialComp;
        }
    }

    //returns the percent buff for a given character
    public double calcBuff(Vocation v){
        double modifier = getModifier(v) * 100;
        Log.i(TAG, "Modifier: " + modifier);
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
            case BLADE: return R.drawable.ic_sword;
            case BOW: return R.drawable.ic_bow;
            case BLUNT: return R.drawable.ic_blunt;
            case STAFF: return R.drawable.ic_staff;
            default: return R.drawable.ic_misc;
        }
    }

    //returns the weapon's price
    //TODO: put actual thought into this
    public int getPrice(){
        return (int)(mMaterial * 100);
    }

}