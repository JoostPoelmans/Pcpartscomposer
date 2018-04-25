package com.example.joost.pcpartscomposer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joost.pcpartscomposer.Data.DataUtil;
import com.example.joost.pcpartscomposer.Data.PartDataContract;
import com.example.joost.pcpartscomposer.Data.PartDataDbHelper;
import com.example.joost.pcpartscomposer.Data.TestUtil;
import com.example.joost.pcpartscomposer.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements PartsListAdapter.PartsListAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView mRecyclerView;

    private PartsListAdapter mPartsListAdapter;

    private TextView mErrorMessageDisplay;
    private Toast mToast;

    private SQLiteDatabase mDb;
    private String jsonData;
    private static final String TAG_ON_LIST_CLICK = "OnListClick";

    //voorbeeld van logging
    private static final String TAG = "MainActivity";
    private static final String TAG_RESPONSE = "ResponseFromHttpUrl";
    //optioneel private ProgressBar mLoadingIndicator;

    /**the method that is called when mainActivity needs to be created
     *
     * @param savedInstanceState savedInstance
     */
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

        //TestUtil.insertFakeData(mDb);

        setupSharedPreferences();

        Cursor cursor = DataUtil.getCursorFromDataBase(this);

        mPartsListAdapter = new PartsListAdapter(this, cursor);

        mRecyclerView.setAdapter(mPartsListAdapter);

        showPartsData();

        if(isNetworkAvailable()){
          makeMockSearchQuery();
        }
        else{
            if(mToast != null){
                mToast.cancel();
            }
            Context context = this;
            mToast = Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mPartsListAdapter.swapCursor(DataUtil.getCursorFromDataBase(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        switch(menuItemThatWasSelected){
            case R.id.action_Config:
                Context context = MainActivity.this;
                Intent intent = new Intent(context , ConfigActivity.class);
                startActivity(intent);
                break;
            // switch om mogelijk later een extra item toe te voegen die nieuwe onderdelen toevoegt
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getAllData() {
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

    private void showPartsData() {
       mErrorMessageDisplay.setVisibility(View.INVISIBLE);
       mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListClick(int adapterPosition, Cursor mCursor) {
//        if(mToast != null){
//            mToast.cancel();
//        }
//        Context context = this;
//        mToast = Toast.makeText(context, partData, Toast.LENGTH_SHORT);
//        mToast.show();
        if(!mCursor.moveToPosition(adapterPosition)) {
            Log.i(TAG_ON_LIST_CLICK, "no data or out of bounds of the cursor");
            return;
        }



        Context context = MainActivity.this;
        Intent intent = new Intent(context , DetailActivity.class);

        String name = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_NAME));
        int price = mCursor.getInt(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_PRICE));
        intent.putExtra(PartDataContract.PartDataEntry.COLUMN_NAME,name);
        intent.putExtra(PartDataContract.PartDataEntry.COLUMN_PRICE,price);
        String details = mCursor.getString(mCursor.getColumnIndex(PartDataContract.PartDataEntry.COLUMN_DETAILS));
        intent.putExtra(PartDataContract.PartDataEntry.COLUMN_DETAILS,details);
        startActivity(intent);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void makeMockSearchQuery() {
        String partsQuery = "http://www.mocky.io/v2/5ad724002e00006d00c93dd5";
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
                Log.i(TAG_RESPONSE, "Exception at Http Response");

            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String partData) {
            if (partData != null) {
                showPartsData();
                //mTest.setText(partData);
                jsonData = partData;
                DataUtil.saveToDataBase(mDb, jsonData);
                mPartsListAdapter.swapCursor(DataUtil.getCursorFromDataBase(MainActivity.this));
            } else {
                showErrorMessage(); //mogelijk met met controle of er ook database is annders dan errormessage
            }
        }

    }






}
