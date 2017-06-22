package com.mdelsordo.stepquest.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.Die;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.model.Race;
import com.mdelsordo.stepquest.model.Stats;
import com.mdelsordo.stepquest.model.Vocation;
import com.mdelsordo.stepquest.util.BasicOKDialog;
import com.mdelsordo.stepquest.util.Logger;
import com.mdelsordo.stepquest.util.PlayEffectListener;

import java.util.Arrays;

/**
 * This fragment handles creating a new character
 */
public class CharacterCreationFragment extends Fragment {

    private static final String TAG = "CharacterCreationFrag";
    public static final String ARG_NAME = "creation_name", ARG_RACE = "creation_race", ARG_CLASS = "creation_class", ARG_STATS = "creation_stats";

    private ImageView mClassImage, mRaceImage;
    private EditText mName;
    private Spinner mClassSpinner, mRaceSpinner;
    private TextView mStrView, mDexView, mConView, mIntView, mWisView, mChrView;
    private Button mRoll, mCreate;
    private CreationCompleteListener mListener;
//    private EffectPlayer mEffectPlayer;

    private int[] mStats = new int[Stats.STAT_VOLUME]; //stats
    private int mRace = Race.BIRDPERSON;
    private int mClass = Vocation.FIGHTER;

    public CharacterCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mEffectPlayer = new EffectPlayer(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_creation, container, false);

        //get views
        mClassImage = (ImageView)view.findViewById(R.id.imageview_creation_class);
        mRaceImage = (ImageView)view.findViewById(R.id.imageview_creation_race);

        mName = (EditText)view.findViewById(R.id.edittext_creation_name);

        mStrView = (TextView)view.findViewById(R.id.textview_create_strval);
        mDexView = (TextView)view.findViewById(R.id.textview_create_dexval);
        mConView = (TextView)view.findViewById(R.id.textview_create_conval);
        mIntView = (TextView)view.findViewById(R.id.textview_create_intval);
        mWisView = (TextView)view.findViewById(R.id.textview_create_wisval);
        mChrView = (TextView)view.findViewById(R.id.textview_create_chrval);

        //spinners
        mClassSpinner = (Spinner)view.findViewById(R.id.spinner_creation_class);
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.class_choices, android.R.layout.simple_spinner_dropdown_item);
        mClassSpinner.setAdapter(classAdapter);
        mClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vocation = (String)parent.getItemAtPosition(position);
                if(vocation.equals("Warrior")){
                    mClass = Vocation.FIGHTER;
                    mClassImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.warrior));
                }
                else if(vocation.equals("Sorcerer")){
                    mClass = Vocation.WIZARD;
                    mClassImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.sorcerer));
                }
                else if(vocation.equals("Ranger")){
                    mClass = Vocation.RANGER;
                    mClassImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ranger));
                }
                else if(vocation.equals("Cleric")){
                    mClass = Vocation.CLERIC;
                    mClassImage.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cleric));
                }
                //else Log.e(TAG, "Incorrect class designation: " + vocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRaceSpinner = (Spinner)view.findViewById(R.id.spinner_creation_race);
        ArrayAdapter<CharSequence> raceAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.race_choices, android.R.layout.simple_spinner_dropdown_item);
        mRaceSpinner.setAdapter(raceAdapter);
        mRaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String race = (String)parent.getItemAtPosition(position);
                if(race.equals("Birdperson")){
                    mRace = Race.BIRDPERSON;
                    mRaceImage.setImageResource(R.drawable.birdperson1);
                }
                else if(race.equals("Math'head")){
                    mRace = Race.MATHHEAD;
                    mRaceImage.setImageResource(R.drawable.mathhead1);
                }
                else if(race.equals("Pump-kin")){
                    mRace = Race.PUMPKIN;
                    mRaceImage.setImageResource(R.drawable.pumpkin1);
                }
                else if(race.equals("Elf")){
                    mRace = Race.ELF;
                    mRaceImage.setImageResource(R.drawable.elf1white);
                }
                //else Log.e(TAG, "Incorrect race designation: " + race);

                updateAllStatViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRoll = (Button)view.findViewById(R.id.button_create_generateStats);
        mRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generateStats();
                mListener.playEffect(EffectPlayer.CLICK);
                animateDiceroll();
            }
        });

        mCreate = (Button)view.findViewById(R.id.button_create_create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                String name = mName.getText().toString();
                if(!name.equals(null) && !name.equals("")){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(ARG_NAME, name);
                    returnIntent.putExtra(ARG_STATS, mStats);
                    returnIntent.putExtra(ARG_CLASS, mClass);
                    returnIntent.putExtra(ARG_RACE, mRace);
                    mListener.creationComplete(returnIntent);
                }else{
                    Toast.makeText(getActivity(), getString(R.string.error_name_required), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //generate stats
        generateStats();
        updateAllStatViews();

        //load stuff from the saved state
        if(savedInstanceState != null){
            mName.setText(savedInstanceState.getString(ARG_NAME));
            mClass = savedInstanceState.getInt(ARG_CLASS);
            mRace = savedInstanceState.getInt(ARG_RACE);
            mStats = savedInstanceState.getIntArray(ARG_STATS);
            updateAllStatViews();
        }

        //handle stat information buttons
        Button strInfo = (Button)view.findViewById(R.id.b_create_strinfo);
        strInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainSTR)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        Button dexInfo = (Button)view.findViewById(R.id.b_create_dexinfo);
        dexInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainDEX)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        Button conInfo = (Button)view.findViewById(R.id.b_create_coninfo);
        conInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainCON)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        Button intInfo = (Button)view.findViewById(R.id.b_create_intinfo);
        intInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainINT)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        Button wisInfo = (Button)view.findViewById(R.id.b_create_wisinfo);
        wisInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainWIS)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });
        Button chrInfo = (Button)view.findViewById(R.id.b_create_chrinfo);
        chrInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                new BasicOKDialog().newInstance(getString(R.string.explainCHR)).show(getActivity().getSupportFragmentManager(), "Explaination");
            }
        });

        return view;
    }

    //generates new stats for the stat distribution
    private void generateStats(){
        mStats[Stats.STR] = Die.d20();
        mStats[Stats.DEX] = Die.d20();
        mStats[Stats.CON] = Die.d20();
        mStats[Stats.INT] = Die.d20();
        mStats[Stats.WIS] = Die.d20();
        mStats[Stats.CHR] = Die.d20();

        //updateAllStatViews();
    }

    private void updateAllStatViews(){
        updateStatView(mStrView,  Stats.STR);
        updateStatView(mDexView,  Stats.DEX);
        updateStatView(mConView,  Stats.CON);
        updateStatView(mIntView,  Stats.INT);
        updateStatView(mWisView,  Stats.WIS);
        updateStatView(mChrView,  Stats.CHR);
    }

    //updates the given textview with the given stat and sets the color depending on how good it is
    private void updateStatView(TextView text, int statMarker){
        int stat = mStats[statMarker] + Race.newRace(mRace).getStatBonus(statMarker);
        text.setText(Integer.toString(stat));
        if(stat < 7) text.setTextColor(ContextCompat.getColor(getContext(), R.color.bad));
        else if(stat > 13) text.setTextColor(ContextCompat.getColor(getContext(), R.color.good));
        else text.setTextColor(ContextCompat.getColor(getContext(), R.color.neutral));
    }

    //persist all this garbage aka kill me
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_NAME, mName.getText().toString());
        outState.putInt(ARG_CLASS, mClass);
        outState.putInt(ARG_RACE, mRace);
        outState.putIntArray(ARG_STATS, mStats);
    }

    //attach listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CreationCompleteListener)context;
    }

    //listener for this fragment
    public interface CreationCompleteListener{
        void creationComplete(Intent intent);
        void playEffect(String effectPath);
    }

    //animates the stat generation process
    public void animateDiceroll(){
        mRoll.setEnabled(false);
        //int[] oldstats = Arrays.copyOf(mStats, mStats.length);
        generateStats();
        ValueAnimator animator = ValueAnimator.ofInt(0, 20);
        animator.setDuration(400);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrView.setText(Integer.toString(Die.d20()));
                mConView.setText(Integer.toString(Die.d20()));
                mDexView.setText(Integer.toString(Die.d20()));
                mIntView.setText(Integer.toString(Die.d20()));
                mWisView.setText(Integer.toString(Die.d20()));
                mChrView.setText(Integer.toString(Die.d20()));
            }
        });
        animator.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //Logger.i(TAG, Arrays.toString(mStats));

                updateAllStatViews();
                mRoll.setEnabled(true);
            }
        });
        mListener.playEffect(EffectPlayer.DICE);
        //mEffectPlayer.play(EffectPlayer.DICE);
        animator.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mEffectPlayer.release();
    }
}
