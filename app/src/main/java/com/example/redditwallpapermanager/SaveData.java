package com.example.redditwallpapermanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class SaveData {

    private ArrayList<Bitmap> favourites = new ArrayList<>();

    private static final SaveData Instance = new SaveData();

    public static SaveData getInstance() {
        return Instance;
    }

    private SaveData(){

    }


    public String saveToInternalStorage(String link, ContextWrapper cw,Context cntxt){
        // path to /data/data/yourapp/app_data/imageDir
        Bitmap bitmapImage = null;
        try {
            bitmapImage = Picasso.get().load(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File directory = cw.getDir("imageDir", MODE_PRIVATE);
        // Create imageDir
        Integer imageNum;
        imageNum = retrieveImageCount(cntxt);
        File mypath=new File(directory,"image" + imageNum + ".jpg");
        imageNum++;
        saveImageCount(imageNum,cntxt);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public ArrayList<Bitmap> loadImageFromStorage(String path,Context cntxt){
        Integer i = retrieveImageCount(cntxt) - 1; //becouse current index is one above the actual index
        while (i != 0){
            try {
                File f = new File(path, "image" + i + ".jpg");
                Bitmap btmp = BitmapFactory.decodeStream(new FileInputStream(f));
                favourites.add(btmp);
                i--;
            }
            catch (FileNotFoundException e) {
                System.out.println("Something is fucked");
                System.out.println(e);
                break;
            }
        }
        return favourites;
    }

    public String saveFile(String link,  Context context){
        // path to /data/data/yourapp/app_data/imageDir
        Bitmap bitmapImage = null;
        try {
            bitmapImage = Picasso.get().load(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmapImage, "current wallpaper", "nice");
//        cntxt.getContentResolver().delete(Uri.parse(path),null,null);
        return path;
    }



    public void saveImageCount(Integer num,Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("imgCount",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("imgNum",num);
        editor.apply();
    }
    public void deleteSharedPref(Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("favLinks",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public Integer retrieveImageCount(Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("imgCount",MODE_PRIVATE);
        return sharedPreferences.getInt("imgNum",1);
    }

    public void saveFavLinks(Context context,ArrayList linksList){
        SharedPreferences sharedPreferences = context.getSharedPreferences("favLinks",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("links",compressLinks(linksList));
        editor.apply();
    }
    public ArrayList<String> getFavLinks(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("favLinks",MODE_PRIVATE);
        return new ArrayList<>(Arrays.asList(sharedPreferences.getString("links","empty").split(",")));
    }

    public String compressLinks(ArrayList<String> linkList){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linkList.size(); i++) {
            sb.append(linkList.get(i)).append(",");
        }
        return sb.toString();
    }



}
