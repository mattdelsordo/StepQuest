package project3.csc214.stepquest.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.Event;
import project3.csc214.stepquest.model.EventQueue;

/**
 * This fragment handles the master progress bar
 */
public class ProgressFragment extends Fragment implements EventQueue.EventUpdateListener{

    private ProgressBar mProgress;
    private TextView mDesc;
    private TextView mTotal;
    private TextView mBoost;
    private ImageView mBoostImage;

    //broadcast receiver for the boost timer
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBoostTimer(intent);
        }
    };

    public ProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        mDesc = (TextView)view.findViewById(R.id.textview_progress_description);
        mProgress = (ProgressBar)view.findViewById(R.id.progressbar_progress_THE_BAR);
        mTotal = (TextView)view.findViewById(R.id.textview_progress_total);
        mBoost = (TextView)view.findViewById(R.id.tv_progress_boosttime);
        mBoostImage = (ImageView) view.findViewById(R.id.iv_progress_boostimage);

        //connect progress bar to listener
        EventQueue.getInstance(getContext()).bindUpdateListener(this);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        EventQueue queue = EventQueue.getInstance(getContext());
        updateEvent(queue.getTopEvent(), (int)queue.getProgress());
    }

    //updates the event, called by the event queue when updates happen
    @Override
    public void updateEvent(Event e, int progress) {
        mDesc.setText(e.getDescription());
        mProgress.setMax(e.getDuration());
        mProgress.setProgress(progress);
        mTotal.setText(e.getDuration().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventQueue.getInstance(getContext()).unbindUpdateListener();
    }

    //updates the boost timer via intent information
    public void updateBoostTimer(Intent intent){
        if(intent.getExtras() == null){
            //no extras == countdown is done
        }else{
            //extras == time left
        }
    }
}
