package com.example.ex_huangzhiyi001.emoji_widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.widget.DelegateView;
import com.example.widget.crush.CrushCardView;
import com.example.widget.dropdown.PulldownView;
import com.example.widget.fling.FlingCardView;
import com.example.widget.fling.FlingOutView;

import java.util.LinkedList;

/**
 *
 * @author ex-huangzhiyi001
 * @date 2019/1/7
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init () {
        if (getIntent() != null){
            String s = getIntent().getStringExtra("view");
            switch (s){
            case "PulldownView":
                setPulldownView();
                break;
            case "FlingOutView":
                setFlingOutView();
                break;
            case  "FlingCardView":
                setFlingCardView();
                break;
            case  "CrushCardView":
                setCrushCardView();
                break;
            case "others":
                break;
            default:
                break;
            }
        }
    }

    private void setCrushCardView () {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        final CrushCardView crushCardView = DelegateView.getInstance().getCrushCardView(this, bitmap);
        setContentView(crushCardView);
        crushCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                crushCardView.startAnimation();
            }
        });

    }

    private void setFlingCardView () {
        LinkedList<FlingCardView.DataHolder> dataHolders = new LinkedList<>();
        for (int i = 0; i < 7; i++){
            FlingCardView.DataHolder dataHolder = new FlingCardView.DataHolder();
            dataHolder.id = i+1;
            if (i % 3 == 0){
                dataHolder.bgId = R.drawable.card_child_bg_blue;
            } else if (i % 3 == 1){
                dataHolder.bgId = R.drawable.card_child_bg_red;
            }else {
                dataHolder.bgId = R.drawable.card_child_bg_green;
            }
            dataHolder.content = "我是第"+dataHolder.id +"个";
            dataHolders.add(dataHolder);
        }

        FlingCardView flingCardView = DelegateView.getInstance()
                .getFlingCardView(this, R.layout.view_fling_card_child, dataHolders);
        setContentView(flingCardView);
        flingCardView.setChildCount(5);
        flingCardView.setCardOffsetX(10);
        flingCardView.setCardOffsetY(10);
        flingCardView.setDataInterface(new FlingCardView.DataInterface() {
            @Override
            public void setCardContent (View view, FlingCardView.DataHolder dataHolder) {
                if (view instanceof RelativeLayout){
                    view.setBackgroundResource(dataHolder.bgId);
                    TextView tv_child_id = view.findViewById(R.id.tv_child_id);
                    tv_child_id.setText("ID: "+dataHolder.id);
                    TextView tv_child_content = view.findViewById(R.id.tv_child_content);
                    tv_child_content.setText("我是--- "+dataHolder.content);
                }
            }
        });

        flingCardView.setCardFlingInterface(new FlingCardView.CardFlingInterface() {
            @Override
            public void cardFlingOut (View view) {
            }

            @Override
            public void cardFlinging (View view, int dx, FlingCardView.DataHolder dataHolder) {
            }

            @Override
            public void cardEnd (View lastView) {
            }
        });

    }

    private void setFlingOutView () {
        RelativeLayout top = (RelativeLayout) View.inflate(this, R.layout.view_fling_out_top, null);
        RelativeLayout content = (RelativeLayout) View.inflate(this, R.layout.view_fling_out_content, null);
        FlingOutView flingOutView = DelegateView.getInstance().getFlingOutView(this, content, top);
        setContentView(flingOutView);

        flingOutView.mTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Log.e(TAG, "onClick: out mt" );
            }
        });
    }

    private void setPulldownView () {

        RelativeLayout top = (RelativeLayout) View.inflate(this, R.layout.view_pull_down_top, null);
        RelativeLayout content = (RelativeLayout) View.inflate(this, R.layout.view_pull_down_content, null);
        PulldownView pulldownView = DelegateView.getInstance().getPulldownView(this, top, content);

        pulldownView.setReleaseHeightCoe(0.3f);
        pulldownView.setHeightCoe(0.4f);
        pulldownView.setChildCallback(new PulldownView.ChildCallback() {
            @Override
            public void dragHeight (View dragView, int h) {
//                Log.e(TAG, "dragHeight: h "+h );
            }
        });
        View v = pulldownView.mContentView.findViewById(R.id.tv_top);
//        v.setPivotX(0);
//        v.setPivotY(0);
//        v.setRotation(10f);
        v.findViewById(R.id.tv_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Log.e(TAG, "onClick: mtop" );
            }
        });
        setContentView(pulldownView);
    }
}
