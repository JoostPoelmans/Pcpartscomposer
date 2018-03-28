package com.example.joost.pcpartscomposer.Data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, "CPU");
        cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, 200);
        cv.put(PartDataContract.PartDataEntry.COLUMN_DETAILS, "test123");
        list.add(cv);

        cv = new ContentValues();
        cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, "GPU");
        cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, 300);
        list.add(cv);

        cv = new ContentValues();
        cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, "Harddrive");
        cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, 99);
        list.add(cv);

        cv = new ContentValues();
        cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, "CPU");
        cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, 200);
        list.add(cv);

        cv = new ContentValues();
        cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, "GPU");
        cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, 400);
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (PartDataContract.PartDataEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(PartDataContract.PartDataEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}