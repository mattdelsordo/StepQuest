package project3.csc214.stepquest.model;

import project3.csc214.stepquest.R;

/**
 * Created by mdelsord on 4/13/17.
 * Models a race
 */

public class Vocation {

    private static final int FIGHTER = 0, RANGER = 1, WIZARD = 2, CLERIC = 3;
    private int mClass;

    private Vocation(int vocation){
        mClass = vocation;
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
        return new Vocation(FIGHTER);
    }

    public static Vocation Ranger(){
        return new Vocation(RANGER);
    }

    public static Vocation Wizard(){
        return new Vocation(WIZARD);
    }

    public static Vocation Cleric(){
        return new Vocation(CLERIC);
    }

    //makes new vocation based on string
    public static Vocation newVocation(int vocation) {
        if (vocation == FIGHTER) return Fighter();
        else if (vocation == RANGER) return Ranger();
        else if (vocation == WIZARD) return Wizard();
        else if (vocation == CLERIC) return Cleric();
        else return null;
    }
}
