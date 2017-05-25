package project3.csc214.stepquest.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.AdventureLog;
import project3.csc214.stepquest.model.JournalEntry;

/**
 * A simple {@link Fragment} subclass.
 * Fragment that displays the misc stats from the adventure log
 */
public class StatisticFragment extends Fragment implements AdventureLog.LogUpdateListener{


    public StatisticFragment() {
        // Required empty public constructor
    }

    private TextView mSteps, mDistance, mMonsters, mGold, mWeapons, mDungeons;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        mSteps = (TextView)view.findViewById(R.id.tv_statistics_stepstaken);
        mDistance = (TextView)view.findViewById(R.id.tv_statistics_distance);
        mMonsters = (TextView)view.findViewById(R.id.tv_statistics_monstertotal);
        mGold = (TextView)view.findViewById(R.id.tv_statistics_goldtotal);
        mWeapons = (TextView)view.findViewById(R.id.tv_statistics_weapontotal);
        mDungeons = (TextView)view.findViewById(R.id.tv_statistics_dungeonscleared);

        //update UI
        AdventureLog log = AdventureLog.getInstance(getContext());
        updateStats(log.getTotalSteps(), log.getApproxDistanceWalkedMiles(), log.getTotalMonstersSlain(), log.getTotalGoldAcquired(), log.getTotalWeaponsAcquired(), log.getTotalDungeonsCleared());

        return view;
    }

    //updates the UI based on new values
    @Override
    public void updateStats(int steps, double distance, int monsters, int gold, int weapons, int dungeons){
        mSteps.setText(getString(R.string.steps_taken_000) + " " + steps);
        mDistance.setText(getString(R.string.approx_distance_travelled_000_miles) + " " + String.format("%.2f",distance));
        mMonsters.setText(getString(R.string.total_monsters_defeated_000) + " " + monsters);
        mGold.setText(getString(R.string.lifetime_gold_acquired_000g) + " " + gold);
        mWeapons.setText(getString(R.string.lifetime_weapons_acquired_000) + " " + weapons);
        mDungeons.setText(getString(R.string.total_dungeons_cleared_000) + " " + dungeons);
    }

    @Override
    public void updateJournal(JournalEntry entry) { }

    @Override
    public void onPause() {
        super.onPause();
        AdventureLog.getInstance(getContext()).unbindLogUpdateListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        AdventureLog.getInstance(getContext()).bindLogUpdateListener(this);
    }
}
