package com.example.cau_coin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class MainActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String major;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private final long CAUCOIN_INTERVAL_TIME = 3000;
    private long caucoinPressedTime = 0;
    private int caucoinClicked = 0;

    private EditText inputSearch;
    private TextView caucoin;

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
    ArrayList<String> temp_commentTime = new ArrayList<String>();

    ArrayList<String> lookupList = new ArrayList<String>();

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    private String fromwhere;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");
        fromwhere = getIntent().getExtras().getString("from");

        caucoin = (TextView) findViewById(R.id.main_caucoin);
        inputSearch = (EditText) findViewById(R.id.main_inputsearch);

        ImageView search = (ImageView) findViewById(R.id.main_search);
        TextView logout = (TextView) findViewById(R.id.main_signout);
        TextView filter = (TextView) findViewById(R.id.main_filter);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab1 = (FloatingActionButton) findViewById(R.id.main_fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.main_fab2);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        GetLookup lookupServer = new GetLookup();
        lookupServer.execute();

        final Database_AutoLogin database = new Database_AutoLogin(getApplicationContext(), "mydb.db", null, 1);

        caucoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - caucoinPressedTime;

                // 인터벌 시간이 유효시간 내인 경우
                if (0 <= intervalTime && CAUCOIN_INTERVAL_TIME >= intervalTime) {
                    if (caucoinClicked < 2) {
                        caucoinClicked++;
                    } else {
                        Intent a = new Intent(MainActivity.this, CauCoinActivity.class);
                        a.putExtra("name", name);
                        a.putExtra("major", major);
                        a.putExtra("id", id);
                        startActivity(a);
                        finish();
                    }
                }
                // 인터벌 시간이 초과된 경우
                else {
                    caucoinClicked = 1;
                    caucoinPressedTime = tempTime;
                }
            }
        });

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

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        searching();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searching();
            }
        });

        recyclerView2 = (RecyclerView) findViewById(R.id.main_recycler2);
        layoutManager2 = new LinearLayoutManager(this);

        ((LinearLayoutManager) layoutManager2).setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView2.setLayoutManager(layoutManager2);
        adapter2 = new MyAdapter2(myList2, this);
        recyclerView2.setAdapter(adapter2);

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
                                        dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore(), dataList.get(i).getEvaluate(), dataList.get(i).getReview()));
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
                                    }
                                    if (selectedIndex[0] == 0) {
                                        if (!filter_dept.contains(items2[selectedIndex[1]])) {
                                            filter_dept.add(items2[selectedIndex[1]]);
                                            num_Filter++;
                                        }
                                    } else if (selectedIndex[0] == 1) {
                                        if (!filter_grade.contains(items5[selectedIndex[1]])) {
                                            filter_grade.add(items5[selectedIndex[1]]);
                                            num_Filter++;
                                        }
                                    } else {
                                        if (!filter_semester.contains(items5[selectedIndex[1]])) {
                                            filter_semester.add(items5[selectedIndex[1]]);
                                            num_Filter++;
                                        }
                                    }
                                    inputSearch.setText("");

                                    setMainPageF();
                                }
                            }).create().show();
                        }
                    }
                }).create().show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
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

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("User Information");
                builder.setIcon(R.drawable.personimg);
                builder.setMessage("\n Student ID : " + id + "\n\n Name : " + name + "\n\n Major : " + major + "\n\n Holding Coin : " + "10");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog mydialog = builder.create();
                mydialog.show();
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
        }
        ReadData temp = new ReadData();
        temp.execute();
    }

    // 검색을 진행하는 함수
    public void searching() {
        hideKeyboard();
        int check2 = 0;
        if (inputSearch.getText().toString().equals("")) {
            if (num_Filter > 0) {
                setMainPageF();
            } else {
                setMainPage5();
            }
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
                                        dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore(), dataList.get(i).getEvaluate(), dataList.get(i).getReview()));
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

    // Floating Action Button 의 애니메이션 처리 함수
    public void anim() {
        if (isFabOpen) {
            fab.setImageResource(R.drawable.plusimg);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab.setImageResource(R.drawable.ximg2);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    // score가 가장 높은 5개 뽑아서 페이지 구성 (인기 게시글)
    public void setMainPage5() {
        myList.clear();
        temp_comment.clear();
        temp_score.clear();
        temp_commentTime.clear();
        Data_Evaluate data1 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment, temp_commentTime);
        Data_Evaluate data2 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment, temp_commentTime);
        Data_Evaluate data3 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment, temp_commentTime);
        Data_Evaluate data4 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment, temp_commentTime);
        Data_Evaluate data5 = new Data_Evaluate("", "", "", "", "", "", "", "", "", temp_score, temp_comment, temp_commentTime);
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
        switch (dataList.size()) {
            case 4:
                myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore(), data1.getEvaluate(), data1.getReview()));
                myList.add(new RecycleItem(data2.getDept(), data2.getGrade(), data2.getSemester(), data2.getSubject(), data2.getTakeYear(), data2.getEvaluateId(), data2.getScore(), data2.getEvaluate(), data2.getReview()));
                myList.add(new RecycleItem(data3.getDept(), data3.getGrade(), data3.getSemester(), data3.getSubject(), data3.getTakeYear(), data3.getEvaluateId(), data3.getScore(), data3.getEvaluate(), data3.getReview()));
                myList.add(new RecycleItem(data4.getDept(), data4.getGrade(), data4.getSemester(), data4.getSubject(), data4.getTakeYear(), data4.getEvaluateId(), data4.getScore(), data4.getEvaluate(), data4.getReview()));
                break;
            case 3:
                myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore(), data1.getEvaluate(), data1.getReview()));
                myList.add(new RecycleItem(data2.getDept(), data2.getGrade(), data2.getSemester(), data2.getSubject(), data2.getTakeYear(), data2.getEvaluateId(), data2.getScore(), data2.getEvaluate(), data2.getReview()));
                myList.add(new RecycleItem(data3.getDept(), data3.getGrade(), data3.getSemester(), data3.getSubject(), data3.getTakeYear(), data3.getEvaluateId(), data3.getScore(), data3.getEvaluate(), data3.getReview()));
                break;
            case 2:
                myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore(), data1.getEvaluate(), data1.getReview()));
                myList.add(new RecycleItem(data2.getDept(), data2.getGrade(), data2.getSemester(), data2.getSubject(), data2.getTakeYear(), data2.getEvaluateId(), data2.getScore(), data2.getEvaluate(), data2.getReview()));
                break;
            case 1:
                myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore(), data1.getEvaluate(), data1.getReview()));
                break;
            case 0:
                break;
            default:
                myList.add(new RecycleItem(data1.getDept(), data1.getGrade(), data1.getSemester(), data1.getSubject(), data1.getTakeYear(), data1.getEvaluateId(), data1.getScore(), data1.getEvaluate(), data1.getReview()));
                myList.add(new RecycleItem(data2.getDept(), data2.getGrade(), data2.getSemester(), data2.getSubject(), data2.getTakeYear(), data2.getEvaluateId(), data2.getScore(), data2.getEvaluate(), data2.getReview()));
                myList.add(new RecycleItem(data3.getDept(), data3.getGrade(), data3.getSemester(), data3.getSubject(), data3.getTakeYear(), data3.getEvaluateId(), data3.getScore(), data3.getEvaluate(), data3.getReview()));
                myList.add(new RecycleItem(data4.getDept(), data4.getGrade(), data4.getSemester(), data4.getSubject(), data4.getTakeYear(), data4.getEvaluateId(), data4.getScore(), data4.getEvaluate(), data4.getReview()));
                myList.add(new RecycleItem(data5.getDept(), data5.getGrade(), data5.getSemester(), data5.getSubject(), data5.getTakeYear(), data5.getEvaluateId(), data5.getScore(), data5.getEvaluate(), data5.getReview()));
                break;
        }
        checkLookup();
        myList2.clear();
        myList2.add(new RecycleItem3("인기게시글"));
        adapter2.notifyDataSetChanged();
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
                                dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore(), dataList.get(i).getEvaluate(), dataList.get(i).getReview()));
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
                ((ViewHolder) holder).firstLayout.setBackgroundColor(0xffeeeeee);

                ((ViewHolder) holder).cardview_status_unlook.setVisibility(View.INVISIBLE);
                ((ViewHolder) holder).cardview_status_look1.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).cardview_status_look2.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).cardview_status_look1.setText("[ 수강자 평점 : " + mItems.get(position).getEvaluate() + ", 평가 평점 : " + mItems.get(position).getScore() + " ]");
                ((ViewHolder) holder).cardview_status_look2.setText(mItems.get(position).getReview());
            } else {
                ((ViewHolder) holder).firstLayout.setBackgroundColor(0xffffffff);
                ((ViewHolder) holder).cardview_status_unlook.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).cardview_status_look1.setVisibility(View.INVISIBLE);
                ((ViewHolder) holder).cardview_status_look2.setVisibility(View.INVISIBLE);
                ((ViewHolder) holder).cardview_status_unlook.setText("미열람 상태이므로 리뷰 내용 확인 불가");
            }

            //String[] sub = mItems.get(position).getSubject().split("-");
            //((ViewHolder) holder).cardview_subject.setText(sub[0] + " : " + sub[1]);
            ((ViewHolder) holder).cardview_subject.setText(mItems.get(position).getSubject());

            ((ViewHolder) holder).cardview_takeyear.setText(mItems.get(position).getTakeYear() + " " + mItems.get(position).getSemester() + "학기 수강자");
            ((ViewHolder) holder).cardview_major.setText(mItems.get(position).getDept() + " " + mItems.get(position).getGrade() + "학년 " + mItems.get(position).getSemester() + "학기");
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout firstLayout;
            public TextView cardview_status_unlook;
            public TextView cardview_status_look1;
            public TextView cardview_status_look2;
            public TextView cardview_takeyear;
            public TextView cardview_subject;
            public TextView cardview_major;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_status_unlook = (TextView) itemView.findViewById(R.id.cardview_status_unlook);
                cardview_status_look1 = (TextView) itemView.findViewById(R.id.cardview_status_look1);
                cardview_status_look2 = (TextView) itemView.findViewById(R.id.cardview_status_look2);
                cardview_takeyear = (TextView) itemView.findViewById(R.id.cardview_takeyear);
                cardview_subject = (TextView) itemView.findViewById(R.id.cardview_subject);
                cardview_major = (TextView) itemView.findViewById(R.id.cardview_major);
                firstLayout = (LinearLayout) itemView.findViewById(R.id.cardview_firstlayout);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = getPosition();
                        // 코인을 사용하는 것에 대한 트랜잭션을 전송해야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        if (!mItems.get(position).getLookup()) {  // 처음 열람한 경우
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("리뷰 상세 보기");
                            builder.setMessage("코인을 사용하여 리뷰를 자세히 보겠어요?");
                            builder.setIcon(R.drawable.reviewimg);
                            builder.setCancelable(true);
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    WriteLookup temp = new WriteLookup();
                                    temp.execute(mItems.get(position).getEvaluateId());

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
                            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
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
                            } else {
                                setMainPageF();
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

            if (fromwhere.equals("detail")) {
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
                                dataList.get(i).getSubject(), dataList.get(i).getTakeYear(), dataList.get(i).getEvaluateId(), dataList.get(i).getScore(), dataList.get(i).getEvaluate(), dataList.get(i).getReview()));
                    }
                    checkLookup();
                } else {
                    setMainPageF();
                }
            } else {
                setMainPage5();
            }
        }
    }

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
                "조성래교수님 사랑해요!", "2015-11-12 01:05:10", temp_score, temp_comment, temp_commentTime));

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

    public void hideKeyboard() {
        imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
    }
}