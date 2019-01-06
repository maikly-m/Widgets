package com.example.emoji.widgets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.emoji.widget.DelegateView;
import com.example.emoji.widget.dropdown.PulldownView;

/**
 * @author emoji
 */
public class TestActivity extends AppCompatActivity{
    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null){
            String extra = getIntent().getStringExtra("view");
            switch (extra){
            case "PullDownView":
                setPullDownView();
                break;
            case "others":

                break;
            default:
                break;
            }
        }

    }

    private void setPullDownView () {
        final RelativeLayout top = (RelativeLayout) View.inflate(this, R.layout.pull_down_view_top, null);
        RelativeLayout content = (RelativeLayout) View.inflate(this, R.layout.pull_down_view_content, null);

        final PulldownView pulldownView = DelegateView.getDelegateView().createPulldownView(this, top, content);
        setContentView(pulldownView);

        pulldownView.setChildCallback(new PulldownView.ChildCallback() {
            @Override
            public void dragHeight (View dragView, int h) {
                if (dragView == pulldownView.mContentView){
                    TextView v = top.findViewById(R.id.tv_v1);
                    if (h > 500){
                        v.setTextColor(Color.YELLOW);
                    }else {
                        v.setTextColor(Color.RED);
                    }
                } else if (dragView == pulldownView.mTopView){

                }
            }
        });
    }
}
