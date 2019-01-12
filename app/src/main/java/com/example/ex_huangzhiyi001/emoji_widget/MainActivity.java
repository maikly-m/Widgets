package com.example.ex_huangzhiyi001.emoji_widget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTvList;
    private RecyclerView mRvList;
    private String[] mStrings;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView () {
        mTvList = (TextView) findViewById(R.id.tv_list);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);

        mStrings = new String[]{"PulldownView", "FlingOutView", "FlingCardView", "CrushCardView","others"};
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, int i) {
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(20);
                textView.setTextColor(Color.GREEN);
                return new RecyclerView.ViewHolder(textView){};
            }

            @Override
            public void onBindViewHolder (@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
                ((TextView)viewHolder.itemView).setText(mStrings[i]);
                ((TextView)viewHolder.itemView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        Intent intent = new Intent(MainActivity.this, TestActivity.class);
                        intent.putExtra("view", mStrings[i]);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount () {
                return mStrings.length;
            }
        });
    }
}
