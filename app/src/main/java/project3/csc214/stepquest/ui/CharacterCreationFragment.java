package project3.csc214.stepquest.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.Die;
import project3.csc214.stepquest.model.Race;
import project3.csc214.stepquest.model.Stats;
import project3.csc214.stepquest.model.Vocation;

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

    private int[] mStats = new int[Stats.STAT_VOLUME]; //stats
    private int mRace = Race.BIRDPERSON;
    private int mClass = Vocation.FIGHTER;

    public CharacterCreationFragment() {
        // Required empty public constructor
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
                else Log.e(TAG, "Incorrect class designation: " + vocation);
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
                else Log.e(TAG, "Incorrect race designation: " + race);

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
                generateStats();
            }
        });

        mCreate = (Button)view.findViewById(R.id.button_create_create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //load stuff from the saved state
        if(savedInstanceState != null){
            mName.setText(savedInstanceState.getString(ARG_NAME));
            mClass = savedInstanceState.getInt(ARG_CLASS);
            mRace = savedInstanceState.getInt(ARG_RACE);
            mStats = savedInstanceState.getIntArray(ARG_STATS);
            updateAllStatViews();
        }

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

        updateAllStatViews();
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
    }
}