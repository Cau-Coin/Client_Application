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

        currentIndex = 0;

        logo = (ImageView) findViewById(R.id.caucoin_logo);
        appear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.applogo_appear);
        scroller = (ScrollView) findViewById(R.id.caucoin_scroller);

        logo.setAnimation(appear);

        scroller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //터치시작
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = event.getX();
                    downY = event.getY();
                }
                //터치종료
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upX = event.getX();
                    upY = event.getY();

                    //왼쪽 -> 오른쪽
                    if (upX < (downX - 300)) {
                        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));

                        //인덱스체크 - 마지막화면이면 동작없음
                        if (currentIndex < 2) {
                            flipper.showNext();
                            currentIndex++;

                            if (currentIndex == 1) {
                                index0.setText("○");
                                index1.setText("●");
                            } else if (currentIndex == 2) {
                                index1.setText("○");
                                index2.setText("●");
                            }
                        }
                    }
                    //오른쪽 -> 왼쪽
                    else if ((upX - 300) > downX) {
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
                }
                return false;
            }
        });

        flipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //터치시작
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = event.getX();
                }
                //터치종료
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upX = event.getX();

                    //왼쪽 -> 오른쪽
                    if (upX < (downX - 300)) {
                        flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                        flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));

                        //인덱스체크 - 마지막화면이면 동작없음
                        if (currentIndex < 2) {
                            flipper.showNext();
                            currentIndex++;

                            if (currentIndex == 1) {
                                index0.setText("○");
                                index1.setText("●");
                            } else if (currentIndex == 2) {
                                index1.setText("○");
                                index2.setText("●");
                            }
                        }
                    }
                    //오른쪽 -> 왼쪽
                    else if ((upX - 300) > downX) {
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
                }

                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
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
