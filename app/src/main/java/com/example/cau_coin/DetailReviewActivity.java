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
import android.widget.Button;
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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<RecycleItem2> myList = new ArrayList<RecycleItem2>();

    private ArrayList<Data_Evaluate> dataList = new ArrayList<Data_Evaluate>();

    ArrayList<String> temp_score = new ArrayList<String>();
    ArrayList<String> temp_comment = new ArrayList<String>();
    ArrayList<String> temp_commentTime = new ArrayList<String>();

    private ArrayList<String> filter_dept;
    private ArrayList<String> filter_semester;
    private ArrayList<String> filter_grade;
    private int num_Filter;

    private ImageView returnbutton;
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
                            Toast.makeText(getApplicationContext(), items[selectedIndex[0]] + "점을 부여했습니다", Toast.LENGTH_SHORT).show();
                            database.insertData_Score(id, evaluateId);
                            giveScore.setText("Already Scored");

                            dataList.get(eval_index).addScore(items[selectedIndex[0]]);
                            setStar();

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
                return false;
            }
        });
    }

    public void registerComment() {
        if (inputComment.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());

            Toast.makeText(getApplicationContext(), "[" + currentDateTime + "] " + inputComment.getText().toString(), Toast.LENGTH_SHORT).show();
            inputComment.setText("");
            hideKeyboard();
        }
    }

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
        startActivity(a);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // 블록으로부터 데이터 받아오기 위해 Transaction 전송
    public class ReadData extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                JSONObject myJsonObject = new JSONObject();
                try {
                    myJsonObject.put("type", "giveme");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://115.68.207.101:4444/read_block";
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
                    ArrayList<String> scoreParsed = new ArrayList<String>();
                    ArrayList<String> commentParsed = new ArrayList<String>();
                    ArrayList<String> commentTimeParsed = new ArrayList<String>();
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

                        evaluateIdFromServer = item.getString("evaluateid");
                        deptFromServer = item.getString("dept");
                        gradeFromServer = item.getString("grade");
                        semesterFromServer = item.getString("semester");
                        subjectFromServer = item.getString("subject");
                        evaluateFromServer = item.getString("evaluate");
                        takeYearFromServer = item.getString("takeyear");
                        reviewFromServer = item.getString("review");
                        timeStampFromServer = item.getString("timestamp");
                        scoreFromServer = item.getString("score");
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
                            scoreTemp = tempArray.getString(j);
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

                        dataList.add(new Data_Evaluate(evaluateIdFromServer, deptFromServer, gradeFromServer, semesterFromServer, subjectFromServer, evaluateFromServer, takeYearFromServer, reviewFromServer,
                                timeStampFromServer, scoreParsed, commentParsed, commentTimeParsed));
                    }


                } catch (JSONException e) {
                }
            }
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 서버쪽 완성되면 메소드와 여기 호출 지우기! @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            setFakeData();
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

    // 데이터 받아오고 나서 list 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void setFakeData() {
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("4");
        temp_score.add("4");
        temp_score.add("4");
        temp_score.add("3");
        temp_score.add("3");
        temp_score.add("1");
        temp_comment.add("교수님 좋아요!");
        temp_comment.add("교수님이 너무 좋은거 동감이에요!");
        temp_comment.add("수업을 안가게 돼요");
        temp_comment.add("기말때 갑자기 어려워짐!");
        temp_comment.add("기말고사 어려워요..ㅇㅈ..");
        temp_comment.add("학점따기 개좋음");
        temp_comment.add("학점 천사임");
        temp_commentTime.add("2017-07-03 05:00:10");
        temp_commentTime.add("2017-07-03 10:24:37");
        temp_commentTime.add("2017-07-04 11:35:57");
        temp_commentTime.add("2017-07-05 05:45:54");
        temp_commentTime.add("2017-07-12 06:21:13");
        temp_commentTime.add("2017-07-20 15:27:24");
        temp_commentTime.add("2017-07-24 13:54:20");
        dataList.add(new Data_Evaluate("00000001", "전자전기공학부", "1", "1", "선형대수학 - 권준석", "4", "2017년",
                "교수님이 좋았어요", "2017-07-03 04:00:01", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("4");
        temp_score.add("5");
        temp_score.add("3");
        temp_comment.add("인정... 영어 그자체");
        temp_comment.add("교수님이 너무 야해요");
        temp_commentTime.add("2017-01-05 20:54:04");
        temp_commentTime.add("2017-01-10 11:50:35");
        dataList.add(new Data_Evaluate("00000002", "소프트웨어학부", "4", "1", "네트워크응용설계 - 백정엽", "4", "2017년",
                "교수님 영어실력은 감탄 그자체", "2017-01-04 23:10:54", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("3");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님은 좋아요");
        temp_commentTime.add("2017-01-05 20:54:04");
        temp_commentTime.add("2017-01-10 11:50:35");
        dataList.add(new Data_Evaluate("00000003", "소프트웨어학부", "3", "1", "컴파일러 - 김중헌", "4", "2017년",
                "교수님이 수업을 잘 안하심", "2017-06-30 20:00:01", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("3");
        temp_comment.add("수업이 너무 지루해요");
        temp_comment.add("교수님 진짜 별로임");
        temp_commentTime.add("2015-08-20 20:54:04");
        temp_commentTime.add("2015-08-21 11:50:35");
        dataList.add(new Data_Evaluate("00000004", "융합공학부", "2", "1", "미적분학 - 김상욱", "1", "2015년",
                "교수님 진짜 별로에요", "2015-08-20 14:07:09", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("5");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님 강의력은 정말 최고");
        temp_comment.add("시험문제가 진짜 어렵긴 함..");
        temp_commentTime.add("2017-01-31 20:54:04");
        temp_commentTime.add("2017-02-01 11:50:35");
        dataList.add(new Data_Evaluate("00000005", "융합공학부", "2", "2", "컴퓨터구조 - 백정엽", "3", "2016년",
                "시험이 너무 어려워요", "2017-01-31 04:44:44", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("4");
        temp_score.add("2");
        temp_score.add("5");
        temp_comment.add("수업시간에 졸수가 없어요...");
        temp_comment.add("논리회로에서 컴공을 포기하게 되었어요ㅠ");
        temp_commentTime.add("2016-11-12 20:54:04");
        temp_commentTime.add("2016-11-13 11:50:35");
        dataList.add(new Data_Evaluate("00000006", "소프트웨어학부", "1", "2", "논리회로 - 조성래", "5", "2015년",
                "조성래교수님 사랑해요!", "2016-11-12 01:05:10", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("2");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님 강의력만은 정말 최고에요");
        temp_comment.add("좀 졸리긴해요");
        temp_commentTime.add("2018-10-18 20:54:04");
        temp_commentTime.add("2018-10-20 11:50:35");
        dataList.add(new Data_Evaluate("00000007", "소프트웨어학부", "4", "2", "설계패턴 - 이찬근", "4", "2017년",
                "교수님이 조금 지루해요. 수업은 잘하세요!", "2018-10-18 02:36:27", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("2");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("너무 어려워요");
        temp_comment.add("수업듣기 좋아요");
        temp_commentTime.add("2018-8-18 20:54:04");
        temp_commentTime.add("2018-8-20 11:50:35");
        dataList.add(new Data_Evaluate("00000008", "전자전기공학부", "4", "1", "광전자융합센서공학 - 최영완,민준홍", "3", "2018년",
                "교수님이 2명이에요!", "2018-10-18 02:36:27", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("2");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("3");
        temp_score.add("3");
        temp_comment.add("과목명이 짱기네");
        temp_comment.add("스마트기어 회로짜는거같은느낌일까");
        temp_commentTime.add("2018-8-18 20:54:04");
        temp_commentTime.add("2018-8-20 11:50:35");
        dataList.add(new Data_Evaluate("00000009", "전자전기공학부", "4", "1", "웨어러블 디바이스용 집적회로설계 - 백광현", "3", "2018년",
                "이건 도대체 무슨 과목일까", "2018-10-18 02:36:27", temp_score, temp_comment, temp_commentTime));

        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        temp_score.add("4");
        temp_score.add("2");
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("5");
        temp_comment.add("잘되나 보자!");
        temp_comment.add("겹치지 않겟지");
        temp_commentTime.add("2016-11-12 20:54:04");
        temp_commentTime.add("2016-11-13 11:50:35");
        dataList.add(new Data_Evaluate("00000010", "소프트웨어학부", "1", "2", "논리회로 - 조성래", "4", "2016년",
                "논리회로를 2과목 넣으면 잘 출력이 되나?", "2016-11-12 01:05:10", temp_score, temp_comment, temp_commentTime));
    }
}
