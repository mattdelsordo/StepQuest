package com.mdelsordo.stepquest.model;

/**
 * Created by mdelsord on 4/13/17.
 */

public class Stats {
    public static final int STAT_VOLUME = 6;
    public static final int STR = 0;
    public static final int DEX = 1;
    public static final int CON = 2;
    public static final int INT = 3;
    public static final int WIS = 4;
    public static final int CHR = 5;

    public static String statToText(int stat){
        switch(stat){
            case STR: return "STR";
            case DEX: return "DEX";
            case CON: return "CON";
            case INT: return "INT";
            case WIS: return "WIS";
            case CHR: return "CHR";
            default: return "????";
        }
    }
}
