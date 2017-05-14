package project3.csc214.stepquest.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.EventQueue;

/**
 * Created by mdelsord on 5/2/17.
 * This just bundles all the stuff to save the game into one place
 */

public class Saver {
    private static final String TAG = "Saver";

    public static void saveAll(Context context, boolean displayToast){
        Log.i(TAG, "Saving game...");
//        ActiveCharacter.getInstance(context).save();
//        EventQueue.getInstance(context).save();
        new SaveTask().execute(context);

        if(displayToast){
            try{
                TypedValue tv = new TypedValue();
                int height = 400;
                if(context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
                    height = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
                }
                Toast toast = Toast.makeText(context, context.getString(R.string.game_saved), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.RIGHT, 10, height + 10);
                toast.show();
            }catch(Exception e){
                Log.e(TAG, "Saver hit exception displaying toast.", e);
            }
        }
    }

    //deletes everything from the sql table
    public static void deleteAll(Context context){
        context.getApplicationContext().deleteDatabase(QuestDbSchema.DATABASE_NAME);
    }

    private static class SaveTask extends AsyncTask<Context, Void, Void>{

        @Override
        protected Void doInBackground(Context... params) {
            ActiveCharacter.getInstance(params[0]).save();
            EventQueue.getInstance(params[0]).save();
            return null;
        }
    }
}
