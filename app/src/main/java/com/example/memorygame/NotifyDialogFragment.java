package com.example.memorygame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NotifyDialogFragment extends DialogFragment {
    boolean doesRecord;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_record_notify_message);
        builder.setPositiveButton(R.string.dialog_record_notify_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(NotifyDialogFragment.this);
            }
        });
        builder.setNegativeButton(R.string.dialog_record_notify_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(NotifyDialogFragment.this);
            }
        });
        builder.show();
        return builder.create();
    }

    public interface NotifyDialogListener {
        public void onDialogPositiveClick(NotifyDialogFragment dialog);
        public void onDialogNegativeClick(NotifyDialogFragment dialog);
    }

    NotifyDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NotifyDialogListener)context;
        }catch (ClassCastException e) {
            throw new ClassCastException("Not implement NotifyDialogListener");
        }
    }
}
