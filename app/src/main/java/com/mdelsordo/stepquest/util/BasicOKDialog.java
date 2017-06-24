package com.mdelsordo.stepquest.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 5/28/17.
 * A simple dialog with a single button so I dont have to rewrite this junk over and over
 */

public class BasicOKDialog extends DialogFragment {
    private static final String TAG = "BasicOkDialog";

    public static final int REQUEST_CODE = 17;
    public static final String ARG_TEXT = "arg_text", ARG_TITLE = "arg_title";

    private Dialog dialog;

    public static BasicOKDialog newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putString(ARG_TITLE, null);
        BasicOKDialog fragment = new BasicOKDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static BasicOKDialog newInstance(String text, String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putString(ARG_TITLE, title);
        BasicOKDialog fragment = new BasicOKDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String text = args.getString(ARG_TEXT);
        String title = args.getString(ARG_TITLE);

        //View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        //TextView vText = (TextView)view.findViewById(R.id.textview_longdialog);
        //vText.setText(text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme).setMessage(text)
                .setPositiveButton(getString(R.string.ok), null);
        if(title != null) builder.setTitle(title);
        dialog = builder.create();
        return dialog;
    }

}
