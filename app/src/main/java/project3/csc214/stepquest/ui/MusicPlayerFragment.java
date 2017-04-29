package project3.csc214.stepquest.ui;


import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import project3.csc214.stepquest.R;

/**
 * A fragment that only plays some music track and otherwise has no layout
 */
public class MusicPlayerFragment extends Fragment {

    private static final String TAG = "MusicPlayer";
    private static final String ARG_TRACK = "arg_track";
    private static final String DIR_MUSIC = "music";

    private MediaPlayer mPlayer;
    private AssetManager mAssets;
    private String mTrackPath;


    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public static MusicPlayerFragment newInstance(String track){
        Bundle bun = new Bundle();
        bun.putString(ARG_TRACK, track);
        MusicPlayerFragment frag = new MusicPlayerFragment();
        frag.setArguments(bun);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate called");

        setRetainInstance(true);
        mAssets = getActivity().getAssets();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setLooping(true); //loop the track
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "Music player is prepared...");
                mPlayer.start();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "OnCreateView called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);

        //get track name argument
        mTrackPath = DIR_MUSIC + "/" + getArguments().getString(ARG_TRACK);
        //Log.i(TAG, mTrackPath);
        play(mTrackPath);

        return view;
    }

    private void play(String track){
        Log.i(TAG, "Attempting to play track " + track);
        try{
            AssetFileDescriptor afd = getActivity().getAssets().openFd(track);

            mPlayer.reset();
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mPlayer.prepareAsync();

        }catch(IOException ioe){
            Log.e(TAG, "Failed to play " + track);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
    }
}
