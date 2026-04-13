package com.example.sosapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SosApp.db";
    
    // Contacts Table
    public static final String TABLE_CONTACTS = "contacts_table";
    public static final String COL_CONTACT_ID = "ID";
    public static final String COL_CONTACT_NAME = "NAME";
    public static final String COL_CONTACT_PHONE = "PHONE";
    public static final String COL_CONTACT_RELATIONSHIP = "RELATIONSHIP";

    // Incidents Table
    public static final String TABLE_INCIDENTS = "incidents_table";
    public static final String COL_INCIDENT_ID = "ID";
    public static final String COL_INCIDENT_NAME = "NAME";
    public static final String COL_INCIDENT_MOBILE = "MOBILE";
    public static final String COL_INCIDENT_DESC = "DESCRIPTION";
    public static final String COL_INCIDENT_LOCATION = "LOCATION";
    public static final String COL_INCIDENT_TIME = "TIME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PHONE TEXT, RELATIONSHIP TEXT)");
        db.execSQL("create table " + TABLE_INCIDENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, MOBILE TEXT, DESCRIPTION TEXT, LOCATION TEXT, TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
        onCreate(db);
    }

    // Contact Methods
    public boolean insertData(String name, String phone, String relationship) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTACT_NAME, name);
        contentValues.put(COL_CONTACT_PHONE, phone);
        contentValues.put(COL_CONTACT_RELATIONSHIP, relationship);
        long result = db.insert(TABLE_CONTACTS, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_CONTACTS, null);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS);
    }

    // Incident Methods
    public boolean insertIncident(String name, String mobile, String desc, String location, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_INCIDENT_NAME, name);
        contentValues.put(COL_INCIDENT_MOBILE, mobile);
        contentValues.put(COL_INCIDENT_DESC, desc);
        contentValues.put(COL_INCIDENT_LOCATION, location);
        contentValues.put(COL_INCIDENT_TIME, time);
        long result = db.insert(TABLE_INCIDENTS, null, contentValues);
        return result != -1;
    }

    public Cursor getAllIncidents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_INCIDENTS + " ORDER BY ID DESC", null);
    }
}