package com.example.joost.pcpartscomposer;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.joost.pcpartscomposer.Data.DataUtil;
import com.example.joost.pcpartscomposer.Data.PartDataDbHelper;
import com.example.joost.pcpartscomposer.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class SplashScreen extends AppCompatActivity {

    private Toast mToast;
    getDataTask dataTask;
    private SQLiteDatabase mDb;
    RingProgressBar ringProgressBar;

    int progress = 0;

    private static final String TAG_RESPONSE = "ResponseFromHttpUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        PartDataDbHelper dbHelper = new PartDataDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        ringProgressBar = (RingProgressBar) findViewById(R.id.progress_bar);


        final int delay = 2000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, delay);

        if(isNetworkAvailable()){
            makeMockSearchQueryWhileLoading();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0 ; i<100;i++){
                    try {
                        Thread.sleep(delay/100);
                        ringProgressBar.setProgress(progress);
                        progress++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void makeMockSearchQueryWhileLoading() {
        String partsQuery = getString(R.string.url);
        Uri uri = Uri.parse(partsQuery);
        try {
            URL mUrl = new URL(uri.toString());
            dataTask = new getDataTask();
            dataTask.execute(mUrl);
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

                DataUtil.saveToDataBase(mDb, partData);
            } else {
                if (mToast != null) {
                    mToast.cancel();
                }
                Context context = SplashScreen.this;
                mToast = Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG);
                mToast.show(); //mogelijk met met controle of er ook database is annders dan errormessage
            }
        }
    }
}
