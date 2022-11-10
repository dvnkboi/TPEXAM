package com.example.tpexam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tpexam.Model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditActivity extends AppCompatActivity {
    private Person editPerson;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextAge;
    private FloatingActionButton editPersonConfirm;

    private boolean inputsValid() {
        if (editTextFirstName.getText().toString().trim().equals("")) {
            return false;
        }
        if (editTextLastName.getText().toString().trim().equals("")) {
            return false;
        }
        try {
            Integer.parseInt(editTextAge.getText().toString());
        } catch (final NumberFormatException e) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText()).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editPerson = (Person) getIntent().getSerializableExtra("editPerson");

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editPersonConfirm = (FloatingActionButton) findViewById(R.id.editPersonConfirm);

        editTextFirstName.setText(editPerson.getFirstName());
        editTextLastName.setText(editPerson.getLastName());
        editTextEmail.setText(editPerson.getEmail());
        editTextAge.setText(String.valueOf(editPerson.getAge()));

        setTitle(String.format("Editing %s", editPerson.getName()));

        validateInput(editTextFirstName);
        validateInput(editTextLastName);
        validateInput(editTextEmail);
        validateInput(editTextAge);

        editPersonConfirm.setEnabled(true);
    }

    public String getEditTextFirstName() {
        return editTextFirstName.getText().toString();
    }

    public String getEditTextLastName() {
        return editTextLastName.getText().toString();
    }

    public String getEditTextEmail() {
        return editTextEmail.getText().toString();
    }

    public int getEditTextAge() {
        return Integer.parseInt(editTextAge.getText().toString());
    }

    public void onEditButtonClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("editPerson", new Person().setId(editPerson.getId()).setFirstName(getEditTextFirstName()).setLastName(getEditTextLastName()).setEmail(getEditTextEmail()).setAge(getEditTextAge()));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void validateInput(EditText input) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editPersonConfirm.setEnabled(inputsValid());
            }
        });
    }
}