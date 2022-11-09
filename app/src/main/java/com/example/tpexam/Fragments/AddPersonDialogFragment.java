package com.example.tpexam.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.example.tpexam.R;
import org.jetbrains.annotations.NotNull;

public class AddPersonDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText ageInput;
    private Button addButton;
    private Button cancelButton;
    private Button positiveButton;
    private Button negativeButton;

    public String getFirstNameInput() {
        return firstNameInput.getText().toString();
    }

    public String getLastNameInput() {
        return lastNameInput.getText().toString();
    }

    public String getEmailInput() {
        return emailInput.getText().toString();
    }

    public String getAgeInput() {
        return ageInput.getText().toString();
    }

    private boolean inputsValid(){
        try {
            Integer.parseInt(ageInput.getText().toString());
        } catch (final NumberFormatException e) {
            return false;
        }

        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.getText()).matches();
    }


    public interface AddPersonDialogListener {
        public void onAddPersonDialogConfirm(AddPersonDialogFragment dialog);

        public void onAddPersonDialogCancel(AddPersonDialogFragment dialog);
    }

    AddPersonDialogListener listener;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddPersonDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getClass().toString()
                    + " must implement AddPerson");
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder
                .setView(inflater.inflate(R.layout.add_person_dialog_box, null))
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onAddPersonDialogConfirm(AddPersonDialogFragment.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onAddPersonDialogCancel(AddPersonDialogFragment.this);
                    }
                });

        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                positiveButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                negativeButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setVisibility(View.GONE);
                negativeButton.setVisibility(View.GONE);

                firstNameInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.firstNameInput);
                lastNameInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.lastNameInput);
                emailInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.emailInput);
                ageInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.ageInput);
                addButton = (Button) ((AlertDialog) dialog).findViewById(R.id.deleteButton);
                cancelButton = (Button) ((AlertDialog) dialog).findViewById(R.id.cancelButton);

                addButton.setOnClickListener(new View.OnClickListener() {
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

                addButton.setEnabled(false);

                validateInput(ageInput);
                validateInput(emailInput);
            }
        });

        return dialog;
    }

    private void validateInput(EditText input) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                addButton.setEnabled(inputsValid());
            }
        });
    }
}
