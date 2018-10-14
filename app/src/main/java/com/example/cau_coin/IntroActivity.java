package com.example.cau_coin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity {
    private Handler handler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent a = new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(a);
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        init();

        handler.postDelayed(runnable,2000);
    }

    public void init(){
        handler = new Handler();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

}