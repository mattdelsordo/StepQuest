package project3.csc214.stepquest.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        //bind to event queue
//        EventQueue queue = EventQueue.getInstance(getContext());
//        queue.bindUpdateListener(this);
        EventQueue.getInstance(getContext()).bindUpdateListener(this);

        //refresh progress
//        updateEvent(queue.getTopEvent(), queue.getProgress());

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        EventQueue queue = EventQueue.getInstance(getContext());
        updateEvent(queue.getTopEvent(), queue.getProgress());
    }

    //updates progress bar/description based on top event on the event queue
//    public void refresh(){
//        EventQueue queue = EventQueue.getInstance(getContext());
//        mDesc.setText(queue.getTopEvent().getDescription());
//        mProgress.setMax(queue.getTopEvent().getDuration());
//        mProgress.setProgress(queue.getProgress());
//    }

    //updates the event, called by the event queue when updates happen
    @Override
    public void updateEvent(Event e, int progress) {
        mDesc.setText(e.getDescription());
        mProgress.setMax(e.getDuration());
        mProgress.setProgress(progress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventQueue.getInstance(getContext()).unbindUpdateListener();
    }
}
