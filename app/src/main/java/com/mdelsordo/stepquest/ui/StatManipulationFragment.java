package com.mdelsordo.stepquest.ui;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.Stats;
import com.mdelsordo.stepquest.util.BasicOKDialog;

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
    private ImageButton mIncrement, mDecrement;
    private Button mStatInfo;
    private int mStat, mOriginal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_stat_manipulation, container, false);

        mStat = getArguments().getInt(ARG_STAT);
        mOriginal = getArguments().getInt(ARG_ORIGINAL);

        mStatInfo = (Button)view.findViewById(R.id.b_statmanip_statinfo);
        initializeStatInfo();

        mStatIndicator = (TextView)view.findViewById(R.id.textview_statmanip_text);
        updateStatIndicator();

        mDecrement = (ImageButton)view.findViewById(R.id.button_statmanip_decrement);
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

        mIncrement = (ImageButton)view.findViewById(R.id.button_statmanip_increment);
        mIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOriginal++;
                updateStatIndicator();
                mListener.statIncremented(mStat);
//   tri             if(pointsLeft == 0) mIncrement.setEnabled(false);
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
        //Log.i(TAG, Stats.statToText(mStat) + ": " + increments + " " + pointsLeft);
        if(increments > 0){
            showButton(mDecrement, true);
        }
        else{
            showButton(mDecrement, false);
        }

        if(pointsLeft > 0) showButton(mIncrement, true);
        else showButton(mIncrement, false);
    }

    private void showButton(ImageButton button, boolean visibility){
        button.setEnabled(visibility);
        if(visibility)button.setVisibility(View.VISIBLE);
        else button.setVisibility(View.INVISIBLE);
    }

    public void updateStatIndicator(){
        mStatIndicator.setText(Integer.toString(mOriginal));
        if(mOriginal < 7) mStatIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.bad));
        else if(mOriginal > 13) mStatIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.good));
        else mStatIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.neutral));
    }

    public void initializeStatInfo(){
        Drawable draw;
        String text;
        final String infoText;
        switch(mStat){
            case Stats.STR:
                text = getString(R.string.str);
                draw = getResources().getDrawable(R.drawable.ic_strength);
                infoText = getString(R.string.explainSTR);
                break;
            case Stats.DEX:
                text = getString(R.string.dex);
                draw = getResources().getDrawable(R.drawable.ic_dexterity);
                infoText = getString(R.string.explainDEX);
                break;
            case Stats.CON:
                text = getString(R.string.con);
                draw = getResources().getDrawable(R.drawable.ic_constitution);
                infoText = getString(R.string.explainCON);
                break;
            case Stats.INT:
                text = getString(R.string.intelligence_abbrev);
                draw = getResources().getDrawable(R.drawable.ic_intelligence);
                infoText = getString(R.string.explainINT);
                break;
            case Stats.WIS:
                text = getString(R.string.wis);
                draw = getResources().getDrawable(R.drawable.ic_wisdom);
                infoText = getString(R.string.explainWIS);
                break;
            case Stats.CHR:
                text = getString(R.string.chr);
                draw = getResources().getDrawable(R.drawable.ic_charisma);
                infoText = getString(R.string.explainCHR);
                break;
            default:
                text = getString(R.string.unknown_parameter);
                draw = getResources().getDrawable(R.drawable.ic_misc);
                infoText = getString(R.string.unknown_parameter);
                break;
        }

        mStatInfo.setText(text);
        mStatInfo.setCompoundDrawablesWithIntrinsicBounds(draw, null, null, null);
        mStatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BasicOKDialog().newInstance(infoText).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
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
