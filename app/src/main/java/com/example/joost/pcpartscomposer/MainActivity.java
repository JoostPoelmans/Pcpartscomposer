package com.example.joost.pcpartscomposer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Network;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joost.pcpartscomposer.Data.PartDataContract;
import com.example.joost.pcpartscomposer.Data.PartDataDbHelper;
import com.example.joost.pcpartscomposer.Data.TestUtil;
import com.example.joost.pcpartscomposer.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements PartsListAdapter.PartsListAdapterOnClickHandler {
    private RecyclerView mRecyclerView;

    private PartsListAdapter mPartsListAdapter;

    private TextView mErrorMessageDisplay;
    private Toast mToast;

    private SQLiteDatabase mDb;

    //voorbeeld van logging
    private static final String TAG = "MainActivity";
    //optioneel private ProgressBar mLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //logging
        Log.v(TAG, "mainActivity created");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_parts);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        PartDataDbHelper dbHelper = new PartDataDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDb);

        Cursor cursor = getAllData();

        cursor.moveToFirst();

        String test = cursor.getString(cursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_NAME));


        mPartsListAdapter = new PartsListAdapter(this, cursor);

        mRecyclerView.setAdapter(mPartsListAdapter);

        showPartsData();
        //makeMockSearchQuery();
    }

    private Cursor getAllData() {
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

    private void showPartsData() {
       mErrorMessageDisplay.setVisibility(View.INVISIBLE);
       mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListClick(String partData, String dataOfPart) {
//        if(mToast != null){
//            mToast.cancel();
//        }
//        Context context = this;
//        mToast = Toast.makeText(context, partData, Toast.LENGTH_SHORT);
//        mToast.show();
        Context context = MainActivity.this;
        Intent intent = new Intent(context , DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,dataOfPart);
        startActivity(intent);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void makeMockSearchQuery() {
        String partsQuery = "http://www.mocky.io/v2/5aad5b302f00005600204971";
        Uri uri = Uri.parse(partsQuery);
        try {
            URL mUrl = new URL(uri.toString());
            new getDataTask().execute(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public class getDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {


            URL searchURL = urls[0];
            String searchResults = null;


            String jsonResponse = null;
            try {
                jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String partData) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (partData != null) {
                showPartsData();
                //mTest.setText(partData);
                mPartsListAdapter.setPartsData(partData);
            } else {
                showErrorMessage();
            }
        }

    }






}
