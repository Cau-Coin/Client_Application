package com.example.cau_coin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

    private String select_grade;
    private String select_semester;
    private String select_subject;
    private String select_year;
    private String select_evaluate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        userid = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");

        TextView userMajor = (TextView)findViewById(R.id.write_major);
        final Spinner input_grade = (Spinner)findViewById(R.id.write_grade);
        final Spinner input_semester = (Spinner)findViewById(R.id.write_semester);
        final Spinner input_subject = (Spinner)findViewById(R.id.write_subject);
        final Spinner input_year = (Spinner)findViewById(R.id.write_year);
        final Spinner input_evaluate = (Spinner)findViewById(R.id.write_evaluate);
        final EditText input_review = (EditText)findViewById(R.id.write_review);
        Button register = (Button)findViewById(R.id.write_register);

        userMajor.setText(major);

        final ArrayAdapter blankAdapter = ArrayAdapter.createFromResource(this,R.array.blank,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter gradeAdapter = ArrayAdapter.createFromResource(this,R.array.grade,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter semesterAdapter = ArrayAdapter.createFromResource(this,R.array.semester,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,R.array.year,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter evaluateAdapter = ArrayAdapter.createFromResource(this,R.array.evaluate,android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter subject_sw_1_1 = ArrayAdapter.createFromResource(this,R.array.subject_sw_1_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_1_2 = ArrayAdapter.createFromResource(this,R.array.subject_sw_1_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_2_1 = ArrayAdapter.createFromResource(this,R.array.subject_sw_2_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_2_2 = ArrayAdapter.createFromResource(this,R.array.subject_sw_2_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_3_1 = ArrayAdapter.createFromResource(this,R.array.subject_sw_3_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_3_2 = ArrayAdapter.createFromResource(this,R.array.subject_sw_3_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_4_1 = ArrayAdapter.createFromResource(this,R.array.subject_sw_4_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_sw_4_2 = ArrayAdapter.createFromResource(this,R.array.subject_sw_4_2,android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter subject_ie_1_1 = ArrayAdapter.createFromResource(this,R.array.subject_ie_1_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_1_2 = ArrayAdapter.createFromResource(this,R.array.subject_ie_1_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_2_1 = ArrayAdapter.createFromResource(this,R.array.subject_ie_2_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_2_2 = ArrayAdapter.createFromResource(this,R.array.subject_ie_2_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_3_1 = ArrayAdapter.createFromResource(this,R.array.subject_ie_3_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_3_2 = ArrayAdapter.createFromResource(this,R.array.subject_ie_3_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_4_1 = ArrayAdapter.createFromResource(this,R.array.subject_ie_4_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ie_4_2 = ArrayAdapter.createFromResource(this,R.array.subject_ie_4_2,android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter subject_ee_1_1 = ArrayAdapter.createFromResource(this,R.array.subject_ee_1_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_1_2 = ArrayAdapter.createFromResource(this,R.array.subject_ee_1_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_2_1 = ArrayAdapter.createFromResource(this,R.array.subject_ee_2_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_2_2 = ArrayAdapter.createFromResource(this,R.array.subject_ee_2_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_3_1 = ArrayAdapter.createFromResource(this,R.array.subject_ee_3_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_3_2 = ArrayAdapter.createFromResource(this,R.array.subject_ee_3_2,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_4_1 = ArrayAdapter.createFromResource(this,R.array.subject_ee_4_1,android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter subject_ee_4_2 = ArrayAdapter.createFromResource(this,R.array.subject_ee_4_2,android.R.layout.simple_spinner_dropdown_item);

        input_year.setAdapter(yearAdapter);
        input_grade.setAdapter(gradeAdapter);
        input_semester.setAdapter(blankAdapter);
        input_subject.setAdapter(blankAdapter);
        input_evaluate.setAdapter(blankAdapter);

        final Database_Evaluate database = new Database_Evaluate(getApplicationContext(), "evaldb.db", null, 1);

        input_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) input_year.getItemAtPosition(position);

                if(selected.equals("-선택-")){
                    select_year="";
                }
                else{
                    select_year = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        input_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) input_grade.getItemAtPosition(position);

                if(selected.equals("-선택-")){
                    input_semester.setAdapter(blankAdapter);
                    input_subject.setAdapter(blankAdapter);
                    input_evaluate.setAdapter(blankAdapter);
                    select_grade="";
                }
                else{
                    input_semester.setAdapter(semesterAdapter);
                    input_subject.setAdapter(blankAdapter);
                    input_evaluate.setAdapter(blankAdapter);
                    select_grade = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        input_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) input_semester.getItemAtPosition(position);

                if(selected.equals("-선택-")){
                    input_subject.setAdapter(blankAdapter);
                    input_evaluate.setAdapter(blankAdapter);
                    select_semester="";
                }
                else{
                    select_semester = selected;
                    switch (select_grade){
                        case "1학년":
                            if(select_semester.equals("1학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_1_1);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_1_1);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_1_1);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else if(select_semester.equals("2학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_1_2);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_1_2);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_1_2);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else{
                                input_subject.setAdapter(blankAdapter);
                            }
                            break;
                        case "2학년":
                            if(select_semester.equals("1학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_2_1);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_2_1);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_2_1);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else if(select_semester.equals("2학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_2_2);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_2_2);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_2_2);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else{
                                input_subject.setAdapter(blankAdapter);
                            }
                            break;
                        case "3학년":
                            if(select_semester.equals("1학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_3_1);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_3_1);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_3_1);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else if(select_semester.equals("2학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_3_2);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_3_2);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_3_2);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else{
                                input_subject.setAdapter(blankAdapter);
                            }
                            break;
                        case "4학년":
                            if(select_semester.equals("1학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_4_1);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_4_1);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_4_1);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else if(select_semester.equals("2학기")){
                                if(major.equals("소프트웨어학부")) input_subject.setAdapter(subject_sw_4_2);
                                else if(major.equals("융합공학부")) input_subject.setAdapter(subject_ie_4_2);
                                else if(major.equals("전자전기공학부"))  input_subject.setAdapter(subject_ee_4_2);
                                else input_subject.setAdapter(blankAdapter);
                            }
                            else{
                                input_subject.setAdapter(blankAdapter);
                            }
                            break;
                        default:
                            input_subject.setAdapter(blankAdapter);
                            break;
                    }
                    input_evaluate.setAdapter(blankAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        input_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) input_subject.getItemAtPosition(position);

                if(selected.equals("-선택-")){
                    select_subject="";
                    input_evaluate.setAdapter(blankAdapter);
                }
                else{
                    if(database.getEvaluate(userid,selected)){
                        input_evaluate.setAdapter(evaluateAdapter);
                        select_subject = selected;
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "이미 이 과목에 대한 강의평가를 했습니다", Toast.LENGTH_SHORT).show();
                        input_subject.setAdapter(input_subject.getAdapter());
                        select_subject="";
                        input_evaluate.setAdapter(blankAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        input_evaluate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) input_evaluate.getItemAtPosition(position);

                if(selected.equals("-선택-")){
                    select_evaluate="";
                }
                else{
                    select_evaluate = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_year.equals("")){
                    Toast.makeText(getApplicationContext(), "항목을 모두 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(select_evaluate.equals("")){
                    Toast.makeText(getApplicationContext(), "항목을 모두 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(input_review.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "리뷰를 한 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.insertData_Evaluate(userid,select_subject);

                    SendData sendData = new SendData();
                    sendData.execute(input_review.getText().toString());

                    Toast.makeText(getApplicationContext(), "평가가 등록되었습니다", Toast.LENGTH_SHORT).show();
                    Intent a = new Intent(WriteActivity.this, MainActivity.class);
                    a.putExtra("name",name);
                    a.putExtra("major",major);
                    a.putExtra("id",userid);
                    a.putExtra("from","write");
                    startActivity(a);
                    finish();
                }
            }
        });
    }

    public class SendData extends AsyncTask<String,Void,String> {

        public String doInBackground(String ...params)
        {
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());

                JSONObject myJsonObject = new JSONObject();
                String review = params[0];

                try {
                    myJsonObject.put("type","evaluate");
                    myJsonObject.put("userid",userid);
                    myJsonObject.put("dept",major);
                    myJsonObject.put("grade",select_grade);
                    myJsonObject.put("semester",select_semester);
                    myJsonObject.put("subject",select_subject);
                    myJsonObject.put("evaluate",select_evaluate);
                    myJsonObject.put("takeyear",select_year);
                    myJsonObject.put("review",review);
                    myJsonObject.put("timestamp",currentDateTime);
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

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while((line = reader.readLine())!=null)
                {
                    sb.append(line);
                }

                reader.close();
                return sb.toString();

            }catch(Exception e){

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

        builder.setTitle("글작성 취소");
        builder.setMessage("작성하던 내용은 저장되지 않습니다. 취소하시겠어요?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.pencilimg);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent a = new Intent(WriteActivity.this, MainActivity.class);
                a.putExtra("name",name);
                a.putExtra("major",major);
                a.putExtra("id",userid);
                a.putExtra("from","write");
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