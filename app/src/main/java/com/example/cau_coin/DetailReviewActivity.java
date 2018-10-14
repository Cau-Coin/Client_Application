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
import android.widget.TextView;

import java.util.ArrayList;

public class DetailReviewActivity extends Activity {
    private String id;
    private String name;
    private String major;
    private String evaluateId;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<RecycleItem2> myList = new ArrayList<RecycleItem2>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailreview);

        id = getIntent().getExtras().getString("id");
        name = getIntent().getExtras().getString("name");
        major = getIntent().getExtras().getString("major");
        evaluateId = getIntent().getExtras().getString("evaluateId");


        recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailReviewActivity.MyAdapter(myList, this);
        recyclerView.setAdapter(adapter);

        // 데이터 받아오고 나서 list에 추가하는 작업 가져야 함@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        myList.clear();
        myList.add(new RecycleItem2("나쁘지 않은 평가에요!"));
        myList.add(new RecycleItem2("제 생각하고 같습니다."));
        myList.add(new RecycleItem2("맞아요 이 교수님은 별로에욧!"));
        adapter.notifyDataSetChanged();
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
                        int position = getPosition();
                        // 화면 전환 ( 상세보기로 보내면 될듯 )
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
                Intent a = new Intent(DetailReviewActivity.this,MainActivity.class);
                a.putExtra("name",name);
                a.putExtra("major",major);
                a.putExtra("id",id);
                startActivity(a);
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
