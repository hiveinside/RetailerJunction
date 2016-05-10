package com.example.mridul.RetailerJunction.db;

/**
 * Created by Mridul on 4/17/2016.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // make it singleton
    private static DatabaseHelper dbInstance;

    private static final String DATABASE_NAME = "installRecords.db";
    private static final int DATABASE_VERSION = 1; // change when schema is changed;
    private static final String TABLE_INSTALL_RECORDS = "install_records_table";
    // Database creation sql statement

    private static final String CREATE_INSTALL_RECORDS_TABLE = "CREATE TABLE " + TABLE_INSTALL_RECORDS +
            "(" +
            "id" + " INTEGER PRIMARY KEY," +
            "json_data" + " TEXT NOT NULL," +
            "timestamp" + " INTEGER," +
            "isUploaded" + " INTEGER" +
            ")";


    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    /*
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_INSTALL_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTALL_RECORDS);
        onCreate(db);
    }


    /*
     * CRUD
     */

    // Insert a post into the database
    public void add(String jsonObj) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        String TAG = "addEntry";

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put("json_data", jsonObj);
            values.put("timestamp", System.currentTimeMillis());
            values.put("isUploaded", 0); // upload to server func will update it.

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_INSTALL_RECORDS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public void delete(String jsonObj) {
    }

    public void deleteAll(String jsonObj) {
    }

    public void update(String jsonObj) {
    }
}