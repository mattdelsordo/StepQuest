package project3.csc214.stepquest.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.model.AdventureLog;
import project3.csc214.stepquest.model.EventQueue;

/**
 * A simple {@link Fragment} subclass.
 */
public class JournalFragment extends Fragment implements AdventureLog.LogUpdateListener{

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
    public void updateJournal(ArrayDeque<String> list){
        String[] listArray = list.toArray(new String[]{});
        ArrayList<String> eventList = new ArrayList<>(Arrays.asList(listArray));
        StringAdapter refresh = new StringAdapter(eventList);
        mRecycler.setAdapter(refresh);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AdventureLog.getInstance(getContext()).bindLogUpdateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        AdventureLog.getInstance(getContext()).unbindLogUpdateListener();
    }

    @Override
    public void updateStats(int steps, double distance, int monsters, int gold, int weapons, int dungeons) {

    }


    private class StringViewHolder extends RecyclerView.ViewHolder{
        private String mText;
        private TextView mTextView;

        public StringViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView)itemView.findViewById(R.id.tv_simpleList);
        }

        public void bindString(String text){
            mText = text;
            mTextView.setText(mText);
        }
    }

    private class StringAdapter extends RecyclerView.Adapter<StringViewHolder>{
        private ArrayList<String> mEventList;

        public StringAdapter(ArrayList<String> list){
            mEventList = list;
        }

        @Override
        public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.view_simple_list_text, parent, false);
            view.setElevation(2);
            StringViewHolder holder = new StringViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(StringViewHolder holder, int position) {
            String event = mEventList.get(position);
            holder.bindString(event);
        }

        @Override
        public int getItemCount() {
            return mEventList.size();
        }
    }

}
