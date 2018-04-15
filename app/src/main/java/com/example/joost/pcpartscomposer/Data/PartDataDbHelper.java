package com.example.joost.pcpartscomposer.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joost on 25/03/2018.
 */

public class PartDataDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PartData.db";

    private static final int DATBASE_VERSION = 1;

    private static final String TAG_DATABASE = "Database";

    public PartDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATBASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

        final String SQL_CREATE_PARTDATA_TABLE = "CREATE TABLE " + PartDataContract.PartDataEntry.TABLE_NAME + " (" +
                PartDataContract.PartDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PartDataContract.PartDataEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PartDataContract.PartDataEntry.COLUMN_PRICE + " INTEGER NOT NULL, " +
                PartDataContract.PartDataEntry.COLUMN_DETAILS + " TEXT" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_PARTDATA_TABLE);
        Log.i(TAG_DATABASE, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartDataContract.PartDataEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
