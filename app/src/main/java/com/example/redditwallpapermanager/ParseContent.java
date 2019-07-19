package com.example.redditwallpapermanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ParseContent {
    private ArrayList<String> localUrls;
    private static ParseContent ourInstance = new ParseContent();

    public static ParseContent getInstance() {
        return ourInstance;
    }

    private ParseContent() {
    }

    public ArrayList<String> createLinks(String url) throws IOException {
        ArrayList<String> list = new ArrayList();
        ArrayList<Bitmap> listImages = new ArrayList();
        try {
            Document doc = Jsoup.connect(url).parser(Parser.xmlParser()).get();
            //parts of the rss stream are "corrupted", when I parse it anything between content tags doesnt get recognized
            //az part of the DOM, and thats beacouse after Jsoup parsed the XML, tags inside content changed like this:
            // <table/> to &lt;table&rt;
            // so I had to convert it to string and replace substrings with correct tag parts.
            String corrupted_xml = String.valueOf(doc.select("entry").select("content"));
            String good_xml = corrupted_xml.replace("&lt;","<").replace("&gt;",">");
            Document doc2 = Jsoup.parse(good_xml);
            // now I extract pngs and jpgs
            Elements pngs = doc2.select("a[href$=.png]");
            Elements jpgs = doc2.select("a[href$=.jpg]");
            //add links into arraylist;
            for (Element e : jpgs){
                list.add(e.attr("abs:href"));
            }

            for (Element e : pngs){
                list.add(e.attr("abs:href"));
            }
            this.localUrls = list;
            return list;
        }catch (Exception e){
            System.out.println(e);
            return list;
        }
    }

    public ArrayList getBitmaps(Context context,ArrayList<String> urls,boolean onlyVertical){
        int i = 0;
        Bitmap btmp;
        ArrayList<Bitmap> listImages = new ArrayList();
        for (String url : urls){
            i++;
            try {
                btmp = Picasso.get().load(url).get();
                if (onlyVertical){
                    if (btmp.getWidth() > btmp.getHeight()) continue;
                }
                listImages.add(btmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //download only top 10, no point in downloading more
            if (i == 10) break;
        }
        return  listImages;
    }

    public boolean isNetworkAvailable(Context cntxt) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) cntxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
