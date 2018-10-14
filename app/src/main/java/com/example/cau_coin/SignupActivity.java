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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends Activity {
    private String selected_major;
    private int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        check = 0;

        final EditText input_id = (EditText) findViewById(R.id.signup_id);
        final EditText input_pwd = (EditText) findViewById(R.id.signup_pwd);
        final EditText input_name = (EditText) findViewById(R.id.signup_name);
        final Spinner input_major = (Spinner) findViewById(R.id.signup_major);

        Button signup = (Button) findViewById(R.id.signup_signup);
        Button goback = (Button) findViewById(R.id.signup_goback);

        ArrayAdapter majorAdapter = ArrayAdapter.createFromResource(this,  R.array.major, android.R.layout.simple_spinner_dropdown_item);
        input_major.setAdapter(majorAdapter);
        input_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_major = (String) input_major.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_id.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "학번을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (input_pwd.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (input_name.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    SignAccount temp = new SignAccount();
                    temp.execute(input_id.getText().toString(), input_pwd.getText().toString(), input_name.getText().toString(), selected_major);
                }
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class SignAccount extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                String input_id = params[0];
                String input_pwd = params[1];
                String input_name = params[2];
                String input_major = params[3];

                String url = "http://115.68.207.101/read_account.php?id=" + input_id;
                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (jsonArray.length() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    String url2 = "http://115.68.207.101/write_account.php?id=" + input_id + "&pwd=" + input_pwd + "&name=" + input_name + "&major=" + input_major;
                    URL obj2 = new URL(url2);

                    HttpURLConnection conn2 = (HttpURLConnection) obj2.openConnection(); // open connection

                    conn2.setReadTimeout(10000);
                    conn2.setConnectTimeout(15000);
                    conn2.setRequestMethod("GET");
                    conn2.setDoInput(true);
                    conn2.setDoOutput(true);

                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn2.getInputStream(), "UTF-8"));

                    String line2;
                    StringBuilder sb2 = new StringBuilder();

                    while ((line2 = reader2.readLine()) != null) {
                        sb2.append(line2);
                    }

                    reader2.close();

                    check = 1;
                    return sb2.toString();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (check == 1) {
                Toast.makeText(SignupActivity.this, "계정이 생성되었습니다", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(a);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("※회원가입 취소");
        builder.setMessage("작성중이던 내용은 저장되지 않습니다. 취소하시겠어요?");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent a = new Intent(SignupActivity.this, LoginActivity.class);
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