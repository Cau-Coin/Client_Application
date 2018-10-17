package com.example.cau_coin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String major;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<RecycleItem> myList = new ArrayList<RecycleItem>();

    private RecyclerView recyclerView2;
    private RecyclerView.LayoutManager layoutManager2;
    private RecyclerView.Adapter adapter2;
    private ArrayList<RecycleItem3> myList2 = new ArrayList<RecycleItem3>();

    private ArrayList<Data_Evaluate> dataList = new ArrayList<Data_Evaluate>();

    private int num_Filter = 0;
    private ArrayList<String> filter_dept = new ArrayList<String>();
    private ArrayList<String> filter_semester = new ArrayList<String>();
    private ArrayList<String> filter_grade = new ArrayList<String>();

    ArrayList<String> temp_score = new ArrayList<String>();
    ArrayList<String> temp_comment = new ArrayList<String>();

    ArrayList<String> lookupList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");
        String fromwhere = getIntent().getExtras().getString("from");

        TextView userName = (TextView) findViewById(R.id.main_userName);
        TextView userID = (TextView) findViewById(R.id.main_userID);
        TextView userMajor = (TextView) findViewById(R.id.main_userMajor);
        final EditText inputSearch = (EditText) findViewById(R.id.main_inputsearch);

        Button write = (Button) findViewById(R.id.main_write);
        Button search = (Button) findViewById(R.id.main_search);
        Button logout = (Button) findViewById(R.id.main_logout);
        Button filter = (Button) findViewById(R.id.main_filter);

        userName.setText(name);
        userID.setText(id);
        userMajor.setText(major);

        // 데이터 받아오고 나서 list 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        setData();

        GetLookup lookupServer = new GetLookup();
        lookupServer.execute();

        final Database_AutoLogin database = new Database_AutoLogin(getApplicationContext(), "mydb.db", null, 1);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.dropTable();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(a);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int check2 = 0;
                if (inputSearch.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "수업명을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    int check = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getSubject().contains(inputSearch.getText().toString())) {
                            check = 0;
                            for (int a = 0; a < filter_dept.size(); a++) {
                                if (dataList.get(i).getDept().contains(filter_dept.get(a)))
                                    check = 1;
                            }
                            if (filter_dept.size() == 0) check = 1;
                            if (check == 1) {
                                check = 0;
                                for (int b = 0; b < filter_grade.size(); b++) {
                                    if (dataList.get(i).getGrade().contains(filter_grade.get(b)))
                                        check = 1;
                                }
                                if (filter_grade.size() == 0) check = 1;

                                if (check == 1) {
                                    check = 0;
                                    for (int c = 0; c < filter_semester.size(); c++) {
                                        if (dataList.get(i).getSemester().contains(filter_semester.get(c)))
                                            check = 1;
                                    }
                                    if (filter_semester.size() == 0) check = 1;

                                    if (check == 1) {
                                        if (check2 == 0) {
                                            myList.clear();
                                            check2 = 1;
                                        }
                                        myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                                                dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                                        if (num_Filter <= 0) {
                                            myList2.clear();
                                            adapter2.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (check2 == 0) {
                        Toast.makeText(getApplicationContext(), "해당 단어를 포함하는 과목은 없습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        checkLookup();
                    }
                }

            }
        });

        recyclerView2 = (RecyclerView) findViewById(R.id.main_recycler2);
        layoutManager2 = new LinearLayoutManager(this);

        ((LinearLayoutManager) layoutManager2).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(layoutManager2);
        adapter2 = new MyAdapter2(myList2, this);
        recyclerView2.setAdapter(adapter2);

        myList2.clear();
        myList2.add(new RecycleItem3("인기게시글"));
        adapter2.notifyDataSetChanged();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"학부", "학년", "학기", "전체글보기"};
                final String[] items2 = new String[]{"소프트웨어학부", "융합공학부", "전자전기공학부"};
                final String[] items3 = new String[]{"1학년", "2학년", "3학년", "4학년"};
                final String[] items4 = new String[]{"1학기", "2학기"};
                final String[] items5 = new String[]{"1", "2", "3", "4"};
                final int[] selectedIndex = {0, 0};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("검색 옵션을 선택하세요").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex[0] = which;
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedIndex[0] == 3) {
                            myList2.clear();
                            myList2.add(new RecycleItem3("전체글보기"));
                            adapter2.notifyDataSetChanged();

                            num_Filter = -1;
                            filter_semester.clear();
                            filter_grade.clear();
                            filter_dept.clear();

                            myList.clear();
                            for (int i = 0; i < dataList.size(); i++) {
                                myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                                        dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                            }
                            checkLookup();
                        } else {
                            final String[] temp;
                            AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
                            if (selectedIndex[0] == 0) temp = items2;
                            else if (selectedIndex[0] == 1) temp = items3;
                            else temp = items4;

                            dialog2.setTitle("세부 옵션을 선택하세요").setSingleChoiceItems(temp, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedIndex[1] = which;
                                }
                            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (num_Filter <= 0) {
                                        num_Filter = 0;
                                        myList2.remove(0);
                                    }
                                    if (selectedIndex[0] == 0) {
                                        if (!filter_dept.contains(items2[selectedIndex[1]])) {
                                            filter_dept.add(items2[selectedIndex[1]]);
                                            myList2.add(new RecycleItem3(items2[selectedIndex[1]]));
                                            adapter2.notifyDataSetChanged();
                                            num_Filter++;
                                        }
                                    } else if (selectedIndex[0] == 1) {
                                        if (!filter_grade.contains(items5[selectedIndex[1]])) {
                                            myList2.add(new RecycleItem3(items3[selectedIndex[1]]));
                                            filter_grade.add(items5[selectedIndex[1]]);
                                            adapter2.notifyDataSetChanged();
                                            num_Filter++;
                                        }
                                    } else {
                                        if (!filter_semester.contains(items5[selectedIndex[1]])) {
                                            myList2.add(new RecycleItem3(items4[selectedIndex[1]]));
                                            filter_semester.add(items5[selectedIndex[1]]);
                                            adapter2.notifyDataSetChanged();
                                            num_Filter++;
                                        }
                                    }
                                    inputSearch.setText("");

                                    myList.clear();
                                    int check = 0;
                                    for (int i = 0; i < dataList.size(); i++) {
                                        check = 0;
                                        for (int a = 0; a < filter_dept.size(); a++) {
                                            if (dataList.get(i).getDept().contains(filter_dept.get(a)))
                                                check = 1;
                                        }
                                        if (filter_dept.size() == 0) check = 1;
                                        if (check == 1) {
                                            check = 0;
                                            for (int b = 0; b < filter_grade.size(); b++) {
                                                if (dataList.get(i).getGrade().contains(filter_grade.get(b)))
                                                    check = 1;
                                            }
                                            if (filter_grade.size() == 0) check = 1;

                                            if (check == 1) {
                                                check = 0;
                                                for (int c = 0; c < filter_semester.size(); c++) {
                                                    if (dataList.get(i).getSemester().contains(filter_semester.get(c)))
                                                        check = 1;
                                                }
                                                if (filter_semester.size() == 0) check = 1;

                                                if (check == 1) {
                                                    myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                                                            dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                                                }
                                            }
                                        }
                                    }
                                    checkLookup();
                                }
                            }).create().show();
                        }
                    }
                }).create().show();
            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, WriteActivity.class);
                a.putExtra("name", name);
                a.putExtra("major", major);
                a.putExtra("id", id);
                startActivity(a);
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(myList, this);
        recyclerView.setAdapter(adapter);

        if (fromwhere.equals("detail")) {
            filter_dept = (ArrayList<String>) getIntent().getSerializableExtra("filter_dept");
            filter_grade = (ArrayList<String>) getIntent().getSerializableExtra("filter_grade");
            filter_semester = (ArrayList<String>) getIntent().getSerializableExtra("filter_semester");
            num_Filter = getIntent().getExtras().getInt("num_Filter");

            if (num_Filter == 0) {
                setMainPage5();
            } else if (num_Filter == -1) {
                myList2.clear();
                myList2.add(new RecycleItem3("전체글보기"));
                adapter2.notifyDataSetChanged();

                filter_semester.clear();
                filter_grade.clear();
                filter_dept.clear();

                myList.clear();
                for (int i = 0; i < dataList.size(); i++) {
                    myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                            dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                }
                checkLookup();
            } else {
                setMainPageF();
            }
        } else {
            setMainPage5();
        }

    }

    // score가 가장 높은 5개 뽑아서 페이지 구성 (인기 게시글)
    public void setMainPage5() {
        myList.clear();
        temp_comment.clear();
        temp_score.clear();
        Data_Evaluate data1 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment);
        Data_Evaluate data2 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment);
        Data_Evaluate data3 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment);
        Data_Evaluate data4 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment);
        Data_Evaluate data5 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment);
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getDoubleScore() > data1.getDoubleScore()) {
                data5 = data4;
                data4 = data3;
                data3 = data2;
                data2 = data1;
                data1 = dataList.get(i);
            } else if (dataList.get(i).getDoubleScore() > data2.getDoubleScore()) {
                data5 = data4;
                data4 = data3;
                data3 = data2;
                data2 = dataList.get(i);
            } else if (dataList.get(i).getDoubleScore() > data3.getDoubleScore()) {
                data5 = data4;
                data4 = data3;
                data3 = dataList.get(i);
            } else if (dataList.get(i).getDoubleScore() > data4.getDoubleScore()) {
                data5 = data4;
                data4 = dataList.get(i);
            } else if (dataList.get(i).getDoubleScore() > data5.getDoubleScore()) {
                data5 = dataList.get(i);
            }
        }
        myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore()));
        myList.add(new RecycleItem(data2.getDept(), data2.getGrade(), data2.getSemester(), data2.getSubject(), data2.getTakeYear(), data2.getEvaluateId(), data2.getScore()));
        myList.add(new RecycleItem(data3.getDept(), data3.getGrade(), data3.getSemester(), data3.getSubject(), data3.getTakeYear(), data3.getEvaluateId(), data3.getScore()));
        myList.add(new RecycleItem(data4.getDept(), data4.getGrade(), data4.getSemester(), data4.getSubject(), data4.getTakeYear(), data4.getEvaluateId(), data4.getScore()));
        myList.add(new RecycleItem(data5.getDept(), data5.getGrade(), data5.getSemester(), data5.getSubject(), data5.getTakeYear(), data5.getEvaluateId(), data5.getScore()));
        checkLookup();
    }

    // 이전에 필터가 적용된 상태에서 상세 보기를 하고 왔을 때, 다시 필터를 적용시켜 준다.
    public void setMainPageF() {
        myList2.clear();
        for (int i = 0; i < filter_dept.size(); i++) {
            myList2.add(new RecycleItem3(filter_dept.get(i)));
        }
        for (int i = 0; i < filter_grade.size(); i++) {
            myList2.add(new RecycleItem3(filter_grade.get(i) + "학년"));
        }
        for (int i = 0; i < filter_semester.size(); i++) {
            myList2.add(new RecycleItem3(filter_semester.get(i) + "학기"));
        }
        adapter2.notifyDataSetChanged();

        myList.clear();
        int check;
        for (int i = 0; i < dataList.size(); i++) {
            check = 0;
            for (int a = 0; a < filter_dept.size(); a++) {
                if (dataList.get(i).getDept().contains(filter_dept.get(a)))
                    check = 1;
            }
            if (filter_dept.size() == 0) check = 1;
            if (check == 1) {
                check = 0;
                for (int b = 0; b < filter_grade.size(); b++) {
                    if (dataList.get(i).getGrade().contains(filter_grade.get(b)))
                        check = 1;
                }
                if (filter_grade.size() == 0) check = 1;

                if (check == 1) {
                    check = 0;
                    for (int c = 0; c < filter_semester.size(); c++) {
                        if (dataList.get(i).getSemester().contains(filter_semester.get(c)))
                            check = 1;
                    }
                    if (filter_semester.size() == 0) check = 1;

                    if (check == 1) {
                        myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                                dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                    }
                }
            }
        }
        checkLookup();
    }

    // 이미 조회한 이력이 있는지 체크하고 myList의 상태를 변경해주는 함수
    public void checkLookup() {
        for (int a = 0; a < myList.size(); a++) {
            for (int b = 0; b < lookupList.size(); b++) {
                if (myList.get(a).getEvaluateId().equals(lookupList.get(b))) {
                    myList.get(a).setLookup(true);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 게시글 리스트 출력하기 위한 리사이클러 뷰의 어댑터
    class MyAdapter extends RecyclerView.Adapter {
        private Context context;
        private ArrayList<RecycleItem> mItems;

        private int lastPosition = -1;

        public MyAdapter(ArrayList items, Context mContext) {
            mItems = items;
            context = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
            ViewHolder holder = new ViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mItems.get(position).getLookup()) {
                ((ViewHolder) holder).cardview_text.setBackgroundColor(0xaaaaaaaa);
                ((ViewHolder) holder).cardview_text.setText("※ 이미 조회한 정보입니다 - 평점 : " + mItems.get(position).getScore() + "\n\n※ " + mItems.get(position).getDept() + ", " +
                        mItems.get(position).getTakeYear() + "년 수강자\n" + mItems.get(position).getGrade() + "학년 " + mItems.get(position).getSemester() + "학기 " + mItems.get(position).getSubject());
            } else {
                ((ViewHolder) holder).cardview_text.setBackgroundColor(0xeeeeeeee);
                ((ViewHolder) holder).cardview_text.setText("※ " + mItems.get(position).getDept() + ", " + mItems.get(position).getTakeYear() + "년 수강자\n" +
                        mItems.get(position).getGrade() + "학년 " + mItems.get(position).getSemester() + "학기 " + mItems.get(position).getSubject());
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView cardview_text;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_text = (TextView) itemView.findViewById(R.id.cardview_text);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition();
                        // 코인을 사용하는 것에 대한 트랜잭션을 전송해야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        if (!mItems.get(position).getLookup()) {  // 처음 열람한 경우
                            WriteLookup temp = new WriteLookup();
                            temp.execute(mItems.get(position).getEvaluateId());
                        }
                        Intent a = new Intent(MainActivity.this, DetailReviewActivity.class);
                        a.putExtra("name", name);
                        a.putExtra("major", major);
                        a.putExtra("id", id);
                        a.putExtra("evaluateId", mItems.get(position).evaluateId);
                        a.putExtra("filter_dept", filter_dept);
                        a.putExtra("filter_grade", filter_grade);
                        a.putExtra("filter_semester", filter_semester);
                        a.putExtra("num_Filter", num_Filter);
                        startActivity(a);
                        finish();
                    }
                });
            }
        }
    }

    // 필터 리스트 출력하기 위한 리사이클러 뷰의 어댑터
    class MyAdapter2 extends RecyclerView.Adapter {
        private Context context;
        private ArrayList<RecycleItem3> mItems;

        private int lastPosition = -1;

        public MyAdapter2(ArrayList items, Context mContext) {
            mItems = items;
            context = mContext;
        }

        @Override
        public MainActivity.MyAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview3, parent, false);
            MainActivity.MyAdapter2.ViewHolder holder = new MainActivity.MyAdapter2.ViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MainActivity.MyAdapter2.ViewHolder) holder).cardview_text.setText(mItems.get(position).getFilter());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView cardview_text;
            public TextView cardview_button;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_text = (TextView) itemView.findViewById(R.id.cardview3_text);
                cardview_button = (TextView) itemView.findViewById(R.id.cardview3_button);

                cardview_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (num_Filter > 0) {
                            int position = getPosition();
                            if (filter_dept.contains(mItems.get(position).getFilter_noType())) {
                                filter_dept.remove(filter_dept.indexOf(mItems.get(position).getFilter_noType()));
                                myList2.remove(position);
                                adapter2.notifyDataSetChanged();
                                num_Filter--;
                            } else if (filter_grade.contains(mItems.get(position).getFilter_noType()) && mItems.get(position).getFilter_Type().equals("학년")) {
                                filter_grade.remove(filter_grade.indexOf(mItems.get(position).getFilter_noType()));
                                myList2.remove(position);
                                adapter2.notifyDataSetChanged();
                                num_Filter--;
                            } else if (filter_semester.contains(mItems.get(position).getFilter_noType()) && mItems.get(position).getFilter_Type().equals("학기")) {
                                filter_semester.remove(filter_semester.indexOf(mItems.get(position).getFilter_noType()));
                                myList2.remove(position);
                                adapter2.notifyDataSetChanged();
                                num_Filter--;
                            }

                            if (num_Filter == 0) {
                                setMainPage5();
                                myList2.add(new RecycleItem3("인기게시글"));
                                adapter2.notifyDataSetChanged();
                            } else {
                                myList.clear();
                                int check = 0;
                                for (int i = 0; i < dataList.size(); i++) {
                                    check = 0;
                                    for (int a = 0; a < filter_dept.size(); a++) {
                                        if (dataList.get(i).getDept().contains(filter_dept.get(a)))
                                            check = 1;
                                    }
                                    if (filter_dept.size() == 0) check = 1;
                                    if (check == 1) {
                                        check = 0;
                                        for (int b = 0; b < filter_grade.size(); b++) {
                                            if (dataList.get(i).getGrade().contains(filter_grade.get(b)))
                                                check = 1;
                                        }
                                        if (filter_grade.size() == 0) check = 1;

                                        if (check == 1) {
                                            check = 0;
                                            for (int c = 0; c < filter_semester.size(); c++) {
                                                if (dataList.get(i).getSemester().contains(filter_semester.get(c)))
                                                    check = 1;
                                            }
                                            if (filter_semester.size() == 0) check = 1;

                                            if (check == 1) {
                                                myList.add(new RecycleItem(dataList.get(i).getDept(), dataList.get(i).getGrade(), dataList.get(i).getSemester(),
                                                        dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore()));
                                            }
                                        }
                                    }
                                }
                                checkLookup();
                            }
                        } else if (num_Filter == -1) {
                            setMainPage5();
                            num_Filter = 0;
                            myList2.clear();
                            myList2.add(new RecycleItem3("인기게시글"));
                            adapter2.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    // 조회 리스트를 가져오기 위한 서버 연동 코드
    public class GetLookup extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                String url = "http://115.68.207.101/read_lookup.php?userid=" + id;

                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); // open connection

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
                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            lookupList.add(item.getString("e_id"));
                        }

                        checkLookup();
                    }

                } catch (JSONException e) {
                }
            }

        }
    }

    // 조회 시 조회 리스트에 추가하기 위한 서버 연동 코드
    public class WriteLookup extends AsyncTask<String, Void, String> {

        public String doInBackground(String... params) {
            try {
                String eval_id = params[0];

                String url = "http://115.68.207.101/write_lookup.php?userid=" + id + "&evalid=" + eval_id;
                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); // open connection

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
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "종료하려면 한번 더 누르세요", Toast.LENGTH_SHORT).show();
        }
    }

    // 데이터 받아오고 나서 list 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void setData() {
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