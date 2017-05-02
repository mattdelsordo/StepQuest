package project3.csc214.stepquest.data;

import android.content.Context;
import android.util.Log;

import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.EventQueue;

/**
 * Created by mdelsord on 5/2/17.
 * This just bundles all the stuff to save the game into one place
 */

public class Saver {
    private static final String TAG = "Saver";

    public static void saveAll(Context context){
        Log.i(TAG, "Saving game...");
        ActiveCharacter.getInstance(context).save();
        EventQueue.getInstance(context).save();
    }
}
