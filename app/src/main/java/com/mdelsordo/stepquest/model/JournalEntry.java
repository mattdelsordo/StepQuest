package com.mdelsordo.stepquest.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mdelsord on 5/25/17.
 * A journal entry for the adventure log
 */

public class JournalEntry {

    private final String mEntryText;
    private final String mEntryDate;

    public JournalEntry(String text, Calendar date){
        mEntryText = text;

        //parse calendar date
        String time = new SimpleDateFormat("HH:mm:ss").format(date.getTime());
        mEntryDate = time + "   " + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" + date.get(Calendar.YEAR);

    }

    public JournalEntry(String text, String date){
        mEntryDate = date;
        mEntryText = text;
    }

    public String getEntryText() {
        return mEntryText;
    }

    public String getEntryDate() {
        return mEntryDate;
    }

    @Override
    public String toString() {
        return mEntryText + " at " + mEntryDate;
    }
}
