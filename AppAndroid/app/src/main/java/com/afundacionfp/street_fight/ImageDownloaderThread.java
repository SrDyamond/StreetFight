package com.afundacionfp.street_fight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageDownloaderThread extends Thread {

    private URL url;
    private final ImageView imageView;
    private final Context context;

    public ImageDownloaderThread(ImageView imageView, Context context) {
        this.imageView = imageView;
        this.context = context;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        super.run();
        //Log.d("imageView", imageView.toString());
        try {
            //Log.d("URL", "'"+url.toString()+"'");
            InputStream inputStream = url.openConnection().getInputStream();
            //Log.d("#######", "PASA");
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            //Log.d("imageBitmap", imageBitmap.toString());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(imageBitmap);
                }
            });
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            //Log.d("ERROR", "Hay un error");
            //Se tiene que ejecutar en un UIThread para poner tocar los views del hilo principal
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Se pone la imagen por defecto en caso de que salte la excepción (connection failed)
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.flag_blue_500t));
                }
            });

        }


    }
}
