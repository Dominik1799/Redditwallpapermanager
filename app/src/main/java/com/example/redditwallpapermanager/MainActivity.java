package com.example.redditwallpapermanager;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> urls;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private String subreddit = "https://www.reddit.com/r/verticalwallpapers/.rss";
    private ProgressBar progressBar;
    private Button changeWallpaper;
    private Button anotherOne;
    private Button random;
    private ImageView imgview;
    private CheckBox VerticalImages;
    private EditText sub;
    private Context cntxt = this;
    private ContextWrapper cw;
    private Integer CurrentIndex = 0;
    private Integer imageNum = 0;
    private Bitmap currentWallpaper;
    private String favPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        imgview = findViewById(R.id.imgview);
        changeWallpaper = findViewById(R.id.changeWallpaper);
        anotherOne = findViewById(R.id.anotherOne);
        random = findViewById(R.id.random);
        sub = findViewById(R.id.subreddit);
        VerticalImages = findViewById(R.id.VerticalImages);
        cw = new ContextWrapper(getApplicationContext());
        favPath = cw.getDir("imageDir",MODE_PRIVATE).getAbsolutePath();
        //uncomment this next line to delete current favourite images
//        SaveData.getInstance().deleteSharedPref(this);
    }


    public void onSetRandomWallpaperClick(final View view){
        final int randomNum = new Random().nextInt(images.size());
        imgview.setImageBitmap(images.get(randomNum));
        currentWallpaper = images.get(randomNum);
        Thread alpha = new Thread(new Runnable() {
            @Override
            public void run() {
                WallpaperManager mngr = WallpaperManager.getInstance(cntxt);
                try {
                    showLoadingWallpaper("Setting wallpaper...",true);
                    mngr.setBitmap(images.get(randomNum));
                    mngr.setWallpaperOffsets(view.getWindowToken(),0.5f,0.5f);
                    showToast("Wallpaper set!");
                    showLoadingWallpaper("Downloading data...",false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alpha.start();
    }

    public void onSetWallpaperClick(View view){
        Thread alpha = new Thread(new Runnable() {
            @Override
            public void run() {
                WallpaperManager mngr = WallpaperManager.getInstance(cntxt);
                try {
                    showLoadingWallpaper("Setting wallpaper...",true);
                    mngr.setBitmap(images.get(CurrentIndex));
                    showToast("Wallpaper set!");
                    showLoadingWallpaper("Downloading data...",false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alpha.start();
    }

    public void onImageViewClick(View view){
        CurrentIndex += 1;
        if (CurrentIndex >= images.size()) CurrentIndex = 0;
        imgview.setImageBitmap(images.get(CurrentIndex));
        currentWallpaper = images.get(CurrentIndex);
    }

    @SuppressLint("ResourceType")
    public void onLoadClick(View view){
        if (!images.isEmpty()) images.clear();
        if (!ParseContent.getInstance().isNetworkAvailable(this)){
            showToast("No connection");
            return;
        }
        if (sub.getText().toString().equalsIgnoreCase("fav")){
            images = SaveData.getInstance().loadImageFromStorage(favPath,this);
            if (images.isEmpty()) showToast("Something went wrong :(");
            else{
                imgview.setImageBitmap(images.get(0));
                changeWallpaper.setEnabled(true);
                random.setEnabled(true);
                showToast("images loaded:" + images.size());
                sub.onEditorAction(EditorInfo.IME_ACTION_DONE);
                return;
            }
        }

        subreddit = "https://www.reddit.com/r/" + sub.getText() + "/.rss";
        if (sub.getText().toString().matches("")) subreddit = "https://www.reddit.com/r/verticalwallpapers/.rss";
        new MyTask(cntxt).execute();
        sub.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }



    public void showToast(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(cntxt, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLoadingWallpaper(final String text,final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show){
                    progressBar.setVisibility(View.VISIBLE);
//                    downloadingIndication.setVisibility(View.VISIBLE);
//                    downloadingIndication.setText(text);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
//                    downloadingIndication.setVisibility(View.INVISIBLE);
//                    downloadingIndication.setText(text);
                }
            }
        });
    }



    public void onSaveToInternalStorageClick(View view){

        Thread alpha = new Thread(new Runnable() {
            @Override
            public void run() {
                SaveData.getInstance().saveToInternalStorage(currentWallpaper,cw,cntxt);
                showToast("Saved");
            }
        });
        alpha.start();
    }








    class MyTask extends AsyncTask<Integer, Integer, String> {

        private Context mContext;

        public MyTask (Context context){
            mContext = context;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                urls = ParseContent.getInstance().createLinks(subreddit);
                images = ParseContent.getInstance().getBitmaps(mContext,urls,VerticalImages.isChecked());

            } catch (Exception e) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showLoadingWallpaper("Downloading data...",true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            showLoadingWallpaper("Downloading data...",false);
            if ((images.isEmpty())){
                showToast("Invalid subreddit");
                return;
            }
            changeWallpaper.setEnabled(true);
            random.setEnabled(true);
            anotherOne.setEnabled(true);
            showToast("Images loaded successfully");
            imgview.setImageBitmap(images.get(0));
            currentWallpaper = images.get(0);
        }
    }


}
