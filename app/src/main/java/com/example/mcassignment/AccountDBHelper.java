package com.example.mcassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AccountDBHelper extends SQLiteOpenHelper {

    private String usersTable = "users_table";

    public AccountDBHelper(@Nullable Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +
                usersTable + "(" +
                "username FLOAT PRIMARY KEY, " +
                "firstname FLOAT, " +
                "lastname FLOAT, " +
                "password FLOAT " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + usersTable);
    }

    public boolean createUser(String username, String firstname, String lastname, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("username", username);
        contentValues.put("firstname", firstname);
        contentValues.put("lastname", lastname);
        contentValues.put("password", password);

        long result = db.insert(usersTable, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public User getUser(String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select username, firstname, lastname, password from " + usersTable + " where username='" + username + "'", null);

        if (cursor.moveToFirst()) {
            User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            return user;
        } else {
            return null;
        }
    }
}

