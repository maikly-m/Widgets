package com.example.emoji.widget.dropdown;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class PulldownView extends FrameLayout {
    private static final String TAG = "PulldownView";

    public RelativeLayout mTopView;
    public RelativeLayout mContentView;
    private int mTempTop = 0;
    private ViewDragHelper.Callback mHelpCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView (@NonNull View view, int i) {
            return mTopView == view || mContentView == view;
        }

        @Override
        public void onViewCaptured (@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased (@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (releasedChild == mTopView){
                if (mTempTop < -mTopView.getMeasuredHeight()*mReleaseHeightCoe){
                    mViewDragHelper.settleCapturedViewAt(0,
                            (int) (-mTopView.getMeasuredHeight()*(1- mHeightCoe)));
                } else{
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                }
            } else if (releasedChild == mContentView){
                if (mTempTop > mContentView.getMeasuredHeight()*mReleaseHeightCoe){
                    mViewDragHelper.settleCapturedViewAt(0, mContentView.getMeasuredHeight());
                } else{
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                }
            }
            invalidate();
        }

        @Override
        public void onViewPositionChanged (@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            int t = 0;
            if (changedView == mTopView){
                t = (int) (top / (1- mHeightCoe));
                mContentView.layout(0, t + changedView.getMeasuredHeight(),
                        mContentView.getMeasuredWidth(),
                        t + changedView.getMeasuredHeight() + mContentView.getMeasuredHeight());
            } else if (changedView == mContentView){
                t = (int) (top * (1- mHeightCoe));
                mTopView.layout(0, (int) (t - mTopView.getMeasuredHeight() * (1- mHeightCoe)),
                        mTopView.getMeasuredWidth(),
                        (int) (t + mTopView.getMeasuredHeight() * mHeightCoe));
            }
            if (mChildCallback != null){
                mChildCallback.dragHeight(changedView, t);
            }
        }

        @Override
        public int clampViewPositionVertical (@NonNull View child, int top, int dy) {
            // top 向下滑动变大，上滑动变小
            // top 就是child的margintop
            // Log.e(TAG, "clampViewPositionVertical: top "+top);
            // 限定位置

            if (child == mTopView){
                if (top < -mTopView.getMeasuredHeight() * (1- mHeightCoe)){
                    top = (int) (-mTopView.getMeasuredHeight() * (1- mHeightCoe));
                } else if (top > 0){
                    top = 0;
                }
            } else if (child == mContentView){
                if (top < 0){
                    top = 0;
                } else if (top > mContentView.getMeasuredHeight()){
                    top = mContentView.getMeasuredHeight();
                }
            }
            //阈值
            mTempTop = top;
            return top;
        }
    };
    private ViewDragHelper mViewDragHelper;
    //topview 初始化显示的高度系数
    //初始遮盖1/4
    private float mHeightCoe = 0.25f;
    private ChildCallback mChildCallback;

    private float mReleaseHeightCoe = 0.4f;

    public PulldownView (@NonNull Context context) {
        this(context, null);
    }

    public PulldownView (@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulldownView (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init (@NonNull Context context) {
        mViewDragHelper = ViewDragHelper.create(this, mHelpCallback);
    }

    /**
     * topview 初始化显示的高度系数
     * @param heightCoe
     */
    public void setHeightCoe (float heightCoe) {
        this.mHeightCoe = heightCoe;
        invalidate();
    }

    /**
     * 释放时的高度设置
     * @param releaseHeightCoe
     */
    public void setReleaseHeightCoe (float releaseHeightCoe) {
        mReleaseHeightCoe = releaseHeightCoe;
        invalidate();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        RelativeLayout topView = (RelativeLayout) getChildAt(0);
        RelativeLayout contentView = (RelativeLayout) getChildAt(1);

        int w_ = topView.getMeasuredWidth();
        int h_ = topView.getMeasuredHeight();
        topView.layout(0, (int) (-h_ * (1- mHeightCoe)), w_, (int) (h_ * mHeightCoe));

        int w = contentView.getMeasuredWidth();
        int h = contentView.getMeasuredHeight();
        contentView.layout(0, 0, w, h);
//        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onInterceptHoverEvent (MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
//        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
//        return super.onTouchEvent(event);
    }

    @Override
    protected void onFinishInflate () {
        super.onFinishInflate();
        if (getChildCount() != 2){
            throw new IllegalArgumentException("children must be two");
        }
        mTopView = (RelativeLayout) getChildAt(0);
        mContentView = (RelativeLayout) getChildAt(1);

    }

    @Override
    public void computeScroll () {
        super.computeScroll();
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    public void setChildCallback (ChildCallback childCallback){
        this.mChildCallback = childCallback;
    }

    public interface ChildCallback {
        /**
         *
         * @param dragView 正在拖拽的view
         * @param h top的高度变化值
         */
        void dragHeight (View dragView, int h);
    }
}
