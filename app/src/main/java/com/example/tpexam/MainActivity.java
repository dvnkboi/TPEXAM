package com.example.tpexam;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tpexam.Adapters.PersonRecycleAdapter;
import com.example.tpexam.Fragments.AddPersonDialogFragment;
import com.example.tpexam.Fragments.DeletePersonDialogFragment;
import com.example.tpexam.Model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding4.widget.RxTextView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AddPersonDialogFragment.AddPersonDialogListener, DeletePersonDialogFragment.DeletePersonDialogListener {
    private FloatingActionButton addPersonButton;

    private Person deletingPerson = null;
    private PersonRecycleAdapter adapter;

    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPersonButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        searchField = (EditText) findViewById(R.id.searchField);

        Person.setContext(this);

        Person p1 = new Person().setId(1).setLastName("yes").setFirstName("no").setAge(18).setEmail("ayman@outlook.com");
        Person p2 = new Person().setId(2).setLastName("no").setFirstName("yis <3").setAge(19).setEmail("me@outlook.com");

        p1.save();
        p2.save();

        adapter = null;
        try {
            adapter = new PersonRecycleAdapter(Person.readAll(), new PersonRecycleAdapter.PersonRecycleViewClickListener() {
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
                    System.out.println("------------ EDIT ------------\n" + person);
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

        RxTextView.textChanges(searchField)
                .map(CharSequence::toString)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(input -> {
                    adapter.search(input);
                });
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
        System.out.println("CLICKED ADD");
        try {
            Person person = new Person()
                    .setFirstName(dialog.getFirstNameInput())
                    .setLastName(dialog.getLastNameInput())
                    .setEmail(dialog.getEmailInput())
                    .setAge(Float.parseFloat(dialog.getAgeInput()));
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
}