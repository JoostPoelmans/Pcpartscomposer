package com.example.joost.pcpartscomposer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.example.joost.pcpartscomposer.Data.DataUtil;
import com.example.joost.pcpartscomposer.Data.PartDataContract;
import com.example.joost.pcpartscomposer.Data.PartDataDbHelper;
import com.example.joost.pcpartscomposer.Data.TestUtil;
import com.example.joost.pcpartscomposer.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static com.example.joost.pcpartscomposer.Data.PartDataContract.PartDataEntry.TABLE_NAME;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements PartsListAdapter.PartsListAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {
    private static RecyclerView mRecyclerView;

    private static PartsListAdapter mPartsListAdapter;

    private static TextView mErrorMessageDisplay;
    private Toast mToast;

    private static SQLiteDatabase mDb;

    private static String search;
    private static final String TAG_ON_LIST_CLICK = "OnListClick";

    //voorbeeld van logging
    private static final String TAG = "MainActivity";
    private static final String TAG_RESPONSE = "ResponseFromHttpUrl";
    private static BroadcastReceiver receiver;



    private static Context mContext;

    private static boolean loadStatus = false;

    //optioneel private ProgressBar mLoadingIndicator;

    /**the method that is called when mainActivity needs to be created
     *
     * @param savedInstanceState savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        //logging
        Log.v(TAG, "mainActivity created");

        setupSharedPreferences();

        search = null;
        if(savedInstanceState != null && savedInstanceState.containsKey(this.getString(R.string.search_key))){
            search = savedInstanceState.getString(this.getString(R.string.search_key));
        }

        PartDataDbHelper dbHelper = new PartDataDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_parts);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        //TestUtil.insertFakeData(mDb);


        Cursor cursor = DataUtil.getCursorFromDataBase(this);
        Log.v("cursor", DatabaseUtils.dumpCursorToString(cursor));
        mPartsListAdapter = new PartsListAdapter(this, cursor);

        mRecyclerView.setAdapter(mPartsListAdapter);

        //intentfilter for the reciever
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        //register the reciever for connectionupdates
        receiver = new ConnectivityChangeReceiver();
        registerReceiver(receiver, filter);

        showPartsData();

        String countQuery = "SELECT  * FROM " + PartDataContract.PartDataEntry.TABLE_NAME;
        Cursor testcursor = mDb.rawQuery(countQuery, null);
        int count = cursor.getCount();

        Log.v("data base count",String.valueOf(count));
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
        unregisterReceiver(receiver);
    }
//    @Override
//    public void onPause() {
//        super.onPause();  // Always call the superclass method first
//        unregisterReceiver(receiver);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();  // Always call the superclass method first
//        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                mPartsListAdapter.swapCursor(DataUtil.getCursorFromDataBase(mContext));
                //clear the focus so the keyboard disappears
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setIconified(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                search = null;
                mPartsListAdapter.swapCursor(DataUtil.getCursorFromDataBase(mContext));
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(this.getString(R.string.search_key), search);
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


    public static boolean isLoadStatus() {
        return loadStatus;
    }

    public static void setLoadStatus(boolean loadStatus) {
        MainActivity.loadStatus = loadStatus;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static String getSearch() {
        return search;
    }

    private static void showPartsData() {
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

    private static void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public static void makeMockSearchQuery() {
        String partsQuery = getmContext().getString(R.string.url);
        Uri uri = Uri.parse(partsQuery);
        try {
            URL mUrl = new URL(uri.toString());
            new getDataTask().execute(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public static class getDataTask extends AsyncTask<URL, Void, String> {

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
                DataUtil.saveToDataBase(mDb, partData);
                mPartsListAdapter.swapCursor(DataUtil.getCursorFromDataBase(mContext));
            } else {
                showErrorMessage(); //mogelijk met met controle of er ook database is annders dan errormessage
            }
        }

    }






}
