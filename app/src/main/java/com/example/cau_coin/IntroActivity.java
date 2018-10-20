package com.example.cau_coin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends Activity {
    private Handler handler;
    private boolean isUserOn = true;

    private ImageView logo;
    private Animation appear;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isUserOn){
                Intent a = new Intent(IntroActivity.this,LoginActivity.class);
                startActivity(a);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        logo = (ImageView)findViewById(R.id.intro_logo);
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.applogo_appear);

        logo.setAnimation(appear);

        init();
        handler.postDelayed(runnable,3000);
    }

    public void init(){
        handler = new Handler();
    }

    @Override
    public void onBackPressed(){
        ;
    }

    @Override
    protected void onUserLeaveHint(){
        isUserOn=false;
        super.onUserLeaveHint();
    }

}