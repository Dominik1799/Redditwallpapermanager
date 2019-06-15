package com.example.redditwallpapermanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class SaveData {

    private ArrayList<Bitmap> favourites = new ArrayList<>();

    private static final SaveData Instance = new SaveData();

    public static SaveData getInstance() {
        return Instance;
    }

    private SaveData(){

    }


    public String saveToInternalStorage(Bitmap bitmapImage, ContextWrapper cw,Context cntxt){
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
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


    public void saveImageCount(Integer num,Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("imgCount",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("imgNum",num);
        editor.apply();
    }
    public void deleteSharedPref(Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("imgCount",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public Integer retrieveImageCount(Context cntxt){
        SharedPreferences sharedPreferences = cntxt.getSharedPreferences("imgCount",MODE_PRIVATE);
        return sharedPreferences.getInt("imgNum",1);
    }


}
