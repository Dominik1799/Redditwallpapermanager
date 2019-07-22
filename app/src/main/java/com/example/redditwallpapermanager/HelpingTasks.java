package com.example.redditwallpapermanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

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

//    public void setAs(String link, Context context, String favPath) {
//       String path = SaveData.getInstance().saveFile(link,contextWrapper,context);
//       Uri uri = Uri.parse(path);
//       Intent i=new Intent(Intent.ACTION_ATTACH_DATA);
//       i.addCategory(Intent.CATEGORY_DEFAULT);
//       i.setDataAndType(uri, "image/*");
//       i.putExtra("mimeType", "image/*");
//       i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//       context.startActivity(Intent.createChooser(i, "Set as:"));
////       context.getContentResolver().delete(uri,null,null);
//    }


}
