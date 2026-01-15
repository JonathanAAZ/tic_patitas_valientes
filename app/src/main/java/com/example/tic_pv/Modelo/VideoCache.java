package com.example.tic_pv.Modelo;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoCache {
    private static final LruCache<String, Uri> cache = new LruCache<>(10 * 1024 * 1024); // 10MB cache
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void getVideo(Context context, String url, VideoCallback callback) {
        Uri cachedUri = cache.get(url);
        if (cachedUri != null) {
            callback.onVideoReady(cachedUri);
            return;
        }

        String fileName = String.valueOf(url.hashCode());
        File file = new File(context.getCacheDir(), fileName);

        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            cache.put(url, uri);
            callback.onVideoReady(uri);
            return;
        }

        executor.execute(() -> {
            try {
                URLConnection connection = new URL(url).openConnection();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();

                Uri uri = Uri.fromFile(file);
                cache.put(url, uri);

                mainHandler.post(() -> callback.onVideoReady(uri));
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    // Manejar el error si es necesario
                    Log.e("VideoCache", "Error descargando el video video: " + e.getMessage());
                });
            }
        });
    }

    public interface VideoCallback {
        void onVideoReady(Uri uri);
    }
}
