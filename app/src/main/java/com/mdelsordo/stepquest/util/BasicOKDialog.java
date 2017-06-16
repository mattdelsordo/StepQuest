package com.mdelsordo.stepquest.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mdelsordo.stepquest.R;

/**
 * Created by mdelsord on 5/28/17.
 * A simple dialog with a single button so I dont have to rewrite this junk over and over
 */

public class BasicOKDialog extends DialogFragment {

    public static final int REQUEST_CODE = 17;
    public static final String ARG_TEXT = "arg_text";

    public static BasicOKDialog newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        BasicOKDialog fragment = new BasicOKDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String text = args.getString(ARG_TEXT);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_long_dialog_text, null);
        TextView vText = (TextView)view.findViewById(R.id.textview_longdialog);
        vText.setText(text);
        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT).setView(view)
                .setPositiveButton(getString(R.string.ok), null).create();
    }


}
