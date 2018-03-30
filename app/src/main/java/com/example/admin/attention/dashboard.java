package com.example.admin.attention;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ADMIN on 2/24/2018.
 */

public class dashboard extends  Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        /*Picasso offline mode*/
//        Picasso.Builder builder=new Picasso.Builder(this);
//        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
//        Picasso built=builder.build();
//        built.setIndicatorsEnabled(true);
//        built.setLoggingEnabled(true);
//        Picasso.setSingletonInstance(built);


    }
}
