package com.example.tpexam.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.tpexam.DAO.PersonPersist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Person {
    private static PersonPersist persistence = null;
    public int id = -1;
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public float age = 0;

    public int getId() {
        return id;
    }

    public Person setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Person setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;
        return this;
    }

    public float getAge() {
        return age;
    }

    public Person setAge(float age) {
        this.age = age;
        return this;
    }

    public Person() {
    }

    public Person(String firstName, String lastName, String email, float age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
    }

    public long delete() {
        SQLiteDatabase db = persistence.getWritableDatabase();
        db.beginTransaction();
        long output = -1;

        boolean isNew = !this.exists();

        if (isNew) {
            output = 0;
        } else {
            String selection = PersonPersist.ID_COL + " = ?";
            String[] selectionArgs = {String.valueOf(this.id)};
            output = db.delete(PersonPersist.TABLE_NAME, selection, selectionArgs);
            this.age = -1;
            this.firstName = null;
            this.lastName = null;
            this.email = null;
            this.id = -1;
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return output;
    }

    public long save() {
        SQLiteDatabase db = persistence.getWritableDatabase();
        db.beginTransaction();
        long output = -1;

        ContentValues values = new ContentValues();
        values.put(PersonPersist.FIRST_NAME_COL, firstName);
        values.put(PersonPersist.LAST_NAME_COL, lastName);
        values.put(PersonPersist.AGE_COL, age);
        values.put(PersonPersist.EMAIL_COL, email);

        boolean isNew = !this.exists();

        if (isNew) {
            output = db.insert(PersonPersist.TABLE_NAME, null, values);
        } else {
            String selection = PersonPersist.ID_COL + " = ?";
            String[] selectionArgs = {String.valueOf(this.id)};
            output = db.update(PersonPersist.TABLE_NAME, values, selection, selectionArgs);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        return output;
    }

    public Cursor getById() {
        SQLiteDatabase db = persistence.getReadableDatabase();


        String[] projection = {PersonPersist.ID_COL, PersonPersist.FIRST_NAME_COL, PersonPersist.LAST_NAME_COL, PersonPersist.EMAIL_COL, PersonPersist.AGE_COL};

        String selection = PersonPersist.ID_COL + " = ?";
        String[] selectionArgs = {String.valueOf(this.id)};

        return db.query(PersonPersist.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
    }

    public boolean exists() {
        boolean result = false;

        Cursor cursor = getById();

        result = cursor.moveToFirst();

        cursor.close();
        return result;
    }

    @Nullable
    public Person read() {
        Person result = null;

        Cursor cursor = getById();

        if (cursor.moveToFirst()) {
            try {
                int ageIndex = cursor.getColumnIndex(PersonPersist.AGE_COL);
                int firstNameIndex = cursor.getColumnIndex(PersonPersist.FIRST_NAME_COL);
                int lastNameIndex = cursor.getColumnIndex(PersonPersist.LAST_NAME_COL);
                int emailIndex = cursor.getColumnIndex(PersonPersist.EMAIL_COL);
                int idIndex = cursor.getColumnIndex(PersonPersist.ID_COL);
                this.age = cursor.getFloat(ageIndex);
                this.firstName = cursor.getString(firstNameIndex);
                this.lastName = cursor.getString(lastNameIndex);
                this.email = cursor.getString(emailIndex);
                this.id = cursor.getInt(idIndex);
                result = this;
            } catch (Exception e) {
                e.printStackTrace();
                this.age = -1;
                this.firstName = null;
                this.lastName = null;
                this.email = null;
                this.id = -1;
            }
        } else {
            this.age = -1;
            this.firstName = null;
            this.lastName = null;
            this.email = null;
            this.id = -1;
        }

        cursor.close();
        return result;
    }

    public static ArrayList<Person> readAll(int limit) throws Exception {
        if (limit < 0) {
            throw new Exception("limit can't be bellow 0");
        }

        SQLiteDatabase db = persistence.getReadableDatabase();

        String[] projection = {PersonPersist.ID_COL, PersonPersist.FIRST_NAME_COL, PersonPersist.LAST_NAME_COL, PersonPersist.EMAIL_COL, PersonPersist.AGE_COL};

        Cursor cursor = db.query(PersonPersist.TABLE_NAME, projection, null, null, null, null, null, limit == 0 ? null : String.valueOf(limit));

        return getFromCursor(cursor);
    }

    public static ArrayList<Person> readAll() throws Exception {
        return readAll(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        return id == person.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @NotNull
    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + ", age=" + age + '}';
    }

    public static void setContext(Context context) {
        persistence = new PersonPersist(context);
    }

    public static ArrayList<Person> filter(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, int limit) throws Exception {
        if (limit < 0) {
            throw new Exception("limit can't be bellow 0");
        }

        SQLiteDatabase db = persistence.getReadableDatabase();

        Cursor cursor = db.query(PersonPersist.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit == 0 ? null : String.valueOf(limit));

        return getFromCursor(cursor);
    }

    public static ArrayList<Person> search(String input) {
        ArrayList<Person> people = null;

        String[] splitInput = input.split(" ");
        System.out.println(Arrays.toString(splitInput));

        StringBuilder selection = new StringBuilder();

        for(String s:splitInput){
            try{
                Float.parseFloat(s);
                selection.append(String.format("%s = %s or ",PersonPersist.AGE_COL,s));
            }
            catch(Exception ignored){
            }
            selection.append(String.format("%s like '%%%s%%' or %s like '%%%s%%' or %s like '%%%s%%' or "
                    ,PersonPersist.FIRST_NAME_COL,s
                    ,PersonPersist.LAST_NAME_COL,s
                    ,PersonPersist.EMAIL_COL,s));
        }

        String selectionString = (String) selection.subSequence(0, selection.length() - 3);


        try {
            people = filter(null, selectionString, null, null, null, null, 0);
        } catch (Exception ignored) {
        }

        return people;
    }

    @NotNull
    private static ArrayList<Person> getFromCursor(Cursor cursor) {
        ArrayList<Person> result = new ArrayList<>();

        int ageIndex = cursor.getColumnIndex(PersonPersist.AGE_COL);
        int firstNameIndex = cursor.getColumnIndex(PersonPersist.FIRST_NAME_COL);
        int lastNameIndex = cursor.getColumnIndex(PersonPersist.LAST_NAME_COL);
        int emailIndex = cursor.getColumnIndex(PersonPersist.EMAIL_COL);
        int idIndex = cursor.getColumnIndex(PersonPersist.ID_COL);

        while (cursor.moveToNext()) {
            try {
                Person tempPerson = new Person();
                tempPerson.age = cursor.getFloat(ageIndex);
                tempPerson.firstName = cursor.getString(firstNameIndex);
                tempPerson.lastName = cursor.getString(lastNameIndex);
                tempPerson.email = cursor.getString(emailIndex);
                tempPerson.id = cursor.getInt(idIndex);
                result.add(tempPerson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();

        return result;
    }
}
