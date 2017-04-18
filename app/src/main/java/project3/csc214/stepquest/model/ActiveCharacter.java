package project3.csc214.stepquest.model;

/**
 * Created by mdelsord on 4/17/17.
 * Wrapper for a character that loads any previous character from the SQL database/stores it as a singleton
 */

public class ActiveCharacter{

    private static ActiveCharacter sActiveCharacter;

    private Character mCharacter;

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
}

