package com.example.redditwallpapermanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
public class HelpingTasks {
    private static final HelpingTasks ourInstance = new HelpingTasks();

    public static HelpingTasks getInstance() {
        return ourInstance;
    }

    private HelpingTasks() {
    }


    public boolean isNetworkAvailable(Context cntxt) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) cntxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
