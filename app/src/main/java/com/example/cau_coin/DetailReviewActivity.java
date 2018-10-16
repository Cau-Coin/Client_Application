package com.example.cau_coin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private ArrayList<String> filter_dept;
    private ArrayList<String> filter_semester;
    private ArrayList<String> filter_grade;
    private int num_Filter;

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

        TextView myGrade = (TextView) findViewById(R.id.detail_grade);
        TextView mySemester = (TextView) findViewById(R.id.detail_semester);
        TextView mySubject = (TextView) findViewById(R.id.detail_subject);
        TextView myEvaluate = (TextView) findViewById(R.id.detail_evaluate);
        TextView myTakeYear = (TextView) findViewById(R.id.detail_takeyear);
        TextView myReview = (TextView) findViewById(R.id.detail_review);
        TextView myTimeStamp = (TextView) findViewById(R.id.detail_timestamp);
        TextView myScore = (TextView) findViewById(R.id.detail_score);

        Button giveScore = (Button)findViewById(R.id.detail_givescore);
        Button registerComment = (Button)findViewById(R.id.detail_registerComment);
        final EditText inputComment = (EditText) findViewById(R.id.detail_inputComment);

        recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailReviewActivity.MyAdapter(myList, this);
        recyclerView.setAdapter(adapter);

        // 데이터 받아오고 나서 list 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        setData();

        for (int a = 0; a < dataList.size(); a++) {
            if (dataList.get(a).getEvaluateId().equals(evaluateId)) {
                myGrade.setText(dataList.get(a).getGrade() + "학년");
                mySemester.setText(dataList.get(a).getSemester() + "학기");
                mySubject.setText(dataList.get(a).getSubject());
                myEvaluate.setText(dataList.get(a).getEvaluate() + "점");
                myTakeYear.setText(dataList.get(a).getTakeYear() + "년 수강생");
                myReview.setText("리뷰 : " + dataList.get(a).getReview());
                myTimeStamp.setText(dataList.get(a).getTimeStamp());
                myScore.setText(dataList.get(a).getScore());

                myList.clear();
                for (int b = 0; b < dataList.get(a).getCommentNum(); b++) {
                    myList.add(new RecycleItem2(dataList.get(a).getComment(b)));
                }
                adapter.notifyDataSetChanged();
                break;
            }
        }

        final Database_Evaluate database = new Database_Evaluate(getApplicationContext(), "evaldb.db", null, 1);

        giveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database.getScore(id,evaluateId)){
                    final String[] items = new String[]{"1", "2", "3", "4","5"};
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
                            Toast.makeText(getApplicationContext(), items[selectedIndex[0]]+"점을 부여했습니다", Toast.LENGTH_SHORT).show();
                            database.insertData_Score(id,evaluateId);
                        }
                    }).create().show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "이미 이 강의평가에 평점을 매겼습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputComment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateTime = dateFormat.format(new Date());

                    Toast.makeText(getApplicationContext(), "["+currentDateTime+"] "+inputComment.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView cardview_text;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_text = (TextView) itemView.findViewById(R.id.cardview2_text);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("※ 상세 보기 종료");
        builder.setMessage("메인 화면으로 돌아가시겠어요?\n다시 보려면 코인을 지불해야 합니다.\n(단, 평점은 확인할 수 있습니다)");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent a = new Intent(DetailReviewActivity.this, MainActivity.class);
                a.putExtra("name", name);
                a.putExtra("major", major);
                a.putExtra("id", id);
                a.putExtra("from","detail");
                a.putExtra("filter_dept",filter_dept);
                a.putExtra("filter_grade",filter_grade);
                a.putExtra("filter_semester",filter_semester);
                a.putExtra("num_Filter",num_Filter);
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

    // 데이터 받아오고 나서 list 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void setData(){
        temp_score.add("5");
        temp_score.add("3");
        temp_score.add("2");
        temp_comment.add("교수님 좋아요!");
        temp_comment.add("교수님이 너무 좋은거 동감이에요!");
        dataList.add(new Data_Evaluate("00000001", "전자전기공학부", "1", "1", "선형대수학", "4", "2017",
                "교수님이 좋았어요", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("4");
        temp_score.add("5");
        temp_score.add("3");
        temp_comment.add("인정... 영어 그자체");
        temp_comment.add("교수님이 너무 야해요");
        dataList.add(new Data_Evaluate("00000002", "소프트웨어학부", "4", "1", "네트워크응용설계", "4", "2018",
                "교수님 영어실력은 감탄 그자체", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("3");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님은 좋아요");
        dataList.add(new Data_Evaluate("00000003", "소프트웨어학부", "3", "1", "컴파일러", "4", "2017",
                "교수님이 수업을 잘 안하심", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("5");
        temp_score.add("5");
        temp_score.add("3");
        temp_comment.add("수업이 너무 지루해요");
        temp_comment.add("교수님 진짜 별로임");
        dataList.add(new Data_Evaluate("00000004", "융합공학부", "2", "1", "미적분학", "1", "2018",
                "교수님 진짜 별로에요", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("5");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님 강의력은 정말 최고");
        temp_comment.add("시험문제가 진짜 어렵긴 함..");
        dataList.add(new Data_Evaluate("00000005", "융합공학부", "2", "2", "컴퓨터구조", "3", "2016",
                "시험이 너무 어려워요", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("4");
        temp_score.add("2");
        temp_score.add("5");
        temp_comment.add("수업시간에 졸수가 없어요...");
        temp_comment.add("논리회로에서 컴공을 포기하게 되었어요ㅠ");
        dataList.add(new Data_Evaluate("00000006", "소프트웨어학부", "1", "2", "논리회로", "5", "2015",
                "조성래교수님 사랑해요!", "???", temp_score, temp_comment));

        temp_comment.clear();
        temp_score.clear();
        temp_score.add("2");
        temp_score.add("4");
        temp_score.add("3");
        temp_comment.add("교수님 강의력만은 정말 최고에요");
        temp_comment.add("좀 졸리긴해요");
        dataList.add(new Data_Evaluate("00000007", "소프트웨어학부", "4", "2", "설계패턴", "4", "2018",
                "교수님이 조금 지루해요. 수업은 잘하세요!", "???", temp_score, temp_comment));
    }
}
