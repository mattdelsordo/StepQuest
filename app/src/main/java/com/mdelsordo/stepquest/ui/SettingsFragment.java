package com.mdelsordo.stepquest.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.data.Saver;
import com.mdelsordo.stepquest.model.EffectPlayer;
import com.mdelsordo.stepquest.util.SureYouWantToDeleteDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";

    public static final String PREF_MUSIC = "pref_music";
    public static final String PREF_EFFECTS = "pref_effects";

    private Switch mMusicSwitch, mEffectSwitch;
    private Button mDelete, mFeedback;
    private ImageButton mInfo;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);



        SharedPreferences prefsGet = PreferenceManager.getDefaultSharedPreferences(getContext());

        //handle switch behavior
        mMusicSwitch = (Switch)view.findViewById(R.id.switch_settings_music);
        mMusicSwitch.setChecked(prefsGet.getBoolean(PREF_MUSIC, true));
        mMusicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.playEffect(EffectPlayer.CLICK);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_MUSIC, isChecked).apply();
                mListener.toggleMusic(isChecked);
                //Log.i(TAG, "Toggled music to " + isChecked);
            }
        });

        mEffectSwitch = (Switch)view.findViewById(R.id.switch_settings_effects);
        mEffectSwitch.setChecked(prefsGet.getBoolean(PREF_EFFECTS, true));
        mEffectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.playEffect(EffectPlayer.CLICK);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_EFFECTS, isChecked).apply();
                mListener.toggleEffects(isChecked);
                //Log.i(TAG, "Toggled effects to " + isChecked);
            }
        });

        mDelete = (Button)view.findViewById(R.id.button_settings_deleteCharacter);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                SureYouWantToDeleteDialog dialog = new SureYouWantToDeleteDialog();
                dialog.setTargetFragment(SettingsFragment.this, SureYouWantToDeleteDialog.REQUEST_CODE);
                dialog.show(manager, "DialogDelete");
            }
        });

        mInfo = (ImageButton)view.findViewById(R.id.b_settings_info);
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                mListener.playEffect(EffectPlayer.DIALOG);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_game_credits, null);
                //arrange hyperlinks
                TextView iconAttribute = (TextView)view.findViewById(R.id.textview_settings_iconattribute);
                iconAttribute.setText(Html.fromHtml("<a href=\"https://icons8.com/\">icons8</a>"));
                iconAttribute.setMovementMethod(LinkMovementMethod.getInstance());
                TextView effectsAttribute = (TextView)view.findViewById(R.id.textview_settings_effectsattribute);
                effectsAttribute.setText(Html.fromHtml("<a href=\"http://soundbible.com/\">SoundBible.com</a>"));
                effectsAttribute.setMovementMethod(LinkMovementMethod.getInstance());
                TextView musicAttribute = (TextView)view.findViewById(R.id.textview_settings_musicattribute);
                musicAttribute.setText(Html.fromHtml("<a href=\"https://www.looperman.com/users/profile/796811\">Zac Wilkins</a>"));
                musicAttribute.setMovementMethod(LinkMovementMethod.getInstance());

                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setView(view)
                        .setPositiveButton(getString(R.string.ok), null).create().show();
            }
        });

        mFeedback = (Button)view.findViewById(R.id.b_settings_feedback);
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playEffect(EffectPlayer.CLICK);
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.dev_email)});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
                email.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_body));
                startActivity(Intent.createChooser(email, getString(R.string.send_feedback_colon)));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SureYouWantToDeleteDialog.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //Saver.deleteAll(getContext());
            //Saver.dontSave = true;
            mListener.quitDelete();
        }
    }

    public interface SettingsListener {
        void toggleEffects(boolean shouldPlay);
        void toggleMusic(boolean shouldPlay);
        void quitDelete();
        void playEffect(String effectPath);
    }
    public SettingsListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (SettingsListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
