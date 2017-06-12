package com.mdelsordo.stepquest.data;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.ActiveCharacter;
import com.mdelsordo.stepquest.model.AdventureLog;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.model.PlotQueue;
import com.mdelsordo.stepquest.util.Logger;

/**
 * Created by mdelsord on 5/2/17.
 * This just bundles all the stuff to save the game into one place
 */

public class Saver {
    private static final String TAG = "Saver";
    public static boolean dontSave;

    public static void saveAll(Context context, boolean displayToast){
        //dont save ever if a delete just happened to prevent attempting to write to a nonexistent database
        if(dontSave == false){
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
                    //Log.e(TAG, "Saver hit exception displaying toast.", e);
                }
            }
        }
    }

    //deletes everything from the sql table/sharedPreferences
    public static void deleteAll(Context context){
        new DeleteTask().execute(context);
    }

    private static class SaveTask extends AsyncTask<Context, Void, Void>{

        @Override
        protected void onPreExecute() {
            Logger.i(TAG, "Saving game...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Context... params) {
            Logger.i(TAG, "Doing save.");
            ActiveCharacter.getInstance(params[0]).save();
            EventQueue.getInstance(params[0]).save();
            AdventureLog.getInstance(params[0]).save();
            PlotQueue.getInstance(params[0]).save();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Logger.i(TAG, "Saving finished.");
            super.onPostExecute(aVoid);
        }
    }

    private static class DeleteTask extends AsyncTask<Context, Void, Void>{
        @Override
        protected void onPreExecute() {
            Logger.i(TAG, "Deleting game...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Context... params) {
            Logger.i(TAG, "Doing delete.");
            ActiveCharacter.getInstance(params[0]).deleteInstance();
            EventQueue.getInstance(params[0]).deleteInstance();
            AdventureLog.getInstance(params[0]).deleteInstance();
            PlotQueue.getInstance(params[0]).deleteInstance();
            params[0].getApplicationContext().deleteDatabase(QuestDbSchema.DATABASE_NAME);
            PreferenceManager.getDefaultSharedPreferences(params[0]).edit().clear().apply();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dontSave = true;
            Logger.i(TAG, "Delete finished.");
            super.onPostExecute(aVoid);
        }
    }
}
