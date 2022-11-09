package com.example.tpexam.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tpexam.Model.Person;
import com.example.tpexam.R;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class PersonRecycleAdapter extends RecyclerView.Adapter<PersonRecycleAdapter.ViewHolder> {

    private ArrayList<Person> localDataSet;

    private final PersonRecycleViewClickListener listener;

    public interface PersonRecycleViewClickListener {
        void onDeleteClicked(Person person);

        void onDeleteLongPress(Person person);

        void onItemLongPress(Person person);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private int id = -1;
        private final LinearLayout mainLayout;
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView ageTextView;
        private final Button deleteButton;
        private final WeakReference<PersonRecycleViewClickListener> listenerRef;

        public ViewHolder(View view, PersonRecycleViewClickListener listener) {
            super(view);

            listenerRef = new WeakReference<>(listener);
            nameTextView = (TextView) view.findViewById(R.id.name);
            emailTextView = (TextView) view.findViewById(R.id.email);
            ageTextView = (TextView) view.findViewById(R.id.age);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);
            mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
            deleteButton.setOnClickListener(this);
            deleteButton.setOnLongClickListener(this);
            mainLayout.setOnLongClickListener(this);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getEmailTextView() {
            return emailTextView;
        }

        public TextView getAgeTextView() {
            return ageTextView;
        }

        public Button getDeleteButtonView() {
            return deleteButton;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == deleteButton.getId()) {
                Person p = new Person()
                        .setId(id);
                p.read();
                listenerRef.get().onDeleteClicked(p);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Person p = new Person()
                    .setId(id);
            p.read();
            if (view.getId() == deleteButton.getId()) {
                listenerRef.get().onDeleteLongPress(p);
            } else {
                listenerRef.get().onItemLongPress(p);
            }
            return true;
        }
    }

    public PersonRecycleAdapter(ArrayList<Person> dataSet, PersonRecycleViewClickListener listener) {
        localDataSet = dataSet;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.person_list_item, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Person person = localDataSet.get(position);
        viewHolder.setId(person.getId());
        viewHolder.getNameTextView().setText(person.getName());
        viewHolder.getEmailTextView().setText(person.getEmail());
        viewHolder.getAgeTextView().setText(String.valueOf(Math.round(person.getAge())));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void append(Person p) {
        localDataSet.add(p);
        this.notifyItemInserted(localDataSet.size() - 1);
        p.save();
    }

    public void delete(Person p) {
        int index = localDataSet.indexOf(p);
        localDataSet.remove(index);
        this.notifyItemRemoved(index);
        p.delete();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void readAll() {
        try {
            localDataSet = Person.readAll();
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void search(String input) {
        if(Objects.equals(input, "")){
            readAll();
        };
        try {
            localDataSet = Person.search(input);
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
