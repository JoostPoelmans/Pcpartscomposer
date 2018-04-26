package com.example.joost.pcpartscomposer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by joost on 25/04/2018.
 */

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(checkInternet(context) && MainActivity.isLoadStatus()){
            MainActivity.makeMockSearchQuery();
            Toast.makeText(context, "Network Available Refreshing database",Toast.LENGTH_LONG).show();
            MainActivity.setLoadStatus(false);
        }
        if(!checkInternet(context)){
            Toast.makeText(context, "Network Not Available",Toast.LENGTH_LONG).show();
            MainActivity.setLoadStatus(true);
        }

    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) return true;else return false;
    }
}
