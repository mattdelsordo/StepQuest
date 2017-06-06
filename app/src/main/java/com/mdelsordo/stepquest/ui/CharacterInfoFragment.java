package com.mdelsordo.stepquest.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mdelsordo.stepquest.model.ActiveCharacter;
import com.mdelsordo.stepquest.model.Character;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.model.Stats;
import com.mdelsordo.stepquest.util.BasicOKDialog;
import com.mdelsordo.stepquest.util.InventorySoundListener;


/**
 * This class displays all character info.
 */
public class CharacterInfoFragment extends Fragment implements ActiveCharacter.ExpUpdateListener{

    private TextView mName, mRaceClass, mLevel, mSTR, mDEX, mCON, mINT, mWIS, mCHR;
    private ImageView mRaceImage, mClassImage;
    private ProgressBar mExpProgress;
    private InventorySoundListener mSoundListener;

    public CharacterInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_info, container, false);

        //get all the views because killll me
        mName = (TextView)view.findViewById(R.id.textview_character_name);
        mRaceClass = (TextView)view.findViewById(R.id.textview_character_raceclass);
        mLevel = (TextView)view.findViewById(R.id.textview_character_level);
        mSTR = (TextView)view.findViewById(R.id.textview_character_str);
        mDEX = (TextView)view.findViewById(R.id.textview_character_dex);
        mCON = (TextView)view.findViewById(R.id.textview_character_con);
        mINT = (TextView)view.findViewById(R.id.textview_character_int);
        mWIS = (TextView)view.findViewById(R.id.textview_character_wis);
        mCHR = (TextView)view.findViewById(R.id.textview_character_chr);
        mRaceImage = (ImageView)view.findViewById(R.id.imageview_character_race);
        mClassImage = (ImageView)view.findViewById(R.id.imageview_character_class);
        mExpProgress = (ProgressBar)view.findViewById(R.id.progressbar_character_exp);

        //on click listeners to provide information to the player
        mSTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainSTR)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        mDEX.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainDEX)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        mCON.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainCON)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        mINT.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainINT)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        mWIS.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainWIS)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        mCHR.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                new BasicOKDialog().newInstance(getString(R.string.explainCHR)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });

        Character active = ActiveCharacter.getInstance(getContext()).getActiveCharacter();
        if(active != null){
            updateUI(active);
            updateExpProgress(active);
        }

        return view;
    }

    //updates the interface based on the active character
    public void updateUI(Character character){
        mName.setText(character.getName());
        mRaceClass.setText(character.getRace() + " " + character.getVocation());
        mLevel.setText("Level: " + character.getLevel());
        mSTR.setText("STR: " + character.getStat(Stats.STR));
        mDEX.setText("DEX: " + character.getStat(Stats.DEX));
        mCON.setText("CON: " + character.getStat(Stats.CON));
        mINT.setText("INT: " + character.getStat(Stats.INT));
        mWIS.setText("WIS: " + character.getStat(Stats.WIS));
        mCHR.setText("CHR: " + character.getStat(Stats.CHR));
        mRaceImage.setImageResource(character.getRace().getDrawable());
        mClassImage.setBackgroundColor(ContextCompat.getColor(getContext(), character.getVocation().getColor()));
    }

    @Override
    public void updateExpProgress(Character c) {
        int level = c.getLevel();

        if(level < 20){
            int floor = Character.levelUpFunction(level - 1);
            int ceiling = Character.levelUpFunction(level);

            mExpProgress.setMax(ceiling);
            mExpProgress.setProgress(c.getExp() - floor);
        }else{
            mExpProgress.setMax(10);
            mExpProgress.setProgress(10);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ActiveCharacter.getInstance(getContext()).bindExpListener(this);
        updateUI(ActiveCharacter.getInstance(getContext()).getActiveCharacter());
    }

    @Override
    public void onPause() {
        super.onPause();
        ActiveCharacter.getInstance(getContext()).unbindExpListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSoundListener = (InventorySoundListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSoundListener = null;
    }
}
