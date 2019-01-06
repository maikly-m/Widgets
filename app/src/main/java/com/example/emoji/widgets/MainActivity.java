package com.example.emoji.widgets;

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

/**
 * @author emoji
 */
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
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mStrings = new String[]{"PullDownView", "others"};
        mRvList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, int i) {
                final TextView textView = new TextView(MainActivity.this);
                textView.setTextColor(Color.BLUE);
                textView.setTextSize(18);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        Intent intent = new Intent(MainActivity.this, TestActivity.class);
                        intent.putExtra("view", textView.getText());
                        startActivity(intent);
                    }
                });
                return new RecyclerView.ViewHolder(textView){};
            }

            @Override
            public void onBindViewHolder (@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                TextView itemView = (TextView) viewHolder.itemView;
                itemView.setText(mStrings[i]);
            }

            @Override
            public int getItemCount () {
                return mStrings.length;
            }
        });
    }
}
