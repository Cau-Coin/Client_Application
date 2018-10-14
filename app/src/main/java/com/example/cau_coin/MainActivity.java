package com.example.cau_coin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String major;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<RecycleItem> myList = new ArrayList<RecycleItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");

        TextView userName = (TextView)findViewById(R.id.main_userName);
        TextView userID = (TextView)findViewById(R.id.main_userID);
        TextView userMajor = (TextView)findViewById(R.id.main_userMajor);
        EditText inputSearch = (EditText)findViewById(R.id.main_inputsearch);

        Button write = (Button)findViewById(R.id.main_write);
        Button search = (Button)findViewById(R.id.main_search);
        Button logout = (Button)findViewById(R.id.main_logout);

        userName.setText(name);
        userID.setText(id);
        userMajor.setText(major);

        final Database_AutoLogin database = new Database_AutoLogin(getApplicationContext(),"mydb.db",null,1);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.dropTable();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                Intent a = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(a);
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this,WriteActivity.class);
                a.putExtra("name",name);
                a.putExtra("major",major);
                a.putExtra("id",id);
                startActivity(a);
                finish();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.main_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(myList,this);
        recyclerView.setAdapter(adapter);

        // 데이터 받아오고 나서 list에 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        myList.clear();
        myList.add(new RecycleItem("1","1","선형대수학","2017"));
        myList.add(new RecycleItem("2","1","자료구조","2018"));
        myList.add(new RecycleItem("2","2","컴퓨터구조","2018"));
        myList.add(new RecycleItem("4","1","캡스톤디자인","2018"));
        myList.add(new RecycleItem("4","2","설계패턴","2018"));
        myList.add(new RecycleItem("4","2","영상처리","2018"));
        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter{
        private Context context;
        private ArrayList<RecycleItem> mItems;

        private int lastPosition=-1;

        public MyAdapter(ArrayList items, Context mContext){
            mItems=items;
            context=mContext;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,parent,false);
            ViewHolder holder = new ViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).cardview_text.setText(mItems.get(position).getGrade()+"학년 "+mItems.get(position).getSemester()+"학기 - "+mItems.get(position).getSubject());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView cardview_text;

            public ViewHolder(View itemView) {
                super(itemView);
                cardview_text = (TextView)itemView.findViewById(R.id.cardview_text);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getPosition();
                        // 화면 전환 ( 상세보기로 보내면 될듯 )
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("※ Cau Coin 종료");
        builder.setMessage("정말로 앱을 종료하시겠어요?");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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