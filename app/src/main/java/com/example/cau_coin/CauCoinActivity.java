package com.example.cau_coin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class CauCoinActivity extends Activity {
    private Context context;

    private String id;
    private String name;
    private String major;

    private float downX = 0;
    private float upX = 0;
    private float downY = 0;
    private float upY = 0;
    private int currentIndex;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private ViewFlipper flipper;

    private ImageView logo;
    private Animation appear;
    private ScrollView scroller;

    private TextView index0;
    private TextView index1;
    private TextView index2;
    private TextView index3;

    private TextView page3_aboutus;

    private ImageView gunhee;
    private ImageView junhui;
    private ImageView cheol;
    private ImageView cheon;

    private ImageView individual_image;
    private TextView individual_name;
    private TextView individual_role;
    private TextView individual_identity;
    private TextView individual_lab;
    private TextView individual_git;
    private TextView individual_mail;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caucoin);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");

        context = this;

        flipper = (ViewFlipper) findViewById(R.id.caucoin_flipper);
        index0 = (TextView) findViewById(R.id.caucoin_index0);
        index1 = (TextView) findViewById(R.id.caucoin_index1);
        index2 = (TextView) findViewById(R.id.caucoin_index2);
        index3 = (TextView) findViewById(R.id.caucoin_index3);

        page3_aboutus = (TextView) findViewById(R.id.caucoin_page3_aboutus);

        gunhee = (ImageView) findViewById(R.id.caucoin_gunhee);
        junhui = (ImageView) findViewById(R.id.caucoin_junhui);
        cheol = (ImageView) findViewById(R.id.caucoin_cheol);
        cheon = (ImageView) findViewById(R.id.caucoin_cheon);

        individual_image = (ImageView) findViewById(R.id.caucoin_individual_image);
        individual_name = (TextView) findViewById(R.id.caucoin_individual_name);
        individual_role = (TextView) findViewById(R.id.caucoin_individual_role);
        individual_identity = (TextView) findViewById(R.id.caucoin_individual_identity);
        individual_lab = (TextView) findViewById(R.id.caucoin_individual_lab);
        individual_git = (TextView) findViewById(R.id.caucoin_individual_git);
        individual_mail = (TextView) findViewById(R.id.caucoin_individual_mail);

        logo = (ImageView) findViewById(R.id.caucoin_logo);
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.applogo_appear);
        scroller = (ScrollView) findViewById(R.id.caucoin_scroller);

        logo.setAnimation(appear);

        currentIndex=0;

        junhui.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                individual_image.setImageResource(R.drawable.jun);
                longClick("Jun Hui Kim","▶ Block-Chain core development","▶ Undergraduate Stundent of Chung-Ang Univ.","▶ Distributed Platforms and Security Lab.",
                        "▶ https://github.com/hihiboss","▶ junhui820@gmail.com");
                return false;
            }
        });

        cheol.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                individual_image.setImageResource(R.drawable.cheol);
                longClick("Cheol Lee","▶ Connection between App. and Chain","▶ Master Stundent of Chung-Ang Univ.","▶ Ubiquitous Computing Lab.",
                        "▶ https://github.com/freedomchurl","▶ clee@uclab.re.kr");
                return false;
            }
        });

        gunhee.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                individual_image.setImageResource(R.drawable.gun);
                longClick("Gun Hee Jang","▶ Client Application development","▶ Undergraduate Stundent of Chung-Ang Univ.","▶ Ubiquitous Computing Lab.",
                        "▶ https://github.com/GunHee5719","▶ ghjang@uclab.re.kr");
                return false;
            }
        });

        cheon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                individual_image.setImageResource(R.drawable.cheon);
                longClick("Jun Hyeon Cheon","▶ Application Logo design","▶ Undergraduate Stundent of Chung-Ang Univ.","▶ Networked Systems Lab.",
                        "▶ https://github.com/jhcheon1000","▶ hhj2134@gmail.com");
                return false;
            }
        });

        junhui.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return false;
            }
        });

        cheol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return false;
            }
        });

        gunhee.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return false;
            }
        });

        cheon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return false;
            }
        });

        scroller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return false;
            }
        });

        flipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch(event);
                return true;
            }
        });


    }

    public void longClick(String name, String role, String identity, String lab, String git, String mail){
        page3_aboutus.setText("");
        
        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_top_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_top_out));

        index0.setText("○");
        index1.setText("○");
        index2.setText("○");
        index3.setVisibility(View.VISIBLE);

        individual_name.setText(name);
        individual_role.setText(role);
        individual_identity.setText(identity);
        individual_lab.setText(lab);
        individual_git.setText(git);
        individual_mail.setText(mail);

        flipper.showNext();
        flipper.showNext();

        currentIndex = 3;
    }

    public void touch(MotionEvent event){
        //터치시작
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
        }
        //터치종료
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            upX = event.getX();
            upY = event.getY();

            //왼쪽 -> 오른쪽으로 이동
            if (upX < (downX - 300) && currentIndex != 3) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));

                //인덱스체크 - 3번째 화면이면 동작없음
                if (currentIndex < 2) {
                    flipper.showNext();
                    currentIndex++;

                    if (currentIndex == 1) {
                        index0.setText("○");
                        index1.setText("●");
                    } else if (currentIndex == 2) {
                        page3_aboutus.setText("About Us");
                        index1.setText("○");
                        index2.setText("●");
                    }
                }
            }
            //오른쪽 -> 왼쪽으로 이동
            else if ((upX - 300) > downX && currentIndex != 3) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
                //인덱스체크 - 첫번째화면이면 동작없음
                if (currentIndex > 0) {
                    flipper.showPrevious();

                    currentIndex--;

                    if (currentIndex == 0) {
                        index1.setText("○");
                        index0.setText("●");
                    } else if (currentIndex == 1) {
                        index2.setText("○");
                        index1.setText("●");
                    }
                }
            }
            // 아래 -> 위로 이동
            else if ((upY - 300) > downY && currentIndex == 3) {
                flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_out));

                flipper.showPrevious();
                flipper.showPrevious();

                index0.setText("○");
                index1.setText("●");
                index2.setText("○");
                index3.setVisibility(View.INVISIBLE);
                currentIndex = 1;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (currentIndex == 3) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_out));

            flipper.showPrevious();
            flipper.showPrevious();

            index0.setText("○");
            index1.setText("●");
            index2.setText("○");
            index3.setVisibility(View.INVISIBLE);
            currentIndex = 1;
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                Intent a = new Intent(CauCoinActivity.this, MainActivity.class);
                a.putExtra("name", name);
                a.putExtra("major", major);
                a.putExtra("id", id);
                a.putExtra("from", "caucoin");
                startActivity(a);
                finish();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), "메인 화면으로 돌아가려면 한번 더 누르세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
