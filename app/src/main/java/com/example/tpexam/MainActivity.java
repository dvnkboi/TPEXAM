package com.example.tpexam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tpexam.Adapters.PersonRecycleAdapter;
import com.example.tpexam.Broadcast.SmsBroadcastReceiver;
import com.example.tpexam.Fragments.AddPersonDialogFragment;
import com.example.tpexam.Fragments.DeletePersonDialogFragment;
import com.example.tpexam.Model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding4.widget.RxTextView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AddPersonDialogFragment.AddPersonDialogListener,
        DeletePersonDialogFragment.DeletePersonDialogListener, SmsBroadcastReceiver.SmsBroadcastListener {
    private FloatingActionButton addPersonButton;
    private Person deletingPerson = null;
    private PersonRecycleAdapter adapter;
    private EditText searchField;
    private ActivityResultLauncher<Intent> editPersonLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPersonButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        searchField = (EditText) findViewById(R.id.searchField);

        Person.setContext(this);
        SmsBroadcastReceiver.setContext(this);

        setupPersonRecyclerView();
        setUpSearch();
        setUpEditActivityLauncher();

        setTitle("People List");
    }

    private void setUpEditActivityLauncher() {
        editPersonLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                adapter.edit((Person) data.getSerializableExtra("editPerson"));
                            }
                        }
                    }
                });
    }

    private void setUpSearch() {
        RxTextView.textChanges(searchField).map(CharSequence::toString).debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(input -> {
                    adapter.search(input);
                });
    }

    private void setupPersonRecyclerView() {
        adapter = null;
        try {
            adapter = new PersonRecycleAdapter(Person.readAll(),
                    new PersonRecycleAdapter.PersonRecycleViewClickListener() {
                        @Override
                        public void onDeleteClicked(Person person) {
                            deletingPerson = person;
                            spawnPersonDeleteModal(MainActivity.this.getCurrentFocus());
                        }

                        @Override
                        public void onDeleteLongPress(Person person) {
                            adapter.delete(person);
                        }

                        @Override
                        public void onItemLongPress(Person person) {
                            openEditActivity(person);
                        }

                        @Override
                        public void onItemClicked(Person person) {
                            Toast.makeText(MainActivity.this, "Long press to edit " + person.getName(), Toast.LENGTH_SHORT).show();
                        }

                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RecyclerView list = (RecyclerView) findViewById(R.id.personList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.notifyItemRangeInserted(0, adapter.getItemCount());
        registerForContextMenu(list);
    }

    public void openEditActivity(Person person) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("editPerson", person);
        editPersonLauncher.launch(intent);
    }

    public void spawnPersonAddModal(View view) {
        DialogFragment addPersonDialog = new AddPersonDialogFragment();
        addPersonDialog.show(getSupportFragmentManager(), "Add Person");
    }

    public void spawnPersonDeleteModal(View view) {
        DialogFragment deletePersonDialog = new DeletePersonDialogFragment();
        deletePersonDialog.show(getSupportFragmentManager(), "Delete Person");
    }

    @Override
    public void onAddPersonDialogConfirm(AddPersonDialogFragment dialog) {
        try {
            Person person = new Person()
                    .setFirstName(dialog.getFirstNameInput())
                    .setLastName(dialog.getLastNameInput())
                    .setEmail(dialog.getEmailInput())
                    .setAge(Integer.parseInt(dialog.getAgeInput()));
            adapter.append(person);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAddPersonDialogCancel(AddPersonDialogFragment dialog) {
    }

    @Override
    public void onDeletePersonDialogConfirm(DeletePersonDialogFragment dialog) {
        adapter.delete(deletingPerson);
    }

    @Override
    public void onDialogNegativeClick(DeletePersonDialogFragment dialog) {

    }

    @Override
    public void onCreatePerson(Person p) {
        adapter.append(p);
    }

    @Override
    public void onEditPerson(Person p) {
        adapter.edit(p);
    }

    @Override
    public void onDeletePerson(Person p) {
        adapter.delete(p);
    }
}