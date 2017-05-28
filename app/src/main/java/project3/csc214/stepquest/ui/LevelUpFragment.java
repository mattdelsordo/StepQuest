package project3.csc214.stepquest.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.ActiveCharacter;
import project3.csc214.stepquest.model.Character;
import project3.csc214.stepquest.model.Stats;

/**
 * Displays information for levelling up the character
 */
public class LevelUpFragment extends Fragment implements StatManipulationFragment.StatManipListener{

    public interface LevelUpDoneListener{
        void lvlUpDone();
    }
    private LevelUpDoneListener mListener;

    //tags to persist state
    private static final String ARG_PT_DISPLAY = "arg_ptdisplay", ARG_TOKENS = "arg_tokens", ARG_INCREMENTS = "arg_increments";

    private ImageView mClass, mRace;
    private TextView mName, mPointDisplay;
    private StatManipulationFragment mStr, mDex, mCon, mInt, mWis, mChr;
    private Button mCommit;

    private int mLevelUpTokenTotal;
    private int[] mStatIncrements = new int[Stats.STAT_VOLUME];

    public LevelUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level_up, container, false);

        mClass = (ImageView)view.findViewById(R.id.imageview_levelup_class);
        mRace = (ImageView)view.findViewById(R.id.imageview_levelup_race);
        mName = (TextView)view.findViewById(R.id.textview_levelup_name);
        mPointDisplay = (TextView)view.findViewById(R.id.textview_levelup_points);



        //get info from character
        Character active = ActiveCharacter.getInstance(getContext()).getActiveCharacter();
        mLevelUpTokenTotal = active.getLvlUpTokenAmnt();

        //reload things from state
        if(savedInstanceState != null){
            mPointDisplay.setText(savedInstanceState.getString(ARG_PT_DISPLAY));
            mLevelUpTokenTotal = savedInstanceState.getInt(ARG_TOKENS);
            mStatIncrements = savedInstanceState.getIntArray(ARG_INCREMENTS);
        }

        //add stat manipulators to layout
        FragmentManager manager = getChildFragmentManager();
        mStr = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_str);
        mDex = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_dex);
        mCon = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_con);
        mInt = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_int);
        mWis = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_wis);
        mChr = (StatManipulationFragment)manager.findFragmentById(R.id.frame_levelup_chr);
        if(mStr == null){
            mStr = StatManipulationFragment.newInstance(Stats.STR, active.getStat(Stats.STR));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_str, mStr).commit();
        }
        if(mDex == null){
            mDex = StatManipulationFragment.newInstance(Stats.DEX, active.getStat(Stats.DEX));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_dex, mDex).commit();
        }
        if(mCon == null){
            mCon = StatManipulationFragment.newInstance(Stats.CON, active.getStat(Stats.CON));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_con, mCon).commit();
        }
        if(mInt == null){
            mInt = StatManipulationFragment.newInstance(Stats.INT, active.getStat(Stats.INT));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_int, mInt).commit();
        }
        if(mWis == null){
            mWis = StatManipulationFragment.newInstance(Stats.WIS, active.getStat(Stats.WIS));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_wis, mWis).commit();
        }
        if(mChr == null){
            mChr = StatManipulationFragment.newInstance(Stats.CHR, active.getStat(Stats.CHR));
            getChildFragmentManager().beginTransaction().add(R.id.frame_levelup_chr, mChr).commit();
        }

        //handle commit button
        mCommit = (Button) view.findViewById(R.id.button_levelup_commit);
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check to see if all points have been allocated
                if(mLevelUpTokenTotal > 0){
                    Toast.makeText(getContext(), "You have " + mLevelUpTokenTotal + " points left to allocate!", Toast.LENGTH_SHORT).show();
                }else{
                    //else, commit the changes and finish
                    for(int i = 0; i < mStatIncrements.length; i++){
                        ActiveCharacter.getInstance(getContext()).getActiveCharacter().addToBaseStat(i, mStatIncrements[i]);
                    }

                    //remove lvl up tokens
                    ActiveCharacter.getInstance(getContext()).getActiveCharacter().clearLvlUpTokens();

                    //done
                    mListener.lvlUpDone();
                }
            }
        });

        updateUI(active);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (LevelUpDoneListener)context;
    }

    //updates the interface based on the active character
    public void updateUI(Character character){
        mName.setText(character.getName());
        mRace.setImageResource(character.getRace().getDrawable());
        mClass.setBackgroundColor(ContextCompat.getColor(getContext(), character.getVocation().getColor()));
        updateTokenTotal(mLevelUpTokenTotal);
    }

    public void updateTokenTotal(int total){
        mPointDisplay.setText("Level-up Points: " + total);
    }


    @Override
    public int statIncremented(int stat) {
        mStatIncrements[stat]++;
        mLevelUpTokenTotal--;
        updateTokenTotal(mLevelUpTokenTotal);

        mStr.updateButtons(mStatIncrements[Stats.STR], mLevelUpTokenTotal);
        mDex.updateButtons(mStatIncrements[Stats.DEX], mLevelUpTokenTotal);
        mInt.updateButtons(mStatIncrements[Stats.INT], mLevelUpTokenTotal);
        mWis.updateButtons(mStatIncrements[Stats.WIS], mLevelUpTokenTotal);
        mCon.updateButtons(mStatIncrements[Stats.CON], mLevelUpTokenTotal);
        mChr.updateButtons(mStatIncrements[Stats.CHR], mLevelUpTokenTotal);

        return mLevelUpTokenTotal;
    }

    @Override
    public int statDecremented(int stat) {
        mStatIncrements[stat]--;
        mLevelUpTokenTotal++;
        updateTokenTotal(mLevelUpTokenTotal);

        mStr.updateButtons(mStatIncrements[Stats.STR], mLevelUpTokenTotal);
        mDex.updateButtons(mStatIncrements[Stats.DEX], mLevelUpTokenTotal);
        mInt.updateButtons(mStatIncrements[Stats.INT], mLevelUpTokenTotal);
        mWis.updateButtons(mStatIncrements[Stats.WIS], mLevelUpTokenTotal);
        mCon.updateButtons(mStatIncrements[Stats.CON], mLevelUpTokenTotal);
        mChr.updateButtons(mStatIncrements[Stats.CHR], mLevelUpTokenTotal);

        return mStatIncrements[stat];
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_PT_DISPLAY, mPointDisplay.getText().toString());
        outState.putInt(ARG_TOKENS, mLevelUpTokenTotal);
        outState.putIntArray(ARG_INCREMENTS, mStatIncrements);
    }


}
