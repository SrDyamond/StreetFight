package com.afundacionfp.street_fight.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class WrongLocationThread extends Thread {

    private final Context context;
    private final ImageView wrongLocationIcon;
    private boolean terminate = false;

    public WrongLocationThread(Context context, ImageView wrongLocationIcon) {
        this.context = context;
        this.wrongLocationIcon = wrongLocationIcon;
    }

    @Override
    public void run() {
        super.run();
        while (!terminate) {
//            Log.d("Thread", "Update = " + wrongLocationIcon.getVisibility());
//            System.out.println("Update = " + wrongLocationIcon.getVisibility());
            if (wrongLocationIcon.getVisibility() == View.GONE) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wrongLocationIcon.setVisibility(View.VISIBLE);
                    }
                });
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void correctLocationRecived() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wrongLocationIcon.setVisibility(View.GONE);
            }
        });
    }

    public void terminate() {
        terminate = true;
    }
}
