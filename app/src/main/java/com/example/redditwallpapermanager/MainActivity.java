package com.example.redditwallpapermanager;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

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
    HorizontalInfiniteCycleViewPager viewPager;
    private ArrayList<String> urls;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> imagesShown = new ArrayList<Bitmap>();
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
    private Boolean isFavourites = false;
    private Bitmap currentWallpaper;
    private String favPath;
    private View view;
    private Activity activity = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar =findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar);
        imgview = findViewById(R.id.imageItem);
        changeWallpaper = findViewById(R.id.changeWallpaper);
        anotherOne = findViewById(R.id.anotherOne);
        random = findViewById(R.id.random);
        sub = findViewById(R.id.subreddit);
        VerticalImages = findViewById(R.id.VerticalImages);
        cw = new ContextWrapper(getApplicationContext());
        favPath = cw.getDir("imageDir",MODE_PRIVATE).getAbsolutePath();
        viewPager = findViewById(R.id.view_pager);
        MyAdapter myAdapter = new MyAdapter(this,images);
        viewPager.setAdapter(myAdapter);
//        imgview.setOnTouchListener(new OnSwipeTouchListener(this){
//            public void onSwipeRight(){
//                if (images.isEmpty()) return;
//                CurrentIndex -= 1;
//                if (CurrentIndex < 0) CurrentIndex = images.size()-1;
//                imgview.setImageBitmap(images.get(CurrentIndex));
//                currentWallpaper = images.get(CurrentIndex);
//            }
//            public void onSwipeLeft(){
//                if (images.isEmpty()) return;
//                CurrentIndex += 1;
//                if (CurrentIndex >= images.size()) CurrentIndex = 0;
//                imgview.setImageBitmap(images.get(CurrentIndex));
//                currentWallpaper = images.get(CurrentIndex);
//            }
//
//        });

//        registerForContextMenu(imgview);
//        uncomment this next line to delete current favourite images
//        SaveData.getInstance().deleteSharedPref(this);
    }


//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.save:
//                onSaveToInternalStorageClick(view);
//                break;
//            case R.id.set:
//                onSetWallpaperClick(view);
//                break;
//
//
//        }
//        return super.onContextItemSelected(item);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getMenuInflater().inflate(R.menu.context,menu);
//    }




    public void onSetRandomWallpaperClick(final View view){
        final int randomNum = new Random().nextInt(images.size());
//        imgview.setImageBitmap(images.get(randomNum));
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
                    mngr.setBitmap(images.get(viewPager.getRealItem()));
                    showToast("Wallpaper set!");
                    showLoadingWallpaper("Downloading data...",false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alpha.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.fav:
                ArrayList temp;
                temp = images;
                images = SaveData.getInstance().loadImageFromStorage(favPath,this);
                if (images.isEmpty()) {
                    showToast("No favourite images.");
                    images = temp;
                }
                else{
                    isFavourites = true;
                    imgview.setImageBitmap(images.get(0));
                    changeWallpaper.setEnabled(true);
                    random.setEnabled(true);
                    showToast("images loaded:" + images.size());
                }
                break;
            case  R.id.delete_fav:
                SaveData.getInstance().deleteSharedPref(this);
                if (isFavourites){
                    imgview.setImageResource(android.R.color.transparent);
                    images.clear();
                }
                showToast("Favourites deleted");

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onImageViewClick(View view){
        if (images.isEmpty()) return;
        currentWallpaper = images.get(viewPager.getRealItem());
        System.out.println("ajjjjjj");
    }

    @SuppressLint("ResourceType")
    public void onLoadClick(View view){
        if (!images.isEmpty()) images.clear();
        isFavourites = false;
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
            for (Bitmap img : images){
                imagesShown.add(Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.8), (int)(img.getHeight()*0.8), true));
            }
            changeWallpaper.setEnabled(true);
            random.setEnabled(true);
            anotherOne.setEnabled(true);
            showToast("Images loaded successfully");
            MyAdapter myAdapter = new MyAdapter(mContext,imagesShown);
            viewPager.setAdapter(myAdapter);
            currentWallpaper = images.get(0);
        }
    }


}
