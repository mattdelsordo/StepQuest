package project3.csc214.stepquest.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.AdventureLog;
import project3.csc214.stepquest.model.JournalEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class JournalFragment extends Fragment implements AdventureLog.LogUpdateListener{
    private static final String TAG = "JournalFragment";

    public JournalFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        mRecycler = (RecyclerView)view.findViewById(R.id.rv_journal);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        updateJournal(AdventureLog.getInstance(getContext()).getEventLog());

        return view;
    }

    @Override
    public void updateJournal(ArrayDeque<JournalEntry> list){
        JournalEntry[] listArray = list.toArray(new JournalEntry[]{});
        ArrayList<JournalEntry> eventList = new ArrayList<>(Arrays.asList(listArray));
        JournalEntryAdapter refresh = new JournalEntryAdapter(eventList);
        mRecycler.setAdapter(refresh);
    }

    @Override
    public void onPause() {
        super.onPause();
        AdventureLog.getInstance(getContext()).unbindLogUpdateListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        AdventureLog.getInstance(getContext()).bindLogUpdateListener(this);
    }

    @Override
    public void updateStats(int steps, double distance, int monsters, int gold, int weapons, int dungeons) {

    }


    private class JournalEntryViewHolder extends RecyclerView.ViewHolder{
        private JournalEntry mEntry;
        private TextView mText, mDate;

        public JournalEntryViewHolder(View itemView) {
            super(itemView);

            mText = (TextView)itemView.findViewById(R.id.tv_Jentry_text);
            mDate = (TextView)itemView.findViewById(R.id.tv_Jentry_date);
        }

        public void bindEntry(JournalEntry entry){
            mEntry = entry;
            mText.setText(mEntry.getEntryText());
            mDate.setText(mEntry.getEntryDate());
        }
    }

    private class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryViewHolder>{
        private ArrayList<JournalEntry> mEventList;

        public JournalEntryAdapter(ArrayList<JournalEntry> list){
            mEventList = list;
        }

        @Override
        public JournalEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_journal_entry, parent, false);
            view.setElevation(2);
            JournalEntryViewHolder holder = new JournalEntryViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(JournalEntryViewHolder holder, int position) {
            JournalEntry event = mEventList.get(position);
            holder.bindEntry(event);
        }

        @Override
        public int getItemCount() {
            return mEventList.size();
        }
    }

}
