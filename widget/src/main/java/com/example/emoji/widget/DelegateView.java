package com.example.emoji.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.emoji.widget.dropdown.PulldownView;

public class DelegateView {
    private static DelegateView instance = new DelegateView();

    public static DelegateView getDelegateView(){
        return instance;
    }

    public PulldownView createPulldownView(Context context, ViewGroup top, ViewGroup content){
        PulldownView pulldownView = (PulldownView) View.inflate(context, R.layout.pull_down_view_parent, null);
        pulldownView.mTopView.addView(top);
        pulldownView.mContentView.addView(content);
        return pulldownView;
    }
}
