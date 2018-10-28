package com.example.cau_coin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends Activity {
    private String userid;
    private String name;
    private String major;

    private String select_dept = "";
    private String select_grade = "";
    private String select_semester = "";
    private String select_subject = "";
    private String select_year = "";
    private String select_evaluate = "";

    private TextView dept_sw;
    private TextView dept_ie;
    private TextView dept_ee;

    private TextView grade_1;
    private TextView grade_2;
    private TextView grade_3;
    private TextView grade_4;

    private TextView semester_1;
    private TextView semester_2;

    private TextView subject;

    private TextView takeYear;

    private TextView evaluate_1;
    private TextView evaluate_2;
    private TextView evaluate_3;
    private TextView evaluate_4;
    private TextView evaluate_5;

    private EditText review;

    private RelativeLayout register;

    private InputMethodManager imm;
    private Database_Evaluate database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        userid = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");

        ImageView returnBack = (ImageView) findViewById(R.id.write_returnback);
        dept_sw = (TextView) findViewById(R.id.write_dept_sw);
        dept_ie = (TextView) findViewById(R.id.write_dept_ie);
        dept_ee = (TextView) findViewById(R.id.write_dept_ee);

        grade_1 = (TextView) findViewById(R.id.write_grade_1);
        grade_2 = (TextView) findViewById(R.id.write_grade_2);
        grade_3 = (TextView) findViewById(R.id.write_grade_3);
        grade_4 = (TextView) findViewById(R.id.write_grade_4);

        semester_1 = (TextView) findViewById(R.id.write_semester_1);
        semester_2 = (TextView) findViewById(R.id.write_semester_2);

        subject = (TextView) findViewById(R.id.write_subject);

        takeYear = (TextView) findViewById(R.id.write_takeyear);

        evaluate_1 = (TextView) findViewById(R.id.write_evaluate_1);
        evaluate_2 = (TextView) findViewById(R.id.write_evaluate_2);
        evaluate_3 = (TextView) findViewById(R.id.write_evaluate_3);
        evaluate_4 = (TextView) findViewById(R.id.write_evaluate_4);
        evaluate_5 = (TextView) findViewById(R.id.write_evaluate_5);

        review = (EditText) findViewById(R.id.write_review);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        register = (RelativeLayout) findViewById(R.id.write_register);

        database = new Database_Evaluate(getApplicationContext(), "evaldb.db", null, 1);

        dept_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDept(dept_sw);
            }
        });
        dept_ie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDept(dept_ie);
            }
        });
        dept_ee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDept(dept_ee);
            }
        });

        grade_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGrade(grade_1);
            }
        });
        grade_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGrade(grade_2);
            }
        });
        grade_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGrade(grade_3);
            }
        });
        grade_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGrade(grade_4);
            }
        });

        semester_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSemester(semester_1);
            }
        });
        semester_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSemester(semester_2);
            }
        });

        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_dept.equals("")) {
                    Toast.makeText(getApplicationContext(), "학부를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (select_grade.equals("")) {
                    Toast.makeText(getApplicationContext(), "학년을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                } else if (select_semester.equals("")) {
                    Toast.makeText(getApplicationContext(), "학기를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    final String[] items = setSubjectItem();
                    final int[] selectedIndex = {0};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity.this);
                    dialog.setTitle("과목을 선택해주세요").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedIndex[0] = which;
                        }
                    }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            select_subject = items[selectedIndex[0]];
                            if(!database.getEvaluate(userid,select_subject)){
                                Toast.makeText(getApplicationContext(), "이미 강의평가를 진행한 과목입니다.", Toast.LENGTH_SHORT).show();
                                select_subject="";
                                subject.setText("과목 선택");
                                subject.setTextColor(0xff565656);
                                subject.setTypeface(null, Typeface.NORMAL);
                            }
                            else if (select_subject.equals("-선택-")) {
                                select_subject="";
                                subject.setText("과목 선택");
                                subject.setTextColor(0xff565656);
                                subject.setTypeface(null, Typeface.NORMAL);
                            } else {
                                subject.setText(select_subject);
                                subject.setTextColor(0xff045473);
                                subject.setTypeface(null, Typeface.BOLD);
                            }
                        }
                    }).create().show();
                }
            }
        });

        takeYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"-선택-", "2018년", "2017년", "2016년", "2015년", "2014년", "2013년", "2012년"};
                final int[] selectedIndex = {0};
                AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity.this);
                dialog.setTitle("수강년도를 선택해주세요").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex[0] = which;
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select_year = items[selectedIndex[0]];
                        if (select_year.equals("-선택-")) {
                            select_year="";
                            takeYear.setText("수강년도 선택");
                            takeYear.setTextColor(0xff565656);
                            takeYear.setTypeface(null, Typeface.NORMAL);
                        } else {
                            takeYear.setText(select_year);
                            takeYear.setTextColor(0xff045473);
                            takeYear.setTypeface(null, Typeface.BOLD);
                        }
                    }
                }).create().show();
            }
        });

        evaluate_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEvaluate(evaluate_1);
            }
        });
        evaluate_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEvaluate(evaluate_2);
            }
        });
        evaluate_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEvaluate(evaluate_3);
            }
        });
        evaluate_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEvaluate(evaluate_4);
            }
        });
        evaluate_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEvaluate(evaluate_5);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEval();
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        review.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        registerEval();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public void registerEval(){
        hideKeyboard();
        if (select_dept.equals("")) {
            Toast.makeText(getApplicationContext(), "학부를 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (select_grade.equals("")) {
            Toast.makeText(getApplicationContext(), "학년을 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (select_semester.equals("")) {
            Toast.makeText(getApplicationContext(), "학기를 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (select_subject.equals("")) {
            Toast.makeText(getApplicationContext(), "과목을 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (select_year.equals("")) {
            Toast.makeText(getApplicationContext(), "수강년도를 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (select_evaluate.equals("")){
            Toast.makeText(getApplicationContext(), "평점을 선택해주세요", Toast.LENGTH_SHORT).show();
        }
        else if (review.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "한줄평을 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            database.insertData_Evaluate(userid, select_subject);

            SendData sendData = new SendData();
            sendData.execute(review.getText().toString());

            Toast.makeText(getApplicationContext(), "평가가 등록되었습니다", Toast.LENGTH_SHORT).show();
            Intent a = new Intent(WriteActivity.this, MainActivity.class);
            a.putExtra("name", name);
            a.putExtra("major", major);
            a.putExtra("id", userid);
            a.putExtra("from", "write");
            startActivity(a);
            finish();
        }
    }

    public void hideKeyboard(){
        imm.hideSoftInputFromWindow(review.getWindowToken(), 0);
    }

    public String[] setSubjectItem() {
        switch (select_grade) {
            case "1학년":
                switch (select_dept){
                    case "소프트웨어학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_sw_1_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_sw_1_2);
                        }
                        break;
                    case "융합공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ie_1_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ie_1_2);
                        }
                        break;
                    case "전자전기공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ee_1_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ee_1_2);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "2학년":
                switch (select_dept){
                    case "소프트웨어학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_sw_2_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_sw_2_2);
                        }
                        break;
                    case "융합공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ie_2_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ie_2_2);
                        }
                        break;
                    case "전자전기공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ee_2_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ee_2_2);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "3학년":
                switch (select_dept){
                    case "소프트웨어학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_sw_3_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_sw_3_2);
                        }
                        break;
                    case "융합공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ie_3_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ie_3_2);
                        }
                        break;
                    case "전자전기공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ee_3_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ee_3_2);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "4학년":
                switch (select_dept){
                    case "소프트웨어학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_sw_4_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_sw_4_2);
                        }
                        break;
                    case "융합공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ie_4_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ie_4_2);
                        }
                        break;
                    case "전자전기공학부":
                        if(select_semester.equals("1학기")){
                            return getResources().getStringArray(R.array.subject_ee_4_1);
                        }
                        else if(select_semester.equals("2학기")){
                            return getResources().getStringArray(R.array.subject_ee_4_2);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        String[] temp = {"0","0"};
        return temp;
    }

    @SuppressLint("ResourceAsColor")
    public void onClickDept(TextView dept) {
        dept_sw.setTextColor(0xff565656);
        dept_sw.setTypeface(null, Typeface.NORMAL);
        dept_ie.setTextColor(0xff565656);
        dept_ie.setTypeface(null, Typeface.NORMAL);
        dept_ee.setTextColor(0xff565656);
        dept_ee.setTypeface(null, Typeface.NORMAL);

        dept.setTextColor(0xff045473);
        dept.setTypeface(null, Typeface.BOLD);
        select_dept = dept.getText().toString();
    }

    @SuppressLint("ResourceAsColor")
    public void onClickGrade(TextView grade) {
        grade_1.setTextColor(0xff565656);
        grade_1.setTypeface(null, Typeface.NORMAL);
        grade_2.setTextColor(0xff565656);
        grade_2.setTypeface(null, Typeface.NORMAL);
        grade_3.setTextColor(0xff565656);
        grade_3.setTypeface(null, Typeface.NORMAL);
        grade_4.setTextColor(0xff565656);
        grade_4.setTypeface(null, Typeface.NORMAL);

        grade.setTextColor(0xff045473);
        grade.setTypeface(null, Typeface.BOLD);
        select_grade = grade.getText().toString();
    }

    @SuppressLint("ResourceAsColor")
    public void onClickSemester(TextView semester) {
        semester_1.setTextColor(0xff565656);
        semester_1.setTypeface(null, Typeface.NORMAL);
        semester_2.setTextColor(0xff565656);
        semester_2.setTypeface(null, Typeface.NORMAL);

        semester.setTextColor(0xff045473);
        semester.setTypeface(null, Typeface.BOLD);
        select_semester = semester.getText().toString();
    }

    @SuppressLint("ResourceAsColor")
    public void onClickEvaluate(TextView evaluate) {
        evaluate_1.setTextColor(0xff565656);
        evaluate_1.setTypeface(null, Typeface.NORMAL);
        evaluate_2.setTextColor(0xff565656);
        evaluate_2.setTypeface(null, Typeface.NORMAL);
        evaluate_3.setTextColor(0xff565656);
        evaluate_3.setTypeface(null, Typeface.NORMAL);
        evaluate_4.setTextColor(0xff565656);
        evaluate_4.setTypeface(null, Typeface.NORMAL);
        evaluate_5.setTextColor(0xff565656);
        evaluate_5.setTypeface(null, Typeface.NORMAL);

        evaluate.setTextColor(0xff045473);
        evaluate.setTypeface(null, Typeface.BOLD);
        select_evaluate = evaluate.getText().toString();
    }

    public class SendData extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());

                JSONObject myJsonObject = new JSONObject();
                String review = params[0];

                try {
                    myJsonObject.put("type", "evaluate");
                    myJsonObject.put("user_id", userid);
                    myJsonObject.put("dept", select_dept);
                    myJsonObject.put("grade", select_grade);
                    myJsonObject.put("semester", select_semester);
                    myJsonObject.put("subject", select_subject);
                    myJsonObject.put("evaluate", select_evaluate);
                    myJsonObject.put("takeyear", select_year);
                    myJsonObject.put("review", review);
                    myJsonObject.put("timestamp", currentDateTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://115.68.207.101:4444/write_transaction";
                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStream os = conn.getOutputStream();
                os.write(myJsonObject.toString().getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                return sb.toString();

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("강의 평가 취소");
        builder.setMessage("작성하던 내용은 저장되지 않습니다. 취소하시겠어요?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pencilimg);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent a = new Intent(WriteActivity.this, MainActivity.class);
                a.putExtra("name", name);
                a.putExtra("major", major);
                a.putExtra("id", userid);
                a.putExtra("from", "write");
                startActivity(a);
                finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}