package com.example.admin.attention;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView img=findViewById(R.id.splashImage);
        final TextView tv=findViewById(R.id.splashText);
        RelativeLayout rv=findViewById(R.id.splashLayout);

        Animation fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        img.setAnimation(fromtop);
        Animation frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        rv.setAnimation(frombottom);




        new CountDownTimer(1000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }.start();
    }
}
