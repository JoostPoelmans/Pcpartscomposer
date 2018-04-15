package com.example.joost.pcpartscomposer.Data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {



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
}