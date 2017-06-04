package com.mdelsordo.stepquest.model;

import java.util.Random;

/**
 * Created by mdelsord on 4/17/17.
 * This is just a set of dice bc dnd aesthetic
 */

public class Die {

    private static Die sDie;
    private Random rand;

    private Die(){
        rand = new Random();
    }

    private static Die getInstance(){
        if(sDie == null) sDie = new Die();
        return sDie;
    }

    //rolls a die of a given size
    public static int roll(int range){
        return getInstance().rand.nextInt(range) + 1;
    }

    public static int d20(){
        return roll(20);
    }
}
