package project3.csc214.stepquest.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.Stats;

/**
 * Handles the increment/decrement buttons for each individual stat
 */
public class StatManipulationFragment extends Fragment {

    private static final String TAG = "StatManipulation";

    //factory method for this fragment
    private static final String ARG_STAT = "arg_stat", ARG_ORIGINAL = "arg_original";
    public static StatManipulationFragment newInstance(int stat, int originalValue){
        Bundle args = new Bundle();
        args.putInt(ARG_STAT, stat);
        args.putInt(ARG_ORIGINAL, originalValue);
        StatManipulationFragment frag = new StatManipulationFragment();
        frag.setArguments(args);
        return frag;
    }


    public StatManipulationFragment() {
        // Required empty public constructor
    }

    private TextView mStatIndicator;
    private Button mIncrement, mDecrement;
    private int mStat, mOriginal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_stat_manipulation, container, false);

        mStat = getArguments().getInt(ARG_STAT);
        mOriginal = getArguments().getInt(ARG_ORIGINAL);

        mStatIndicator = (TextView)view.findViewById(R.id.textview_statmanip_text);
        updateStatIndicator();

        mDecrement = (Button)view.findViewById(R.id.button_statmanip_decrement);
        mDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriginal--;
                updateStatIndicator();
                mListener.statDecremented(mStat);
//                if(increments == 0) mDecrement.setEnabled(false);
//                mIncrement.setEnabled(true);
            }
        });

        mIncrement = (Button)view.findViewById(R.id.button_statmanip_increment);
        mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriginal++;
                updateStatIndicator();
                mListener.statIncremented(mStat);
//                if(pointsLeft == 0) mIncrement.setEnabled(false);
//                mDecrement.setEnabled(true);
            }
        });

        //restore things from state
        if(savedInstanceState != null){
            mOriginal = savedInstanceState.getInt(ARG_ORIGINAL);
            updateStatIndicator();
        }

        return view;
    }

    //updates the buttons based on some parameters
    public void updateButtons(int increments, int pointsLeft){
        Log.i(TAG, Stats.statToText(mStat) + ": " + increments + " " + pointsLeft);
        if(increments > 0) mDecrement.setEnabled(true);
        else mDecrement.setEnabled(false);

        if(pointsLeft > 0) mIncrement.setEnabled(true);
        else mIncrement.setEnabled(false);
    }

    public void updateStatIndicator(){
        mStatIndicator.setText(Stats.statToText(mStat) + ": " + mOriginal);
    }

    public interface StatManipListener{
        int statIncremented(int stat); //returns the remaining amount of points
        int statDecremented(int stat); //returns the number of increments done so far
    }
    private StatManipListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (StatManipListener)getParentFragment(); //TODO: not sure if this'll work if it goes here.
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_ORIGINAL, mOriginal);
    }
}
