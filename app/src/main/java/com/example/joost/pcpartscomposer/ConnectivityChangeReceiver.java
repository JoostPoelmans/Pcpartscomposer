package com.example.joost.pcpartscomposer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
        if(checkInternet(context)){
            Toast.makeText(context, "Network Available Refreshing database",Toast.LENGTH_LONG).show();
            String partsQuery = "http://www.mocky.io/v2/5ae0c6d33200002a00510d2c";
            Uri uri = Uri.parse(partsQuery);

            URL mUrl = null;
            try {
                mUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new MainActivity.getDataTask().execute(mUrl);
            //new MainActivity.getDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
        }
        if(!checkInternet(context)){
            Toast.makeText(context, "Network Not Available",Toast.LENGTH_LONG).show();
        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) return true;else return false;
    }
}
