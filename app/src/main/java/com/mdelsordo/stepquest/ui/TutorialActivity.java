package com.mdelsordo.stepquest.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;
import com.mdelsordo.stepquest.model.EventQueue;
import com.mdelsordo.stepquest.services.MusicManagerService;

public class TutorialActivity extends AppCompatActivity implements EventQueue.MakeToastListener{

    public static final int REQUEST_CODE = 9;
    private static final String ARG_REMINDER = "arg_reminder";

    public static Intent newInstance(Context c, boolean showReminder){
        Intent intent = new Intent(c, TutorialActivity.class);
        intent.putExtra(ARG_REMINDER, showReminder);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        setTitle(getString(R.string.tutorial_title));

        //set up recyclerview
        RecyclerView recycler = (RecyclerView)findViewById(R.id.r_tutorial_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new ImageAdapter());

        final boolean showReminder = getIntent().getBooleanExtra(ARG_REMINDER, false);

        Button cont = (Button)findViewById(R.id.b_tutorial_continue);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showReminder){
                    View view = LayoutInflater.from(TutorialActivity.this).inflate(R.layout.view_long_dialog_text, null);
                    TextView text = (TextView)view.findViewById(R.id.textview_longdialog);
                    text.setText(getString(R.string.tutorial_reminder));
                    AlertDialog dialog = new AlertDialog.Builder(TutorialActivity.this)
                            .setView(view)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    close();
                                }
                            }).create();
                    dialog.show();
                }else{
                    close();
                }
            }
        });
    }

    private void close(){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void makeToast(String text, int duration) {

    }

    @Override
    public void playJingle() {

    }

    //recyclerview components
    private class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;

        public ImageViewHolder(View itemView){
            super(itemView);
            mImageView = (ImageView)itemView;
        }

        public void bindImage(int image_id){
            mImageView.setImageResource(image_id);
        }
    }
    private class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder>{
        private int[] mImageIds;

        public ImageAdapter(){
            mImageIds = new int[]{R.drawable.temp_tutorial, R.drawable.temp_tutorial, R.drawable.temp_tutorial};
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_scaled_image, parent, false);
            ImageViewHolder holder = new ImageViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.bindImage(mImageIds[position]);
        }

        @Override
        public int getItemCount() {
            return mImageIds.length;
        }
    }

    //Bind activity to music player service
    private boolean mPlayMusic;
    private boolean mIsBound = false;
    private MusicManagerService mMusicPlayer;
    private ServiceConnection mSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.i(TAG, "Music service connected.");
            MusicManagerService.MusicBinder binder = (MusicManagerService.MusicBinder)service;
            mMusicPlayer = binder.getService();
            mIsBound = true;

            //do check for music playing
            if(mPlayMusic&&!mMusicPlayer.isPlaying())mMusicPlayer.play(MusicManagerService.MAIN_JINGLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.i(TAG, "Music service disconnected");
            mIsBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayMusic = prefs.getBoolean(SettingsFragment.PREF_MUSIC, true);

        Intent intent = new Intent(this, MusicManagerService.class);
        bindService(intent, mSCon, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mIsBound){
            unbindService(mSCon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventQueue.getInstance(this).bindToastListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventQueue.getInstance(this).unbindToastListener();
    }
}
