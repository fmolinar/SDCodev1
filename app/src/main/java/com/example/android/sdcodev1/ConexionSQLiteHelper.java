package com.example.android.sdcodev1;




import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.android.sdcodev1.Utilities.utils;

// This class will serve as a connection with the database SQLite by extending its class with
// SQLiteOpenHelper, make sure to create an instance in main to create the connection

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    // String that runs the SQL script to create the database
    // its final because we dont want to mess up the table
    // the name of the TABLE is ------------- keys -----------------


    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the table CREATE_TABLE_USER with all of its entries
        db.execSQL(utils.CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // if the application is reinstalled, check if there exits a newer version of the table keys
        //db.execSQL("ALTER TABLE keys ADD COLUMN TEXT DEFAULT 0");
        db.execSQL("DROP TABLE IF EXISTS keys");
        onCreate(db);
    }
}
