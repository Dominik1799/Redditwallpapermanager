package com.example.redditwallpapermanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    HorizontalInfiniteCycleViewPager viewPager;
    private ArrayList<String> urls;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> imagesShown = new ArrayList<Bitmap>();
    private String subreddit = "https://www.reddit.com/r/verticalwallpapers/.rss";
    private ProgressBar progressBar;
    private ImageButton changeWallpaper;
    private Button anotherOne;
    private Button random;
    private ImageView imgview;
    private CheckBox VerticalImages;
    private EditText sub;
    private TextView guide;
    private Context cntxt = this;
    private ContextWrapper cw;
    private Boolean isFavourites = false;
    private Uri currentWallpaper;
    private String favPath;
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
        changeWallpaper.setClickable(false);
        changeWallpaper.setEnabled(false);
        anotherOne = findViewById(R.id.anotherOne);
        random = findViewById(R.id.random);
        sub = findViewById(R.id.subreddit);
        VerticalImages = findViewById(R.id.VerticalImages);
        cw = new ContextWrapper(getApplicationContext());
        favPath = cw.getDir("imageDir",MODE_PRIVATE).getAbsolutePath();
        viewPager = findViewById(R.id.view_pager);
        guide = findViewById(R.id.guide);
//        MyAdapter myAdapter = new MyAdapter(this,images);
//        viewPager.setAdapter(myAdapter);
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
        final int randomNum = new Random().nextInt(urls.size());
        viewPager.setCurrentItem(randomNum,false);
        changeWallpaper(urls.get(randomNum));
    }

    public void onSetWallpaperClick(View view){
//        Thread alpha = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                WallpaperManager mngr = WallpaperManager.getInstance(cntxt);
//                try {
//                    showLoadingWallpaper(true);
//                    mngr.setBitmap(images.get(viewPager.getRealItem()));
//                    showToast("Wallpaper set!");
//                    showLoadingWallpaper(false);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        alpha.start();
        changeWallpaper(urls.get(viewPager.getRealItem()));

    }

    public void changeWallpaper(final String imageURL){
        Thread alpha = new Thread(new Runnable() {
            @Override
            public void run() {
                showLoadingWallpaper(true);
                String path = SaveData.getInstance().saveFile(imageURL,cntxt);
                Uri uri = Uri.parse(path);
                currentWallpaper = uri;
                Intent i=new Intent(Intent.ACTION_ATTACH_DATA);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setDataAndType(uri, "image/*");
                i.putExtra("mimeType", "image/*");
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                showLoadingWallpaper(false);
                startActivityForResult(Intent.createChooser(i, "Set as:"),1);
            }
        });
        alpha.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.fav:
                loadFavourites(this);
//                showLoadingWallpaper(true);
//                ArrayList temp;
//                temp = images;
//                images = SaveData.getInstance().loadImageFromStorage(favPath,this);
//                if (images.isEmpty()) {
//                    showToast("No favourite images.");
//                    images = temp;
//                }
//                else{
//                    anotherOne.setEnabled(false);
//                    setNewImages(this,images);
//                    isFavourites = true;
//                    changeWallpaper.setClickable(true);
//                    random.setEnabled(true);
//                    showToast("images loaded:" + images.size());
//                }
//                showLoadingWallpaper(false);
                break;
            case  R.id.delete_fav:
                showLoadingWallpaper(true);
                SaveData.getInstance().deleteSharedPref(this);
                if (isFavourites){
                    images.clear();
                    guide.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                    changeWallpaper.setClickable(false);
                    changeWallpaper.setEnabled(false);
                    random.setEnabled(false);
                }
                showToast("Favourites deleted");
                showLoadingWallpaper(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadFavourites(final Context context){
        new Thread(new Runnable() {
            public void run() {
                showLoadingWallpaper(true);
                ArrayList temp;
                temp = images;
                images.clear();
                images = SaveData.getInstance().loadImageFromStorage(favPath,context);
                if (images.isEmpty()) {
                    showToast("No favourite images.");
                    images = temp;
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNewImages(context,images);
                            anotherOne.setEnabled(false);
                            changeWallpaper.setClickable(true);
                            random.setEnabled(true);
                        }
                    });
                    isFavourites = true;
                    showToast("images loaded:" + images.size());
                }
                showLoadingWallpaper(false);
            }
        }).start();
        setNewImages(this,images);
        guide.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onImageViewClick(View view){
//        if (images.isEmpty()) return;
//        currentWallpaper = images.get(viewPager.getRealItem());
//        System.out.println("ajjjjjj");
////        viewPager.setVisibility(View.INVISIBLE);
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
                changeWallpaper.setClickable(true);
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

    public void setNewImages(Context context,ArrayList<Bitmap> images){
        imagesShown.clear();
        for (Bitmap img : images){
            imagesShown.add(Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.6), (int)(img.getHeight()*0.6), true));
        }
        MyAdapter myAdapter = new MyAdapter(context,imagesShown,urls);
        viewPager.setAdapter(myAdapter);

    }



    public void showToast(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(cntxt, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLoadingWallpaper(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show){
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    public void onSaveToInternalStorageClick(View view){

        Thread alpha = new Thread(new Runnable() {
            @Override
            public void run() {
                showLoadingWallpaper(true);
                SaveData.getInstance().saveToInternalStorage(urls.get(viewPager.getRealItem()),cw,cntxt);
                showToast("Saved");
                showLoadingWallpaper(false);
            }
        });
        alpha.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            this.getContentResolver().delete(currentWallpaper,null,null);
            if (resultCode == RESULT_OK) {
                showToast("Succes!");
            }

        }

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
//                images = ParseContent.getInstance().getBitmaps(mContext,urls,VerticalImages.isChecked());

            } catch (Exception e) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showLoadingWallpaper(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            imagesShown.clear();
            showLoadingWallpaper(false);
//            if ((images.isEmpty())){
//                showToast("Invalid subreddit");
//                return;
//            }
            changeWallpaper.setClickable(true);
            changeWallpaper.setEnabled(true);
            random.setEnabled(true);
            anotherOne.setEnabled(true);
            showToast("Images loaded successfully");
            setNewImages(mContext,images);
//            currentWallpaper = images.get(0);
            guide.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }


}
