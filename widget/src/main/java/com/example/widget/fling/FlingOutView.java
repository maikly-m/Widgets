package com.example.widget.fling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

/**
 * Created by ex-huangzhiyi001 on 2019/1/8.
 */
public class FlingOutView extends FrameLayout {
    private static final String TAG = "FlingOutView";
    public RelativeLayout mBottomView;
    public RelativeLayout mTopView;
    private float mInterceptDownX;
    private float mInterceptDownY;
    private float mTouchDownX;
    private float mTouchDownY;
    private int mTopView_top;
    private int mTopView_left;
    private float mTempMoveX;
    private float mTempMoveY;
    private AccelerateInterpolator mAccelerateInterpolator;
    private OverScroller mOverScroller;
    private boolean directionToRight;


    public FlingOutView (@NonNull Context context) {
        this(context, null);
    }

    public FlingOutView (@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlingOutView (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init (Context context) {
        mAccelerateInterpolator = new AccelerateInterpolator();
        mOverScroller = new OverScroller(context, mAccelerateInterpolator);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
        boolean flag = false;
        switch (ev.getAction() & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            mInterceptDownX = ev.getX();
            mInterceptDownY = ev.getY();
            flag = false;
            break;
        case MotionEvent.ACTION_MOVE:
            float x = ev.getX();
            float y = ev.getY();
            float mX = Math.abs(x - mInterceptDownX);
            float mY = Math.abs(y - mInterceptDownY);
            if (mX > 25){
                // 滑动
                flag = true;
            } else{
                // 点击
                flag = false;
            }
            break;
        case MotionEvent.ACTION_UP:
            flag = false;
            break;
        default:
            break;
        }
        return flag;
        //return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            mTouchDownX = event.getX();
            mTouchDownY = event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            float x = event.getX();
            float y = event.getY();
            //没有走down事件的时候
            if (mTouchDownX == 0){
                mTouchDownX = x;
            }
            if (mTouchDownY == 0){
                mTouchDownY = y;
            }

            int dx = (int) (x - mTouchDownX);
            directionToRight = dx > 0;
            if (dx > mTopView.getMeasuredWidth() * 1.2){
                break;
            } else if (dx < -mTopView.getMeasuredWidth() * 1.2){
                break;
            }
            dx = dx << 1;
            layoutTop(dx);
            layoutBottom(dx);

            mTempMoveX = x;
            mTempMoveY = y;
            break;
        case MotionEvent.ACTION_UP:
            mTouchDownX = 0;
            mTouchDownY = 0;
            startResetLayout();
            break;
        default:
            break;
        }
        return true;
//        return super.onTouchEvent(event);
    }

    private void layoutBottom (int dx) {
        float degree = 0.9f - dx * 1f / (mTopView.getMeasuredWidth() * 0.6f) * 0.05f;
        if (dx > 0){
            degree = 0.9f + dx * 1f / (mTopView.getMeasuredWidth() * 0.6f) * 0.05f;
        }
        mBottomView.setPivotX(mBottomView.getMeasuredWidth() >> 1);
        mBottomView.setPivotY(mBottomView.getMeasuredHeight() >> 1);
        mBottomView.setScaleX(degree);
        mBottomView.setScaleY(degree);
        invalidate();
    }

    private void startResetLayout () {
        //移动
        mOverScroller.startScroll(mTopView.getLeft(), mTopView.getTop(),
                mTopView_left - mTopView.getLeft(), mTopView_top - mTopView.getTop(), 300);
        invalidate();
    }

    private void layoutTop (int dx) {
        //调整方向
        int dy = dx / 12;
        if (dx > 0){
            dy = -dx / 12;
        }
        mTopView.layout(mTopView_left + dx, mTopView_top + dy, mTopView_left + dx + mTopView.getMeasuredWidth(),
                mTopView_top + dy + mTopView.getMeasuredHeight());
        float degree = dx * 10f / mTopView.getMeasuredWidth();
        mTopView.setPivotX(mTopView.getMeasuredWidth() >> 1);
        mTopView.setPivotY(mTopView.getMeasuredHeight() >> 1);
        mTopView.setRotation(degree);
        invalidate();

    }

    @Override
    public void computeScroll () {
        super.computeScroll();
        if (mOverScroller.computeScrollOffset()){
            resetTopLayout();
            resetBottomLayout();
        }
    }

    private void resetBottomLayout () {
        int currX = mOverScroller.getCurrX();
        float degree = 0.9f - currX * 1f / (mTopView.getMeasuredWidth() * 0.6f) * 0.05f;
        if (currX > 0){
            degree = 0.9f + currX * 1f / (mTopView.getMeasuredWidth() * 0.6f) * 0.05f;
        }
        mBottomView.setPivotX(mBottomView.getMeasuredWidth() >> 1);
        mBottomView.setPivotY(mBottomView.getMeasuredHeight() >> 1);
        mBottomView.setScaleX(degree);
        mBottomView.setScaleY(degree);
        invalidate();
    }

    private void resetTopLayout () {
        int currX = mOverScroller.getCurrX();
        int currY = mOverScroller.getCurrY();

        mTopView.layout(currX, currY, currX + mTopView.getMeasuredWidth(),
                currY + mTopView.getMeasuredHeight());
        float degree = currX * 10f / mTopView.getMeasuredWidth();
        //防止复位失败
        if (Math.abs(degree) < 2.5f){
            degree = 0f;
        }
        mTopView.setPivotX(mTopView.getMeasuredWidth() >> 1);
        mTopView.setPivotY(mTopView.getMeasuredHeight() >> 1);
        mTopView.setRotation(degree);
        invalidate();

    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();

        RelativeLayout bottomView = (RelativeLayout) getChildAt(0);
        RelativeLayout topView = (RelativeLayout) getChildAt(1);

        int w_ = bottomView.getMeasuredWidth();
        int h_ = bottomView.getMeasuredHeight();
        bottomView.layout((parentWidth - w_) / 2, (parentHeight - h_) / 2,
                (parentWidth + w_) / 2, (parentHeight + h_) / 2);

        int w = topView.getMeasuredWidth();
        int h = topView.getMeasuredHeight();
        topView.layout((parentWidth - w) / 2, (parentHeight - h) / 2,
                (parentWidth + w) / 2, (parentHeight + h) / 2);

        mTopView_top = mTopView.getTop();
        mTopView_left = mTopView.getLeft();
//        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onFinishInflate () {
        super.onFinishInflate();
        if (getChildCount() != 2){
            throw new IllegalArgumentException("children must be two");
        }
        mBottomView = (RelativeLayout) getChildAt(0);
        mTopView = (RelativeLayout) getChildAt(1);

    }
}
