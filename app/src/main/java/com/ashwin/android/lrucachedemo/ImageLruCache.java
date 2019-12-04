package com.ashwin.android.lrucachedemo;

import android.graphics.Bitmap;
import android.util.LruCache;

class ImageLruCache extends LruCache<String, Bitmap> {
    ImageLruCache(int maxSize) {
        super(maxSize);
    }
}
