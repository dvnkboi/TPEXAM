package com.example.tpexam.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonPersist extends SQLiteOpenHelper {
    private static final String DB_NAME = "TP_EXAM_DB";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "Person";
    public static final String ID_COL = "id";
    public static final String FIRST_NAME_COL = "first_name";
    public static final String LAST_NAME_COL = "last_name";
    public static final String AGE_COL = "age";
    public static final String EMAIL_COL = "email";
    public PersonPersist(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT,%s TEXT,%s FLOAT,%s TEXT)", TABLE_NAME, ID_COL, FIRST_NAME_COL, LAST_NAME_COL, AGE_COL, EMAIL_COL);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}