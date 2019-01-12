package com.example.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.example.widget.crush.CrushCardView;
import com.example.widget.dropdown.PulldownView;
import com.example.widget.fling.FlingCardView;
import com.example.widget.fling.FlingOutView;

import java.util.LinkedList;

/**
 * Created by ex-huangzhiyi001 on 2019/1/7.
 */
public class DelegateView {
    private static DelegateView instance = new DelegateView();

    public static DelegateView getInstance(){
        return instance;
    }

    public PulldownView getPulldownView(Context context, ViewGroup top, ViewGroup content){
        PulldownView view = (PulldownView) View.inflate(context, R.layout.view_pull_down, null);
        view.mTopView.addView(top);
        view.mContentView.addView(content);
        return view;
    }

    public FlingOutView getFlingOutView(Context context, ViewGroup bottom, ViewGroup top){
        FlingOutView view = (FlingOutView) View.inflate(context, R.layout.view_fling_out, null);
        view.mBottomView.addView(bottom);
        view.mTopView.addView(top);
        return view;
    }

    public FlingCardView getFlingCardView(Context context, @LayoutRes int child, LinkedList<FlingCardView.DataHolder> linkedList){
        FlingCardView view = new FlingCardView(context);
        view.setChild(child, linkedList);
        return view;
    }

    public CrushCardView getCrushCardView(Context context, Bitmap bitmap){
//        CrushCardView view = (CrushCardView) View.inflate(context, R.layout.view_crush_card, null);
        CrushCardView view = new CrushCardView(context);
        view.setCrushBitmap(bitmap);
        return view;
    }
}
