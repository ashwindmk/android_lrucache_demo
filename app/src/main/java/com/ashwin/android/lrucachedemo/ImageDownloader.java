package com.ashwin.android.lrucachedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Integer> {
    private ImageLruCache cache;
    private ImageView imageview;
    private Bitmap bmp;

    public ImageDownloader(ImageView imageview, ImageLruCache cache) {
        this.imageview = imageview;
        this.cache = cache;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String url = params[0];
        try {
            bmp = getBitmapFromURL(url);
            if (bmp != null) {
                Log.w("lru-cache", "Saving image bitmap");
                cache.put(url, bmp);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            Log.w("lru-cache", "Downloading image: " + src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e("lru-cache", "Exception while downloading image", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result != null && result == 1 && imageview != null) {
            imageview.setImageBitmap(bmp);
        }
        super.onPostExecute(result);
    }
}
