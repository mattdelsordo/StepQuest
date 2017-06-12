package com.mdelsordo.stepquest.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.QuestCursorWrapper;
import com.mdelsordo.stepquest.data.QuestDatabaseHelper;
import com.mdelsordo.stepquest.data.QuestDbSchema;
import com.mdelsordo.stepquest.util.Logger;

import java.util.ArrayDeque;

/**
 * Created by mdelsord on 6/5/17.
 * Uses queue to determine what plot points to serve to the user.
 */

public class PlotQueue {
    private static final String TAG = "PlotQueue";

    private static PlotQueue sPlot;
    private Context mAppContext;
    private final String[] mPlotPoints;
    private ArrayDeque<String> mPlotQueue;
    private SQLiteDatabase mDatabase;

    private int mQuestStage = -1;
    private static final String PREF_QUEST_STAGE = "pref_quest_stage";

    private PlotQueue(Context context){
        mAppContext = context.getApplicationContext();
        mPlotPoints = mAppContext.getResources().getStringArray(R.array.leg_plot);
        mPlotQueue = new ArrayDeque<>();
        mDatabase = new QuestDatabaseHelper(mAppContext).getWritableDatabase();

        load();
    }

    public static synchronized PlotQueue getInstance(Context context){
        if(sPlot == null) sPlot = new PlotQueue(context);
        return sPlot;
    }

    public void advancePlot(){
        mQuestStage++;
        if(mQuestStage < mPlotPoints.length){
            String plotToAdd = mPlotPoints[mQuestStage];
            //Logger.i(TAG, "added" + plotToAdd);
            mPlotQueue.addLast(plotToAdd);
            if(mPlotListener != null)mPlotListener.popDialog();
        }
    }

    public String plotAvailable(){
        if(mPlotQueue.isEmpty()){
            Logger.i(TAG, "No plot available.");
            return null;
        }
        else{
            Logger.i(TAG, "Plot available.");
            return mPlotQueue.removeFirst();
        }
    }

    /** Save/load queue to database */
    private QuestCursorWrapper queryPlotTable(String where, String args[]){
        Cursor cursor = mDatabase.query(
                QuestDbSchema.PlotQueueTable.NAME,
                null,
                where,
                args,
                null,
                null,
                QuestDbSchema.PlotQueueTable.Params.ORDER
        );
        return new QuestCursorWrapper(cursor);
    }

    private void load(){
        mPlotQueue.clear();

        QuestCursorWrapper wrapper = queryPlotTable(null, null);
        try{
            if(wrapper.getCount() > 0){
                wrapper.moveToFirst();
                while(wrapper.isAfterLast() == false){
                    String text = wrapper.getPlotEntry();

                    mPlotQueue.addLast(text);
                    wrapper.moveToNext();
                }
            }
        }finally {
            wrapper.close();
        }

        //load quest stage
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        mQuestStage = prefs.getInt(PREF_QUEST_STAGE, -1);
    }

    private ContentValues getEntryContentValues(String text, int order){
        ContentValues values = new ContentValues();
        values.put(QuestDbSchema.PlotQueueTable.Params.ORDER, order);
        values.put(QuestDbSchema.PlotQueueTable.Params.TEXT, text);
        return values;
    }

    public void save(){
        mDatabase.delete(QuestDbSchema.PlotQueueTable.NAME, null, null);
        int counter = 0;
        ArrayDeque<String> copy = mPlotQueue.clone();
        while(!copy.isEmpty()){
            mDatabase.insert(QuestDbSchema.PlotQueueTable.NAME, null, getEntryContentValues(copy.removeFirst(), counter));
            counter++;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        prefs.edit().putInt(PREF_QUEST_STAGE, mQuestStage).apply();
    }

    /**Listener for forcing the dialog to pop up*/
    public interface PlotAdvancedListener{
        void popDialog();
    }
    private PlotAdvancedListener mPlotListener;
    public void bindPlotListener(PlotAdvancedListener pal){
        mPlotListener = pal;
    }
    public void unbindPlotListener(){mPlotListener = null;}

    //makes the active instance null
    public static void deleteInstance(){
       sPlot = null;
    }
}
