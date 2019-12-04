package com.ashwin.android.lrucachedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ComponentCallbacks2 {
    private static final String img1 = "https://github.com/ashwin-mavila/resources/raw/master/imgs/banner_android.jpg";
    private static final String img2 = "https://github.com/ashwin-mavila/resources/raw/master/imgs/banner_androidp.jpg";
    private static final String img3 = "https://github.com/ashwin-mavila/resources/raw/master/imgs/banner_crashlytics.png";
    private static List<String> urls;
    static {
        urls = new ArrayList<>();
        urls.add(img1);
        urls.add(img2);
        urls.add(img3);
    }

    private int index = 0;
    private String url = null;

    private ImageLruCache cache;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCache(getApplicationContext());

        imageView = (ImageView) findViewById(R.id.my_imageview);
        loadImage(imageView);
    }

    private void initCache(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int totalRamKiB = am.getMemoryClass() * 1024;
        int maxSizeKiB = totalRamKiB / 8;
        this.cache = new ImageLruCache(maxSizeKiB);
    }

    public void loadImage(View v) {
        url = urls.get(index);
        index = (index + 1) % urls.size();

        Bitmap b = cache.get(url);
        if (b == null) {
            Log.w("lru-cache", "Lru cache miss: " + url);
            ImageDownloader imageDownloader = new ImageDownloader(imageView, cache);
            imageDownloader.execute(url);
        } else {
            Log.w("lru-cache", "Lru cache hit: " + url);
            imageView.setImageBitmap(b);
        }
    }

    public void remove(View v) {
        Bitmap b = cache.remove(url);
        if (b == null) {
            Log.e("lru-cache", "Image not found in cache: " + url);
        } else {
            Log.w("lru-cache", "Image removed from cache: " + url);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onLowMemory();
        if (level >= TRIM_MEMORY_MODERATE) {
            cache.evictAll();
        } else if (level >= TRIM_MEMORY_BACKGROUND) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cache.trimToSize(cache.size() / 2);
            } else {
                cache.evictAll();
            }
        }
    }
}
