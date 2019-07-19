package com.example.redditwallpapermanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends PagerAdapter {
    Context context;
    ArrayList<Bitmap> images;
    ArrayList<String> links;

    public MyAdapter(Context context, ArrayList<Bitmap> list,ArrayList<String> links){
        this.context = context;
        this.images = list;
        this.links = links;
    }

    @Override
    public int getCount() {
//        return images.size();
        return links.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_card,container,false);
        ImageView imageView = view.findViewById(R.id.imageItem);
//        imageView.setImageBitmap(images.get(position));
        Picasso.get().load(links.get(position)).into(imageView);
        container.addView(view);
        return view;
    }
}
