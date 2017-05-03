package project3.csc214.stepquest.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import project3.csc214.stepquest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public static final String PREF_MUSIC = "pref_music";
    public static final String PREF_EFFECTS = "pref_effects";

    private Switch mMusicSwitch, mEffectSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //arrange hyperlinks
        TextView iconAttribute = (TextView)view.findViewById(R.id.textview_settings_iconattribute);
        iconAttribute.setText(Html.fromHtml("<a href=\"https://icons8.com/\">icons8</a>"));
        iconAttribute.setMovementMethod(LinkMovementMethod.getInstance());
        TextView effectsAttribute = (TextView)view.findViewById(R.id.textview_settings_effectsattribute);
        effectsAttribute.setText(Html.fromHtml("<a href=\"https://www.looperman.com/users/profile/796811\">Zac Wilkins</a>"));
        effectsAttribute.setMovementMethod(LinkMovementMethod.getInstance());
        TextView musicAttribute = (TextView)view.findViewById(R.id.textview_settings_musicattribute);
        musicAttribute.setText(Html.fromHtml("<a href=\"https://www.freesound.org/people/LittleRobotSoundFactory/\">LittleRobotSoundFactory</a>"));
        musicAttribute.setMovementMethod(LinkMovementMethod.getInstance());

        //handle switch behavior
        mMusicSwitch = (Switch)view.findViewById(R.id.switch_settings_music);
        mMusicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_MUSIC, isChecked).apply();
                mListener.toggleMusic(isChecked);
            }
        });

        mEffectSwitch = (Switch)view.findViewById(R.id.switch_settings_effects);
        mEffectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_EFFECTS, isChecked).apply();
                mListener.toggleEffects(isChecked);
            }
        });

        return view;
    }

    public interface SoundSettingsListener{
        void toggleEffects(boolean shouldPlay);
        void toggleMusic(boolean shouldPlay);
    }
    public SoundSettingsListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (SoundSettingsListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
