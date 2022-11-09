package com.example.tpexam.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.example.tpexam.R;
import org.jetbrains.annotations.NotNull;

public class DeletePersonDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private Button deleteButton;
    private Button cancelButton;
    private Button positiveButton;
    private Button negativeButton;


    public interface DeletePersonDialogListener {
        public void onDeletePersonDialogConfirm(DeletePersonDialogFragment dialog);

        public void onDialogNegativeClick(DeletePersonDialogFragment dialog);
    }

    DeletePersonDialogListener listener;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeletePersonDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getClass().toString() + " must implement delete person");
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.delete_person_dialog_box, null)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDeletePersonDialogConfirm(DeletePersonDialogFragment.this);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(DeletePersonDialogFragment.this);
            }
        });

        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setVisibility(View.GONE);
                negativeButton.setVisibility(View.GONE);

                deleteButton = (Button) ((AlertDialog) dialog).findViewById(R.id.deleteButton);
                cancelButton = (Button) ((AlertDialog) dialog).findViewById(R.id.cancelButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positiveButton.performClick();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeButton.performClick();
                    }
                });

            }
        });

        return dialog;
    }
}
