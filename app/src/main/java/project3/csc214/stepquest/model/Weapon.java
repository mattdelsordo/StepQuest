package project3.csc214.stepquest.model;

import android.util.NoSuchPropertyException;

import java.util.NoSuchElementException;

/**
 * Created by mdelsord on 4/15/17.
 * Each weapon has a type and a modifier that changes based on the class.
 */

public class Weapon {

    public static final int BLADE = 0, BOW = 1, STAFF = 2, BLUNT = 3; //weapon classes
    public static final double WOOD = 1.0, BRONZE = 1.5, IRON = 2.0, STEEL = 2.5, OBSIDIAN = 3.0, MITHRIL = 4.0;//material bonuses


    public static final double GOOD = 1.5, BAD = (2 / 3); //multipliers for experience gain for weapons

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

    public double getModifier(Vocation vocation) {
        if (mType == vocation.getGoodWeapon()) {
            return mMaterial * GOOD;
        } else if (mType == vocation.getBadWeapon()) {
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

    public double getMaterial() {
        return mMaterial;
    }
}