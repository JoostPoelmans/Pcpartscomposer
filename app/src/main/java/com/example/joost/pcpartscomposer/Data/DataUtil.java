package com.example.joost.pcpartscomposer.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.joost.pcpartscomposer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    private static SQLiteDatabase mDb;
    private static PartDataDbHelper dbHelper;


    public static void saveToDataBase(SQLiteDatabase db, String dataResponse){
        if(db == null){
            return;
        }
        JSONArray partArray;


        List<ContentValues> list = new ArrayList<ContentValues>();
        ContentValues cv;


        try {
            String result = dataResponse.replace("\r", "").replace("\n)", "");

             partArray = new JSONArray(result);


            for(int i = 0; i<partArray.length();i++ ){

                JSONObject part = partArray.getJSONObject(i);

                //create a list of data
                cv = new ContentValues();
                cv.put(PartDataContract.PartDataEntry.COLUMN_NAME, part.getString("name"));
                cv.put(PartDataContract.PartDataEntry.COLUMN_PRICE, part.getString("price"));
                if (part.has("details")) {
                    cv.put(PartDataContract.PartDataEntry.COLUMN_DETAILS, part.getString("details"));
                }


                list.add(cv);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


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

            Log.v("testing", db.toString());
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

    public static Cursor getCursorFromDataBase(Context context){
        dbHelper = new PartDataDbHelper(context);
        mDb = dbHelper.getReadableDatabase();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String sortBy = prefs.getString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_default));
        Cursor value;
        switch(sortBy){
            case "0":
                value = sortAlphabetical();
                break;
            case "1":
                value = sortByHighestPrice();
                break;
            case "2":
                value = sortByLowestPrice();
                break;
            default:
                value = getAllData();
                break;
        }
        return value;


    }

    /**sorts alphabetical
     *
     * @return cursor of data
     */
    public static Cursor sortAlphabetical(){
        return mDb.query(
                PartDataContract.PartDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PartDataContract.PartDataEntry.COLUMN_NAME
        );

    }
    public static Cursor sortByHighestPrice(){
        return mDb.query(
                PartDataContract.PartDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PartDataContract.PartDataEntry.COLUMN_PRICE+" DESC"
        );

    }
    public static Cursor sortByLowestPrice(){
        return mDb.query(
                PartDataContract.PartDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                PartDataContract.PartDataEntry.COLUMN_PRICE+" ASC"
        );

    }
    public static Cursor getAllData() {
        return mDb.query(
                PartDataContract.PartDataEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
                //PartDataContract.PartDataEntry.COLUMN_NAME
        );

    }
}