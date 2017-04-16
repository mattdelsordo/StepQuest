package project3.csc214.stepquest.model;

/**
 * Created by mdelsord on 4/15/17.
 * Each weapon has a type and a modifier that changes based on the class.
 */

public class Weapon {

    public static final int BLADE = 0, BOW = 1, STAFF = 2, BLUNT = 3; //weapon classes
    public static final double GOOD = 1.5, BAD = (2/3); //multipliers for experience gain for weapons
    private final String mName;
    private final int mType;
    private final double mModifier;

    public Weapon(String name, int type, double modifier){
        mName = name;
        mType = type;
        mModifier = modifier;
    }
}
