package com.example.cau_coin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailReviewActivity extends Activity {
    private String id;
    private String name;
    private String major;
    private String evaluateId;
    private String lookup;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<RecycleItem2> myList = new ArrayList<RecycleItem2>();

    private ArrayList<Data_Evaluate> dataList = new ArrayList<Data_Evaluate>();

    private ArrayList<String> filter_dept;
    private ArrayList<String> filter_semester;
    private ArrayList<String> filter_grade;
    private int num_Filter;

    private ImageView returnbutton;
    private ImageView refreshbutton;
    private TextView myProfessor;
    private TextView mySemester;
    private TextView mySubject;
    private TextView myEvaluate;
    private TextView myTakeYear;
    private TextView myReview;
    private TextView myTimeStamp;
    private TextView myScore;
    private TextView myTitle;

    private TextView left_5;
    private TextView left_4;
    private TextView left_3;
    private TextView left_2;
    private TextView left_1;
    private TextView right_5;
    private TextView right_4;
    private TextView right_3;
    private TextView right_2;
    private TextView right_1;
    private TextView numRating;

    private InputMethodManager imm;
    private EditText inputComment;

    private int eval_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailreview);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");
        evaluateId = getIntent().getExtras().getString("evaluateId");
        lookup = getIntent().getExtras().getString("lookup");

        filter_dept = (ArrayList<String>) getIntent().getSerializableExtra("filter_dept");
        filter_grade = (ArrayList<String>) getIntent().getSerializableExtra("filter_grade");
        filter_semester = (ArrayList<String>) getIntent().getSerializableExtra("filter_semester");
        num_Filter = getIntent().getExtras().getInt("num_Filter");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        final TextView giveScore = (TextView) findViewById(R.id.detail_givescore);
        ImageView registerComment = (ImageView) findViewById(R.id.detail_registerComment);
        inputComment = (EditText) findViewById(R.id.detail_inputComment);

        recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailReviewActivity.MyAdapter(myList, this);
        recyclerView.setAdapter(adapter);

        returnbutton = (ImageView) findViewById(R.id.detail_returnback);
        refreshbutton = (ImageView) findViewById(R.id.detail_refresh);
        myProfessor = (TextView) findViewById(R.id.detail_professor);
        mySemester = (TextView) findViewById(R.id.detail_semester);
        mySubject = (TextView) findViewById(R.id.detail_subject);
        myEvaluate = (TextView) findViewById(R.id.detail_evaluate);
        myTakeYear = (TextView) findViewById(R.id.detail_takeyear);
        myReview = (TextView) findViewById(R.id.detail_review);
        myTimeStamp = (TextView) findViewById(R.id.detail_timestamp);
        myScore = (TextView) findViewById(R.id.detail_score);
        myTitle = (TextView) findViewById(R.id.detail_title);

        left_5 = (TextView) findViewById(R.id.detail_leftstar5);
        left_4 = (TextView) findViewById(R.id.detail_leftstar4);
        left_3 = (TextView) findViewById(R.id.detail_leftstar3);
        left_2 = (TextView) findViewById(R.id.detail_leftstar2);
        left_1 = (TextView) findViewById(R.id.detail_leftstar1);
        right_5 = (TextView) findViewById(R.id.detail_rightstar5);
        right_4 = (TextView) findViewById(R.id.detail_rightstar4);
        right_3 = (TextView) findViewById(R.id.detail_rightstar3);
        right_2 = (TextView) findViewById(R.id.detail_rightstar2);
        right_1 = (TextView) findViewById(R.id.detail_rightstar1);
        numRating = (TextView) findViewById(R.id.detail_numRating);

        ReadData temp = new ReadData();
        temp.execute();

        final Database_Evaluate database = new Database_Evaluate(getApplicationContext(), "evaldb.db", null, 1);

        if (!database.getScore(id, evaluateId)) {
            giveScore.setText("Already Scored");
        }

        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(DetailReviewActivity.this, DetailReviewActivity.class);
                a.putExtra("name", name);
                a.putExtra("major", major);
                a.putExtra("id", id);
                a.putExtra("evaluateId", evaluateId);
                a.putExtra("lookup", "yes");
                a.putExtra("filter_dept", filter_dept);
                a.putExtra("filter_grade", filter_grade);
                a.putExtra("filter_semester", filter_semester);
                a.putExtra("num_Filter", num_Filter);
                startActivity(a);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        giveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (database.getScore(id, evaluateId)) {
                    final String[] items = new String[]{"1", "2", "3", "4", "5"};
                    final int[] selectedIndex = {0};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DetailReviewActivity.this);
                    dialog.setTitle("평점을 선택해주세요").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedIndex[0] = which;
                        }
                    }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendData sendData = new SendData();
                            sendData.execute("score", items[selectedIndex[0]]);

                            Toast.makeText(getApplicationContext(), items[selectedIndex[0]] + "점을 부여했습니다. 블록체인 시스템에 등록까지 일정 시간이 소요될 수 있습니다.", Toast.LENGTH_SHORT).show();
                            database.insertData_Score(id, evaluateId);
                            giveScore.setText("Already Scored");

                            ReadData temp = new ReadData();
                            temp.execute();

                        }
                    }).create().show();
                } else {
                    Toast.makeText(getApplicationContext(), "이미 이 강의평가에 평점을 매겼습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        inputComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        registerComment();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        registerComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerComment();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
    }

    // 댓글 입력
    public void registerComment() {
        if (inputComment.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            SendData sendData = new SendData();
            sendData.execute("comment", inputComment.getText().toString());

            inputComment.setText("");
            hideKeyboard();

            Toast.makeText(getApplicationContext(), "댓글을 등록하였습니다. 블록체인 시스템에 등록까지 일정 시간이 소요될 수 있습니다.", Toast.LENGTH_SHORT).show();

            ReadData temp = new ReadData();
            temp.execute();
        }
    }

    // 댓글을 표기하기 위한 리사이클러 뷰
    class MyAdapter extends RecyclerView.Adapter {
        private Context context;
        private ArrayList<RecycleItem2> mItems;

        private int lastPosition = -1;

        public MyAdapter(ArrayList items, Context mContext) {
            mItems = items;
            context = mContext;
        }

        @Override
        public DetailReviewActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview2, parent, false);
            DetailReviewActivity.MyAdapter.ViewHolder holder = new DetailReviewActivity.MyAdapter.ViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((DetailReviewActivity.MyAdapter.ViewHolder) holder).cardview_text.setText(mItems.get(position).getComment());

            String[] time = mItems.get(position).getTimeStamp().split("-");
            ((DetailReviewActivity.MyAdapter.ViewHolder) holder).cardview_timestamp.setText(time[0] + "-" + time[1] + "-" + time[2].substring(0, 2));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView cardview_text;
            public TextView cardview_timestamp;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_text = (TextView) itemView.findViewById(R.id.cardview2_text);
                cardview_timestamp = (TextView) itemView.findViewById(R.id.cardview2_timestamp);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(DetailReviewActivity.this, MainActivity.class);
        a.putExtra("name", name);
        a.putExtra("major", major);
        a.putExtra("id", id);
        a.putExtra("from", "detail");
        a.putExtra("filter_dept", filter_dept);
        a.putExtra("filter_grade", filter_grade);
        a.putExtra("filter_semester", filter_semester);
        a.putExtra("num_Filter", num_Filter);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(a);
        finish();
    }

    // Score과 Comment를 부여하는것에 대한 Transaction 전송
    public class SendData extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());

                JSONObject myJsonObject = new JSONObject();
                String type = params[0];
                String input = params[1];

                try {
                    myJsonObject.put("type", type);
                    myJsonObject.put("user_id", id);
                    myJsonObject.put("evaluate_id", evaluateId);

                    if (type.equals("score")) {
                        myJsonObject.put("score", input);
                    } else {
                        myJsonObject.put("comment", input);
                    }
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

    // 블록으로부터 데이터 받아오기 위해 Transaction 전송
    public class ReadData extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                String url;
                if (lookup.equals("yes")) {
                    url = "http://115.68.207.101:4444/read_one_data/" + evaluateId;
                } else {
                    //url = "http://115.68.207.101:4444/read_one_data/" + evaluateId + "&" + id;
                    url = "http://115.68.207.101:4444/read_one_data/" + evaluateId;
                }
                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                conn.disconnect();
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    dataList.clear();

                    ArrayList<String> scoreParsed = new ArrayList<String>();
                    ArrayList<String> commentParsed = new ArrayList<String>();
                    ArrayList<String> commentTimeParsed = new ArrayList<String>();
                    String userIdFromServer;
                    String evaluateIdFromServer;
                    String deptFromServer;
                    String gradeFromServer;
                    String semesterFromServer;
                    String subjectFromServer;
                    String evaluateFromServer;
                    String takeYearFromServer;
                    String reviewFromServer;
                    String timeStampFromServer;
                    String scoreFromServer;
                    String commentFromServer;

                    JSONObject jsonObject = new JSONObject(s);
                    String jsonDataString = jsonObject.getString("eval");
                    JSONArray jsonArray = new JSONArray(jsonDataString);

                    JSONArray tempArray;
                    String scoreTemp;
                    String commentTemp;
                    JSONObject item;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = jsonArray.getJSONObject(i);

                        userIdFromServer = item.getString("user_id");
                        evaluateIdFromServer = item.getString("evaluate_id");
                        deptFromServer = item.getString("dept");
                        gradeFromServer = item.getString("grade");
                        semesterFromServer = item.getString("semester");
                        subjectFromServer = item.getString("subject");
                        evaluateFromServer = item.getString("evaluate");
                        takeYearFromServer = item.getString("takeyear");
                        reviewFromServer = item.getString("review");
                        timeStampFromServer = item.getString("timestamp");
                        scoreFromServer = item.getString("scores");
                        commentFromServer = item.getString("comments");

                        if (gradeFromServer.contains("학년")) {
                            gradeFromServer = gradeFromServer.substring(0, 1);
                        }
                        if (semesterFromServer.contains("학기")) {
                            semesterFromServer = semesterFromServer.substring(0, 1);
                        }

                        tempArray = new JSONArray(scoreFromServer);
                        scoreParsed.clear();
                        for (int j = 0; j < tempArray.length(); j++) {
                            JSONObject scoresTemp = tempArray.getJSONObject(j);
                            scoreTemp = scoresTemp.getString("score");
                            scoreParsed.add(scoreTemp);
                        }

                        tempArray = new JSONArray(commentFromServer);
                        commentParsed.clear();
                        commentTimeParsed.clear();
                        for (int j = 0; j < tempArray.length(); j++) {
                            JSONObject commentsTemp = tempArray.getJSONObject(j);
                            commentTemp = commentsTemp.getString("comment");
                            commentParsed.add(commentTemp);
                            commentTemp = commentsTemp.getString("timestamp");
                            commentTimeParsed.add(commentTemp);
                        }

                        dataList.add(new Data_Evaluate(userIdFromServer, evaluateIdFromServer, deptFromServer, gradeFromServer, semesterFromServer, subjectFromServer, evaluateFromServer, takeYearFromServer, reviewFromServer,
                                timeStampFromServer, scoreParsed, commentParsed, commentTimeParsed));
                    }
                } catch (JSONException e) {
                }
            }

            for (int a = 0; a < dataList.size(); a++) {
                if (dataList.get(a).getEvaluateId().equals(evaluateId)) {
                    String[] temp = dataList.get(a).getSubject().split("-");
                    String[] temp2 = dataList.get(a).getTimeStamp().split("-");

                    myTitle.setText(temp[0] + ":" + temp[1]);
                    mySubject.setText(temp[0].trim());
                    myProfessor.setText(temp[1].trim());
                    mySemester.setText(dataList.get(a).getGrade() + "학년 " + dataList.get(a).getSemester() + "학기");
                    myEvaluate.setText(dataList.get(a).getEvaluate() + "점");
                    myTakeYear.setText(dataList.get(a).getTakeYear() + " " + dataList.get(a).getSemester() + "학기 수강자");
                    myReview.setText(dataList.get(a).getReview());
                    myTimeStamp.setText(temp2[0] + "-" + temp2[1] + "-" + temp2[2].substring(0, 2));

                    eval_index = a;
                    setStar();

                    myList.clear();
                    for (int b = 0; b < dataList.get(a).getCommentNum(); b++) {
                        myList.add(new RecycleItem2(dataList.get(a).getComment(b), dataList.get(a).getCommentTime(b)));
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void hideKeyboard() {
        imm.hideSoftInputFromWindow(inputComment.getWindowToken(), 0);
    }

    // 별점 비율 표기
    public void setStar() {
        myScore.setText(dataList.get(eval_index).getScore());

        LinearLayout.LayoutParams i;
        LinearLayout.LayoutParams j;

        float starTemp1;
        float starTemp2;
        starTemp2 = dataList.get(eval_index).getNumScore();
        int num = (int) starTemp2;

        numRating.setText(num + " Ratings");

        i = (LinearLayout.LayoutParams) left_5.getLayoutParams();
        j = (LinearLayout.LayoutParams) right_5.getLayoutParams();
        starTemp1 = dataList.get(eval_index).getNumStar("5");
        if (starTemp1 == 0) {
            i.weight = 1;
            j.weight = 99;
        } else {
            i.weight = starTemp1;
            j.weight = (starTemp2 - starTemp1);
        }
        left_5.setLayoutParams(i);
        right_5.setLayoutParams(j);

        i = (LinearLayout.LayoutParams) left_4.getLayoutParams();
        j = (LinearLayout.LayoutParams) right_4.getLayoutParams();
        starTemp1 = dataList.get(eval_index).getNumStar("4");
        if (starTemp1 == 0) {
            i.weight = 1;
            j.weight = 99;
        } else {
            i.weight = starTemp1;
            j.weight = (starTemp2 - starTemp1);
        }
        left_4.setLayoutParams(i);
        right_4.setLayoutParams(j);

        i = (LinearLayout.LayoutParams) left_3.getLayoutParams();
        j = (LinearLayout.LayoutParams) right_3.getLayoutParams();
        starTemp1 = dataList.get(eval_index).getNumStar("3");
        if (starTemp1 == 0) {
            i.weight = 1;
            j.weight = 99;
        } else {
            i.weight = starTemp1;
            j.weight = (starTemp2 - starTemp1);
        }
        left_3.setLayoutParams(i);
        right_3.setLayoutParams(j);

        i = (LinearLayout.LayoutParams) left_2.getLayoutParams();
        j = (LinearLayout.LayoutParams) right_2.getLayoutParams();
        starTemp1 = dataList.get(eval_index).getNumStar("2");
        if (starTemp1 == 0) {
            i.weight = 1;
            j.weight = 99;
        } else {
            i.weight = starTemp1;
            j.weight = (starTemp2 - starTemp1);
        }
        left_2.setLayoutParams(i);
        right_2.setLayoutParams(j);

        i = (LinearLayout.LayoutParams) left_1.getLayoutParams();
        j = (LinearLayout.LayoutParams) right_1.getLayoutParams();
        starTemp1 = dataList.get(eval_index).getNumStar("1");
        if (starTemp1 == 0) {
            i.weight = 1;
            j.weight = 99;
        } else {
            i.weight = starTemp1;
            j.weight = (starTemp2 - starTemp1);
        }
        left_1.setLayoutParams(i);
        right_1.setLayoutParams(j);
    }
}
